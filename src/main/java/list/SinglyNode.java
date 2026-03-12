package list;

public class SinglyNode<T> implements Position<T>{
    T value;
    SinglyNode<T> next;

    public SinglyNode(T value){
        this.value = value;
    }

    @Override
    public T getValue() {
        return null;
    }

    public SinglyNode<T> getNext() {
        return next;
    }

    public void setNext(SinglyNode<T> next) {
        this.next = next;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
