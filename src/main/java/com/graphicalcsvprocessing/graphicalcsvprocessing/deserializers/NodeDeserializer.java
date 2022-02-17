package com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Attributes;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Operations;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.OpenFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.WriteFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.ConcatTablesProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.OrProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.SetComplimentProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations.*;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.JoinProcessingNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Operations.*;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Attributes.*;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor.JoinType;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.FilterProcessor.FilterType;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.OrderColumnProcessor.OrderType;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.MathProcessor.StatisticalType;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.MergeColumnsProcessor.MergeType;

public class NodeDeserializer extends StdDeserializer<Node> {

    private static final Map<String, BiFunction<JsonNode, Map<String, String>, Node>> operationDeserialize = new HashMap<>();

    static {
        operationDeserialize.put(Operations.ALIAS, NodeDeserializer::aliasDeserialize);
        operationDeserialize.put(COLUMN_MATH, NodeDeserializer::columnMathDeserialize);
        operationDeserialize.put(CONCAT_COLUMNS, NodeDeserializer::concatColumnsDeserialize);
        operationDeserialize.put(CONCAT_TABLES, NodeDeserializer::concatTableDeserialize);
        operationDeserialize.put(DROP_ALIAS, NodeDeserializer::dropAliasDeserialize);
        operationDeserialize.put(DROP_COLUMNS, NodeDeserializer::dropColumnDeserialize);
        operationDeserialize.put(FILTER, NodeDeserializer::filterDeserialize);
        operationDeserialize.put(JOIN, NodeDeserializer::joinDeserialize);
        operationDeserialize.put(Operations.LIMIT, NodeDeserializer::limitDeserialize);
        operationDeserialize.put(MERGE_COLUMNS, NodeDeserializer::mergeColumnsDeserialize);
        operationDeserialize.put(MERGE_ROWS, NodeDeserializer::mergeRowsDeserialize);
        operationDeserialize.put(OPEN_FILE, NodeDeserializer::openFileDeserialize);
        operationDeserialize.put(OR, NodeDeserializer::orDeserialize);
        operationDeserialize.put(ORDER_COLUMN, NodeDeserializer::orderColumnDeserialize);
        operationDeserialize.put(RENAME_COLUMN, NodeDeserializer::renameDeserialize);
        operationDeserialize.put(ROW_MATH, NodeDeserializer::rowMathDeserialize);
        operationDeserialize.put(SET_COMPLIMENT, NodeDeserializer::setComplimentDeserialize);
        operationDeserialize.put(TAKE_COLUMNS, NodeDeserializer::takeColumnDeserialize);
        operationDeserialize.put(UNIQUE_COLUMN, NodeDeserializer::uniqueColumnDeserialize);
        operationDeserialize.put(WRITE_FILE, NodeDeserializer::writeFileDeserialize);
    }

    private final Map<String, String> defaultValues = GraphDataModelDeserializer.getRequestDefaultValues(Thread.currentThread().getId());

    @SuppressWarnings("unused")
    public NodeDeserializer() {
        this(null);
    }

    public NodeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Node deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode jsonContents = jsonParser.getCodec().readTree(jsonParser);

