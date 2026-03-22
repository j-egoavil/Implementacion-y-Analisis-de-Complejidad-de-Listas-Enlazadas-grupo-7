# Notas internas - Queue

## Implementacion actual
La cola se implementa en CircularArrayQueue<T> usando arreglo circular dinamico.

Interfaz expuesta por MyQueue<T>:

- enqueue(T x)
- dequeue()
- front()
- isEmpty()
- size()
- delete(T n)

## Invariantes de estado
CircularArrayQueue mantiene:

- data: arreglo base
- front: indice del primer elemento valido
- rear: indice de insercion del siguiente enqueue
- size: cantidad de elementos actuales
- capacity: capacidad del arreglo

## Complejidad esperada

- enqueue: O(1) amortizado, O(n) cuando hay resize
- dequeue: O(1)
- front: O(1)
- size: O(1)
- delete(T n): O(n) en el peor caso

## Comportamiento de delete(T n)
La implementacion actual elimina la primera ocurrencia del valor n y preserva el
orden relativo del resto de elementos.

Flujo resumido:

1. recorre currentSize elementos haciendo dequeue
2. al encontrar la primera coincidencia, la omite
3. reencola los demas elementos

## Resize
Cuando size == capacity, se duplica la capacidad y se copian elementos al nuevo
arreglo manteniendo el orden logico de la cola.

## Uso en benchmark
QueueBenchmark mide:

- enqueue
- dequeue
- front
- delete

Los tiempos se guardan en data/data-queue.
