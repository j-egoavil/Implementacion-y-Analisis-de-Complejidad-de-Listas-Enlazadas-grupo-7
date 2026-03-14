package list;

public class SinglyLinkedList<T> implements ListADT<T> {
    private Node<T> head;
    private int size;

    public SinglyLinkedList(){
        this.size = 0;
    }

    private static class Node<T> implements Position<T>{
        private T value;
        private Node<T> next;

        public Node(T value){
            this.value = value;
        }

        @Override
        public T getValue() {
            return value;
        }
    }

    @Override
    public void pushFront(T value) {
        Node<T> newNode = new Node<>(value);
        newNode.next = head;
        head = newNode;
        size++;
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
        return size == 0;
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
        return size;
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