
public class Person {
	
	private String name;
	private int age;
	private String[] interests;
	
	public Person() {
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public int getAge() {
		return this.age;
	}
	
	public void setAge(int newAge) {
		this.age = newAge;
	}
	
	public String[] getInterests() {
		return this.interests;
	}
	
	public void setInterests(String[] newInterests) {
		this.interests = newInterests;
	}
	
	public String toString() {
		String info = name + "\n" + Integer.toString(age) + "\n";
		for(String interest : interests) {
			info += interest + "\n";
		}
		return info;
	}
	
}
