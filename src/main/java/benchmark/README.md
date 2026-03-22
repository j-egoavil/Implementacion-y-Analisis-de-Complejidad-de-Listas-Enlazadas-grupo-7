# Notas internas - Benchmark

## Objetivo del paquete
Este paquete centraliza la logica de medicion experimental para:

- listas enlazadas
- pilas
- colas

La salida de cada benchmark se guarda en CSV para analisis posterior y generacion de graficas.

## Clases principales

- BenchmarkRunner: ejecuta warmup y repeticiones medidas, y calcula estadisticos.
- BenchmarkStats: contenedor de promedio, mediana, minimo y maximo.
- ListBenchmark: corre operaciones para 4 implementaciones de lista.
- StackBenchmark: corre operaciones de stack.
- QueueBenchmark: corre operaciones de queue.

## Configuracion de ejecucion
BenchmarkRunner usa propiedades del sistema para controlar la corrida:

- benchmark.warmup: cantidad de ejecuciones de calentamiento.
- benchmark.repetitions: cantidad de ejecuciones medidas.
- benchmark.include10pow8: habilita tamanos 10^7 y 10^8 cuando es true.

Valores por defecto actuales:

- warmup = 3
- repetitions = 5
- include10pow8 = false

En graficas se recomienda usar median_ns como metrica principal para reducir el impacto de outliers.

## Conjunto de tamanos
Cuando include10pow8 es false, la base de tamanos es:

- 10, 100, 1_000, 10_000, 100_000, 1_000_000

ListBenchmark tambien define tamanos reducidos para operaciones mas costosas,
para evitar tiempos de ejecucion excesivos en metodos con crecimiento alto.

## Criterio de medicion
Cada operacion se mide con Timer.measure y reporta:

- avg_time_ns
- median_ns
- min_ns
- max_ns

Los CSV usan encabezado:

size,avg_time_ns,median_ns,min_ns,max_ns

## Rutas de salida de datos

- stack: data/data-stack/stack_<operacion>.csv
- queue: data/data-queue/queue_<operacion>.csv
- list singly no-tail: data/data-list/list-singly/no-tail/
- list singly tail: data/data-list/list-singly/whit-tail/
- list doubly no-tail: data/data-list/list-doubly/no-tail/
- list doubly tail: data/data-list/list-doubly/whit-tail/

## Ejecucion parcial por operacion
Main permite ejecutar benchmarks puntuales:

- stack: Main stack <operacion>
- queue: Main queue <operacion>
- list: Main list <implementacion> <operacion>

Esto permite regenerar solo CSV faltantes sin volver a ejecutar todo el suite.
