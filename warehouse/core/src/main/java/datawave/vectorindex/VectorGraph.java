package datawave.vectorindex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class VectorGraph {
    abstract public boolean isEmpty();
    abstract public void addVertex(Vertex v);

    abstract public Vertex getVertex(String uuid);
    abstract public int numVerticies();

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

    abstract public Iterator<Vertex> getNeighbors(Vertex v, int level);

    public List<Vertex> getNeighborList(Vertex v, int level){
        ArrayList<Vertex> neighbors = new ArrayList<>();
        Iterator<Vertex> itr = this.getNeighbors(v, level);
        while(itr.hasNext()){
            neighbors.add(itr.next());
        }
        return neighbors;
    }
}
