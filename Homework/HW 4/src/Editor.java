import java.io.Console;
import java.util.Arrays;
import java.io.IOException;

public class Editor {
	
	private ArrayStack<String> text;

	public static void main(String[] args) 
	{
		Editor myEditor = new Editor();
		myEditor.go();
	}
	
	public Editor()
	{
		 text = new ArrayStack<String>();
	}

	public void go()
	{
		Console c = System.console();
		if (c == null) 
		{
            System.err.println("No console.");
            System.exit(1);
        }
		boolean done = false;
		while(done != true) {
			
			String line = c.readLine();
			if(line.equals("#exit")) {
				done = true;
				System.exit(1);
			}
			if (line.equals("#print")) {
				print();
				continue;
			}
			if (line.startsWith("#print ")) {
				print(Integer.parseInt(line.substring(7)));
				continue;
			}
			if (line.equals("#delete")) {
				delete();
				continue;
			}
			if (line.startsWith("#delete ")) {
				delete(Integer.parseInt(line.substring(8)));
				continue;
			}
			if (text.isFull()) {
				ArrayStack<String> text2 = new ArrayStack<String> (text.length()*2);
				int length = text.length();
				ArrayStack<String> holder = new ArrayStack<String> (text.length());
				for(int i = 0; i < length; i++) {
					String lastLine = text.pop();
					holder.push(lastLine);
				}
				for(int i = 0; i < length; i++) {
					text2.push(holder.pop());
				}
				text = text2;
			}
			text.push(line);
		}		
	}
	
	private void print() {
		ArrayStack<String> holder = new ArrayStack<String> (text.length());
		int length = text.length();
		for(int i = 0; i < length; i++) {
			String lastLine = text.pop();
			holder.push(lastLine);
		}
		length = holder.length();
		for(int i = 0; i < length; i++) {
			String lastLine = holder.pop();
			if(lastLine != null) {
				System.out.println(lastLine);
			}
			text.push(lastLine);
		}
	}
	
	private void print(int n) {
		int numToPop = text.length() - n;
		ArrayStack<String> holder = new ArrayStack<String> (numToPop);
		for(int i = 0; i < numToPop; i++) {
			String lastLine = text.pop();
			holder.push(lastLine);
		}
		String lineToPrint = text.pop();
		System.out.println(lineToPrint);
		text.push(lineToPrint);
		for(int i = 0; i < numToPop; i++) {
			String lastLine = holder.pop();
			text.push(lastLine);
		}
	}
	
	private void delete() {
		int length = text.length();
		for(int i = 0; i < length; i++) {
			text.pop();
		}
	}
	
	private void delete(int n) {
		int numToPop = text.length() - n;
		ArrayStack<String> holder = new ArrayStack<String> (numToPop);
		for(int i = 0; i < numToPop; i++) {
			String lastLine = text.pop();
			holder.push(lastLine);
		}
		text.pop();
		for(int i = 0; i <= numToPop; i++) {
			String lastLine = holder.pop();
			text.push(lastLine);
		}
	}
	
	

}
