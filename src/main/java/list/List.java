package list;

public interface List <T>{
    void pushFront(T value);
    void pushBack(T value);
    T popFront();
    T popBack();
    boolean empty();
    T topFront();
    T topBack();
    int size();

    Position<T> find(T value);
    void erase(Position<T> position);
    void addBefore(Position<T> position, T value);
    void addAfter(Position<T> position, T value);
}
