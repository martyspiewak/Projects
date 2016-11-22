import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Students {
	
	private Person[] students;
	
	public Students() {
		
	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		
		//read json file data to String
		byte[] jsonData = Files.readAllBytes(Paths.get("src/main/resources/students.text"));
				
		//create ObjectMapper instance
		ObjectMapper objectMapper = new ObjectMapper();
				
		//convert json string to object
		Students stu = objectMapper.readValue(jsonData, Students.class);
				
		System.out.println(stu);
		stu.sortByAge();
		stu.sortByName();
	}
	
	public Person[] getStudents() {
		return students;
	}
	
	public void setStudents(Person[] studs) {
		students = studs;
	}
	
	public String toString() {
		String result = "";
		for(Person student : students) {
			result += student.toString() + "\n";
		}
		return result;
	}
	
	
	public void sortByAge() {
		Arrays.sort(students, 
				new Comparator<Person>() {
		    		public int compare(Person p1, Person p2) {
		    			return p1.getAge() - p2.getAge(); 
		    		} 
		    	}
		);
		for(Person student : students) {
			System.out.println(student.toString());
		}
	}
	
	public void sortByName() {
		Arrays.sort(students,
                new Comparator<Person>() {
                    public int compare(Person p1, Person p2) {
                        return p1.toString().compareTo(p2.toString());
                    }        
                }
		);
		for(Person student : students) {
			System.out.println(student.toString());
		}
	}
	
}
