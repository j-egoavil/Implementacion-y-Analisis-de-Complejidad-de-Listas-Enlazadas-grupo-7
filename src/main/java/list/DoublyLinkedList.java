package list;

public class DoublyLinkedList<T> implements ListADT<T>{

    private static class DoublyNode<T> implements Position<T>{
        private T value;
        private DoublyNode<T> next;
        private DoublyNode<T> prev;

        public DoublyNode(T value){
            this.value = value;
            this.next = null;
            this.prev = null;
        }

        @Override
        public T getValue() {
            return null;
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