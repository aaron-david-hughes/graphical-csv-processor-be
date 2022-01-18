package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes;

public class Operations {
    public static final String ALIAS = "alias";
    public static final String DROP_COLUMN = "drop_column";
    public static final String FILTER = "filter";
    public static final String OPEN_FILE = "open_file";
    public static final String WRITE_FILE = "write_file";
    public static final String JOIN = "join";

    //limit => cuts row number
    //order => order on a column => will likely have some logic to understand if the column is numerical or alphabetical
    //drop identifier from table
    //rename column
    //set identifier alias
    //merge column => new column name will be supplied by user
    //concat


    //collective csv functions (will take all rows and produce one => and potentially return a single col and single entry)
    //avg => mathematical
    //max => mathematical
    //min => mathematical

    private Operations() {}
}
