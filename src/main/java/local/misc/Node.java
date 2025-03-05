package local.misc;

public class Node<T> {
    public Node<T> next = null;
    public T value;

    public Node(T value) {
        this.value = value;
    }

    public T removeNext() {
        T value = next.value;
        next = next.next;
        return value;
    }

    public void addNext(T value) {
        Node<T> node = new Node<>(value);
        node.next = next;
        next = node;
    }
}
