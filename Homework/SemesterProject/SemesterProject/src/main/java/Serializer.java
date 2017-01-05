import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class Serializer {
	
	private static String log = "src/Logging/QueryLog.txt";
	
	protected Serializer() {
		
	}
	
	protected void updateLog(String str) {
		Long time = System.currentTimeMillis();
		String timeStamp = time.toString();
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(log)));
			bw.append(timeStamp + "\n");
			bw.append(str + "\n");
			bw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: Logging did not work.");
		}
		
	}
	
	protected void serialize(Database d) {
		//re-save the old backup
		Database old = null;
		try{
			FileInputStream door = new FileInputStream("src/Logging/Database.ser");
			ObjectInputStream reader = new ObjectInputStream(door);
			old = (Database) reader.readObject();
			reader.close();
			System.out.println("Deserialized.");
		}
		catch (IOException e) {
			System.out.println("Deserialization did not work.");
		}
		catch (ClassNotFoundException c) {
			System.out.println("Database class was not found.");
		}
		try{
			// Serialize data object to a file
			File db = new File("src/Logging/OldDatabase.ser");
			FileOutputStream stream = new FileOutputStream(db);
			ObjectOutputStream out = new ObjectOutputStream(stream);
			out.writeObject(old);
			out.close();
			System.out.println("Serialized.");
		}
		catch (IOException e) {
			System.out.println("Serialization didn't work.");
		}
		try{
			//make time stamp
			Long time = System.currentTimeMillis();
			String timeStamp = time.toString();
			// Serialize data object to a file
			File db = new File("src/Logging/Database.ser");
			File dbTime = new File("src/Logging/DatabaseTimeStamp.txt");
			FileOutputStream stream = new FileOutputStream(db);
			ObjectOutputStream out = new ObjectOutputStream(stream);
			out.writeObject(d);
			out.close();
			FileOutputStream stream2 = new FileOutputStream(dbTime);
			ObjectOutputStream out2 = new ObjectOutputStream(stream2);
			out2.writeObject(timeStamp);
			out2.close();
			System.out.println("Serialized.");
		}
		catch (IOException e) {
			System.out.println("Serialization didn't work.");
		}
		File f = new File(log);
		try {
			PrintWriter writer = new PrintWriter(f);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't clear log.");
		}
	}
	
	protected Object[] deserialize() {
		Database d = null;
		String timeStamp = "";
		Object[] queries = new Object[15];
		try{
			FileInputStream door = new FileInputStream("src/Logging/Database.ser");
			ObjectInputStream reader = new ObjectInputStream(door);
			d = (Database) reader.readObject();
			reader.close();
			System.out.println("Deserialized.");
		}
		catch (IOException e) {
			System.out.println("Deserialization did not work.");
		}
		catch (ClassNotFoundException c) {
			System.out.println("Database class was not found.");
		}
		try {
			FileInputStream door = new FileInputStream("src/Logging/DatabaseTimeStamp.txt");
			ObjectInputStream reader = new ObjectInputStream(door);
			timeStamp = (String) reader.readObject();
			reader.close();
			System.out.println("Got timestamp.");
		}
		catch (IOException e) {
			System.out.println("Couldn't get timestamp.");
		}
		catch (ClassNotFoundException c) {
			System.out.println("Couldn't get timestamp.");
		}
		try {
			String line;
			BufferedReader br = new BufferedReader(new FileReader(new File(log)));
			for(int i = 2; i < queries.length; i++) {
				if((line = br.readLine()) != null) {
					queries[i] = line;
					System.out.println("Read line: " + line);
				}
				else {
					break;
				}
			}
			br.close();
		}
		catch (IOException e) {
			System.out.println("Exception e. Couldn't read log file.");
		}
		queries[0] = d;
		queries[1] = timeStamp;
		return queries;
	}
	
}
