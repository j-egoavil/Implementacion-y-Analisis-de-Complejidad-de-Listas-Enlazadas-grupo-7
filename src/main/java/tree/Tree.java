package tree;

import list.SinglyLinkedList;

public interface Tree<T extends Comparable<T>> {

    boolean contains(T value);

    void insert(T value);

    boolean remove(T value);

    int size();

    SinglyLinkedList<T> inOrder();
}
