# Implementación y Análisis de Complejidad de Listas, Pilas y Colas en Java

## Descripción

Este proyecto corresponde a la tarea del curso **Estructuras de Datos (2026-I)**.
El objetivo es implementar distintas estructuras de datos fundamentales en **Java** y realizar un **análisis teórico y experimental de su complejidad**.

Las estructuras implementadas son:

* **Listas enlazadas**

  * Lista simplemente enlazada
  * Lista simplemente enlazada con cola
  * Lista doblemente enlazada
  * Lista doblemente enlazada con cola

* **Pilas (Stack)** implementadas con **arreglos dinámicos**

* **Colas (Queue)** implementadas con **arreglos circulares**

Además, se realizan **pruebas experimentales de rendimiento** para comparar las implementaciones y verificar su complejidad utilizando **notación Big-O**.

---

# Objetivos

* Implementar estructuras de datos fundamentales sin utilizar librerías nativas de Java.
* Analizar la complejidad teórica de los métodos principales.
* Medir experimentalmente el tiempo de ejecución con distintos tamaños de entrada.
* Comparar los resultados experimentales con la complejidad teórica.

---

# Estructura del Proyecto

```
data-structures-analysis/
│
├─ README.md
├─ .gitignore
│
├── docs/
│   ├── informe.pdf
│   ├── graficas/
│   └── tablas/
│
├── data/
│   ├── list/
│   ├── stack/
│   └── queue/
│
├── scripts/
│   ├── graficar_resultados.py
│   └── procesar_datos.py
│
├── src/
│   └── main/
│       └── java/
│           │
│           ├── Main.java
│           │
│           ├── list/
│           │   ├── Node.java
│           │   ├── SinglyLinkedList.java
│           │   ├── SinglyLinkedListWithTail.java
│           │   ├── DoublyLinkedList.java
│           │   └── DoublyLinkedListWithTail.java
│           │
│           ├── stack/
│           │   ├── MyStack.java
│           │   └── ArrayStack.java
│           │
│           ├── queue/
│           │   ├── MyQueue.java
│           │   └── CircularArrayQueue.java
│           │
│           ├── benchmark/
│           │   ├── ListBenchmark.java
│           │   ├── StackBenchmark.java
│           │   └── QueueBenchmark.java
│           │
│           └── utils/
│               ├── Timer.java
│               └── RandomGenerator.java
│
└── tests/
    ├── TestList.java
    ├── TestStack.java
    └── TestQueue.java
```

---

# Estructuras Implementadas

## List

Métodos implementados:

* `pushFront()`
* `pushBack()`
* `popFront()`
* `popBack()`
* `find()`
* `erase()`
* `addBefore()`
* `addAfter()`
* `isEmpty()`

Se implementaron **cuatro variantes de listas enlazadas** para comparar su eficiencia.

---

## Stack (MyStack<T>)

Implementación basada en **arreglos dinámicos**.

Métodos:

* `push(T x)`
* `pop()`
* `peek()`
* `isEmpty()`
* `size()`
* `delete(T n)`

---

## Queue (MyQueue<T>)

Implementación basada en **arreglo circular dinámico**.

Métodos:

* `enqueue(T x)`
* `dequeue()`
* `front()`
* `isEmpty()`
* `size()`
* `delete(T n)`

---

# Medición de Complejidad

Para evaluar el rendimiento de cada estructura se realizan pruebas con distintos tamaños de entrada:

```
10
100
10^4
10^6
10^8
```

El tiempo de ejecución se mide utilizando **nanosegundos** mediante:

```java
System.nanoTime();
```

Los resultados se almacenan en archivos dentro de la carpeta:

```
data/
```

---

# Visualización de Resultados

Los datos obtenidos se grafican usando **Python y Matplotlib**.

Las gráficas muestran:

```
Tiempo de ejecución vs Tamaño de entrada
```

Estas gráficas permiten comparar:

* Implementaciones de listas
* Stack vs Queue
* Métodos equivalentes entre estructuras

---

# Ejecución del Proyecto

Compilar el proyecto:

```
javac src/main/java/**/*.java
```

Ejecutar el programa principal:

```
java src.main.java.Main
```

Esto ejecutará las pruebas de rendimiento y generará los datos experimentales.

---

## Integrantes
- Egovail Cardozo Juan Daniel
- Romero Villalba Jean Pierre
- Toro Moreno Kevin Andrés 

---

# Conclusiones Esperadas

A partir del análisis experimental se busca:

* Identificar bajo qué condiciones es mejor usar **listas enlazadas o arreglos dinámicos**.
* Comparar el comportamiento de **Stack y Queue** frente a diferentes tamaños de entrada.
* Verificar si los resultados experimentales coinciden con la **complejidad teórica Big-O**.

---
