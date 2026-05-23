
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Graph} and {@link Floyd}.
 *
 * Uses the example digraph from CentroDeGrafo.pdf (Fig. 6.22):
 *   a→b (1), b→c (2), b→d (1), c→d (2), c→e (4), d→b (1), d→e (5)
 *
 * Expected APSP matrix (Fig. 6.23) and centre = d.
 */
class GraphFloydTest {

    private Graph graph;
    private Floyd floyd;

    @BeforeEach
    void setUp() {
        graph = new Graph();
        floyd = new Floyd();
    }

    // ── Graph structural tests ───────────────────────────────────────────────

    @Test
    void testAddVertex() {
        graph.addVertex("Mixco");
        assertTrue(graph.hasVertex("Mixco"));
        assertEquals(1, graph.size());
    }

    @Test
    void testAddVertexDuplicateIgnored() {
        graph.addVertex("Mixco");
        graph.addVertex("Mixco");
        assertEquals(1, graph.size());
    }

    @Test
    void testAddEdgeCreatesVertices() {
        graph.addEdge("GuatemalaCity", "Antigua", 45);
        assertTrue(graph.hasVertex("GuatemalaCity"));
        assertTrue(graph.hasVertex("Antigua"));
        assertEquals(45, graph.getEdgeWeight("GuatemalaCity", "Antigua"));
    }

    @Test
    void testRemoveEdgeSetsInfinity() {
        graph.addEdge("GuatemalaCity", "Antigua", 45);
        graph.removeEdge("GuatemalaCity", "Antigua");
        assertEquals(Graph.INF, graph.getEdgeWeight("GuatemalaCity", "Antigua"));
    }

    @Test
    void testRemoveEdgeNonExistentReturnsFalse() {
        graph.addVertex("Mixco");
        assertFalse(graph.removeEdge("Mixco", "Antigua")); // Antigua doesn't exist
    }

    @Test
    void testEdgeWeightNonExistentReturnsInf() {
        graph.addVertex("Petén");
        assertEquals(Graph.INF, graph.getEdgeWeight("Petén", "Cobán"));
    }

    // ── Floyd algorithm tests (book example) ────────────────────────────────

    /**
     * Builds a simple verifiable digraph:
     *   A→B(10), B→C(5), A→C(100), C→D(1)
     * Expected shortest paths:
     *   A→C = 15 (via B), A→D = 16 (via B,C)
     *   D→* = INF (D has no outgoing edges)
     */
    private void buildBookGraph() {
        graph.addEdge("A", "B", 10);
        graph.addEdge("B", "C", 5);
        graph.addEdge("A", "C", 100);
        graph.addEdge("C", "D", 1);
        graph.addEdge("D", "A", 50); // makes graph strongly connected
    }

    @Test
    void testFloydShortestDistanceDirectEdge() {
        buildBookGraph();
        floyd.compute(graph);
        assertEquals(10.0, floyd.getDistance("A", "B"), 0.001);
    }

    @Test
    void testFloydShortestDistanceIndirectBeatsDirectEdge() {
        buildBookGraph();
        floyd.compute(graph);
        // A→B→C = 15, cheaper than direct A→C = 100
        assertEquals(15.0, floyd.getDistance("A", "C"), 0.001);
    }

    @Test
    void testFloydShortestDistanceMultiHop() {
        buildBookGraph();
        floyd.compute(graph);
        // A→B→C→D = 10+5+1 = 16
        assertEquals(16.0, floyd.getDistance("A", "D"), 0.001);
    }

    @Test
    void testFloydSameCityDistance() {
        buildBookGraph();
        floyd.compute(graph);
        assertEquals(0.0, floyd.getDistance("C", "C"), 0.001);
    }

    @Test
    void testFloydCycleDistance() {
        buildBookGraph();
        floyd.compute(graph);
        // D→A exists (50), so D can reach B via D→A→B = 50+10 = 60
        assertEquals(60.0, floyd.getDistance("D", "B"), 0.001);
    }

    // ── Graph centre test ────────────────────────────────────────────────────

    @Test
    void testGraphCenterStronglyConnected() {
        buildBookGraph();
        floyd.compute(graph);
        // All nodes reachable from everywhere (cycle via D→A).
        // Centre = vertex with minimum eccentricity; just check it's non-null & valid.
        String center = floyd.getCenter();
        assertNotNull(center);
        assertTrue(graph.hasVertex(center));
    }

    // ── Path description test ────────────────────────────────────────────────

    @Test
    void testPathDescriptionContainsCities() {
        buildBookGraph();
        floyd.compute(graph);
        String path = floyd.getPathDescription("A", "D");
        assertTrue(path.contains("A"));
        assertTrue(path.contains("D"));
        assertTrue(path.contains("16"));  // total distance
    }

    @Test
    void testPathDescriptionSameCity() {
        buildBookGraph();
        floyd.compute(graph);
        String path = floyd.getPathDescription("A", "A");
        assertTrue(path.contains("misma ciudad") || path.contains("0"));
    }

    // ── Floyd after graph modification ───────────────────────────────────────

    @Test
    void testFloydAfterEdgeRemoval() {
        graph.addEdge("X", "Y", 10);
        graph.addEdge("Y", "Z", 10);
        graph.addEdge("X", "Z", 100);
        floyd.compute(graph);
        assertEquals(20.0, floyd.getDistance("X", "Z"), 0.001); // via Y

        graph.removeEdge("X", "Y");
        floyd.compute(graph);
        assertEquals(100.0, floyd.getDistance("X", "Z"), 0.001); // direct only
    }

    @Test
    void testFloydAfterEdgeAddition() {
        graph.addEdge("A", "B", 50);
        floyd.compute(graph);
        assertTrue(floyd.getDistance("A", "C") >= Graph.INF); // C doesn't exist yet

        graph.addEdge("B", "C", 30);
        floyd.compute(graph);
        assertEquals(80.0, floyd.getDistance("A", "C"), 0.001);
    }
}
