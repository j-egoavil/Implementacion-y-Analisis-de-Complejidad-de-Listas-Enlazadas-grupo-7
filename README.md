# Implementación y análisis de complejidad de listas, pilas y colas en Java

## Descripción

Proyecto del curso Estructuras de Datos (2026-I) orientado a la implementación y evaluación experimental de:

- 4 variantes de listas enlazadas
- una pila basada en arreglo dinámico
- una cola basada en arreglo circular dinámico

Además de la implementación, el proyecto incluye benchmarks y scripts de generación de gráficas a partir de archivos CSV.

---

## Requisitos

### Java
- Java 21

### Python
- Python 3

### Librerías de Python
Los scripts de gráficas usan:

- `pandas`
- `matplotlib`

Instalación sugerida:

```bash
pip install pandas matplotlib
```
## Estructuras implementadas

### List

Implementaciones disponibles:

 - SinglyLinkedList
 - SinglyLinkedListTail
 - DoublyLinkedList
 - DoublyLinkedListTail

Operaciones Benchmarkeadas: 

- push_front
- push_back
- pop_front
- pop_back
- find
- erase
- add_before
- add_after

### Stack

Implementación: 

- ArrayStack

Operaciones Benchmarkeadas:

- push
- pop
- peek
- delete

### Queue

Implementación:

- CircularArrayQueue

Operaciones Benchmarkeadas:

- enqueue
- dequeue
- front
- delete

## Configuración actual de los benchmark. 

Los benchmarks usan actualmente:

- 3 corridas de calentamiento (warmup)
- 5 corridas medidas por tamaño de entrada

### Tamaños de muestra.

La suite trabaja con los siguientes tamaños:

- 10
- 100
- 1_000
- 10_000
- 100_000
- 1_000_000
- 10_000_000
- 100_000_000

### Formato de salida csv 

Cada benchmark genera archivos con este encabezado:
```bash
size,avg_time_ns,median_ns,min_ns,max_ns
```
Los tiempos se almacenan en nanosegundos.

### Advertencia importante sobre la ejecución

Aunque el proyecto permite ejecutar toda la suite desde Main, los resultados pueden verse alterados si se ejecutan todos los benchmarks en una sola corrida, especialmente en operaciones pequeñas (O(1)), debido a:

- presión de memoria
- recolección de basura de la JVM
- interferencia entre benchmarks previos
- ruido adicional del entorno de ejecución
- Recomendación

Para obtener resultados más consistentes, se recomienda ejecutar los benchmarks método por método, dejando que cada corrida regenere su CSV correspondiente.

## Compilación 

Linux / macOS / Git Bash

Desde la raíz del proyecto:

```bash
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
```

PowerShell (Windows)

```bash
$files = Get-ChildItem -Path src/main/java -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
javac -d out $files
```

## Ejecución 

Ejecutar la suite completa 

```bash
java -cp out Main
```

No es la opción recomendada para la generación final de resultados si se busca máxima consistencia en los benchmarks.

## Ejecutar Benchmark individuales.

### Stack 

Ejemplos: 

```bash
java -cp out Main stack push
java -cp out Main stack pop
java -cp out Main stack peek
java -cp out Main stack delete
```

### Queue

Ejemplos:

```bash
java -cp out Main queue enqueue
java -cp out Main queue dequeue
java -cp out Main queue front
java -cp out Main queue delete
```

### List

Formato general:

```bash
java -cp out Main list <implementacion> <operacion>
```

Implementaciones válidas:

- singly
- singly_tail
- doubly
- doubly_tail

Ejemplos: 

```bash
java -cp out Main list singly push_front
java -cp out Main list singly pop_front
java -cp out Main list singly add_after

java -cp out Main list singly_tail push_back
java -cp out Main list singly_tail pop_front

java -cp out Main list doubly erase
java -cp out Main list doubly add_before
java -cp out Main list doubly add_after

java -cp out Main list doubly_tail push_back
java -cp out Main list doubly_tail pop_back
```

### Flujo recomendado. 
1. Compilar el proyecto.
2. Ejecutar los benchmarks uno por uno.
3. Verificar que los CSV se hayan actualizado correctamente.
4. Ejecutar los scripts de Python para regenerar las gráficas.

### Generación de gráficas. 

Scripts disponibles
- scripts/graficar_resultados.py
- scripts/graficar_comparacion.py
- scripts/graficar_avanzadas.py

Ejecución

Desde la raíz del proyecto:

```bash
python3 scripts/graficar_resultados.py
python3 scripts/graficar_comparacion.py
python3 scripts/graficar_avanzadas.py
```

Si tu sistema usa python en lugar de python3:

```bash
python scripts/graficar_resultados.py
python scripts/graficar_comparacion.py
python scripts/graficar_avanzadas.py
```

### Salidas generadas por los scripts

Los scripts regeneran gráficas a partir de los CSV ya existentes.

Tipos de salida:

- gráficas individuales
- gráficas comparativas
- gráficas avanzadas

Entre las salidas disponibles se incluyen comparaciones por método, comparaciones entre implementaciones, pendientes log-log, speedups y heatmaps.

### Notas de uso.
- Si una gráfica no se genera, revisa primero que el CSV correspondiente exista y no esté vacío.
- Si cambias la metodología o vuelves a ejecutar benchmarks, conviene regenerar todas las gráficas para mantener consistencia.
- Si una operación presenta resultados extraños al correr la suite completa, vuelve a ejecutarla de forma individual antes de aceptar ese CSV como definitivo.

## Integrantes

- Egovail Cardozo Juan Daniel
- Romero Villalba Jean Pierre
- Toro Moreno Kevin Andres