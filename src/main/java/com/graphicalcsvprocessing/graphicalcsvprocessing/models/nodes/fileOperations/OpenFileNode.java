package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations;

/**
 * model to represent the opening of a file into the query
 */
public class OpenFileNode extends FileOperationNode {

    public static final int NUMBER_INBOUND_EDGES = 0;

    public OpenFileNode(String id, String group, String operation, String name) {
        super(id, group, operation, name);
    }

    @Override
    public int getAllowedNumberEdges() {
        return NUMBER_INBOUND_EDGES;
    }
}
