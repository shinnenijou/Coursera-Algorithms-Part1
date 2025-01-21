import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private final Node emptyNode;
    private int nodeSize;

    private class DequeIterator implements Iterator<Item> {
        private Node current;

        public DequeIterator(Node node) {
            current = node;
        }

        @Override
        public boolean hasNext() {
            return current.next.value != null;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            current = current.next;
            return current.value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class Node {
        public Item value = null;
        public Node next = null;
        public Node prev = null;
    }

    private void print() {
        StringBuilder sb = new StringBuilder();
        sb.append("Current elements: ");

        for (Item i : this) {
            sb.append(i.toString());
            sb.append(" ");
        }

        sb.deleteCharAt(sb.length() - 1);

        StdOut.println(sb.toString());
    }

    // construct an empty deque
    public Deque() {
        emptyNode = new Node();
        emptyNode.prev = emptyNode;
        emptyNode.next = emptyNode;

        nodeSize = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size() == 0;
    }

    // return the number of items on the deque
    public int size() {
        return nodeSize;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null");
        }

        Node oldFirstNode = emptyNode.next;
        Node newNode = new Node();

        newNode.value = item;
        newNode.next = oldFirstNode;
        newNode.prev = emptyNode;
        emptyNode.next = newNode;
        oldFirstNode.prev = newNode;

        nodeSize++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null");
        }

        Node oldLastNode = emptyNode.prev;
        Node newNode = new Node();

        newNode.value = item;
        newNode.next = emptyNode;
        newNode.prev = oldLastNode;
        emptyNode.prev = newNode;
        oldLastNode.next = newNode;

        nodeSize++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        Node newFirstNode = emptyNode.next.next;
        Node firstNode = emptyNode.next;

        emptyNode.next = newFirstNode;
        newFirstNode.prev = emptyNode;

        nodeSize--;

        return firstNode.value;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Node newLastNode = emptyNode.prev.prev;
        Node lastNode = emptyNode.prev;

        emptyNode.prev = newLastNode;
        newLastNode.next = emptyNode;

        nodeSize--;

        return lastNode.value;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator(emptyNode);
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        Integer element;

        // Test 1
        StdOut.println("--- Test 1: Constructor ---");
        assert deque.isEmpty();
        assert deque.size() == 0;
        deque.print();
        StdOut.println("--- Test Pass ---\n");

        // Test 2
        StdOut.println("--- Test 2: addFirst ---");
        deque.addFirst(1);  // 1
        assert deque.size() == 1;
        assert !deque.isEmpty();
        deque.print();
        StdOut.println("--- Test Pass ---\n");

        // Test 3
        StdOut.println("--- Test 3: addLast ---");
        deque.addLast(2);  // 1 2
        assert deque.size() == 2;
        assert !deque.isEmpty();
        deque.print();
        StdOut.println("--- Test Pass ---\n");

        // Test 4
        StdOut.println("--- Test 4: iterator ---");
        Iterator<Integer> iterator = deque.iterator();

        element = iterator.next();
        assert element == 1;
        assert iterator.hasNext();

        element = iterator.next();
        assert element == 2;
        assert !iterator.hasNext();
        StdOut.println("--- Test Pass ---\n");


        // Test 5
        StdOut.println("--- Test 5: removeFirst ---");
        element = deque.removeFirst(); // 1
        assert deque.size() == 1;
        assert !deque.isEmpty();
        assert element == 1 : "element is now " + element + ", expected 1";
        deque.print();
        StdOut.println("--- Test Pass ---\n");

        // Test 6
        StdOut.println("--- Test 6: removeLast ---");
        element = deque.removeLast(); // (empty)
        assert deque.size() == 0;
        assert deque.isEmpty();
        assert element == 2 : "element is now " + element + ", expected 2";
        deque.print();
        StdOut.println("--- Test Pass ---\n");
    }
}