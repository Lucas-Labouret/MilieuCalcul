package local.misc.linkedList;

/**
 * A simple Node class for a singly linked list.
 *
 * @param <T> the type of value stored in the node
 */
public class Node<T> {
    public Node<T> next = null;
    public T value;

    public Node(T value) {
        this.value = value;
    }

    /** Removes the next node in the list and returns its value. */
    public T removeNext() {
        T value = next.value;
        next = next.next;
        return value;
    }

    /** Adds a new node with the given value after this node. */
    public void addNext(T value) {
        Node<T> node = new Node<>(value);
        node.next = next;
        next = node;
    }
}
