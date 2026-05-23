public class Floyd {

    private double[][] dist;
    private int[][]    next;
    private int        n;
    private Graph      graph;

    public Floyd() {}

    public void compute(Graph g) {
        this.graph = g;
        this.n = g.size();
        dist = g.getAdjacencyMatrix();
        next = new int[n][n];
//ASIGNAR NODS
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                next[i][j] = (dist[i][j] < Graph.INF && i != j) ? j : -1;
            }
        }
        //RECORRIDO
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] < Graph.INF && dist[k][j] < Graph.INF) {
                        double newDist = dist[i][k] + dist[k][j];
                        if (newDist < dist[i][j]) {
                            dist[i][j] = newDist;
                            next[i][j] = next[i][k];
                        }
                    }
                }
            }
        }
    }

    public double getDistance(String city1, String city2) {
        int i = graph.getIndex(city1);
        int j = graph.getIndex(city2);
        if (i == -1 || j == -1) return Graph.INF;
        return dist[i][j];
    }

    public double getDistance(int i, int j) {
        return dist[i][j];
    }

    public String getPathDescription(String city1, String city2) {
        int src = graph.getIndex(city1);
        int dst = graph.getIndex(city2);

        if (src == -1 || dst == -1)
            return "Una o ambas ciudades no existen en el grafo.";

        if (dist[src][dst] >= Graph.INF)
            return "No existe ruta entre " + city1 + " y " + city2 + ".";

        if (src == dst)
            return city1 + " → " + city2 + "  (misma ciudad, distancia = 0)";

        StringBuilder sb = new StringBuilder();
        sb.append("Ruta: ").append(city1);

        int cur = src;
        while (cur != dst) {
            cur = next[cur][dst];
            sb.append(" → ").append(graph.getVertex(cur));
        }

        sb.append("\nDistancia total: ").append((int) dist[src][dst]).append(" km");
        return sb.toString();
    }

    public String getCenter() {
        if (n == 0) return null;

        double minEccentricity = Graph.INF;
        int    centerIndex     = -1;

        for (int i = 0; i < n; i++) {
            double eccentricity = 0;
            for (int w = 0; w < n; w++) {
                if (w == i) continue;
                if (dist[w][i] >= Graph.INF) {
                    eccentricity = Graph.INF;
                    break;
                }
                if (dist[w][i] > eccentricity) {
                    eccentricity = dist[w][i];
                }
            }

            if (eccentricity < minEccentricity) {
                minEccentricity = eccentricity;
                centerIndex = i;
            }
        }

        return centerIndex == -1 ? null : graph.getVertex(centerIndex);
    }

    public String getEccentricityTable() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-25s %s%n", "Ciudad", "Excentricidad"));
        sb.append("-".repeat(40)).append("\n");

        for (int i = 0; i < n; i++) {
            double eccentricity = 0;
            boolean infinite = false;
            for (int w = 0; w < n; w++) {
                if (w == i) continue;
                if (dist[w][i] >= Graph.INF) { infinite = true; break; }
                if (dist[w][i] > eccentricity) eccentricity = dist[w][i];
            }
            String eccStr = infinite ? "∞" : String.valueOf((int) eccentricity);
            sb.append(String.format("%-25s %s%n", graph.getVertex(i), eccStr));
        }
        return sb.toString();
    }

    public void printDistanceMatrix() {
        System.out.printf("%-22s", "");
        for (int j = 0; j < n; j++) System.out.printf("%-22s", graph.getVertex(j));
        System.out.println();

        for (int i = 0; i < n; i++) {
            System.out.printf("%-22s", graph.getVertex(i));
            for (int j = 0; j < n; j++) {
                String val = (dist[i][j] >= Graph.INF) ? "INF" : String.valueOf((int) dist[i][j]);
                System.out.printf("%-22s", val);
            }
            System.out.println();
        }
    }
}
