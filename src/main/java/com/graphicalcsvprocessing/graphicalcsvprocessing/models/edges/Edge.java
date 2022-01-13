package com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers.EdgeDeserializer;

@JsonDeserialize(using = EdgeDeserializer.class)
public class Edge {
    private final String from;
    private final String to;

    public Edge(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
