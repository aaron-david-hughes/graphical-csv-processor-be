package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.Tree;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;

import java.util.Map;

public class FilterProcessingNode extends UnaryOperationNode {
    protected String condition;

    public FilterProcessingNode(String id, String group, String operation, String condition) {
        super(id, group, operation);
        this.condition = condition;
    }

    @Override
    public CSV process(Tree.TreeNode<Node> node, Map<String, CSV> csvParsers) {
        return null;
    }
}
