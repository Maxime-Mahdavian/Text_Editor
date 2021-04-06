package smartundo;

import java.util.Stack;

/**
 * @param <T> Object that is stored in the stack.
 *
 * The built-in java stack doesn't have an upper limit, so this class adds one.
 * It acts the same way as a normal stack
 */
public class SizedStack<T> extends Stack<T> {

    private int maxSize;

    /**
     * @param size Maximum size of the stack
     */
    public SizedStack(int size){
        super();
        this.maxSize = size;
    }

    /**
     * @param object Object to push onto the stack
     * @return return the object
     *
     * It acts the same way as a normal push command, except it removes the first element if the stack is full
     */
    @Override
    public T push(T object){
        while(this.size() >= maxSize){
            this.remove(0);
        }

        return super.push(object);
    }
}
