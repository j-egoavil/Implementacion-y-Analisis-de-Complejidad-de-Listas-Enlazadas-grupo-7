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
        Node<T> newNode = new Node<>(value);

        if(head == null){
            head = newNode;
            size++;
            return;
        }

        Node<T> aux = head;
        while(aux.next != null){
            aux = aux.next;
        }

        aux.next = newNode;
        newNode.next = null;
        size++;
    }

    @Override
    public T popFront() {
        if(isEmpty()) throw new IllegalStateException("The list is empty.");

        T deleted = head.value;
        head = head.next;
        size--;
        return deleted;
    }

    @Override
    public T popBack() {
        if(isEmpty()) throw new IllegalStateException("The list is empty.");

        if (head.next == null) {
            T deleted = head.value;
            head = null;
            size--;
            return deleted;
        }

        Node<T> aux = head;
        while(aux.next.next != null){
            aux = aux.next;
        }

        T deleted = aux.next.value;
        aux.next = null;
        size--;
        return deleted;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public T topFront() {
        if(isEmpty()) throw new IllegalStateException("The list is Empty.");

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
            head = head.next;
            size--;
            return;
        }

        Node<T> aux = head;
        while (aux.next != null && aux.next != target){
            aux = aux.next;
        }
        if(aux.next == null) throw new IllegalArgumentException("The position does not exist in the list.");

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

        while (aux.next != null && aux.next != target ){
            aux = aux.next;
        }
        if(aux.next == null) throw new IllegalArgumentException("The position does not exist in the list.");

        newNode.next = target;
        aux.next =newNode;
        size++;
    }

    @Override
    public void addAfter(Position<T> position, T value) {
        if(position == null) throw new IllegalArgumentException("Position can not be null.");

        Node<T> newNode = new Node<>(value);
        Node<T> target = (Node<T>) position;

        Node<T> aux = head;
        while (aux != null && aux != target ){
            aux = aux.next;
        }
        if(aux == null) throw new IllegalArgumentException("The position does not exist in the list.");

        newNode.next = target.next;
        target.next = newNode;
        size++;
    }
}