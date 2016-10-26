import java.util.List;
import java.util.ArrayList;

/**
 * An implementation of a set using a skip list.
 *
 * @author Jim Glenn
 * @version 0.2 2016-10-17 ungenericized, implemented IntSet301
 * @version 0.1 2015-10-05
 */

public class SkipList implements IntSet301
{
    /*
     * Records how many times links are traversed at a given level.
    List<Integer> counts;
    */

    /**
     * The dummy head node in this skip list.
     */
    private Node head;

    /**
     * The dummy tail node in this skip list.
     */
    private Node tail;

    /**
     * The count of non-dummy nodes at each level.
     */
    private List<Integer> nodeCounts;

    /**
     * Creates an empty set.
     */
    public SkipList()
	{
	    head = new Node(Integer.MIN_VALUE, 1);
	    tail = new Node(Integer.MAX_VALUE, 1);
	    head.next.set(0, tail);
	    nodeCounts = new ArrayList<Integer>();
	    nodeCounts.add(0); // NOT counting head or tail
	}

    /**
     * Returns the size of this skip list.
     *
     * @return the size of this skip list
     */
    public int size()
    {
	// from an external point of view, size does not include the dummy head
	return nodeCounts.get(0);
    }

    /**
     * Finds the node with the given value in this skip list.  The returned
     * list is a list of nodes, one per level, such that the next references
     * at the nodes from the corresponding levels is either the node containing
     * the data to search for or the 1st node beyond it.  The value to search
     * for is in this skip list if and only if
     * <code>last.get(0).next.get(0).equals(key)</code>
     * 
     * @param key an integer in [Integer.MIN_VALUE+1, Integer.MAX_VALUE]
     * @return a list of nodes before the key at each level
     */
    private List<Node> find(int key)
    {
	List<Node> last = new ArrayList<Node>(head.next.size());
	for (int i = 0; i < head.next.size(); i++)
	    {
		last.add(head);
	    }

	Node curr = head;
	int level = head.next.size() - 1;
	while (level >= 0)
	    {
		while (curr.next.get(level).data < key)
		    {
			//counts.set(level, counts.get(level) + 1);
			curr = curr.next.get(level);
		    }
		last.set(level, curr);
		level--;
	    }

	return last;
    }

    /**
     * Determines if the given value is present in this skip list.
     *
     * @param value an integer in [Integer.MIN_VALUE+1, Integer.MAX_VALUE-1]
     * @return true if that value is in this skip list and false otherwise
     */
    public boolean contains(int value)
    {
	Node last = find(value).get(0);

	return last.next.get(0).data == value;
    }

    /**
     * Adds the given value to this skip list if it is not already present.
     *
     * @param value an integer in [Integer.MIN_VALUE+2, Integer.MAX_VALUE-2]
     */
    public void add(int value)
    {
	List<Node> last = find(value);
	Node curr = last.get(0).next.get(0);

	if (curr.data > value)
	    {
		// CHECK FOR MERGING HERE!

		// not found -- add
		int newHeight = chooseHeight();
		
		// ensure that the head node and the last references are
		// at least newHeight high
		while (head.next.size() < newHeight)
		    {
			head.addLevel();
			tail.addLevel();
			head.next.set(head.next.size() - 1, tail);
			last.add(head);
			nodeCounts.add(0);
			//counts.add(0);
		    }

		// make the new node
		Node newNode = new Node(value, newHeight);

		for (int h = 0; h < newHeight; h++)
		    {
			// count the new node
			nodeCounts.set(h, nodeCounts.get(h) + 1);
			// link up the new node
			Node oldNext = last.get(h).next.get(h);
			last.get(h).next.set(h, newNode);
			newNode.next.set(h, oldNext);
		    }
	    }
    }

    /**
     * Removes the given integer from this set if it is in the set.
     *
     * @param n an integer
     */
    public void remove(int value)
    {
	// find the previous value at each level
	List<Node> last = find(value);
	Node toDelete = last.get(0).next.get(0);
	
	if (toDelete.data == value)
	    {
		// found -- delete
		deleteNode(last, toDelete);
	    }
    }

