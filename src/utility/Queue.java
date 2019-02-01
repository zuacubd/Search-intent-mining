package utility;

/*************************************************************************
 *  Compilation:  javac Queue.java
 *  Execution:    java Queue
 *
 *  A queue, implemented using a linked list.
 *
 *************************************************************************/

public class Queue {
    private Node first;        // beginning of queue
    private Node last;         // end of queue

    private static class Node {
        Object item;
        Node next;
    }

    public boolean isEmpty() { return (first == null); }
    
    public int size()
    {
    	int count=0;
    	for (Node x = first; x != null; x = x.next)
    		count++;
    	return count;
    }
    
    // add anItem to the queue
    public void enqueue(Object anItem) {
        Node x = new Node();
        x.item = anItem;
        x.next = null;
        if (isEmpty()) first = x;
        else last.next = x;
        last = x;
    }

    // remove and return the least recently added item
    public Object dequeue() {
        Object val = first.item;
        first = first.next;
        return val;
    }
}
