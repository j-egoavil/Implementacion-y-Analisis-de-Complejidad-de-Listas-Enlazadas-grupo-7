# Implementacion y Analisis de Complejidad de Listas, Pilas y Colas en Java

## Descripcion
Proyecto del curso Estructuras de Datos (2026-I) para implementar y analizar:

- 4 variantes de listas enlazadas
- stack basado en arreglo dinamico
- queue basada en arreglo circular dinamico

El proyecto combina analisis teorico Big-O con medicion experimental y graficas.

## Estructuras implementadas

### List

Implementaciones:

- SinglyLinkedList
- SinglyLinkedListTail
- DoublyLinkedList
- DoublyLinkedListTail

Contrato de ListADT:

- pushFront
- pushBack
- popFront
- popBack
- isEmpty
- topFront
- topBack
- size
- find
- erase
- addBefore
- addAfter

Nota: se usa abstraccion Position<T> para las operaciones basadas en referencia.

### Stack

Implementacion: ArrayStack sobre DynamicArray.

Metodos:

- push
- pop
- peek
- isEmpty
- size
- delete

### Queue

Implementacion: CircularArrayQueue con resize dinamico.

Metodos:

- enqueue
- dequeue
- front
- isEmpty
- size
- delete

## Benchmarking actual

El paquete benchmark contiene:

- BenchmarkRunner
- BenchmarkStats
- ListBenchmark
- StackBenchmark
- QueueBenchmark

### Configuracion por propiedades JVM

- benchmark.warmup (default: 1)
- benchmark.repetitions (default: 2)
- benchmark.include10pow8 (default: false)

### Tamanos base actuales

Con benchmark.include10pow8=false:

- 10, 100, 1_000, 10_000, 100_000, 1_000_000

Con benchmark.include10pow8=true:

- 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000

### Formato de CSV

Los benchmarks guardan:

size,avg_time_ns,median_ns,min_ns,max_ns

## Estructura real del proyecto

```
Implementacion-y-Analisis-de-Complejidad-de-Listas-Enlazadas-grupo-7/
├── README.md
├── data/
│   ├── data-list/
│   │   ├── list-singly/
│   │   │   ├── no-tail/
│   │   │   └── whit-tail/
│   │   └── list-doubly/
│   │       ├── no-tail/
│   │       └── whit-tail/
│   ├── data-stack/
│   └── data-queue/
├── docs/
│   └── graficas/
├── scripts/
│   ├── graficar_resultados.py
│   ├── graficar_comparacion.py
│   └── graficar_avanzadas.py
├── src/main/java/
│   ├── Main.java
│   ├── benchmark/
│   ├── list/
│   ├── stack/
│   ├── queue/
│   └── utils/
└── tests/
```

## Ejecucion

### Compilar

PowerShell:

```
$files = Get-ChildItem -Path src/main/java -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
& 'F:\Eclipse Adoptium\bin\javac.exe' -d out $files
```

### Ejecutar suite completa

```
& 'F:\Eclipse Adoptium\bin\java.exe' -cp out Main
```

### Ejecutar benchmark selectivo

Stack:

```
& 'F:\Eclipse Adoptium\bin\java.exe' -cp out Main stack push
```

Queue:

```
& 'F:\Eclipse Adoptium\bin\java.exe' -cp out Main queue delete
```

List:

```
& 'F:\Eclipse Adoptium\bin\java.exe' -cp out Main list singly push_front
```

## Graficas

### Scripts disponibles

- scripts/graficar_resultados.py
- scripts/graficar_comparacion.py
- scripts/graficar_avanzadas.py

### Tipos de salida

- individuales
- comparaciones
- avanzadas (pendiente log-log, speedup tail/no-tail, heatmaps y lineas por operacion)

## Notas internas por paquete

Documentacion tecnica adicional:

- src/main/java/list/NotasInternasListas.md
- src/main/java/benchmark/README.md
- src/main/java/queue/README.md
- src/main/java/stack/README.md
- src/main/java/utils/README.md

## Integrantes

- Egovail Cardozo Juan Daniel
- Romero Villalba Jean Pierre
- Toro Moreno Kevin Andres
