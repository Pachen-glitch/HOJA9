import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GraphReader {

    public static void readFromFile(String filePath, Graph graph) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                if (parts.length < 3) {
                    System.err.println("Línea " + lineNum + " ignorada (formato incorrecto): " + line);
                    continue;
                }

                String city1 = parts[0];
                String city2 = parts[1];
                double km;
                try { //CONSULTAS AL GRAFO
                    km = Double.parseDouble(parts[2]);
                } catch (NumberFormatException e) {
                    System.err.println("Línea " + lineNum + " ignorada (KM no numérico): " + line);
                    continue;
                }

                graph.addEdge(city1, city2, km);
            }
        }
    }
}
