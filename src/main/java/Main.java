import java.io.IOException;
import java.util.Scanner;
import java.util.List;

public class Main {

    private static final String DATA_FILE = "guategrafo.txt";

    public static void main(String[] args) {
        Graph graph = new Graph();
        Floyd floyd = new Floyd();


        System.out.println("   Centro de Respuesta al COVID-19 – Guatemala    ");
        System.out.println("        Sistema de Rutas Más Cortas (Floyd)       ");

        try {
            GraphReader.readFromFile(DATA_FILE, graph);
            System.out.println("Grafo cargado desde '" + DATA_FILE + "'  (" + graph.size() + " ciudades)");
        } catch (IOException e) {
            System.err.println("No se pudo leer el archivo '" + DATA_FILE + "': " + e.getMessage());
            System.err.println(" Asegúrese de ejecutar el programa desde el directorio que contiene guategrafo.txt");
            System.exit(1);
        }

        floyd.compute(graph);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> handleShortestPath(scanner, floyd, graph);
                case "2" -> handleCenter(floyd);
                case "3" -> handleModify(scanner, graph, floyd);
                case "4" -> handleAdjacencyMatrix(graph);
                case "5" -> handleDistanceMatrix(floyd);
                case "6" -> {
                    System.out.println("Cerrando programa.");
                    running = false;
                }
                default -> System.out.println("  Opción no válida. Intente de nuevo.");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("  1. Calcular ruta más corta entre dos ciudades");
        System.out.println("  2. Mostrar centro del grafo");
        System.out.println("  3. Modificar grafo (agregar / eliminar arco)");
        System.out.println("  4. Mostrar matriz de adyacencia");
        System.out.println("  5. Mostrar matriz de distancias mínimas (APSP)");
        System.out.println("  6. Salir");
        System.out.print("  Seleccione una opción: ");
    }

    private static void handleShortestPath(Scanner sc, Floyd floyd, Graph graph) {
        // Mostrar todas las ciudades disponibles separadas por comas
        List<String> cities = graph.getVertices();
        if (cities.isEmpty()) {
            System.out.println("  No hay ciudades en el grafo.");
        } else {
            System.out.println("  Ciudades: " + String.join(", ", cities));
        }

        System.out.print("  Ciudad origen : ");
        String origin = sc.nextLine().trim();
        System.out.print("  Ciudad destino: ");
        String dest = sc.nextLine().trim();
        System.out.println();
        System.out.println(floyd.getPathDescription(origin, dest));
        System.out.println();
    }

    private static void handleCenter(Floyd floyd) {
        String center = floyd.getCenter();
        System.out.println();
        if (center == null) {
            System.out.println("  El grafo está vacío.");
        } else {
            System.out.println("  Tabla de excentricidades:");
            System.out.println(floyd.getEccentricityTable());
            System.out.println("  ► Centro del grafo: " + center);
        }
        System.out.println();
    }

    private static void handleModify(Scanner sc, Graph graph, Floyd floyd) {
        System.out.println("\n  a) Eliminar arco (interrupción de tráfico)");
        System.out.println("  b) Agregar / actualizar arco");
        System.out.print("  Elija (a/b): ");
        String choice = sc.nextLine().trim().toLowerCase();

        if (choice.equals("a")) {
            // Mostrar todas las ciudades
            List<String> cities = graph.getVertices();
            if (cities.isEmpty()) {
                System.out.println("  No hay ciudades en el grafo.");
            } else {
                System.out.println("  Ciudades: " + String.join(", ", cities));
            }

            System.out.print("  Ciudad origen : ");
            String c1 = sc.nextLine().trim();
            System.out.print("  Ciudad destino: ");
            String c2 = sc.nextLine().trim();
            boolean removed = graph.removeEdge(c1, c2);
            if (removed) {
                System.out.println("  Arco " + c1 + " → " + c2 + " eliminado.");
            } else {
                System.out.println("  Una o ambas ciudades no existen en el grafo.");
            }
        } else if (choice.equals("b")) {
            System.out.print("  Ciudad origen  : ");
            String c1 = sc.nextLine().trim();
            System.out.print("  Ciudad destino : ");
            String c2 = sc.nextLine().trim();
            System.out.print("  Distancia (km) : ");
            try {
                double km = Double.parseDouble(sc.nextLine().trim());
                graph.addEdge(c1, c2, km);
                System.out.println("  Arco " + c1 + " → " + c2 + " (" + (int)km + " km) agregado/actualizado.");
            } catch (NumberFormatException e) {
                System.out.println("  Distancia inválida. Operación cancelada.");
                return;
            }
        } else {
            System.out.println("  Opción no reconocida.");
            return;
        }

        floyd.compute(graph);
        System.out.println("  ✔ Rutas y centro del grafo recalculados.\n");
    }

    private static void handleAdjacencyMatrix(Graph graph) {
        System.out.println("\n  Matriz de adyacencia:\n");
        graph.printAdjacencyMatrix();
        System.out.println();
    }

    private static void handleDistanceMatrix(Floyd floyd) {
        System.out.println("\n  Matriz APSP (distancias mínimas):\n");
        floyd.printDistanceMatrix();
        System.out.println();
    }
}
