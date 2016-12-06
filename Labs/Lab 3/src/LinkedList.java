
public class LinkedList {

	private Node head;
	
	public LinkedList() {
		
	}
	
	public Node getHead() {
		return this.head;
	}
	
	public void setHead(Node n) {
		this.head = n;
	}
	
	@Override
	public String toString() {
		String str = "";
		Node current = head;
		while (current != null) {
			str += current.getValue() + ", ";
			current = current.getNext();
		}
		return str;
	}
}
