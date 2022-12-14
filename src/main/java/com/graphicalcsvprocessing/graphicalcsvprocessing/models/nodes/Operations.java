package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes;

/**
 * corresponding name of operation in serialized graph sent to API
 * used for node deserialization
 */
public class Operations {
    public static final String ALIAS = "alias";
    public static final String COLUMN_MATH = "column_math";
    public static final String CONCAT_COLUMNS = "concat_columns";
    public static final String CONCAT_TABLES = "concat_tables";
    public static final String DROP_ALIAS = "drop_alias";
    public static final String DROP_COLUMNS = "drop_columns";
    public static final String FILTER = "filter";
    public static final String JOIN = "join";
    public static final String LIMIT = "limit";
    public static final String MERGE_COLUMNS = "merge_columns";
    public static final String MERGE_ROWS = "merge_rows";
    public static final String OPEN_FILE = "open_file";
    public static final String OR = "or";
    public static final String ORDER_COLUMN = "order_column";
    public static final String RENAME_COLUMN = "rename_column";
    public static final String ROW_BASIC_MATH = "row_basic_math";
    public static final String ROW_STAT_MATH = "row_stat_math";
    public static final String SET_COMPLEMENT = "set_complement";
    public static final String TAKE_COLUMNS = "take_columns";
    public static final String UNIQUE_COLUMN = "unique_column";
    public static final String WRITE_FILE = "write_file";

    private Operations() {}
}