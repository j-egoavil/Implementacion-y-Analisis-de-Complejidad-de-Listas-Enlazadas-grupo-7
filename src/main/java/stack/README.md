# Notas internas - Stack

## Implementacion actual
ArrayStack<T> implementa MyStack<T> sobre DynamicArray<T>.

Metodos de la interfaz:

- push(T x)
- pop()
- peek()
- isEmpty()
- size()
- delete(T n)

## Comportamiento por metodo

- push: agrega al final del arreglo dinamico
- pop: remueve y retorna el ultimo elemento
- peek: retorna el ultimo elemento sin remover
- isEmpty: verifica si size es 0
- size: retorna cantidad de elementos actual

## Complejidad esperada

- push: O(1) amortizado
- pop: O(1)
- peek: O(1)
- size: O(1)
- delete(T n): O(n) en el peor caso

## Logica de delete(T n)
Se usa una estructura temporal para retirar elementos hasta encontrar n,
y luego reconstruir el stack preservando el orden del segmento restante.

Notas:

- se detiene en la primera coincidencia encontrada desde el tope
- si no existe n, extrae y repone todo

## Uso en benchmark
StackBenchmark mide:

- push
- pop
- peek
- delete

Los datos se escriben en data/data-stack.
