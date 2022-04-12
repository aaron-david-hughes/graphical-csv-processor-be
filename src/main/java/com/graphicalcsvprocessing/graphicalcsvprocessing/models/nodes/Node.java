package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers.NodeDeserializer;

/**
 * base node model
 */
@JsonDeserialize(using = NodeDeserializer.class)
public abstract class Node {
    protected String id;
    protected String group;
    protected String operation;

    protected Node(String id, String group, String operation) {
        this.id = id;
        this.group = group;
        this.operation = operation;
    }

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public String getOperation() {
        return operation;
    }

    public abstract int getAllowedNumberEdges();
}
