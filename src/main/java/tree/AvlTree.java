package tree;

import list.SinglyLinkedList;

public class AvlTree<T extends Comparable<T>> implements Tree<T> {

    private Node root;
    private int size;

    private class Node {
        T value;
        Node left;
        Node right;
        int height;

        Node(T value) {
            this.value = value;
            this.height = 1;
        }
    }

    public AvlTree() {
        root = null;
        size = 0;
    }

    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = 1 + Math.max(height(y.left), height(y.right));
        x.height = 1 + Math.max(height(x.left), height(x.right));

        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));

        return y;
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
        } else {
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // LL
        if (balance > 1 && value.compareTo(node.left.value) < 0) {
            return rotateRight(node);
        }

        // RR
        if (balance < -1 && value.compareTo(node.right.value) > 0) {
            return rotateLeft(node);
        }

        // LR
        if (balance > 1 && value.compareTo(node.left.value) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // RL
        if (balance < -1 && value.compareTo(node.right.value) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
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
            // nodo con 1 o ningún hijo
            if (node.left == null || node.right == null) {
                Node temp = (node.left != null) ? node.left : node.right;

                if (temp == null) {
                    return null;
                } else {
                    node = temp;
                }
            } else {
                Node successor = getMin(node.right);
                node.value = successor.value;
                node.right = remove(node.right, successor.value);
            }
        }

        if (node == null) return null;

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // LL
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        // LR
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // RR
        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        // RL
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
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
