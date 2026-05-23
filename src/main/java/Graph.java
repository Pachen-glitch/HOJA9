import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//CREACION DEL GRAF
public class Graph {

    public static final double INF = Double.MAX_VALUE / 2;

    private final List<String> vertices;
    private final Map<String, Integer> indexMap;
    private double[][] adjMatrix;

    public Graph() {
        vertices = new ArrayList<>();
        indexMap = new HashMap<>();
        adjMatrix = new double[0][0];
    }

    public void addVertex(String city) {
        if (indexMap.containsKey(city)) return;

        int n = vertices.size();
        vertices.add(city);
        indexMap.put(city, n);

        double[][] newMatrix = new double[n + 1][n + 1];
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                if (i < n && j < n) {
                    newMatrix[i][j] = adjMatrix[i][j];
                } else if (i == j) {
                    newMatrix[i][j] = 0;
                } else {
                    newMatrix[i][j] = INF;
                }
            }
        }
        adjMatrix = newMatrix;
    }

    public boolean hasVertex(String city) {
        return indexMap.containsKey(city);
    }

    public List<String> getVertices() {
        return java.util.Collections.unmodifiableList(vertices);
    }

    public void addEdge(String city1, String city2, double km) {
        addVertex(city1);
        addVertex(city2);
        int i = indexMap.get(city1);
        int j = indexMap.get(city2);
        adjMatrix[i][j] = km;
    }

    public boolean removeEdge(String city1, String city2) {
        if (!indexMap.containsKey(city1) || !indexMap.containsKey(city2)) return false;
        int i = indexMap.get(city1);
        int j = indexMap.get(city2);
        adjMatrix[i][j] = INF;
        return true;
    }

    public double getEdgeWeight(String city1, String city2) {
        if (!indexMap.containsKey(city1) || !indexMap.containsKey(city2)) return INF;
        return adjMatrix[indexMap.get(city1)][indexMap.get(city2)];
    }

    public int size() {
        return vertices.size();
    }

    public String getVertex(int index) {
        return vertices.get(index);
    }

    public int getIndex(String city) {
        Integer idx = indexMap.get(city);
        return idx == null ? -1 : idx;
    }

    public double[][] getAdjacencyMatrix() {
        int n = vertices.size();
        double[][] copy = new double[n][n];
        for (int i = 0; i < n; i++)
            copy[i] = adjMatrix[i].clone();
        return copy;
    }

    public void printAdjacencyMatrix() {
        int n = vertices.size();
        System.out.printf("%-22s", "");
        for (String v : vertices) System.out.printf("%-22s", v);
        System.out.println();

        for (int i = 0; i < n; i++) {
            System.out.printf("%-22s", vertices.get(i));
            for (int j = 0; j < n; j++) {
                String val = (adjMatrix[i][j] >= INF) ? "INF" : String.valueOf((int) adjMatrix[i][j]);
                System.out.printf("%-22s", val);
            }
            System.out.println();
        }
    }
}
