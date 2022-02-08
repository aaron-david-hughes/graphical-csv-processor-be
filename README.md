Graphical CSV Processing API
============================
Processes and queries supplied CSV files using a comprehensive graph model.  
This repo has been implemented with conscious effort to enable further extension easily through forking,should a need arise for an additional node or changing to an existing node.  
This API is capable of returning multiple csvs from the same or separate data streams and as it works on a copy of the contents at each stage will never impact the input data.

---

Integration Guide
-----------------

### _Example Request_
```console
curl --location --request POST 'http://localhost:8080/process' \
--form 'csvFiles=@"/redacted/Scores.csv"' \
--form 'graph="{
	\"defaultValues\": {},
	\"nodes\": [
		{
			\"id\": \"0\",
      			\"group\": \"file\", 
     			\"operation\":\"open_file\",
      			\"name\": \"Scores.csv\"
		},
		{
			\"id\": \"1\",
      			\"group\": \"processing\", 
      			\"operation\": \"filter\",
      			\"filterType\": \"less_than\",
      			\"column\": \"Score\",
     			\"condition\": \"40\",
			\"equal\": \"true\"
		},
		{
			\"id\": \"2\",
			\"group\": \"file\", 
     			\"operation\": \"write_file\",
      			\"name\": \"Output.csv\"
		}
	],
	\"edges\": [
		{
			\"from\": \"0\",
			\"to\": \"1\"
		},
		{
			\"from\": \"1\",
			\"to\": \"2\"
		}
	]
}";type=application/json'
```
**Files:** 1 or more to be supplied - matching number of open file nodes.  
**Graph:** contains the following:

*   Default Values: any node attribute default
*   Nodes: 1 or more nodes (must contain an open file node).
*   Edges: 0 or more edges representing flow of data.

### _General Integration Rules_

*   Must ensure minimum 1 file is returned.
*   Must ensure minimum 1 file is supplied.
*   Must ensure each file supplied has a corresponding Open File Node.
*   Files must be .csv.
*   Nodes supplied must be listed or be added.
*   Nodes supplied must have the required attributes specified, or have a default supplied.
*   Edges must contain both 'from' and 'to' attributes referring to node ids.

### _Valid Columns Names_ <a id="valid_columns"></a>

*   A column name may be any alphanumeric string with spaces, underscores and hyphens possible.
*   An alias is optional
*   If an alias is present it must start alphanumeric then may also include spaces, underscores and hyphens.
*   A single '.' character will separate the alias from the column name.

### _Identifying Columns_ <a id="identifying_columns"></a>

*   If a column name alone is unique across the list of csvs supplied to a node, this is sufficient to deduce the column referenced.
*   Otherwise, the alias is required to identify the column being referenced.
*   In the event a missing or an ambiguous column name is supplied to the API, it will return exception indicating the issue.

### _Clashing Data_ <a id="clashing_data"></a>
On a merge of any type, checks are taken to ensure cells don't lose genuine data.
Genuine data is considered cells with non-empty string content.

*   Matching genuine data in cells=> merge
*   Genuine data in one cell and empty or null in other => merge
*   Non-matching genuine data in cells => exception

### _Supplying default values_
As seen above, Default Values can be supplied. This will be done through key value pairs as json object.
```json
{
  "column": "StudentNum"
}
```
The above will enable defaulting column attributes of nodes to "StudentNum" if missing in node object.

### _How the data passes from node to node_
Each node will output a single csv result. This is then able to be used as input into any number of  non-dependent nodes.  
That is to emphasise **you cannot have loops in the supplied graph**

---

Node Types
----------