        try {
            String operation = jsonContents.get("operation").asText();

            return operationDeserialize.getOrDefault(
                operation,
                (json, defaults) -> {
                    throw new IllegalArgumentException("No nodes matching operation: " + operation);
                }
            ).apply(jsonContents, defaultValues);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Node missing necessary attribute - refer to README.md", e);
        }
    }

    private static String[] getCoreAttributes(JsonNode jsonContents) {
        return new String[] {
            jsonContents.get(ID).asText(),
            jsonContents.get(GROUP).asText(),
            jsonContents.get(OPERATION).asText()
        };
    }

    private static String[] getSpecificAttributes(JsonNode jsonContents, Map<String, String> defaults, String... attributes) {
        String[] output = new String[attributes.length];

        for (int i = 0; i < attributes.length; i++) {
            try {
                output[i] = jsonContents.get(attributes[i]).asText();
            } catch (NullPointerException e) {
                if (!defaults.containsKey(attributes[i]))
                    throw new IllegalArgumentException("Node missing necessary attribute - refer to README.md", e);

                output[i] = defaults.get(attributes[i]);
            }
        }

        return output;
    }

    private static String[] splitCommaSeparatedList(String s) {
        return s.replaceAll("\\s*,\\s*", ",").split(",");
    }

    private static OpenFileNode openFileDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, NAME);

        return new OpenFileNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0]);
    }

    private static WriteFileNode writeFileDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, NAME);

        return new WriteFileNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0]);
    }

    private static JoinProcessingNode joinDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, ON_LEFT, ON_RIGHT, JOIN_TYPE);
        JoinType joinType = JoinType.valueOf(attributes[2].toUpperCase());

        return new JoinProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0], attributes[1], joinType);
    }

    private static FilterProcessingNode filterDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, COLUMN, CONDITION, FILTER_TYPE, EQUAL);
        FilterType filterType = FilterType.valueOf(attributes[2].toUpperCase());
        boolean equal = Boolean.parseBoolean(attributes[3]);

        return new FilterProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0], attributes[1], filterType, equal);
    }

    private static DropColumnProcessingNode dropColumnDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] columns = splitCommaSeparatedList(getSpecificAttributes(jsonContents, defaults, COLUMNS)[0]);

        return new DropColumnProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], columns);
    }

    private static TakeColumnProcessingNode takeColumnDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] columns = splitCommaSeparatedList(getSpecificAttributes(jsonContents, defaults, COLUMNS)[0]);

        return new TakeColumnProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], columns);
    }

    private static AliasProcessingNode aliasDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, Attributes.ALIAS);

        return new AliasProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0]);
    }

    private static RenameColumnProcessingNode renameDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, COLUMN, NEW_NAME);

        return new RenameColumnProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0], attributes[1]);
    }

    private static MergeColumnsProcessingNode mergeColumnsDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, COLUMN_1, COLUMN_2, MERGE_COL_NAME, MERGE_TYPE);
        MergeType mergeType = MergeType.valueOf(attributes[3].toUpperCase());

        return new MergeColumnsProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0], attributes[1], attributes[2], mergeType);
    }

    private static MergeRowsProcessingNode mergeRowsDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, COLUMN, VALUE);

        return new MergeRowsProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0], attributes[1]);
    }

    private static LimitProcessingNode limitDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        int limit = Integer.parseInt(getSpecificAttributes(jsonContents, defaults, Attributes.LIMIT)[0]);

        return new LimitProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], limit);
    }

    private static ConcatTablesProcessingNode concatTableDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);

        return new ConcatTablesProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2]);
    }

    private static UniqueColumnProcessingNode uniqueColumnDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, COLUMN);

        return new UniqueColumnProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0]);
    }

    private static OrProcessingNode orDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, COLUMN);

        return new OrProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0]);
    }

    private static OrderColumnProcessingNode orderColumnDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, COLUMN, ORDER_TYPE);
        OrderType orderType = OrderType.valueOf(attributes[1].toUpperCase());

        return new OrderColumnProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0], orderType);
    }

    private static DropAliasProcessingNode dropAliasDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);

        return new DropAliasProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2]);
    }

    private static SetComplimentProcessingNode setComplimentDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, KEY_HEADER);

        return new SetComplimentProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0]);
    }

    private static ConcatColumnsProcessingNode concatColumnsDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, COLUMN_1, COLUMN_2, CONCAT_HEADER);

        return new ConcatColumnsProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0], attributes[1], attributes[2]);
    }

    private static RowMathProcessingNode rowMathDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, COLUMNS, NEW_NAME, MATH_OPERATION);
        String[] columns = splitCommaSeparatedList(attributes[0]);
        StatisticalType mathOp = StatisticalType.valueOf(attributes[2].toUpperCase());

        return new RowMathProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], columns, attributes[1], mathOp);
    }

    private static ColumnMathProcessingNode columnMathDeserialize(JsonNode jsonContents, Map<String, String> defaults) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] attributes = getSpecificAttributes(jsonContents, defaults, COLUMN, MATH_OPERATION);
        StatisticalType mathOp = StatisticalType.valueOf(attributes[1].toUpperCase());

        return new ColumnMathProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], attributes[0], mathOp);
    }
}
