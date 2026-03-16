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
        newNode.next = head;
        head = newNode;
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
        tail.next = newNode;
        tail = newNode;
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
        if(position == null) throw new IllegalArgumentException("Position can not be null.");

        Node<T> target = (Node<T>) position;
        if(head == tail && target == head){
            head = null;
            tail = null;
            size--;
            return;
        }
        if(target == head){
            head = head.next;
            size--;
            return;
        }

        Node<T> aux = head;
        while (aux.next != target){
            aux = aux.next;
        }

        if(target == tail){
            tail = aux;
            aux.next = target.next;
            size--;
            target.next = null;
            return;
        }

        aux.next = aux.next.next;
        size--;
        target.next = null;
    }

    @Override
    public void addBefore(Position<T> position, T value) {
        if(position == null) throw new IllegalArgumentException("Position can not be null.");

        Node<T> newNode = new Node<>(value);
        Node<T> target = (Node<T>) position;
        if(target == head){
            newNode.next = head;
            head = newNode;
            size++;
            return;
        }

        Node<T> aux = head;
        while (aux.next != target ){
            aux = aux.next;
        }

        newNode.next = target;
        aux.next =newNode;
        size++;
    }

    @Override
    public void addAfter(Position<T> position, T value) {
        if (position == null) {
            throw new IllegalArgumentException("Position can not be null.");
        }

        Node<T> newNode = new Node<>(value);
        Node<T> target = (Node<T>) position;

        newNode.next = target.next;
        target.next = newNode;

        if (target == tail) {
            tail = newNode;
        }
        size++;
    }
}