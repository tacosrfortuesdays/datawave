package datawave.vectorIndex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.shaded.org.apache.commons.collections.IteratorUtils;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

//Will make this abstract to test accumulo and in memory implementations
public class MemoryVectorGraphTest {
    MemoryVectorGraph graph;

    @BeforeEach
    public void init() {
        graph = new MemoryVectorGraph();
        MemoryVertex A = new MemoryVertex("0", new byte[] {});
        MemoryVertex B = new MemoryVertex("1", new byte[] {});
        MemoryVertex C = new MemoryVertex("2", new byte[] {});

        graph.addEdge(A, B);
        graph.addEdge(B, C);

    }

    @Test
    public void checkInit() {
        List<Vertex> nbrsA = graph.getNeighborList("0");
        List<Vertex> nbrsB = graph.getNeighborList("1");
        List<Vertex> nbrsC = graph.getNeighborList("2");

        assertNotNull(nbrsA);
        assertNotNull(nbrsB);
        assertNotNull(nbrsC);

        assertEquals(nbrsA.size(), 1);
        assertEquals(nbrsB.size(), 2);
        assertEquals(nbrsC.size(), 1);

        assertEquals("0", nbrsB.get(0).uid());
        assertEquals("2", nbrsB.get(1).uid());
        assertEquals("1", nbrsA.get(0).uid());
        assertEquals("1", nbrsC.get(0).uid());
    }

    @Test
    public void testAddVertex() {
        MemoryVertex D = new MemoryVertex("3", new byte[] {});
        graph.addVertex(D);
        assertEquals(4, graph.numVertices());
    }

    @Test
    public void testRemoveVertex() {
        MemoryVertex B = new MemoryVertex("1", new byte[] {});
        graph.removeVertex(B);
        assertEquals(2, graph.numVertices());
    }

    @Test
    public void testAddEdge() {
        MemoryVertex A = new MemoryVertex("0", new byte[] {});
        MemoryVertex C = new MemoryVertex("2", new byte[] {});
        graph.addEdge(A, C);
        List<Vertex> nbrsA = graph.getNeighborList("0");
        List<Vertex> nbrsC = graph.getNeighborList("2");
        assertEquals(2, nbrsA.size());
        assertEquals(2, nbrsC.size());
    }

    @Test
    public void testRemoveEdge() {
        MemoryVertex A = new MemoryVertex("0", new byte[] {});
        MemoryVertex B = new MemoryVertex("1", new byte[] {});
        graph.removeEdge(A, B);
        List<Vertex> nbrsA = graph.getNeighborList("0");
        List<Vertex> nbrsB = graph.getNeighborList("1");
        assertEquals(0, nbrsA.size());
        assertEquals(1, nbrsB.size());
    }

    @Test
    public void testGetNeighbors() {
        Iterator<Vertex> itr = graph.getNeighbors(new Vertex("1", new byte[] {}));
        List<Vertex> nbrs = IteratorUtils.toList(itr);
        assertEquals(2, nbrs.size());
        assertEquals("0", nbrs.get(0).uid());
        assertEquals("2", nbrs.get(2).uid());
    }
}
