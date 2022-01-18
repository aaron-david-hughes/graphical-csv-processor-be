package com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.OpenFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.WriteFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations.AliasProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations.DropColumnProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations.FilterProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.JoinProcessingNode;

import java.io.IOException;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Operations.*;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor.JoinType;


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
                case DROP_COLUMN:
                    return dropColumnDeserialize(jsonContents);
                case ALIAS:
                    return aliasDeserialize(jsonContents);
                case WRITE_FILE:
                    return writeFileDeserialize(jsonContents);
                default:
                    throw new IllegalArgumentException("No nodes matching operation: " + operation);
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Node missing necessary attribute - refer to integration guide", e);
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
        boolean equal = jsonContents.get("equal").asBoolean(true);

        return new FilterProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], column, condition, equal);
    }

    private DropColumnProcessingNode dropColumnDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String column = jsonContents.get("column").asText();

        return new DropColumnProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], column);
    }

    private AliasProcessingNode aliasDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String alias = jsonContents.get("alias").asText();

        return new AliasProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], alias);
    }
}
