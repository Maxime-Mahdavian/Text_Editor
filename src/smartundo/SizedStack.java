package smartundo;

import java.util.Stack;

//I discovered that the built-in java stack doesn't have a upper limit, so i created this
//to give it one, it acts exactly like a normal stack

public class SizedStack<T> extends Stack<T> {

    private int maxSize;

    public SizedStack(int size){
        super();
        this.maxSize = size;
    }

    @Override
    public T push(T object){
        while(this.size() >= maxSize){
            this.remove(0);
        }

        return super.push(object);
    }
}
