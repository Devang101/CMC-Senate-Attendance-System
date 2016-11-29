import java.awt.Color;
import java.awt.Font;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class app {
	private static String today = SenateDB.getToday();
	
	static JTextField text = new JTextField(30);
	static JLabel label = new JLabel("Welcome to ASCMC Senate!");
	static JLabel instruct = new JLabel("Swipe your ID:");
	static JButton save = new JButton("save");
	static JTextField nameField = new JTextField(30);
	static JLabel name = new JLabel("");
	
	
	
	public static void makeNewSenator(String id)
	{
		text.setText("");
		nameField.requestFocus();
		label.setText("You are not in our database yet.");
		instruct.setText("Enter FIRST and LAST name and press ENTER: ");
		nameField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				
				String input = nameField.getText();
				if (input.contains(";") || input.equals("")){
					nameField.setText("");
					label.setText("Please try again");
				}
				else{
					HashSet<String> datesAttended = new HashSet<String>();
					datesAttended.add(today);
					
					Person newSenator = new Person(id, input, false, new Integer(1), datesAttended);
					SenateDB.senators.put(id, newSenator);
					label.setText("Welcome to ASCMC Senate!");
					name.setText(newSenator.name);
					instruct.setText("Swipe ID:");
					text.requestFocus();
					nameField.setText("");
					nameField.removeActionListener(this);
				}
			}
		}
		);
	}
	
	public static void main(String[] args){
		//load data
		SenateDB.importDB();
		
		//set frame
		JFrame f = new JFrame("Card Swiper");
		f.setSize(600, 400);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//set panel
		JPanel p = new JPanel();
		p.setBackground(new Color(152,26,49));
		
		//color labels
		label.setForeground(new Color(158,124,10));
		instruct.setForeground(new Color(158,124,10));
		name.setForeground(Color.white);
		Font font = label.getFont();
		// same font but bold
		Font bigBoldFont = new Font(font.getFontName(), Font.BOLD, font.getSize()*2);
		label.setFont(bigBoldFont);
		Font bigFont = new Font(font.getFontName(), Font.PLAIN, font.getSize()*2);
		instruct.setFont(bigFont);
		Font nameFont = new Font(font.getFontName(), Font.PLAIN, font.getSize());
		name.setFont(nameFont);
		
		//set button
		p.add(label);
		p.add(instruct);
		p.add(text);
		p.add(save);
		p.add(nameField);
		p.add(name);
		f.add(p);
		f.setVisible(true);
		
		//run program
		text.requestFocus();
		text.setText("");
		label.setText("Welcome to ASCMC Senate!");
		instruct.setText("\n Swipe ID:");
		text.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				instruct.setText("");
				label.setText("Processing...");
				String command = text.getText();
				//get rid of last two digits in ID
				if (command.length() == 12){
					String id = command.substring(0,command.length()-2);
					System.out.println(id);
					//get person from database
					Person thisPerson = SenateDB.senators.get(id);
					//to check in case if person has already scanned in
					if(thisPerson != null && thisPerson.datesAttended.contains(today)){
						label.setText("You have already scanned in today!");
						text.setText("");
						instruct.setText("\n Next in line, please swipe ID:");
						name.setText(thisPerson.name);
					}
					//if you have been to a senate meeting before you are in our database
					else if(SenateDB.senators.containsKey(id)){
						//if you are a senator
						if(thisPerson.isSenator){
							name.setText(thisPerson.name);
							label.setText("Welcome, Senator.");
							//today's date will be recorded
							thisPerson.datesAttended.add(today);
							//your senatorShip is reaffirmed to level 3
							thisPerson.attendanceCount = 3;
							//update database
							instruct.setText("\n Next in line, please swipe ID:");
						}
						//if you are not a senator
						else{
							//record your attendance
							name.setText(thisPerson.name);
							thisPerson.attendanceCount++;
							thisPerson.datesAttended.add(today);
							//if your attendanceCount is 3 you become a senator
							if(thisPerson.attendanceCount == 3){
								label.setText("Congratulations! You are now a senator and can vote!");
								thisPerson.isSenator = true;
								//update database
								SenateDB.senators.put(id, thisPerson);
							}
							//if your attendanceCount<3 you are still not a senator
							name.setText(thisPerson.name);
							label.setText("You are not yet a senator! Please attend the next " 
						    + (3-thisPerson.attendanceCount) + " meetings.");
							instruct.setText("\n Next in line, please swipe ID:");
						}
						SenateDB.senators.put(id, thisPerson);
						text.setText("");
					}	
					//if this is your first time at senate
					else{
						makeNewSenator(id);
						thisPerson = SenateDB.senators.get(id);
						label.setText("This is your first senate meeting! Please attend the next " 
						    + (3-thisPerson.attendanceCount) + " meetings to become a Senator!");
						text.setText("");
						instruct.setText("\n Next in line, please swipe ID:");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						
					}
					
				}
				else{
					label.setText("Invalid ID Number.");
					text.setText("");
					instruct.setText("\n Next in line, please swipe ID:");
				}
				
				
			}
		}
		);
		//click stop button to export
		save.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				SenateDB.exportDB();
				label.setText("Please close/restart the prorgam.");
				nameField.setText("");
				instruct.setText("");
				text.setText("");
				text.removeActionListener(this);
				save.removeActionListener(this);
			}
		}
		);
	}

}
