import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SenateDB{
	public static HashMap<String, Person> senators = new HashMap<String, Person>();
	private static String csvSplitBy = ","; 
    private static PrintWriter pw;
	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private static Date date = new Date();
	private static String today = dateFormat.format(date);
	
	//returns today's date
    public static String getToday(){
    	return today;
    }
    
    //imports data from excel file
    public static void importDB(){
        System.out.println("Populating SenateDB");
        String csvFile = "SenateDB.csv";
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))){
            while ((line = br.readLine()) != null) {
            	//[id, name, isSenator, attendanceCount... datesAttended]
                String[] personData = line.split(csvSplitBy);
                String id = personData[0];
                String name = personData[1];
                String senatorShip = personData[2]; // either "Y" or "N"
                //convert String to boolean
                boolean isSenator = false;
            	if(senatorShip.equals("Y")){ 
            		isSenator = true;
            	}
            	Integer attendanceCount = new Integer(personData[3]);
                //convert rest of array to hashset of datesAttended
                HashSet<String> datesAttended = new HashSet<String>();
            	for(int i=4; i<personData.length; i++)
            	{
            		datesAttended.add(personData[i]);
            	}
            	//create the person and put into database
            	Person person = new Person(id, name, isSenator, attendanceCount, datesAttended);                                
            	senators.put(id, person);
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //exports data to excel file
    public static void exportDB(){
        try {
            pw = new PrintWriter(new File("SenateDB.csv"));
            StringBuilder sb = new StringBuilder();
            for (String id: senators.keySet()){
            	sb.append(id);
                sb.append(csvSplitBy);
                Person person = senators.get(id);
                //if the person did not attend today
                if (!person.datesAttended.contains(today)){
                	if(person.isSenator){
                		person.attendanceCount--;
                		//if attendanceCount is 0 then this person has not come to the last 3 senate meetings and is no longer a senator
                		if(person.attendanceCount==0){
                			person.isSenator = false;
                		}
                	}
                	//if person is not a senator reset their attendanceCount to 0 because they need to come 3 times in a row
                	else{
                		person.attendanceCount = 0;
                	}
                }
                sb.append(person.name);
                sb.append(csvSplitBy);
                if(person.isSenator){
                	sb.append("Y");
                	sb.append(csvSplitBy);
                }
                else{
                	sb.append("N");
                	sb.append(csvSplitBy);
                }
                sb.append(person.attendanceCount);
                sb.append(csvSplitBy); 
                for(String date : person.datesAttended){
                	sb.append(date);
                	sb.append(csvSplitBy);
                }
                sb.append('\n');
             }
            pw.write(sb.toString());
            pw.close();
            System.out.println("Finished Writing SenateDB CSV File!"); 
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
