package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges.Edge;

import java.util.*;

public interface BinaryOperation {

    int NUMBER_INBOUND_EDGES = 2;

    //likely to not need the below but left in for now
    /**
     * important to remember that you add edges on frontend in order if operator is dependent on order
     */
    default String[] getInputNodes(Edge[] edges, String id) {
        List<String> nodeIds = new ArrayList<>();

        for (Edge edge : edges) {
            if (edge.getTo().equals(id)) nodeIds.add(edge.getFrom());
        }

        if (nodeIds.size() != 2) {
            throw new IllegalArgumentException(String.format("Binary Processing Node '%s' must have 2 distinct input edges.", id));
        }

        return nodeIds.toArray(new String[0]);
    }
}
