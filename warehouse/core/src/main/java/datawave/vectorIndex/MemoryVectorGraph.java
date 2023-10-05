package datawave.vectorIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;

public class MemoryVectorGraph extends VectorGraph {
    private static final Logger log = Logger.getLogger(MemoryVectorGraph.class);

    private final Map<Vertex,List<Vertex>> adjVertices;

    public MemoryVectorGraph() {
        super();
        this.adjVertices = new HashMap<>();
    }

    @VisibleForTesting
    public int numVertices() {
        return adjVertices.size();
    }

    public void addVertex(Vertex v) {
        adjVertices.putIfAbsent(v, new ArrayList<>());
    }

    public void removeVertex(Vertex v) {
        adjVertices.values().forEach(e -> e.remove(v));
        adjVertices.remove(new Vertex(v.uid(), new byte[] {}));
    }

    public void addDirectedEdge(Vertex source, Vertex sink) {
        addVertex(source);
        adjVertices.get(source).add(sink);
    }

    public void removeDirectedEdge(Vertex source, Vertex sink) {
        List<Vertex> nbrs = adjVertices.get(source);
        if (nbrs != null) {
            nbrs.remove(sink);
        }
    }

    public Iterator<Vertex> getNeighbors(Vertex x) {
        return adjVertices.get(x).iterator();
    }

    public List<Vertex> getNeighborList(String uid) {
        return adjVertices.get(new MemoryVertex(uid, new byte[] {}));
    }
}
