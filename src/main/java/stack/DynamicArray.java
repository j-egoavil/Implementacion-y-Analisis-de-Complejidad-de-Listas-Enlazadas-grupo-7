package stack;

public class DynamicArray<T> {

    private T[] data;
    private int size;
    private int capacity;

    public DynamicArray() {
        capacity = 10;
        data = (T[]) new Object[capacity];
        size = 0;
    }

    public void add(T value) {
        if (size == capacity) {
            resize();
        }
        data[size++] = value;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return data[index];
    }

    public void set(int index, T value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        data[index] = value;
    }

    public T removeLast() {
        if (size == 0) {
            throw new RuntimeException("Array vacío");
        }

        T value = data[size - 1];
        data[size - 1] = null;
        size--;
        return value;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void resize() {
        capacity *= 2;
        T[] newData = (T[]) new Object[capacity];

        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }

        data = newData;
    }
}
