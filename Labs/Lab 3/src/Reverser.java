import java.util.Scanner;

public class Reverser {
	
	private static LinkedList list = new LinkedList();
	private static Node current;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int num = 0;
		while (sc.hasNextInt()) {
			if (num == 0) {
				num = sc.nextInt();
			}
			int i = sc.nextInt();
			makeNode(i);
		}
		sc.close();
		printList();
		list.setHead(reverse());
		printList();
		list.setHead(reverseWithLimit(num));
		printList();
	}
	
	public Reverser() {
		current = null;
	}
	
	private static void makeNode(int value) {
		Node temp = new Node(value);
		if (current != null) {
			current.setNext(temp);
		}
		else {
			list.setHead(temp);
		}
		current = temp;
	}
	
	private static void printList() {
		System.out.println(list.toString());
	}
	
	private static Node reverse() {
		return reverse(list.getHead());
	}
	
	private static Node reverse(Node head) {
		Node cur = head;
		Node prev = null;
		while (cur != null) {
			Node realNext = cur.getNext();
			cur.setNext(prev);
			prev = cur;
			cur = realNext;
		}
		return prev;
	}
	
	private static Node reverseWithLimit(int n) {
		return reverseWithLimit(list.getHead(), n);
	}
	
	private static Node reverseWithLimit(Node head, int n) {
		Node cur = head;
		Node prev = null;
		Node initHead = head;
		int i = 0;
		while (cur != null && i != n) {
			Node realNext = cur.getNext();
			cur.setNext(prev);
			prev = cur;
			cur = realNext;
			i++;
			initHead.setNext(cur);
		}
		if(cur != null) {
			initHead.setNext(reverseWithLimit(cur, n));
		}
		return prev;
	}
	
}
