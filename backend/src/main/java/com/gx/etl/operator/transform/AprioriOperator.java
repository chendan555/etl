package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Apriori关联规则算子
 * 实现Apriori算法，发现频繁项集和关联规则
 */
@Component
public class AprioriOperator extends AbstractOperator {

    @Override public String getId() { return "Apriori"; }
    @Override public String getName() { return "Apriori关联规则"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("delta", "迭代递减单位", OperatorSchema.PropertyType.NUMBER)
                .property("minMetric", "度量最小值", OperatorSchema.PropertyType.NUMBER)
                .property("lowerBoundMinSupport", "最小支持度下界", OperatorSchema.PropertyType.NUMBER)
                .property("upperBoundMinSupport", "最小支持度上界", OperatorSchema.PropertyType.NUMBER)
                .property("numRules", "规则数", OperatorSchema.PropertyType.NUMBER)
                .property("inputField", "分析字段", OperatorSchema.PropertyType.STRING, true);
    }

    @Override
    public boolean requiresMaterializedInput() { return true; }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        double lowerBound = toDouble(ctx.get("lowerBoundMinSupport"), 0.3);
        double upperBound = toDouble(ctx.get("upperBoundMinSupport"), 0.9);
        double delta = toDouble(ctx.get("delta"), 0.05);
        double minMetric = toDouble(ctx.get("minMetric"), 0.9);
        int numRules = toInt(ctx.get("numRules"), 10);

        List<String> inputFields = parseInputFields(ctx);
        List<DataRow> rows = input.collect();

        if (rows.isEmpty() || inputFields.isEmpty()) return input;

        // 提取事务数据
        List<Set<String>> transactions = new ArrayList<>();
        for (DataRow row : rows) {
            Set<String> transaction = new LinkedHashSet<>();
            for (String field : inputFields) {
                Object val = row.get(field);
                if (val != null && !val.toString().isEmpty()) {
                    transaction.add(val.toString().trim());
                }
            }
            if (!transaction.isEmpty()) {
                transactions.add(transaction);
            }
        }

        int totalTransactions = transactions.size();
        if (totalTransactions == 0) return input;

        // Apriori算法：寻找频繁项集
        List<List<Set<String>>> allFrequentItemsets = new ArrayList<>();
        double support = upperBound;

        while (support >= lowerBound) {
            Map<Set<String>, Integer> itemCounts = countItemsets(transactions, support, totalTransactions);
            List<Set<String>> frequentItemsets = new ArrayList<>(itemCounts.keySet());
            allFrequentItemsets.add(frequentItemsets);
            support -= delta;
        }

        // 生成关联规则
        List<String> columns = new ArrayList<>(input.getColumnNames());
        columns.add("antecedent");
        columns.add("consequent");
        columns.add("support");
        columns.add("confidence");

        List<DataRow> result = new ArrayList<>();
        int ruleCount = 0;
        for (List<Set<String>> itemsets : allFrequentItemsets) {
            for (Set<String> itemset : itemsets) {
                if (itemset.size() < 2) continue;
                List<Set<String>> subsets = getSubsets(itemset);
                for (Set<String> antecedent : subsets) {
                    if (antecedent.isEmpty() || antecedent.equals(itemset)) continue;
                    Set<String> consequent = new LinkedHashSet<>(itemset);
                    consequent.removeAll(antecedent);
                    if (consequent.isEmpty()) continue;

                    double ruleSupport = (double) countSupport(transactions, itemset) / totalTransactions;
                    double antecedentSupport = (double) countSupport(transactions, antecedent) / totalTransactions;
                    double confidence = antecedentSupport > 0 ? ruleSupport / antecedentSupport : 0;

                    if (confidence >= minMetric && ruleCount < numRules) {
                        DataRow newRow = new DataRow();
                        newRow.put("antecedent", antecedent.toString());
                        newRow.put("consequent", consequent.toString());
                        newRow.put("support", ruleSupport);
                        newRow.put("confidence", confidence);
                        result.add(newRow);
                        ruleCount++;
                    }
                }
            }
        }

        return DataSet.of(columns, result);
    }

    private Map<Set<String>, Integer> countItemsets(List<Set<String>> transactions,
                                                     double minSupport, int totalTransactions) {
        Map<Set<String>, Integer> counts = new LinkedHashMap<>();
        for (Set<String> transaction : transactions) {
            for (Set<String> itemset : generateItemsets(transaction)) {
                counts.merge(itemset, 1, Integer::sum);
            }
        }
        Map<Set<String>, Integer> frequent = new LinkedHashMap<>();
        for (Map.Entry<Set<String>, Integer> entry : counts.entrySet()) {
            if ((double) entry.getValue() / totalTransactions >= minSupport) {
                frequent.put(entry.getKey(), entry.getValue());
            }
        }
        return frequent;
    }

    private List<Set<String>> generateItemsets(Set<String> transaction) {
        List<Set<String>> itemsets = new ArrayList<>();
        List<String> items = new ArrayList<>(transaction);
        int n = items.size();
        for (int mask = 1; mask < (1 << n); mask++) {
            Set<String> itemset = new LinkedHashSet<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    itemset.add(items.get(i));
                }
            }
            itemsets.add(itemset);
        }
        return itemsets;
    }

    private List<Set<String>> getSubsets(Set<String> set) {
        List<Set<String>> subsets = new ArrayList<>();
        List<String> items = new ArrayList<>(set);
        int n = items.size();
        for (int mask = 0; mask < (1 << n); mask++) {
            Set<String> subset = new LinkedHashSet<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    subset.add(items.get(i));
                }
            }
            subsets.add(subset);
        }
        return subsets;
    }

    private int countSupport(List<Set<String>> transactions, Set<String> itemset) {
        int count = 0;
        for (Set<String> transaction : transactions) {
            if (transaction.containsAll(itemset)) {
                count++;
            }
        }
        return count;
    }

    @SuppressWarnings("unchecked")
    private List<String> parseInputFields(OperatorContext ctx) {
        Object fieldObj = ctx.get("inputField");
        if (fieldObj instanceof List) {
            return (List<String>) fieldObj;
        }
        if (fieldObj instanceof String) {
            return Arrays.asList(((String) fieldObj).split(","));
        }
        return Collections.emptyList();
    }

    private double toDouble(Object val, double def) {
        if (val == null) return def;
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return def; }
    }

    private int toInt(Object val, int def) {
        if (val == null) return def;
        try { return Integer.parseInt(val.toString()); } catch (Exception e) { return def; }
    }
}
