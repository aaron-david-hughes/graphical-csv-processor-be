package com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.OpenFileNode;
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

            switch (operation) {
                case OPEN_FILE:
                    return openFileDeserialize(jsonContents);
                case JOIN:
                    return joinDeserialize(jsonContents);
                case FILTER:
                    return filterDeserialize(jsonContents);
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

    private JoinProcessingNode joinDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String leftCol = jsonContents.get("onLeft").asText();
        String rightCol = jsonContents.get("onRight").asText();
        JoinType joinType = JoinType.valueOf(jsonContents.get("joinType").asText().toUpperCase());

        return new JoinProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], leftCol, rightCol, joinType);
    }

    private FilterProcessingNode filterDeserialize(JsonNode jsonContents) {
        String[] coreAttributes = getCoreAttributes(jsonContents);
        String condition = jsonContents.get("condition").asText();

        return new FilterProcessingNode(coreAttributes[0], coreAttributes[1], coreAttributes[2], condition);
    }

    private void test(String... args) {

    }
}
