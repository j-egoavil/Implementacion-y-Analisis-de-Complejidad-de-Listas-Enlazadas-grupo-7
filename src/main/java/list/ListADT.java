package list;

public interface ListADT<T>{
    void pushFront(T value);
    void pushBack(T value);
    T popFront();
    T popBack();
    boolean isEmpty();
    T topFront();
    T topBack();
    int size();
    Position<T> find(T value);
    void erase(Position<T> position);
    void addBefore(Position<T> position, T value);
    void addAfter(Position<T> position, T value);
}
