package com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.OpenFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.WriteFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.ConcatTablesProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations.*;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.JoinProcessingNode;

import java.io.IOException;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Operations.*;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor.JoinType;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.FilterProcessor.FilterType;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.MergeColumnsProcessor.MergeType;

public class NodeDeserializer extends StdDeserializer<Node> {

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

            //this would have to now take from startup variables and act as per the configs
            //what if FE json works out what type the node will be and maintains json format to be
            //deserialized specifically in explicit service
            switch (operation) {
                case OPEN_FILE:
                    return openFileDeserialize(jsonContents);
                case JOIN:
                    return joinDeserialize(jsonContents);
                case FILTER:
                    return filterDeserialize(jsonContents);
                case DROP_COLUMNS:
                    return dropColumnDeserialize(jsonContents);
                case TAKE_COLUMNS:
                    return takeColumnDeserialize(jsonContents);
                case ALIAS:
                    return aliasDeserialize(jsonContents);
                case RENAME_COLUMN:
                    return renameDeserialize(jsonContents);
                case MERGE_COLUMNS:
                    return mergeColumnsDeserialize(jsonContents);
                case MERGE_ROWS:
                    return mergeRowsDeserialize(jsonContents);
                case LIMIT:
                    return limitDeserialize(jsonContents);
                case CONCAT_TABLES:
                    return concatTableDeserialize(jsonContents);
                case WRITE_FILE:
                    return writeFileDeserialize(jsonContents);
                default:
                    throw new IllegalArgumentException("No nodes matching operation: " + operation);
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Node missing necessary attribute - refer to readme", e);
        }
    }

    private String[] getCoreAttributes(JsonNode jsonContents) {
        return new String[] {
            jsonContents.get("id").asText(),
            jsonContents.get("group").asText(),
            jsonContents.get("operation").asText()
        };
    }

    private OpenFileNode openFileDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String name = jsonContents.get("name").asText();

        return new OpenFileNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], name);
    }

    private WriteFileNode writeFileDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String name = jsonContents.get("name").asText();

        return new WriteFileNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], name);
    }

    private JoinProcessingNode joinDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String leftCol = jsonContents.get("onLeft").asText();
        String rightCol = jsonContents.get("onRight").asText();
        JoinType joinType = JoinType.valueOf(jsonContents.get("joinType").asText().toUpperCase());

        return new JoinProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], leftCol, rightCol, joinType);
    }

    private FilterProcessingNode filterDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String column = jsonContents.get("column").asText();
        String condition = jsonContents.get("condition").asText();
        FilterType filterType = FilterType.valueOf(jsonContents.get("filterType").asText().toUpperCase());
        boolean equal = jsonContents.get("equal").asBoolean(true);

        return new FilterProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], column, condition, filterType, equal);
    }

    private DropColumnProcessingNode dropColumnDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] columns = jsonContents.get("columns").asText().replaceAll("\\s*,\\s*", ",").split(",");

        return new DropColumnProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], columns);
    }

    private TakeColumnProcessingNode takeColumnDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String[] columns = jsonContents.get("columns").asText().replaceAll("\\s*,\\s*", ",").split(",");

        return new TakeColumnProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], columns);
    }

    private AliasProcessingNode aliasDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String alias = jsonContents.get("alias").asText();

        return new AliasProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], alias);
    }

    private RenameColumnProcessingNode renameDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String column = jsonContents.get("column").asText();
        String newName = jsonContents.get("newName").asText();

        return new RenameColumnProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], column, newName);
    }

    private MergeColumnsProcessingNode mergeColumnsDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String column1 = jsonContents.get("column1").asText();
        String column2 = jsonContents.get("column2").asText();
        String mergeColName = jsonContents.get("mergeColName").asText();
        MergeType mergeType = MergeType.valueOf(jsonContents.get("mergeType").asText().toUpperCase());

        return new MergeColumnsProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], column1, column2, mergeColName, mergeType);
    }

    private MergeRowsProcessingNode mergeRowsDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String column = jsonContents.get("column").asText();
        String value = jsonContents.get("value").asText();

        return new MergeRowsProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], column, value);
    }

    private LimitProcessingNode limitDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        int limit = Integer.parseInt(jsonContents.get("limit").asText());

        return new LimitProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], limit);
    }

    private ConcatTablesProcessingNode concatTableDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);

        return new ConcatTablesProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2]);
    }
}
