package com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.GraphDataModel;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges.Edge;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;

import java.io.IOException;

public class GraphDataModelDeserializer extends StdDeserializer<GraphDataModel> {

    public GraphDataModelDeserializer() {
        this(null);
    }

    public GraphDataModelDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public GraphDataModel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode jsonNode = jsonParser.readValueAsTree();

        JsonParser nodeParser = jsonNode.get("nodes").traverse();
        nodeParser.setCodec(jsonParser.getCodec());
        Node[] nodes = nodeParser.readValueAs(Node[].class);

        JsonParser edgeParser = jsonNode.get("edges").traverse();
        edgeParser.setCodec(jsonParser.getCodec());
        Edge[] edges = edgeParser.readValueAs(Edge[].class);

        return new GraphDataModel(nodes, edges);
    }
}
