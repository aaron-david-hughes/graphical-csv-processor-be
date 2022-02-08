package com.graphicalcsvprocessing.graphicalcsvprocessing.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers.GraphDataModelDeserializer;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges.Edge;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.OpenFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.WriteFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.ProcessingOperationNode;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@JsonDeserialize(using = GraphDataModelDeserializer.class)
public class GraphDataModel {
    private final Node[] nodes;

    private final Edge[] edges;

    public GraphDataModel(Node[] nodes, Edge[] edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public Map<String, CSV> process(Map<String, CSV> csvData) throws IOException {
        Map<String, CSV> csvReturns = new HashMap<>();

        if (
            this.getNodes() == null || this.getEdges() == null ||
            this.getNodes().length == 0 || this.getEdges().length == 0 ||
            csvData == null || csvData.isEmpty()
        ) return csvReturns;

        List<Node> mutableNodes = Arrays.stream(this.getNodes()).collect(Collectors.toList());
        int nodesToProcess = mutableNodes.size();

        while (!mutableNodes.isEmpty()) {
            List<Node> removedNodes = new ArrayList<>();
            processIteration(mutableNodes, csvData, csvReturns, removedNodes);

            mutableNodes.removeAll(removedNodes);

            if (nodesToProcess == mutableNodes.size()) {
                throw new IllegalArgumentException("Graph has codependency within - likely due to loop");
            }

            nodesToProcess = mutableNodes.size();
        }

        return csvReturns;
    }

    private void processIteration(List<Node> mutableNodes, Map<String, CSV> csvData, Map<String, CSV> csvReturns, List<Node> remove) throws IOException {
        for (Node node : mutableNodes) {
            List<String> prerequisiteNodes = Arrays.stream(edges)
                    .filter(edge -> edge.getTo().equals(node.getId()))
                    .map(Edge::getFrom)
                    .collect(Collectors.toList());

            List<CSV> availableInputs = prerequisiteNodes.stream()
                    .filter(csvData::containsKey)
                    .map(csvData::get)
                    .collect(Collectors.toList());

            if (node instanceof ProcessingOperationNode && availableInputs.size() == prerequisiteNodes.size()) {
                CSV csv = ((ProcessingOperationNode) node).process(availableInputs);
                //log process completed with node id and perhaps data details like size etc

                if (Arrays.stream(edges).noneMatch(edge -> edge.getFrom().equals(node.getId()))) {
                    csvReturns.put(node.getId(), csv);
                } else {
                    csvData.put(node.getId(), csv);
                }

                remove.add(node);
            } else if (node instanceof WriteFileNode && this.handleWriteFileNode(node, prerequisiteNodes) &&
                    !availableInputs.isEmpty()) {
                csvData.put(node.getId(), availableInputs.get(0));
                csvReturns.put(((WriteFileNode) node).getName(), availableInputs.get(0));
                remove.add(node);
            } else if (node instanceof OpenFileNode && this.handleOpenFileNode(node, csvData)) {
                remove.add(node);
            }
        }
    }

    private boolean handleWriteFileNode(Node node, List<String> prerequisiteNodes) {
        if (node.getAllowedNumberEdges() != prerequisiteNodes.size()) {
            throw new IllegalArgumentException(
                    "Write File Nodes must have '" + node.getAllowedNumberEdges() + "' input"
            );
        }

        return true;
    }

    private boolean handleOpenFileNode(Node node, Map<String, CSV> csvData) {
        if (csvData.containsKey(node.getId())) {
            return true;
        } else {
            throw new IllegalArgumentException(
                "Some open file nodes refer to files which have not been supplied"
            );
        }
    }

    public Node[] getNodes() {
        return this.nodes != null ? nodes.clone() : null;
    }

    public Edge[] getEdges() {
        return this.edges != null ? edges.clone() : null;
    }
}
