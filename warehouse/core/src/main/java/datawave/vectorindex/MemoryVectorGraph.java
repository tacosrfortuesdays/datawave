package datawave.vectorindex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;

/**
 * Code is modified from https://www.baeldung.com/java-graphs
 */
public class MemoryVectorGraph extends VectorGraph {
    private int cnt = 0;
    private static final Logger log = Logger.getLogger(MemoryVectorGraph.class);

    private final Map<Vertex,List<Vertex>> adjVertices;
    private final Map<String,Vertex> vertices; //for hnsw, vertices index is only used on bottom level grpah

    public MemoryVectorGraph() {
        super();
        this.adjVertices = new HashMap<>();
        this.vertices = new HashMap<>();
    }


    public Vertex getVertex(String uuid) {
        return vertices.get(uuid);
    }

    @Override
    public int numVerticies() {
        return cnt;
    }

    @VisibleForTesting
    public int numEdges() {
        int edgeCnt = 0;
        for (List<Vertex> L : adjVertices.values()) {
            edgeCnt += L.size();
        }
        return edgeCnt;
    }

    public void addVertex(Vertex v) {
        adjVertices.putIfAbsent(v, new ArrayList<>());
        Vertex foundVert = vertices.putIfAbsent(v.uid(), v);
        if (foundVert == null) {
            cnt++;
        }

    }

    public void removeVertex(Vertex u) {
        // remove incoming edges
        List<Vertex> nbrs = getNeighborList(u.uid());
        for (Vertex v : nbrs) {
            removeDirectedEdge(v, u);
        }

        // remove outgoing edges and node
        adjVertices.remove(new Vertex(u.uid(), new double[] {}));
        vertices.remove(u.uid());
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

    @Override
    public Iterator<Vertex> getNeighbors(Vertex v, int level) {
        return adjVertices.get(v).iterator();
    }

    public boolean isEmpty(){
        return this.adjVertices.isEmpty();
    }

    public List<Vertex> getNeighborList(String uid) {
        return adjVertices.get(new Vertex(uid, new double[] {}));
    }
}
