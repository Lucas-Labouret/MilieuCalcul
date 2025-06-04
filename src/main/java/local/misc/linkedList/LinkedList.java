package local.misc.linkedList;

import java.util.Iterator;

/**
 * A simple implementation of a singly linked list.<br>
 * Java default LinkedList is not used because it does not provide access in O(1) time to the nodes even when their predecessors are known.
 *
 * @param <T> the type of elements in this list
 */
public class LinkedList<T> implements Iterable<T> {
    public Node<T> head; // The first node in the linked list
    private int size = 0;

    /** Add t as the first element in the linked list. */
    public void addFirst(T t) {
        Node<T> node = new Node<>(t);
        node.next = head;
        head = node;
        size++;
    }

    /**
     * @param index the index of the element to retrieve
     * @return the element at the specified index
     */
    public T get(int index) {
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;
    }

    /** @return true if the list contains t, false otherwise */
    public boolean contains(T t) {
        Node<T> current = head;
        while (current != null) {
            if (current.value.equals(t)) return true;
            current = current.next;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                T value = current.value;
                current = current.next;
                return value;
            }
        };
    }
}
