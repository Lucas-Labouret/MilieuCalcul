package local.furthestpointoptimization.model.misc;

public class LinkedList<T> {
    public Node<T> head;

    public void addFirst(T t) {
        Node<T> node = new Node<>(t);
        node.next = head;
        head = node;
    }

    public T get(int index) {
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;
    }

    public boolean contains(T vertex) {
        Node<T> current = head;
        while (current != null) {
            if (current.value.equals(vertex)) return true;
            current = current.next;
        }
        return false;
    }
}
