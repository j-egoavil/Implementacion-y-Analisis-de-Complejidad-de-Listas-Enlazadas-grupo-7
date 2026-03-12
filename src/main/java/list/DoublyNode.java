package list;

public class DoublyNode<T> implements Position<T>{
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

    public void setValue(T value) {
        this.value = value;
    }

    public DoublyNode<T> getNext() {
        return next;
    }

    public void setNext(DoublyNode<T> next) {
        this.next = next;
    }

    public DoublyNode<T> getPrev() {
        return prev;
    }

    public void setPrev(DoublyNode<T> prev) {
        this.prev = prev;
    }
}
