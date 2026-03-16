# Notas internas - Implementaciones de listas enlazadas

## Implementaciones actuales
Se están trabajando las siguientes variantes:

- `SinglyLinkedList<T>`
- `SinglyLinkedListTail<T>`
- `DoublyLinkedList<T>`
- `DoublyLinkedListTail<T>`

## Decisión de diseño adoptada
Se continuará con el diseño actual basado en la abstracción `Position<T>`.

Esto significa que internamente cada lista tiene su propio `Node`, pero hacia afuera la referencia al elemento encontrado se expone como `Position<T>`.

La motivación de esta decisión es que el enunciado del taller, en el método `Find`, pide explícitamente **retornar la referencia del elemento en la lista**.

## Aclaración del monitor

El monitor confirmó que esta interpretación es válida.

Puntos importantes de su respuesta:

- `find` puede implementarse retornando el nodo completo encontrado.
- `find` debe retornar el **primer valor igual** al buscado.
- También era válida una solución basada en buscar por valor, pero se decidió mantener la versión basada en referencia (`Position<T>`).
- Para benchmarks, en operaciones como `erase`, `addBefore` y `addAfter`, **el tiempo medido debe incluir también el costo de encontrar el valor o nodo objetivo**, no solo el tiempo de cambiar referencias.

## Contrato actual de la interfaz
La interfaz usada actualmente es:

- `void pushFront(T value)`
- `void pushBack(T value)`
- `T popFront()`
- `T popBack()`
- `boolean isEmpty()`
- `T topFront()`
- `T topBack()`
- `int size()`
- `Position<T> find(T value)`
- `void erase(Position<T> position)`
- `void addBefore(Position<T> position, T value)`
- `void addAfter(Position<T> position, T value)`

## Significado de cada método relevante para benchmarks

### `find(T value)`
**Recibe:** un valor de tipo `T`  
**Retorna:** un `Position<T>` correspondiente al **primer nodo** cuyo valor sea igual al buscado.  
**Si no encuentra:** retorna `null`.

### `erase(Position<T> position)`
**Recibe:** una referencia a una posición/nodo previamente obtenida.  
**Retorna:** nada (`void`).  
**Comportamiento esperado:** elimina de la lista el nodo indicado por esa posición.

### `addBefore(Position<T> position, T value)`
**Recibe:** una referencia a una posición/nodo y un nuevo valor.  
**Retorna:** nada (`void`).  
**Comportamiento esperado:** inserta un nuevo nodo **antes** de la posición indicada.

### `addAfter(Position<T> position, T value)`
**Recibe:** una referencia a una posición/nodo y un nuevo valor.  
**Retorna:** nada (`void`).  
**Comportamiento esperado:** inserta un nuevo nodo **después** de la posición indicada.

## Implicación práctica para benchmarks
Como se adoptó el diseño con `Position<T>`, el flujo esperado para medir operaciones como `erase`, `addBefore` y `addAfter` es:

1. elegir un valor objetivo
2. buscarlo con `find(value)`
3. si `find` retorna una posición válida, ejecutar la operación correspondiente

Ejemplo conceptual:

- generar un entero aleatorio dentro del rango cargado en la lista
- hacer `find(valorAleatorio)`
- usar la posición retornada en `erase`, `addBefore` o `addAfter`

**Importante:** el tiempo total de benchmark debe contemplar tanto la búsqueda como la modificación.

## Estado actual de implementación
### Implementación finalizada