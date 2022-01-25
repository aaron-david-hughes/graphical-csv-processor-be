package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes;

public class Operations {
    public static final String ALIAS = "alias";
//    public static final String CONCAT_STRING = "concat_string";
    public static final String CONCAT_TABLES = "concat_tables";
    public static final String DROP_COLUMNS = "drop_columns";
    public static final String FILTER = "filter";
    public static final String JOIN = "join";
    public static final String LIMIT = "limit";
    public static final String MERGE_COLUMNS = "merge_columns";                     //should probably rename to merge columns
    public static final String MERGE_ROWS = "merge_rows";                     //should probably rename to merge columns
    public static final String OPEN_FILE = "open_file";
    public static final String RENAME_COLUMN = "rename_column";
    public static final String TAKE_COLUMNS = "take_columns";
    public static final String WRITE_FILE = "write_file";

    //order => order on a column => will likely have some logic to understand if the column is numerical or alphabetical => will utilise fastest possible sort
    //unique column => runs a merge rows for every column in table until each value is unique to data set
    //drop alias => not allowing duplicate headers
    //concat strings => will produce a third column on the output, as per user name

    //for mathematical functions it will likely require a user specified name for the column which results => following rule of not making names up for the user
    //row avg => mathematical 2 d.p.
    //      row include or ignore list (boolean to specify => default to excl so all incl)
    //      will read row entries in the columns desired put the average into a renamed column
    //          i think suffix a column on end named as per user indication for each row result
    //count


    //collective csv functions (will take all rows and produce one => and potentially return a single col and single entry)
    //avg => mathematical
    //max => mathematical
    //min => mathematical
    //count => number of records

    private Operations() {}
}



/*
ultimately
concatenation will need to cover the adding two instances of the same table with diff data => avoiding duplicate entries
this is to say regardless
 */