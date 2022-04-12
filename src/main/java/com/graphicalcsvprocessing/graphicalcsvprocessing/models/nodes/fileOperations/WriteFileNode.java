package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations;

/**
 * model enabling representation of writing a file to be returned after processing
 */
public class WriteFileNode extends FileOperationNode {

    public WriteFileNode(String id, String group, String operation, String name) {
        super(id, group, operation, name);
    }

    @Override
    public int getAllowedNumberEdges() {
        return 1;
    }
}
