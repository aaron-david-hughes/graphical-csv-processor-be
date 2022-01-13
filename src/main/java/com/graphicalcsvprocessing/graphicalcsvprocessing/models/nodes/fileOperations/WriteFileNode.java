package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations;

public class WriteFileNode extends FileOperationNode {

    public WriteFileNode(String id, String group, String operation, String name) {
        super(id, group, operation, name);
    }

    @Override
    public int getAllowedNumberEdges() {
        return 1;
    }
}
