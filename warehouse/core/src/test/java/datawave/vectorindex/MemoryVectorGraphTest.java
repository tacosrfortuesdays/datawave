package datawave.vectorindex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.shaded.org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

//Will make this abstract to test accumulo and in memory implementations
public class MemoryVectorGraphTest {
    private static final Logger log = Logger.getLogger(MemoryVectorGraphTest.class);
    private MemoryVectorGraph graph;

    @Before
    public void setup() {
        this.graph = new MemoryVectorGraph();
        Vertex A = new Vertex("0", new byte[] {});
        Vertex B = new Vertex("1", new byte[] {});
        Vertex C = new Vertex("2", new byte[] {});

        this.graph.addEdge(A, B);
        this.graph.addEdge(B, C);

    }

    @Test
    public void checkInit() {
        List<Vertex> nbrsA = this.graph.getNeighborList("0");
        List<Vertex> nbrsB = this.graph.getNeighborList("1");
        List<Vertex> nbrsC = this.graph.getNeighborList("2");

        assertNotNull(nbrsA);
        assertNotNull(nbrsB);
        assertNotNull(nbrsC);

        assertEquals(1, nbrsA.size());
        assertEquals(2, nbrsB.size());
        assertEquals(1, nbrsC.size());

        assertEquals("0", nbrsB.get(0).uid());
        assertEquals("2", nbrsB.get(1).uid());
        assertEquals("1", nbrsA.get(0).uid());
        assertEquals("1", nbrsC.get(0).uid());
    }

    @Test
    public void testAddVertex() {
        Vertex D = new Vertex("3", new byte[] {});
        this.graph.addVertex(D);
        assertEquals(4, this.graph.numVertices());
    }

    @Test
    public void testRemoveVertex() {
        Vertex B = new Vertex("1", new byte[] {});
        graph.removeVertex(B);
        assertEquals(2, graph.numVertices());
        assertEquals(0, graph.numEdges());
    }

    @Test
    public void testAddEdge() {
        Vertex A = new Vertex("0", new byte[] {});
        Vertex C = new Vertex("2", new byte[] {});
        graph.addEdge(A, C);
        List<Vertex> nbrsA = graph.getNeighborList("0");
        List<Vertex> nbrsC = graph.getNeighborList("2");
        assertEquals(2, nbrsA.size());
        assertEquals(2, nbrsC.size());
    }

    @Test
    public void testRemoveEdge() {
        Vertex A = new Vertex("0", new byte[] {});
        Vertex B = new Vertex("1", new byte[] {});
        graph.removeEdge(A, B);
        List<Vertex> nbrsA = graph.getNeighborList("0");
        List<Vertex> nbrsB = graph.getNeighborList("1");
        assertEquals(0, nbrsA.size());
        assertEquals(1, nbrsB.size());
    }

    @Test
    public void testGetNeighbors() {
        Iterator<Vertex> itr = graph.getNeighbors(new Vertex("1", new byte[] {}));
        List nbrs = IteratorUtils.toList(itr);
        assertEquals(2, nbrs.size());
        assertEquals("0", ((Vertex) (nbrs.get(0))).uid());
        assertEquals("2", ((Vertex) (nbrs.get(1))).uid());
    }
}
