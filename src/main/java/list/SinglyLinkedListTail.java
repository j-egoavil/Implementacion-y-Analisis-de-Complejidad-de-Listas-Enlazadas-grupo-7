package list;

public class SinglyLinkedListTail<T>  implements ListADT<T>{
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public SinglyLinkedListTail(){
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

        if(isEmpty()){
            head = newNode;
            tail = newNode;
            size++;
            return;
        }
        Node<T> oldHead = head;
        head = newNode;
        head.next = oldHead;
        size++;
    }

    @Override
    public void pushBack(T value) {
        Node<T> newNode = new Node<>(value);

        if(isEmpty()){
            head = newNode;
            tail = newNode;
            size++;
            return;
        }
        Node<T> oldTail = tail;
        tail = newNode;
        oldTail.next = tail;
        size++;
    }

    @Override
    public T popFront() {
        if(isEmpty()) throw new IllegalStateException("The list is empty.");

        if(head == tail){
            T deleted = head.value;
            head = null;
            tail = null;
            size--;
            return deleted;
        }

        T deleted = head.value;
        head = head.next;
        size--;
        return deleted;
    }

    @Override
    public T popBack() {
        if(isEmpty()) throw new IllegalStateException("The list is empty.");

        if(head == tail){
            T deleted = head.value;
            head = null;
            tail = null;
            size--;
            return deleted;
        }

        T deleted = tail.value;
        Node<T> aux = head;
        while (aux.next.next != null){
            aux = aux.next;
        }
        tail = aux;
        tail.next = null;
        size--;
        return deleted;
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