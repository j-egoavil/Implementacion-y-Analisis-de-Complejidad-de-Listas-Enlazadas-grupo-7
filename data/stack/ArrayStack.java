public class ArrayStack<T> implements MyStack<T> {

    private DynamicArray<T> array;

    public ArrayStack() {
        array = new DynamicArray<>();
    }

    @Override
    public void push(T x) {
        array.add(x);
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("Stack vacío");
        }
        return array.removeLast();
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Stack vacío");
        }
        return array.get(array.size() - 1);
    }

    @Override
    public boolean isEmpty() {
        return array.isEmpty();
    }

    @Override
    public int size() {
        return array.size();
    }

    @Override
    public void delete(T n) {
        DynamicArray<T> temp = new DynamicArray<>();

        // sacar elementos hasta encontrar n
        while (!isEmpty()) {
            T value = pop();

            if (value.equals(n)) {
                break;
            }

            temp.add(value);
        }

        // devolver los elementos al stack
        for (int i = temp.size() - 1; i >= 0; i--) {
            push(temp.get(i));
        }
    }
}
