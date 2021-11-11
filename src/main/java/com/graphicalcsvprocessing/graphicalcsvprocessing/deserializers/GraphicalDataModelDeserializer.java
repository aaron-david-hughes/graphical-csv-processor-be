package com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.Tree;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.GraphicalDataModel;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges.Edge;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.BinaryOperationNode;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.models.Tree.TreeNode;

public class GraphicalDataModelDeserializer extends StdDeserializer<GraphicalDataModel> {

    public GraphicalDataModelDeserializer() {
        this(null);
    }

    public GraphicalDataModelDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public GraphicalDataModel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode jsonNode = jsonParser.readValueAsTree();

        JsonParser nodeParser = jsonNode.get("nodes").traverse();
        nodeParser.setCodec(jsonParser.getCodec());
        Node[] nodes = nodeParser.readValueAs(Node[].class);

        JsonParser edgeParser = jsonNode.get("edges").traverse();
        edgeParser.setCodec(jsonParser.getCodec());
        Edge[] edges = edgeParser.readValueAs(Edge[].class);

        return new GraphicalDataModel(getProcessTree(nodes, edges), nodes);
    }

    /**
     * node ids to int as while still not to do with ordering, it will allow for much quicker access
     * read edges for one with to the parent id, take from id
     * from id represents node id
     */
    private Tree<Node> getProcessTree(Node[] nodes, Edge[] edges) {
        String topNodeId = getTopNodeId(nodes, edges);

        if (isCyclic(topNodeId, edges, new HashSet<>())) {
            throw new IllegalArgumentException("The graph supplied is cyclic - must be linear.");
        }

        Map<String, Node> nodeMap = new HashMap<>();
        Arrays.stream(nodes).forEach(node -> nodeMap.put(node.getId(), node));

        return new Tree<>(getNextTreeNode(topNodeId, nodeMap, edges));
    }

    /**
     * will ensure that the graph culminates in one node
     */
    private String getTopNodeId(Node[] nodes, Edge[] edges) {
        List<String> fromIds = new ArrayList<>();
        List<String> nodeIds = new ArrayList<>();

        for (Edge edge : edges) {
            fromIds.add(edge.getFrom());
        }

        for (Node node : nodes) {
            nodeIds.add(node.getId());
        }

        for (String fromId : fromIds) {
            nodeIds.remove(fromId);
        }

        if (nodeIds.size() != 1) {
            throw new IllegalArgumentException("Graph must have one and only one end node");
        }

        return nodeIds.get(0);
    }

    /**
     * as we already know that the graph ends with a single point, we can use that point to efficiently determine if
     * cyclic or not
     * O(n)
     */
    private boolean isCyclic(String nodeId, Edge[] edges, Set<String> visited) {
        if (visited.contains(nodeId)) {
            return true;
        }

        visited.add(nodeId);

        List<String> inputNodeIds = Arrays.stream(edges)
                .filter(edge -> edge.getTo().equals(nodeId))
                .map(Edge::getFrom)
                .collect(Collectors.toList());

        for (String id : inputNodeIds) {
            if (isCyclic(id, edges, visited)) return true;
        }

        return false;
    }

    /**
     * Edges into binary nodes must specify if they are priority: y/n (must have exactly 1y and 1n)
     */
    private TreeNode<Node> getNextTreeNode(String nodeId, Map<String, Node> nodes, Edge[] edges) {
        List<Edge> relevantEdges = Arrays.stream(edges)
                .filter(edge -> edge.getTo().equals(nodeId))
                .collect(Collectors.toList());

        //size check per node to ensure right format
        Node node = nodes.get(nodeId);

        if (node.getAllowedNumberEdges() != relevantEdges.size()) {
            throw new IllegalArgumentException(
                    String.format("Node with id '%s' requires '%s' input edges, but has '%s'",
                            nodeId, node.getAllowedNumberEdges(), relevantEdges.size())
            );
        }

        //TODO
        // ensure the order of nodes is as per priority - might change if non biased binary operation comes into play
        if (node instanceof BinaryOperationNode) {
            //make sure one y and one n for priority
            if (!binaryNodeContainsCorrectEdges(relevantEdges)) {
                throw new IllegalArgumentException("Binary nodes must have one priority y and one priority n edge");
            }

            if (!relevantEdges.get(0).getPriority().equalsIgnoreCase("y")) {
                Collections.reverse(relevantEdges);
            }
        }

        //note: no need to check on edge.from == null as will not deserialize

        //if node's required edges is at least 1
        TreeNode<Node> left = null;
        if (node.getAllowedNumberEdges() >= 1) {
            left = getNextTreeNode(relevantEdges.get(0).getFrom(), nodes, edges);
        }

        //if node's required edges is at least 2
        TreeNode<Node> right = null;
        if (node.getAllowedNumberEdges() >= 2) {
            right = getNextTreeNode(relevantEdges.get(1).getFrom(), nodes, edges);
        }

        return new TreeNode<>(nodes.get(nodeId), left, right);
    }

    private boolean binaryNodeContainsCorrectEdges(List<Edge> relevantEdges) {
        String priority0 = relevantEdges.get(0).getPriority();
        String priority1 = relevantEdges.get(1).getPriority();

        return priority0 != null && priority1 != null &&
                (priority0.equalsIgnoreCase("y") || priority1.equalsIgnoreCase("y")) &&
                (priority0.equalsIgnoreCase("n") || priority1.equalsIgnoreCase("n"));
    }
}
