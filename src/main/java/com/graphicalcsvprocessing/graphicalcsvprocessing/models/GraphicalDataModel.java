package com.graphicalcsvprocessing.graphicalcsvprocessing.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers.GraphicalDataModelDeserializer;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.ProcessingOperationNode;

import java.io.IOException;
import java.util.Map;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.models.Tree.TreeNode;

@JsonDeserialize(using = GraphicalDataModelDeserializer.class)
public class GraphicalDataModel {

    private final Node[] nodes;

    private final Tree<Node> processTree;

    public GraphicalDataModel(Tree<Node> processTree, Node[] nodes) {
        this.nodes = nodes;
        this.processTree = processTree;
    }

    public CSV process(Map<String, CSV> csvData) throws IOException {
        return process(processTree.getTop(), csvData);
    }

    private CSV process(TreeNode<Node> current, Map<String, CSV> csvData) throws IOException {
        CSV result;

        if (current.getLeft() != null && current.getLeft().getElement() instanceof ProcessingOperationNode) {
            csvData.put(current.getLeft().getElement().getId(), process(current.getLeft(), csvData));
        }

        if (current.getRight() != null && current.getLeft().getElement() instanceof ProcessingOperationNode) {
            csvData.put(current.getRight().getElement().getId(), process(current.getLeft(), csvData));
        }

        if (current.getElement() instanceof ProcessingOperationNode) {
            result = ((ProcessingOperationNode) current.getElement()).process(current, csvData);
        } else {
            //this will probably only be here for node if / when non processing node has inward edge number > 0
            result = csvData.get(current.getElement().getId());
        }

        return result;
    }

    //kept to allow me to iterate nodes quickly if this data structure suits more (validating etc)
    public Node[] getNodes() {
        return nodes;
    }
}
