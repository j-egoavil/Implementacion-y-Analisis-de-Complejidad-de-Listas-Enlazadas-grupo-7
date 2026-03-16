package list;

public class DoublyLinkedList<T> implements ListADT<T>{
    private Node<T> head;
    private int size;

    public DoublyLinkedList(){
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
        Node<T> newNode = new Node<>(value);

        if(isEmpty()){
            head = newNode;
            size++;
            return;
        }

        Node<T> aux = head;
        while(aux.next != null){
            aux = aux.next;
        }
        newNode.prev = aux;
        aux.next = newNode;
        size++;
    }

    @Override
    public T popFront() {
        if(isEmpty()) throw new IllegalStateException("The list is empty.");

        T deleted = head.value;
        if(head.next == null){
            head = null;
            size--;
            return deleted;
        }

        head = head.next;
        head.prev = null;
        size--;
        return deleted;
    }

    @Override
    public T popBack() {
        if(isEmpty()) throw new IllegalStateException("The list is empty.");

        if(head.next == null){
            T deleted = head.value;
            head = null;
            size--;
            return deleted;
        }

        Node<T> aux = head;
        while(aux.next != null){
            aux = aux.next;
        }
        T deleted = aux.value;
        aux.prev.next = null;
        aux.prev = null;
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
        if(isEmpty()) throw new IllegalStateException("The list is Empty.");

        Node<T> aux = head;
        while (aux.next != null){
            aux = aux.next;
        }
        return aux.value;
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
        if(target == head){
            if(target.next == null){
                head = null;
                size--;
                target.prev = null;
                return;
            }
            head = head.next;
            head.prev = null;
            size--;
            target.prev = null;
            target.next = null;
            return;
        }

        if(target.next == null){
            target.prev.next = null;
            size--;
            target.prev = null;
            return;
        }

        target.prev.next = target.next;
        target.next.prev = target.prev;
        target.prev = null;
        target.next = null;
        size--;
    }

    @Override
    public void addBefore(Position<T> position, T value) {
        if(position == null) throw new IllegalArgumentException("Position can not be null.");

        Node<T> newNode = new Node<>(value);
        Node<T> target = (Node<T>) position;

        if(target == head){
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
            size++;
            return;
        }

        target.prev.next = newNode;
        newNode.prev = target.prev;
        newNode.next = target;
        target.prev = newNode;
        size++;
    }

    @Override
    public void addAfter(Position<T> position, T value) {
        if(position == null) throw new IllegalArgumentException("Position can not be null.");

        Node<T> newNode = new Node<>(value);
        Node<T> target = (Node<T>) position;

        if(target.next == null){
            newNode.prev = target;
            target.next = newNode;
            size++;
            return;
        }

        newNode.next = target.next;
        newNode.prev = target;
        target.next.prev = newNode;
        target.next = newNode;
        size++;
    }
}