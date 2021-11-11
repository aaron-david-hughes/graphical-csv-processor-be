package com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges.Edge;

import java.io.IOException;

public class EdgeDeserializer extends StdDeserializer<Edge> {

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
            throw new IllegalArgumentException("Edge must not contain a null 'from' or 'to' label");
        }

        String priority;
        try {
            priority = jsonContents.get("priority").asText();
        } catch (NullPointerException ignore) {
            priority = null;
        }

        return new Edge(from, to, priority);
    }
}
