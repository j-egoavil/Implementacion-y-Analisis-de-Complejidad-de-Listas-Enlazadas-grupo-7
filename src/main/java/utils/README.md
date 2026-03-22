# Notas internas - Utils

## Objetivo del paquete
Este paquete concentra utilidades compartidas por benchmarks y estructuras.

Componentes actuales:

- Timer
- CSVWriter

## Timer
Timer mide duracion en nanosegundos usando System.nanoTime().

Metodo:

- measure(Runnable task): ejecuta task y retorna tiempo total en ns.

Uso tipico:

- envolver la porcion exacta que se desea medir
- evitar incluir inicializaciones externas si no forman parte del experimento

## CSVWriter
CSVWriter simplifica la escritura de resultados a disco.

Caracteristicas:

- crea carpetas padre automaticamente si no existen
- permite cabecera personalizada
- soporta escritura simple (size,time)
- soporta escritura de estadisticos via BenchmarkStats

Metodos clave:

- write(int size, long time)
- writeStats(int size, BenchmarkStats stats)
- writeRaw(String row)
- close()

## Formato recomendado de benchmark
En este proyecto, el formato estandar para resultados medidos es:

size,avg_time_ns,median_ns,min_ns,max_ns

Esto permite que los scripts de graficacion funcionen de manera uniforme.