*   [Alias Node](#alias)
*   [Concat Columns Node](#concat_columns)
*   [Concat Tables Node](#concat_tables)
*   [Drop Alias Node](#drop_alias)
*   [Drop Columns Node](#drop_columns)
*   [Filter Node](#filter)
*   [Join Node](#join)
*   [Limit Node](#limit)
*   [Merge Columns Node](#merge_columns)
*   [Merge Rows Node](#merge_rows)
*   [Open File Node](#open_file)
*   [Or Node](#or)
*   [Order Column Node](#order_column)
*   [Rename Column Node](#rename_column)
*   [Set Compliment Node](#set_compliment)
*   [Take Columns Node](#take_columns)
*   [Unique Columns Node](#unique_columns)
*   [Write File Node](#write_file)

### _Alias Node_ <a id="alias"></a>

**Description:** Prefixes supplied alias onto the columns of the supplied csv.  
**Inputs:** 1 csv file (will remove existing alias on any of the columns if present).  
**Outputs:** Copy of csv with the new identifier prefixed to column headers.  
**Attributes:** 1

*   String: Alias

**Exceptions if:**

*   Not 1 and only 1 inbound csv
*   Resultant csv does not have unique column headers

### _Concat Columns Node_ <a id="concat_columns"></a>

**Description:** Creates a suffixed column on csv in the order of first column value + second column value.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with the new column suffixed csv.  
**Attributes:** 3

*   String: column1
*   String: column2
*   String: concat header

**Exceptions if:**

*   Column1 or column2 cannot be [identified](#identifying_columns)
*   Concat header is not [valid](#valid_columns)

### _Concat Tables Node_ <a id="concat_tables"></a>

**Description:** Takes 2 input csvs and produces single csv with number of rows matching sum of two inputs rows. Headers are merged to avoid duplicate headers.  
**Inputs:** 2 csv files.  
**Outputs:** A table of the two inputs.  
**Attributes:** 0

### _Drop Alias Node_ <a id="drop_alias"></a>

**Description:** Removes any identifier alias on the columns of the input csv.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with de-aliased column headers.  
**Attributes:** 0  
**Exceptions if:**

*   Resultant csv does not have [unique](#identifying_columns) column headers

### _Drop Columns Node_ <a id="drop_columns"></a>

**Description:** Removes the blacklist of columns from the resultant csv.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with only rows unspecified.  
**Attributes:** 1

*   Comma-separated-list: columns

**Exceptions if:**

*   Any column in list cannot be [identified](#identifying_columns)

### _Filter Node_ <a id="filter"></a>

**Description:** Filter for equality or an inequality.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with only rows matching or not matching filter depending on equal boolean.  
**Attributes:** 4

*   String: column
*   String/Double: condition
*   [FilterType](src/main/java/com/graphicalcsvprocessing/graphicalcsvprocessing/processors/FilterProcessor.java): filter type
    *   String Equality (String condition)
    *   Numeric Equality (Double condition)
    *   Greater Than (Double condition)
    *   Greater Than or Equal (Double condition)
    *   Less Than (Double condition)
    *   Less Than or Equal (Double condition)
*   Boolean: equal

**Exceptions if:**

*   Any column in list cannot be [identified](#identifying_columns)
*   Condition requires double and cannot be parsed to double

### _Join Node_ <a id="join"></a>

**Description:** Join two csv files on commonality of two columns.  
**Inputs:** 2 csv files.  
**Outputs:** Joined csv of the two input csvs as per the join type chosen 
(duplicate headers are possible and user must mitigate this if problem).  
**Attributes:** 3

*   String: left column
*   String: right column
*   [JoinType](src/main/java/com/graphicalcsvprocessing/graphicalcsvprocessing/processors/JoinProcessor.java): join type
    *   Left Join
    *   Right Join
    *   Inner Join
    *   Outer Join

**Exceptions if:**

*   Either column specified cannot be [identified](#identifying_columns)

### _Limit Node_ <a id="limit"></a>

**Description:** Limit number of rows in the supplied csv.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of input csv with specified number of rows.  
**Attributes:** 1

*   Integer: limit

**Exceptions if:**

*   Limit supplied is negative

### _Merge Columns Node_ <a id="merge_columns"></a>

**Description:** Merge two columns into one in the position of the first column.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of input csv with the specified columns merged.  
**Attributes:** 4

*   String: column1
*   String: column2
*   [MergeType](src/main/java/com/graphicalcsvprocessing/graphicalcsvprocessing/processors/MergeColumnsProcessor.java): merge type
    *   String Equality
    *   Numeric Equality
*   String: merged column name

**Exceptions if:**

*   Either column specified cannot be [identified](#identifying_columns)
*   New column name is [invalid](#valid_columns)
*   Data in two columns of any row [clash](#clashing_data)

### _Merge Rows Node_ <a id="merge_rows"></a>

**Description:** Merge rows which hold value in the column specified into one.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of input csv with the rows merged.  
**Attributes:** 2

*   String: column
*   String: value

**Exceptions if:**

*   Column specified cannot be [identified](#identifying_columns)
*   Data in rows [clash](#clashing_data)

### _Open File Node_ <a id="open_file"></a>

**Description:** Node to represent supplied file by setting name to filename.  
**Inputs:** 0 csv file.  
**Outputs:** Copy of input csv supplied in request.  
**Attributes:** 1

*   String: name

### _Or Node_ <a id="or"></a>

**Description:** Logical or node. To be applied on two filter nodes enabling logical or.  
**Inputs:** 2 csv files.  
**Outputs:** Concatenation of the two input csvs and unique column applied to avoid duplicate entries matching both filter expressions.  
**Attributes:** 1

*   String: column

**Exceptions if:**

*   Column specified cannot be [identified](#identifying_columns) post concat
*   Data in rows [clash](#clashing_data) on unique

### _Order Column Node_ <a id="order_column"></a>

**Description:** Orders specified column as per order type supplied.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of input csv with rows ordered as specified.  
**Attributes:** 2

*   String: column
*   [OrderType](src/main/java/com/graphicalcsvprocessing/graphicalcsvprocessing/processors/OrderColumnProcessor.java): order type
    *   Alphabetic Order Asc
    *   Alphabetic Order Desc
    *   Numeric Order Asc
    *   Numeric Order Desc

**Exceptions if:**

*   Column specified cannot be [identified](#identifying_columns)

### _Rename Column Node_ <a id="rename_column"></a>

**Description:** Renames the specified column in the supplied csv.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of input csv with the specified columns merged.  
**Attributes:** 2

*   String: column
*   String: new name

**Exceptions if:**

*   Column specified cannot be [identified](#identifying_columns)
*   New column name is [invalid](#valid_columns)

### _Set Compliment Node_ <a id="set_compliment"></a>

**Description:** Returns the complimentary set of subset compared to super set using key header as indicator for which fields are present. Note bigger csv supplied assumed as super set.  
**Inputs:** 2 csv files.  
**Outputs:** Complimentary set of the subset compared to the super set.  
**Attributes:** 1

*   String: key header

**Exceptions if:**

*   Column specified cannot be [identified](#identifying_columns)
*   Headers in two supplied files are not identical

### _Take Columns Node_ <a id="take_columns"></a>

**Description:** Takes the whitelist of columns from the resultant csv.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with only rows specified.  
**Attributes:** 1

*   Comma-separated-list: columns

**Exceptions if:**

*   Any column in list cannot be [identified](#identifying_columns)

### _Unique Column Node_ <a id="unique_column"></a>

**Description:** Merges the rows with matching value in specified column into one row.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with only unique rows on the column specified.  
**Attributes:** 1

*   String: column

**Exceptions if:**

*   Column supplied cannot be [identified](#identifying_columns)
*   Any of the row merges contain [clashing](#clashing_data) data

### _Write File Node_ <a href="#write_file" id="write_file"></a>

**Description:** Renames input file and ensures it is in the returned files in response. Useful in debugging queries too.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of input csv.  
**Attributes:** 1

*   String: name
