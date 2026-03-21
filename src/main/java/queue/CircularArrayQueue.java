package queue;

public class CircularArrayQueue<T> implements MyQueue<T> {

    private T[] data;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public CircularArrayQueue() {
        capacity = 10;
        data = (T[]) new Object[capacity];
        front = 0;
        rear = 0;
        size = 0;
    }

    @Override
    public void enqueue(T x) {
        if (size == capacity) {
            resize();
        }

        data[rear] = x;
        rear = (rear + 1) % capacity;
        size++;
    }

    @Override
    public T dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("Queue vacía");
        }

        T value = data[front];
        data[front] = null;
        front = (front + 1) % capacity;
        size--;

        return value;
    }

    @Override
    public T front() {
        if (isEmpty()) {
            throw new RuntimeException("Queue vacía");
        }
        return data[front];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    private void resize() {
        capacity *= 2;
        T[] newData = (T[]) new Object[capacity];

        for (int i = 0; i < size; i++) {
            newData[i] = data[(front + i) % capacity];
        }

        data = newData;
        front = 0;
        rear = size;
    }

    @Override
    public void delete(T n) {
        int currentSize = size;

        for (int i = 0; i < currentSize; i++) {
            T value = dequeue();

            if (!value.equals(n)) {
                enqueue(value);
            }
        }
    }
}