    /**
     * Deletes the given node from this skip list.
     *
     * @param last the largest node strictly before toDelete at each level
     * @param toDelete a node in this skip list other than the head and tail
     */
    private void deleteNode(List<Node> last, Node toDelete)
    {
	// link around deleted node
	int height = toDelete.next.size();
	for (int h = 0; h < height; h++)
	    {
		last.get(h).next.set(h, toDelete.next.get(h));
		nodeCounts.set(h, nodeCounts.get(h) - 1);
	    }
	
	// remove extra levels in dummy nodes
	while (head.next.size() > 1 && nodeCounts.get(head.next.size() - 1) == 0)
	    {
		head.next.remove(head.next.size() - 1);
		tail.next.remove(tail.next.size() - 1);
		nodeCounts.remove(nodeCounts.size() - 1);
	    }
    }

    /**
     * Finds the smallest integer greater than or equal to the given
     * integer that is not in this set.
     *
     * @param n an integer
     */
    public int nextExcluded(int n)
    {
	// find the node before where n would go...
	Node last = find(n).get(0);

	// ...and the node after that node
	Node curr = last.next.get(0);

	// now look at that node -- does it contain n?
	if (curr.data != n)
	    {
		// no? then n itself is the next excluded
		return n;
	    }
	else
	    {
		// yes? then find a gap
		last = curr;
		curr = curr.next.get(0);
		while (curr.data == last.data + 1)
		    {
			last = curr;
			curr = curr.next.get(0);
		    }
		return last.data + 1;
	    }
    }

    /**
     * Returns a printable representation of this skip list.
     *
     * @return a printable representation of this skip list
     */
    public String toString()
    {
	StringBuilder out = new StringBuilder();
	Node curr = head.next.get(0);
	while (curr != tail)
	    {
		out.append(curr.toString());
		curr = curr.next.get(0);
	    }
	return out.toString();
    }

    /**
     * Randomly returns a height.  Height 1 is returned with probability 1/2;
     * 2 with probability 1/4, ...
     *
     * @return a randomly chosen height
     */
    private int chooseHeight()
    {
	int height = 1;
	while (Math.random() < 0.5)
	    {
		height++;
	    }
	return height;
    }
    
    /**
     * A node in a skip list.
     */    
    private static class Node
    {
	private int data;
	private List<Node> next;

	/**
	 * Creates a new node of the given level holding the given data.
	 *
	 * @param d the data in the new node
	 * @param h a nonnegative integer for the height of the new node
	 */
	public Node(int d, int h)
	    {
		data = d;
		next = new ArrayList<Node>(h);
		for (int i = 0; i < h; i++)
		    {
			next.add(null);
		    }
	    }

	/**
	 * Adds a level to this node.
	 */
	public void addLevel()
	{
	    next.add(null);
	}

	public String toString()
	{
	    return "<" + data + "; " + next.size() + ">";
	}
    }

    public static void main(String[] args)
    {
	SkipList s = new SkipList();

	int[] testValues = {2, 1, 4, 6, 8, 3, 9, 10, 5, 7};

	for (int i : testValues)
	    {
		System.out.printf("=== ADDING %d ===\n", i);
		s.add(i);
		System.out.println(s);
		for (int j = 0; j <= 11; j++)
		    {
			System.out.printf("nextExcluded(%d) = %d\n", j, s.nextExcluded(j));
		    }
	    }

	for (int i = 0; i <= 11; i++)
	    {
		System.out.println(i + ": " + s.contains(i));
	    }
	System.out.println(s);

	int[] removeValues = {1, 10, 5, 2, 4, 8, 7, 6, 9, 3};
	for (int i : removeValues)
	    {
		System.out.printf("=== REMOVING %d ===\n", i);
		s.remove(i);
		System.out.println(s);
	    }

	System.out.println("=== ADDING BACK ===");
	for (int i : testValues)
	    {
		s.add(i);
	    }
	System.out.println(s);
    }
}
