package com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers.EdgeDeserializer;
import org.springframework.lang.Nullable;

@JsonDeserialize(using = EdgeDeserializer.class)
public class Edge {
    String from;
    String to;
    String priority;

    public Edge(String from, String to, @Nullable String priority) {
        this.from = from;
        this.to = to;
        this.priority = priority;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getPriority() {
        return priority;
    }
}
