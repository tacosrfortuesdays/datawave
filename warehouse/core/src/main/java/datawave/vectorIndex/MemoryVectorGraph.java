package datawave.vectorIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;

/**
 * Code is modified from https://www.baeldung.com/java-graphs
 */
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

    @VisibleForTesting
    public int numEdges() {
        int cnt = 0;
        for (List<Vertex> L : adjVertices.values()) {
            cnt += L.size();
        }
        return cnt;
    }

    public void addVertex(Vertex v) {
        adjVertices.putIfAbsent(v, new ArrayList<>());
    }

    public void removeVertex(Vertex u) {
        // remove incoming edges
        List<Vertex> nbrs = getNeighborList(u.uid());
        for (Vertex v : nbrs) {
            removeDirectedEdge(v, u);
        }

        // remove outgoing edges and node
        adjVertices.remove(new Vertex(u.uid(), new byte[] {}));
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
        return adjVertices.get(new Vertex(uid, new byte[] {}));
    }
}
