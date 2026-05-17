package com.gx.etl.operator.core;

/**
 * Base class for all ETL operators.
 * Each operator must implement this class and be annotated with @Component.
 */
public abstract class AbstractOperator {

    /**
     * Unique operator ID, matches frontend TaskTypeConfig.type
     */
    public abstract String getId();

    /**
     * Display name (Chinese)
     */
    public abstract String getName();

    /**
     * Category: input, output, transform, script
     */
    public abstract String getCategory();

    /**
     * JSON Schema for frontend configuration form
     */
    public abstract OperatorSchema getSchema();

    /**
     * Execute the operator.
     * @param ctx execution context with config and data source access
     * @param input input dataset from upstream operator (empty for source operators)
     * @return output dataset (empty for sink operators)
     */
    public abstract DataSet execute(OperatorContext ctx, DataSet input);

    /**
     * Whether this operator requires materialized (collected) input.
     * Override to return true for Sort, Join, Unique etc.
     */
    public boolean requiresMaterializedInput() {
        return false;
    }
}
