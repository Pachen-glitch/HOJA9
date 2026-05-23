"""
CC2003 – Algoritmos y Estructura de Datos
Hoja de Trabajo 10 – Implementación con NetworkX (opcional)

Equivalente Python del programa Java.
Requiere: pip install networkx
"""

import math
import sys
from pathlib import Path

try:
    import networkx as nx
except ImportError:
    print("NetworkX no está instalado. Ejecute: pip install networkx")
    sys.exit(1)


# ── Helpers ─────────────────────────────────────────────────────────────────

def load_graph(filepath: str) -> nx.DiGraph:
    """Reads guategrafo.txt and returns a directed weighted graph."""
    G = nx.DiGraph()
    with open(filepath, encoding="utf-8") as f:
        for line_no, line in enumerate(f, start=1):
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            parts = line.split()
            if len(parts) < 3:
                print(f"  Línea {line_no} ignorada: '{line}'", file=sys.stderr)
                continue
            city1, city2 = parts[0], parts[1]
            try:
                km = float(parts[2])
            except ValueError:
                print(f"  Línea {line_no}: KM inválido, ignorada.", file=sys.stderr)
                continue
            G.add_edge(city1, city2, weight=km)
    return G


def run_floyd(G: nx.DiGraph):
    """
    Runs Floyd-Warshall via NetworkX and returns the all-pairs distance dict.
    Returns dict: {source: {target: distance}}
    """
    return dict(nx.floyd_warshall(G, weight="weight"))


def graph_center(dist: dict) -> tuple[str, float]:
    """
    Calculates graph centre following CentroDeGrafo.pdf algorithm:
      1. dist  = Floyd result (all-pairs shortest paths)
      2. For each vertex i, eccentricity = max{ dist[w][i]  for all w }
      3. Centre = vertex with minimum eccentricity
    Returns (center_city, eccentricity).
    """
    nodes = list(dist.keys())
    best_city = None
    best_ecc = math.inf

    for i in nodes:
        ecc = 0.0
        for w in nodes:
            if w == i:
                continue
            d = dist[w].get(i, math.inf)
            if d == math.inf:
                ecc = math.inf
                break
            if d > ecc:
                ecc = d
        if ecc < best_ecc:
            best_ecc = ecc
            best_city = i

    return best_city, best_ecc


def shortest_path_info(G: nx.DiGraph, dist: dict, origin: str, dest: str) -> str:
    """Returns a human-readable description of the shortest path."""
    if origin not in G:
        return f"La ciudad '{origin}' no existe en el grafo."
    if dest not in G:
        return f"La ciudad '{dest}' no existe en el grafo."

    d = dist.get(origin, {}).get(dest, math.inf)
    if d == math.inf:
        return f"No existe ruta entre '{origin}' y '{dest}'."

    try:
        path = nx.shortest_path(G, source=origin, target=dest, weight="weight")
        path_str = " → ".join(path)
        return f"Ruta: {path_str}\nDistancia total: {int(d)} km"
    except nx.NetworkXNoPath:
        return f"No existe ruta entre '{origin}' y '{dest}'."


def print_distance_matrix(dist: dict):
    """Prints the all-pairs shortest-path matrix."""
    nodes = sorted(dist.keys())
    col_w = 20

    print(" " * col_w, end="")
    for n in nodes:
        print(f"{n:<{col_w}}", end="")
    print()

    for src in nodes:
        print(f"{src:<{col_w}}", end="")
        for dst in nodes:
            d = dist[src].get(dst, math.inf)
            val = "INF" if d == math.inf else str(int(d))
            print(f"{val:<{col_w}}", end="")
        print()


def eccentricity_table(dist: dict) -> str:
    """Returns formatted eccentricity table."""
    nodes = list(dist.keys())
    lines = [f"{'Ciudad':<25} {'Excentricidad'}"]
    lines.append("-" * 40)
    for i in nodes:
        ecc = 0.0
        for w in nodes:
            if w == i:
                continue
            d = dist[w].get(i, math.inf)
            if d == math.inf:
                ecc = math.inf
                break
            if d > ecc:
                ecc = d
        ecc_str = "∞" if ecc == math.inf else str(int(ecc))
        lines.append(f"{i:<25} {ecc_str}")
    return "\n".join(lines)


# ── Interactive menu ─────────────────────────────────────────────────────────

DATA_FILE = "guategrafo.txt"


def menu(G: nx.DiGraph, dist: dict):
    while True:
        print("\n──────────────────────────────────────────────────")
        print("  1. Ruta más corta entre dos ciudades")
        print("  2. Mostrar centro del grafo")
        print("  3. Modificar grafo (agregar / eliminar arco)")
        print("  4. Mostrar matriz de distancias mínimas (APSP)")
        print("  5. Salir")
        print("──────────────────────────────────────────────────")
        opt = input("  Seleccione una opción: ").strip()

        if opt == "1":
            origin = input("  Ciudad origen : ").strip()
            dest   = input("  Ciudad destino: ").strip()
            print()
            print(shortest_path_info(G, dist, origin, dest))

        elif opt == "2":
            center, ecc = graph_center(dist)
            print()
            print(eccentricity_table(dist))
            if center:
                print(f"\n  ► Centro del grafo: {center}  (excentricidad = {int(ecc)} km)")
            else:
                print("  El grafo está vacío.")

        elif opt == "3":
            print("  a) Eliminar arco   b) Agregar / actualizar arco")
            choice = input("  Elija (a/b): ").strip().lower()
            if choice == "a":
                c1 = input("  Ciudad origen : ").strip()
                c2 = input("  Ciudad destino: ").strip()
                if G.has_edge(c1, c2):
                    G.remove_edge(c1, c2)
                    print(f"  Arco {c1} → {c2} eliminado.")
                else:
                    print("  El arco no existe.")
            elif choice == "b":
                c1  = input("  Ciudad origen  : ").strip()
                c2  = input("  Ciudad destino : ").strip()
                try:
                    km = float(input("  Distancia (km) : ").strip())
                    G.add_edge(c1, c2, weight=km)
                    print(f"  Arco {c1} → {c2} ({int(km)} km) agregado/actualizado.")
                except ValueError:
                    print("  Distancia inválida.")
                    continue
            else:
                print("  Opción no reconocida.")
                continue

            dist = run_floyd(G)
            print("  ✔ Rutas y centro del grafo recalculados.")

        elif opt == "4":
            print()
            print_distance_matrix(dist)

        elif opt == "5":
            print("\nCerrando programa. ¡Hasta pronto!")
            break
        else:
            print("  Opción no válida.")

    return dist


def main():
    print("╔══════════════════════════════════════════════════╗")
    print("║   Centro de Respuesta al COVID-19 – Guatemala    ║")
    print("║      Sistema de Rutas Más Cortas (NetworkX)      ║")
    print("╚══════════════════════════════════════════════════╝\n")

    # Locate data file
    data_path = Path(DATA_FILE)
    if not data_path.exists():
        # Try one level up (when running from python/ subdirectory)
        data_path = Path("..") / DATA_FILE
    if not data_path.exists():
        print(f"✘  No se encontró '{DATA_FILE}'. Colóquelo en el mismo directorio.", file=sys.stderr)
        sys.exit(1)

    G = load_graph(str(data_path))
    print(f"✔  Grafo cargado: {G.number_of_nodes()} ciudades, {G.number_of_edges()} arcos\n")

    dist = run_floyd(G)
    menu(G, dist)


if __name__ == "__main__":
    main()
