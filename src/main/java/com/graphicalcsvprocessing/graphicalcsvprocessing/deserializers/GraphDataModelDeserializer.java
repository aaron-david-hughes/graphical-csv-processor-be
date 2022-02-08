package com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.GraphDataModel;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges.Edge;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GraphDataModelDeserializer extends StdDeserializer<GraphDataModel> {

    @SuppressWarnings("unused")
    public GraphDataModelDeserializer() {
        this(null);
    }

    public GraphDataModelDeserializer(Class<?> vc) {
        super(vc);
    }

    private static final Map<Long, Map<String, String>> requestDefaultValueBank = new HashMap<>();

    @Override
    public GraphDataModel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode jsonNode = jsonParser.readValueAsTree();

        try {
            String defaultValuesString = jsonNode.get("defaultValues").toString();
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {};
            Map<String, String> defaultValues = new ObjectMapper().readValue(defaultValuesString, typeRef);
            requestDefaultValueBank.put(Thread.currentThread().getId(), defaultValues);
        } catch (NullPointerException ignore) {/*ignore as not required and if necessary and not used will problem elsewhere*/}

        JsonParser nodeParser = jsonNode.get("nodes").traverse();
        nodeParser.setCodec(jsonParser.getCodec());
        Node[] nodes = nodeParser.readValueAs(Node[].class);

        JsonParser edgeParser = jsonNode.get("edges").traverse();
        edgeParser.setCodec(jsonParser.getCodec());
        Edge[] edges = edgeParser.readValueAs(Edge[].class);

        requestDefaultValueBank.remove(Thread.currentThread().getId());

        return new GraphDataModel(nodes, edges);
    }

    public static Map<String, String> getRequestDefaultValues(long reqId) {
        return new HashMap<>(requestDefaultValueBank.get(reqId));
    }
}
