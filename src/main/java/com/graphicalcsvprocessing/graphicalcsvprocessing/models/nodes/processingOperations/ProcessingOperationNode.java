package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.Tree;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;

import java.io.IOException;
import java.util.Map;

public abstract class ProcessingOperationNode extends Node {
    protected ProcessingOperationNode(String id, String group, String operation) {
        super(id, group, operation);
    }

    public abstract CSV process(Tree.TreeNode<Node> node, Map<String, CSV> csvParsers) throws IOException;
}
