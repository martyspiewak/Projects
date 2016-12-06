
public class Node {

	private Node next;
	private int value;
	
	public Node(int v) {
		next = null;
		value = v;
	}
	
	public Node getNext() {
		return this.next;
	}
	
	public void setNext(Node n) {
		this.next = n;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int i) {
		this.value = i;
	}
}
