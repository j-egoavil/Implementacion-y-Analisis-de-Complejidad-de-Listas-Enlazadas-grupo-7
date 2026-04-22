package tree;

import list.SinglyLinkedList;

public class BstTree<T extends Comparable<T>> implements Tree<T> {

    private Node root;
    private int size;

    private class Node {
        T value;
        Node left;
        Node right;

        Node(T value) {
            this.value = value;
        }
    }

    public BstTree() {
        root = null;
        size = 0;
    }

    @Override
    public void insert(T value) {
        root = insert(root, value);
    }

    private Node insert(Node node, T value) {
        if (node == null) {
            size++;
            return new Node(value);
        }

        int cmp = value.compareTo(node.value);

        if (cmp < 0) {
            node.left = insert(node.left, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, value);
        }

        return node;
    }

    @Override
    public boolean remove(T value) {
        if (!contains(value)) return false;
        root = remove(root, value);
        size--;
        return true;
    }

    private Node remove(Node node, T value) {
        if (node == null) return null;

        int cmp = value.compareTo(node.value);

        if (cmp < 0) {
            node.left = remove(node.left, value);
        } else if (cmp > 0) {
            node.right = remove(node.right, value);
        } else {
            // caso 1 y 2
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            // caso 3 (dos hijos)
            Node successor = getMin(node.right);
            node.value = successor.value;
            node.right = remove(node.right, successor.value);
        }

        return node;
    }

    private Node getMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    public boolean contains(T value) {
        return contains(root, value);
    }

    private boolean contains(Node node, T value) {
        if (node == null) return false;

        int cmp = value.compareTo(node.value);

        if (cmp == 0) return true;
        else if (cmp < 0) return contains(node.left, value);
        else return contains(node.right, value);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public SinglyLinkedList<T> inOrder() {
        SinglyLinkedList<T> list = new SinglyLinkedList<>();
        inOrder(root, list);
        return list;
    }

    private void inOrder(Node node, SinglyLinkedList<T> list) {
        if (node == null) return;

        inOrder(node.left, list);
        list.pushBack(node.value);
        inOrder(node.right, list);
    }
}
