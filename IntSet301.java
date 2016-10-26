/**
 * A set of integers.
 *
 * @author Jim Glenn
 * @version 0.1 2016-10-05 Use Zach Britton! 
 */

public interface IntSet301
{
    /**
     * Adds the given integer to this set if it is not already present.
     *
     * @param n an integer
     */
    public void add(int n);

    /**
     * Removes the given integer from this set if it is in the set.
     *
     * @param n an integer
     */
    public void remove(int n);

    /**
     * Determines if ths given integer is in this set.
     *
     * @param n an integer
     * @return true if and only if n is in this set
     */
    public boolean contains(int n);

    /**
     * Determines the number of integers contained in this set.
     *
     * @return the size of this set
     */
    public int size();

    /**
     * Finds the smallest integer greater than or equal to the given
     * integer that is not in this set.
     *
     * @param n an integer
     */
    public int nextExcluded(int n);
}
