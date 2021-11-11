package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor;

import java.io.IOException;
import java.util.Map;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.models.Tree.TreeNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor.JoinType;

public class JoinProcessingNode extends BinaryOperationNode {
    protected String leftCol;
    protected String rightCol;
    protected JoinType joinType;

    public JoinProcessingNode(String id, String group, String operation, String leftCol, String rightCol, JoinType joinType) {
        super(id, group, operation);
        this.leftCol = leftCol;
        this.rightCol = rightCol;
        this.joinType = joinType;
    }

    @Override
    public CSV process(TreeNode<Node> processTreeNode, Map<String, CSV> csvParsers) throws IOException {
        CSV[] inputs = {
                csvParsers.get(processTreeNode.getLeft().getElement().getId()),
                csvParsers.get(processTreeNode.getRight().getElement().getId())
        };
        return JoinProcessor.join(joinType, this, inputs);
    }

    public String getLeftCol() {
        return leftCol;
    }

    public String getRightCol() {
        return rightCol;
    }
}
