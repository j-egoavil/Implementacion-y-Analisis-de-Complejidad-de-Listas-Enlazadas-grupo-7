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
}
