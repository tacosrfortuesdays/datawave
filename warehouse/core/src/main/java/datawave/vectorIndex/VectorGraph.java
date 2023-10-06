package datawave.vectorIndex;

import java.util.Iterator;

public abstract class VectorGraph {
    abstract public int isEmpty();
    abstract public void addVertex(Vertex v);

    abstract public void removeVertex(Vertex v);

    abstract public void addDirectedEdge(Vertex source, Vertex sink);

    public void addEdge(Vertex x, Vertex y) {
        this.addDirectedEdge(x, y);
        this.addDirectedEdge(y, x);
    }

    abstract public void removeDirectedEdge(Vertex source, Vertex sink);

    public void removeEdge(Vertex x, Vertex y) {
        this.removeDirectedEdge(x, y);
        this.removeDirectedEdge(y, x);
    }

    abstract public Iterator<Vertex> getGraph(int level);
    abstract public Iterator<Vertex> getNeighbors(Vertex v, int level);
}
