package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;

/**
 * base file operation node
 */
public abstract class FileOperationNode extends Node {
    protected String name;

    protected FileOperationNode(String id, String group, String operation, String name) {
        super(id, group, operation);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
