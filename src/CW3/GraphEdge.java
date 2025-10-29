package CW3;


public interface GraphEdge<T extends GraphNode> {
    //represents an edge of the graph
    //Getting the start of the edge
    T getSource();

    //Getting the end of the edge
    T getDestination();

    //Getting the weight of the edge
    int getWeight();

    boolean connects(T node);

    T getOtherNode(T node);
}