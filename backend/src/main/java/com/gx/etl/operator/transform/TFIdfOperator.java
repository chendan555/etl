package com.gx.etl.operator.transform;

import com.gx.etl.operator.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.*;

/**
 * TF-IDF 关键词权重算子
 * 计算指定关键词在文本列中的 TF-IDF 值。
 * 参考: gx-plugin-tfidf TFIdf + TF + IDF
 *
 * 处理流程:
 *   1. 读取文本列，将所有行文本拼接
 *   2. 对每个关键词计算 TF = 词频 / 总词数
 *   3. 对每个关键词计算 IDF = log(总文档数 / 包含该词的文档数)
 *   4. TF-IDF = TF * IDF
 *
 * 参数:
 *   textField  - 文本列名
 *   keywords   - 关键词列表（逗号分隔）
 *   outField   - 输出字段名前缀（默认 tfidf）
 */
@Component
public class TFIdfOperator extends AbstractOperator {

    @Override public String getId() { return "TFIdf"; }
    @Override public String getName() { return "TF-IDF"; }
    @Override public String getCategory() { return "transform"; }

    @Override
    public OperatorSchema getSchema() {
        return OperatorSchema.builder()
                .property("stepName", "步骤名称", OperatorSchema.PropertyType.STRING, true)
                .property("textField", "文本字段", OperatorSchema.PropertyType.STRING, true)
                .property("keywords", "关键词(逗号分隔)", OperatorSchema.PropertyType.STRING, true)
                .property("outFieldName", "输出字段名", OperatorSchema.PropertyType.STRING);
    }

    @Override
    public DataSet execute(OperatorContext ctx, DataSet input) {
        String textField = ctx.getString("textField");
        String keywordsStr = ctx.getString("keywords");
        String outFieldName = ctx.getString("outFieldName");
        if (outFieldName == null || outFieldName.isEmpty()) outFieldName = "tfidf";

        if (textField == null || textField.isEmpty() || keywordsStr == null || keywordsStr.isEmpty()) {
            return input;
        }

        // 解析关键词列表
        List<String> keywordList = new ArrayList<>();
        for (String kw : keywordsStr.split(",")) {
            String trimmed = kw.trim();
            if (!trimmed.isEmpty()) keywordList.add(trimmed);
        }
        if (keywordList.isEmpty()) return input;

        // 收集所有行的文本，同时按行分词用于IDF计算
        List<String> allTexts = new ArrayList<>();
        List<List<String>> docTokens = new ArrayList<>();
        int totalWordCount = 0;

        for (DataRow row : input) {
            Object textObj = row.get(textField);
            String text = textObj != null ? textObj.toString() : "";
            allTexts.add(text);
            List<String> tokens = tokenize(text);
            docTokens.add(tokens);
            totalWordCount += tokens.size();
        }

        int totalDocs = docTokens.size();
        if (totalDocs == 0 || totalWordCount == 0) {
            return input;
        }

        // 计算每个关键词在所有文档中的词频（用于IDF）
        Map<String, Integer> docFrequency = new HashMap<>();
        for (List<String> tokens : docTokens) {
            Set<String> uniqueTokens = new HashSet<>(tokens);
            for (String token : uniqueTokens) {
                docFrequency.merge(token, 1, Integer::sum);
            }
        }

        // 计算全局 TF：所有文本拼接后的词频
        Map<String, Integer> globalTf = new HashMap<>();
        for (List<String> tokens : docTokens) {
            for (String token : tokens) {
                globalTf.merge(token, 1, Integer::sum);
            }
        }

        // 构建输出
        List<String> columns = new ArrayList<>();
        columns.add("keyword");
        columns.add(outFieldName);

        List<DataRow> result = new ArrayList<>();
        for (String keyword : keywordList) {
            DataRow outRow = new DataRow();
            outRow.put("keyword", keyword);

            // TF: 全局词频 / 总词数
            int count = globalTf.getOrDefault(keyword, 0);
            double tf = (double) count / totalWordCount;

            // IDF: log(总文档数 / 包含该词的文档数)
            int df = docFrequency.getOrDefault(keyword, 0);
            double idf = df > 0 ? Math.log((double) totalDocs / df) : 0.0;

            // TF-IDF
            double tfidf = tf * idf;
            outRow.put(outFieldName, tfidf);
            result.add(outRow);
        }

        return DataSet.of(columns, result);
    }

    /**
     * 简易分词器:
     * - 英文按空格/标点分割
     * - 中文按单字分割（unigram）
     * - 保留连续中文序列的 bigram（二元组）
     */
    private List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.isEmpty()) return tokens;

        // 提取中文字符序列和英文单词
        Matcher matcher = Pattern.compile("[\\u4e00-\\u9fff]+|[a-zA-Z]+|[0-9]+").matcher(text);
        while (matcher.find()) {
            String segment = matcher.group();
            if (isChinese(segment)) {
                // 中文：unigram + bigram
                for (int i = 0; i < segment.length(); i++) {
                    tokens.add(String.valueOf(segment.charAt(i)));
                }
                for (int i = 0; i < segment.length() - 1; i++) {
                    tokens.add(segment.substring(i, i + 2));
                }
            } else {
                // 英文/数字：转小写
                tokens.add(segment.toLowerCase());
            }
        }
        return tokens;
    }

    private boolean isChinese(String str) {
        for (char c : str.toCharArray()) {
            if (c < 0x4e00 || c > 0x9fff) return false;
        }
        return true;
    }
}
