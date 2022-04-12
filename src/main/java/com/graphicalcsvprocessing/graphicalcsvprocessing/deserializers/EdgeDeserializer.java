package com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges.Edge;

import java.io.IOException;

/**
 * enables the deserialization of the Edge class
 */
public class EdgeDeserializer extends StdDeserializer<Edge> {

    @SuppressWarnings("unused")
    public EdgeDeserializer() {
        this(null);
    }

    public EdgeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Edge deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode jsonContents = jsonParser.getCodec().readTree(jsonParser);

        String from;
        String to;
        try {
            from = jsonContents.get("from").asText();
            to = jsonContents.get("to").asText();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Edge must contain both 'to' and 'from' values");
        }

        return new Edge(from, to);
    }
}
