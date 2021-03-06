import java.util.HashSet;

public class Person {
	public boolean isSenator;
	public String name;
	public HashSet<String> datesAttended;
	public String id;
	public Integer attendanceCount;
	
	public Person(String id, String name, boolean isSenator,Integer attendanceCount,HashSet<String> datesAttended){
		this.id = id;
		this.name = name;
		this.isSenator = isSenator;
		this.attendanceCount = attendanceCount;
		this.datesAttended = datesAttended;
	}
	
	public void promote(){
		this.isSenator = true;
	}
	
	public void demote(){
		this.isSenator = false;
	}
}
