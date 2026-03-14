package list;

public class DoublyLinkedListTail<T> implements ListADT<T>{
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public DoublyLinkedListTail(){
        this.size = 0;
    }

    private static class Node<T> implements Position<T>{
        private T value;
        private Node<T> next;
        private Node<T> prev;

        public Node(T value){
            this.value = value;
            this.next = null;
            this.prev = null;
        }

        @Override
        public T getValue() {
            return value;
        }
    }

    @Override
    public void pushFront(T value) {
        Node<T> newNode = new Node<>(value);

        if(isEmpty()){
            head = newNode;
            tail = newNode;
            size++;
            return;
        }

        newNode.next = head;
        head.prev = newNode;
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
        if(isEmpty()) throw new IllegalStateException("The list is empty.");

        return head.value;
    }

    @Override
    public T topBack() {
        if(isEmpty()) throw new IllegalStateException("The list is empty.");

        return tail.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Position<T> find(T value) {
        Node<T> aux = head;
        while(aux != null){
            if(aux.value.equals(value)){
                return aux;
            }
            aux = aux.next;
        }
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