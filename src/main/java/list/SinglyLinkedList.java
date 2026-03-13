package list;

public class SinglyLinkedList<T> implements ListADT<T> {
    private SinglyNode<T> next;
    private int size;

    private static class SinglyNode<T> implements Position<T>{
        private T value;
        private SinglyNode<T> next;

        public SinglyNode(T value){
            this.value = value;
        }

        @Override
        public T getValue() {
            return value;
        }
    }

    @Override
    public void pushFront(T value) {

    }

    @Override
    public void pushBack(T value) {

    }

    @Override
    public T popFront() {
        return null;
    }

    @Override
    public T popBack() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public T topFront() {
        return null;
    }

    @Override
    public T topBack() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Position<T> find(T value) {
        return null;
    }

    @Override
    public void erase(Position<T> position) {

    }

    @Override
    public void addBefore(Position<T> position, T value) {

    }

    @Override
    public void addAfter(Position<T> position, T value) {

    }
}