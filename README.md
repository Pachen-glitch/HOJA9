# Sistema de Rutas Más Cortas (Floyd)

Aplicación Java para cargar un grafo de ciudades desde un archivo, calcular rutas más cortas (Floyd–Warshall), y explorar/modificar las aristas. Incluye vistas en consola y ventanas GUI con tablas para visualizar matrices.

**Requisitos**
- Java 17+ (JDK). Se ha probado con JDK 21.
- Maven (para compilar y empaquetar).
- Entorno Windows / Linux / macOS con capacidad para abrir ventanas Swing.

**Estructura del proyecto (resumen)**
- **Source**: [src/main/java](src/main/java)
  - `Main.java` — interfaz de usuario por consola + ventanas para matrices.
  - `Graph.java` — modelo del grafo (vértices y matriz de adyacencia).
  - `Floyd.java` — implementación del algoritmo de Floyd–Warshall y utilidades.
  - `GraphReader.java` — lector del fichero de arcos.
- **Datos**: `guategrafo.txt` — archivo con las aristas y distancias (véase formato).

**Formato del archivo de datos**
- Cada línea contiene: `<CiudadOrigen> <CiudadDestino> <DistanciaKm>` separados por espacios.
- Ejemplo:
  - `GuatemalaCity Mixco 15`
  - `Antigua Escuintla 55`
- Líneas vacías o que empiecen por `#` se ignoran.
- El grafo es dirigido: si quiere arista en ambos sentidos, incluya ambas líneas.

**Compilar**
Desde la raíz del proyecto ejecute:

```bash
mvn package
```

El bytecode queda en `target/classes`.

**Ejecutar**
Ejecute la clase `Main` desde la carpeta del proyecto:

```bash
java -cp target/classes Main
```

Se mostrará un menú por consola con opciones:
- **1**: Calcular ruta más corta entre dos ciudades (se muestra la lista de ciudades antes de pedir origen y destino).
- **2**: Mostrar centro del grafo y tabla de excentricidades.
- **3**: Modificar grafo — eliminar o agregar/arreglar un arco (luego se recalculan rutas).
- **4**: Mostrar matriz de adyacencia — abre una ventana con una tabla (JTable).
- **5**: Mostrar matriz APSP (distancias mínimas) — abre una ventana con una tabla (JTable).
- **6**: Salir.

**Comportamiento de la GUI (tablas)**
- Las matrices se muestran en ventanas Swing con `JTable` y barras de desplazamiento.
- Las celdas se centran y las columnas se autoajustan al contenido (por defecto).
- Si la tabla aparece muy densa o con valores `INF`, revise el archivo `guategrafo.txt` y los mensajes de depuración en consola.

**Depuración rápida**
- Al abrir las matrices, el programa imprime en consola líneas como:
  - `[DEBUG] Ciudades cargadas: N, arcos finitos: M`
  - `[DEBUG] Matriz APSP: ciudades=N, entradas finitas=M`
- Si `arcos finitos` o `entradas finitas` son muy bajos, significa que muchas entradas quedaron en `INF` (no conectadas). Verifique formato y consistencia de `guategrafo.txt`.

**Sugerencias de uso**
- Mantenga `guategrafo.txt` en el mismo directorio desde donde ejecuta el programa, o modifique `DATA_FILE` en `Main.java` si lo mueve.
- Para inspeccionar fuera de la GUI, puede exportar la salida de matrices a CSV modificando `Main.java` (si lo desea, puedo agregar esta funcionalidad).

**Contribuir / Extensiones sugeridas**
- Añadir exportación a CSV o Excel.
- Añadir búsqueda y filtrado en la ventana de la tabla.
- Añadir guardado automático de cambios en el grafo.

**Licencia**
- Código proporcionado sin licencia explícita; consulte al autor para atribución o uso comercial.

---

Si quieres, puedo:
- Añadir un script `run.bat`/`run.sh` para ejecutar rápido.
- Generar exportación CSV al abrir la matriz.
- Mejorar la GUI (resaltar entradas no-`INF`, permitir copiar celdas).

Indica qué prefieres que haga a continuación.