import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private static final int DEFAULT_CAPACITY = 2;

    private Item[] items;
    private int end;

    private class RandomizedQueueIterator implements Iterator<Item> {
        private final int[] indexes;
        private final Item[] items;
        private int indexSize;

        public RandomizedQueueIterator(Item[] items, int end) {
            this.items = items;
            this.indexSize = end;
            this.indexes = new int[indexSize];

            for (int i = 0; i < indexSize; i++) {
                indexes[i] = i;
            }
        }

        @Override
        public boolean hasNext() {
            return indexSize > 0;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int rand = StdRandom.uniformInt(indexSize);
            Item item = items[indexes[rand]];
            indexes[rand] = indexes[--indexSize];
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private void resize(int capacity) {
        if (size() > capacity) {
            throw new IllegalArgumentException();
        }

        Item[] newItems = (Item[]) new Object[capacity];

        for (int i = 0; i < end; i++) {
            newItems[i] = items[i];
        }

        items = newItems;
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

    // construct an empty randomized queue
    public RandomizedQueue() {
        items = (Item[]) new Object[DEFAULT_CAPACITY];
        end = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size() == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return end;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        if (size() == items.length) {
            resize(items.length * 2);
        }

        items[end++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        int rand = StdRandom.uniformInt(end);
        Item temp = items[rand];
        items[rand] = items[--end];
        items[end] = null;

        if (end < items.length / 4) {
            resize(items.length / 2);
        }

        return temp;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        int rand = StdRandom.uniformInt(end);
        return items[rand];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator(items, end);
    }

    // unit testing (required)
    public static void main(String[] args) {
        {
            RandomizedQueue<Integer> queue = new RandomizedQueue<>();

            // Test 1
            StdOut.println("--- Test 1: Default Constructor ---");
            assert queue.isEmpty();
            assert queue.size() == 0;
            queue.print();
            StdOut.println("--- Test Pass ---\n");

            // Test 2
            StdOut.println("--- Test 2: Enqueue ---");
            queue.enqueue(1);
            queue.enqueue(2);
            assert queue.size() == 2;
            assert !queue.isEmpty();
            queue.print();
            StdOut.println("--- Test Pass ---\n");

            // Test 3
            StdOut.println("--- Test 3: Sample ---");
            StdOut.println("sampled: " + queue.sample());
            StdOut.println("sampled: " + queue.sample());
            assert queue.size() == 2;
            assert !queue.isEmpty();
            queue.print();
            StdOut.println("--- Test Pass ---\n");

            // Test 4
            StdOut.println("--- Test 4: Dequeue ---");
            StdOut.println("dequeued: " + queue.dequeue());
            StdOut.println("dequeued: " + queue.dequeue());
            assert queue.size() == 0;
            assert queue.isEmpty();
            queue.print();
            StdOut.println("--- Test Pass ---\n");

            // Test 5
            StdOut.println("--- Test 5: resize ---");
            for (int i = 0; i < 20; i++) {
                queue.enqueue(i);
            }
            assert queue.size() == 10;
            assert !queue.isEmpty();
            queue.print();
            StdOut.println("--- Test Pass ---\n");
        }
    }
}