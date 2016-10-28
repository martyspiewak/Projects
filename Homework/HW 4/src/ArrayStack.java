public class ArrayStack<T> {
	
	private int size;
	private int top;
	public T[] head;


	@SuppressWarnings("unchecked")
	public ArrayStack() {
		size = 10;
		top = -1;
		head = (T[]) new Object[size];
	}
	
	@SuppressWarnings("unchecked")
	public ArrayStack(int setSize) {
		size = setSize;
		top = -1;
		head = (T[]) new Object[size];
	}
	
	public void push(T t) throws IllegalStateException {
		if(isFull()) {
			throw new IllegalStateException("Stack is full");
		}
		top++;
		head[top] = t;
	}

	public T pop() {
		if(top == -1) {
			return null;
		}
		T item = head[top];
		head[top] = null;
		top--;
		return item;
	}
	
	public int length() {
		return top + 1;
	}
	
	public boolean isFull()
	{
		if(top == size-1)
		{
			return true;
		}
		return false;
	}

}