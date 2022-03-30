Graphical CSV Processing API
============================
Processes and queries supplied CSV files using a comprehensive graph model.  
This repo has been implemented with conscious effort to enable further extension easily through forking,should a need arise for an additional node or changing to an existing node.  
This API is capable of returning multiple csvs from the same or separate data streams and as it works on a copy of the contents at each stage will never impact the input data.

---

Docker Quick Start
------------------

### Registry
[Docker Hub](https://hub.docker.com/r/qub40232046/40232046-graphical-processing-be): qub40232046/40232046-graphical-processing-be  

### Starting
```shell
docker run -d -p 8080:8080 -e APP_FRONTEND="{space-separated-list-cors-urls-allowed}" qub40232046/40232046-graphical-processing-be:{tag}
```
Note the APP_FRONTEND variable defaults to http://localhost:3000 so to enable quick running of the paired [frontend](https://hub.docker.com/r/qub40232046/40232046-graphical-processing-fe) application locally.  

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
*   Nodes supplied must have the required attributes specified.
*   Edges must contain both 'from' and 'to' attributes referring to node ids.
*   No cycles may be in the supplied graph, G = (N, E).

### _Valid Columns Names_

*   A column name may be any alphanumeric string with spaces, underscores and hyphens possible.
*   An alias is optional
*   If an alias is present it must start alphanumeric then may also include spaces, underscores and hyphens.
*   A single '.' character will separate the alias from the column name.

### _Identifying Columns_

*   If a column name alone is unique across the list of csvs supplied to a node, this is sufficient to deduce the column referenced.
*   Otherwise, the alias is required to identify the column being referenced.
*   In the event a missing or an ambiguous column name is supplied to the API, it will return exception indicating the issue.

### _Clashing Data_
On a merge of any type, checks are taken to ensure cells don't lose genuine data.
Genuine data is considered cells with non-empty string content.

*   Matching genuine data in cells => merge
*   Genuine data in one cell and empty or null in other => merge
*   Non-matching genuine data in cells => exception

### _How the data passes from node to node_
Each node will output a single csv result. This is then able to be used as input into any number of  non-dependent nodes.  
That is to emphasise **you cannot have loops in the supplied graph**

---

Node Types
----------

*   [Alias Node](#user-content-alias-node)
*   [Columns Math Node](#user-content-column-math-node)
*   [Concat Columns Node](#user-content-concat-columns-node)
*   [Concat Tables Node](#user-content-concat-tables-node)
*   [Drop Alias Node](#user-content-drop-alias-node)
*   [Drop Columns Node](#user-content-drop-columns-node)
*   [Filter Node](#user-content-filter-node)
*   [Join Node](#user-content-join-node)
*   [Limit Node](#user-content-limit-node)
*   [Merge Columns Node](#user-content-merge-columns-node)
*   [Merge Rows Node](#user-content-merge-rows-node)
*   [Open File Node](#user-content-open-file-node)
*   [Or Node](#user-content-or-node)
*   [Order Column Node](#user-content-order-column-node)
*   [Rename Column Node](#user-content-rename-column-node)
*   [Row Basic Math Node](#user-content-row-basic-math-node)
*   [Row Statistical Math Node](#user-content-row-statistical-math-node)
*   [Set Complement Node](#user-content-set-complement-node)
*   [Take Columns Node](#user-content-take-columns-node)
*   [Unique Columns Node](#user-content-unique-column-node)
*   [Write File Node](#user-content-write-file-node)

### _Alias Node_

**Description:** Prefixes supplied alias onto the columns of the supplied csv.  
**Inputs:** 1 csv file (will remove existing alias on any of the columns if present).  
**Outputs:** Copy of csv with the new identifier prefixed to column headers.  
**Attributes:** 1

*   String: Alias

**Exceptions if:**

*   Resultant csv does not have unique column headers
*   Alias supplied is not [valid](#user-content-valid-column-names)

### _Column Math Node_

**Description:** Calculate the count, min, max, average or sum of a specified column.  
**Inputs:** 1 csv file (will remove existing alias on any of the columns if present).  
**Outputs:** Csv with just the calculated value.  
**Attributes:** 2

*   String: column
*   [StatisticalType](src/main/java/com/graphicalcsvprocessing/graphicalcsvprocessing/processors/StatisticalMathProcessor.java): math operation
    *   Average
    *   Count
    *   Max
    *   Min
    *   Sum

**Exceptions if:**

*   Column cannot be [identified](#user-content-identifying-columns)
*   Any non-empty cell in the column cannot be parsed to double

### _Concat Columns Node_

**Description:** Creates a suffixed column on csv in the order of first column value + second column value.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with the new column suffixed csv.  
**Attributes:** 3

*   String: column1
*   String: column2
*   String: concat header

**Exceptions if:**

*   Column1 or column2 cannot be [identified](#user-content-identifying-columns)
*   Concat header is not [valid](#user-content-valid-columns-names)

### _Concat Tables Node_

**Description:** Takes 2 input csvs and produces single csv with number of rows matching sum of two inputs rows. Headers are merged to avoid duplicate headers.  
**Inputs:** 2 csv files.  
**Outputs:** A table of the two inputs.  
**Attributes:** 0

### _Drop Alias Node_

**Description:** Removes any identifier alias on the columns of the input csv.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with de-aliased column headers.  
**Attributes:** 0  
**Exceptions if:**

*   Resultant csv does not have [unique](#user-content-identifying-columns) column headers

### _Drop Columns Node_

**Description:** Removes the blacklist of columns from the resultant csv.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with only rows unspecified.  
**Attributes:** 1

*   Comma-separated-list: columns

**Exceptions if:**

*   Any column in list cannot be [identified](#user-content-identifying-columns)

### _Filter Node_

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

*   Specified column cannot be [identified](#user-content-identifying-columns)
*   Condition requires double and cannot be parsed to double

### _Join Node_

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

*   Either column specified cannot be [identified](#user-content-identifying-columns)

### _Limit Node_

**Description:** Limit number of rows in the supplied csv.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of input csv with specified number of rows.  
**Attributes:** 1

*   Integer: limit

**Exceptions if:**

*   Limit supplied is negative

### _Merge Columns Node_

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

*   Either column specified cannot be [identified](#user-content-identifying-columns)
*   New column name is [invalid](#user-content-valid-columns-names)
*   Data in two columns of any row [clash](#user-content-clashing-data)

### _Merge Rows Node_

**Description:** Merge rows which hold value in the column specified into one.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of input csv with the rows merged.  
**Attributes:** 2

*   String: column
*   String: value

**Exceptions if:**

*   Column specified cannot be [identified](#user-content-identifying-columns)
*   Data in rows [clash](#user-content-clashing-data)

### _Open File Node_

**Description:** Node to represent supplied file by setting name to filename.  
**Inputs:** 0 csv file.  
**Outputs:** Copy of input csv supplied in request.  
**Attributes:** 1

*   String: name

### _Or Node_

**Description:** Logical or node. To be applied on two filter nodes enabling logical or.  
**Inputs:** 2 csv files.  
**Outputs:** Concatenation of the two input csvs and unique column applied to avoid duplicate entries matching both filter expressions.  
**Attributes:** 1

*   String: column

**Exceptions if:**

*   Column specified cannot be [identified](#user-content-identifying-columns) post concat
*   Data in rows [clash](#user-content-clashing-data) on unique

### _Order Column Node_

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

*   Column specified cannot be [identified](#user-content-identifying-columns)

### _Rename Column Node_

**Description:** Renames the specified column in the supplied csv.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of input csv with the specified columns merged.  
**Attributes:** 2

*   String: column
*   String: new name

**Exceptions if:**

*   Column specified cannot be [identified](#user-content-identifying-columns)
*   New column name is [invalid](#user-content-valid-columns-names)

### _Row Basic Math Node_

**Description:** Add, subtract, multiply, divide or modulo of either two columns or one column and a literal.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with an additional column suffixed which takes the value in column1 and applies the column or literal in value.  
**Attributes:** 5

*   String: column1
*   String: value
*   String: new name
*   [MathOperation](src/main/java/com/graphicalcsvprocessing/graphicalcsvprocessing/processors/BasicMathProcessor.java): math operation
    *   Add
    *   Divide
    *   Modulo
    *   Multiply
    *   Subtract
*   boolean: literal

**Exceptions if:**

*   Any column1 or, if literal is false, value cannot be [identified](#user-content-identifying-columns)
*   New name header is not [valid](#user-content-valid-columns-names)
*   Any non-empty cell in the columns specified cannot be parsed to double

### _Row Statistical Math Node_

**Description:** Calculate the count, min, max, average or sum of a specified columns of a row.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with an additional column suffixed.  
**Attributes:** 3

*   Comma-separated-list: columns
*   String: new name
*   [StatisticalType](src/main/java/com/graphicalcsvprocessing/graphicalcsvprocessing/processors/StatisticalMathProcessor.java): math operation
    *   Average
    *   Count
    *   Max
    *   Min
    *   Sum

**Exceptions if:**

*   Any column in columns cannot be [identified](#user-content-identifying-columns)
*   Any non-empty cell in the columns specified cannot be parsed to double

### _Set Complement Node_

**Description:** Returns the complementary set of subset compared to super set using key header as indicator for which fields are present. Note bigger csv supplied assumed as super set. Worthy of note is that the assumption is the column specified is unique.  
**Inputs:** 2 csv files.  
**Outputs:** Complementary set of the subset compared to the super set. If either set passed in is empty it will output the csv passed as the outer set.  
**Attributes:** 1

*   String: key header

**Exceptions if:**

*   Column specified cannot be [identified](#user-content-identifying-columns)
*   Headers in two supplied files are not identical

### _Take Columns Node_

**Description:** Takes the whitelist of columns from the resultant csv.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with only rows specified.  
**Attributes:** 1

*   Comma-separated-list: columns

**Exceptions if:**

*   Any column in list cannot be [identified](#user-content-identifying-columns)

### _Unique Column Node_

**Description:** Merges the rows with matching value in specified column into one row.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of csv with only unique rows on the column specified.  
**Attributes:** 1

*   String: column

**Exceptions if:**

*   Column supplied cannot be [identified](#user-content-identifying-columns)
*   Any of the row merges contain [clashing](#user-content-clashing-data) data

### _Write File Node_

**Description:** Renames input file and ensures it is in the returned files in response. Useful in debugging queries too.  
**Inputs:** 1 csv file.  
**Outputs:** Copy of input csv.  
**Attributes:** 1

*   String: name
