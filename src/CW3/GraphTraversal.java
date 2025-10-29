package CW3;

import CW3.GraphNode;

import java.util.List;

public interface GraphTraversal<T extends GraphNode> {
    List<T> findShortestPath(T source, T target);

    int calculatePathWeight(List<T> path);
}
