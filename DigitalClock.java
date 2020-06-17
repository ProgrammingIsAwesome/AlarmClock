import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;


public class DigitalClock extends JFrame{
    WatchPanel watch = new WatchPanel();
	JMenuItem mItem;
	JFrame alarmEditor;
    public DigitalClock() {
        super("Digital Clock");
        setSize(500, 600);
		setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

		
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(1, 1, 15, 15));
        pane.add(watch);
        setContentPane(pane);
        show();
		
    }
	


	
    public static void main(String[] arguments) {
        DigitalClock clock = new DigitalClock();
    }
	
	
	
}
class AlarmTime{
	int hour, minute,snoozeMinutes;
	String label;
	JButton minusButton=new JButton("-");
	JButton snoozeButton=new JButton("Snooze");
	
	boolean isON=false;
	boolean isSnoozing=false;
	Date dateReserved=null;
	public AlarmTime(int thour, int tminute, int tsnoozeMinutes, String tlabel){
		hour=thour;
		minute=tminute;
		snoozeMinutes=tsnoozeMinutes;
		label=tlabel;
		dateReserved=new Date();

		while(dateReserved.getHours()!=thour){
			dateReserved.setHours(dateReserved.getHours()+1);

		}
		dateReserved.setMinutes(tminute);
		dateReserved.setSeconds(0);
		
	}
}
class WatchPanel extends JPanel implements Runnable, ActionListener {
    Thread runner;

	java.util.ArrayList<AlarmTime> listOfAlarms=new java.util.ArrayList<AlarmTime>();
	JButton addAlarm;
	JButton editAlarm;
	
	JTextField snoozeMinutes;
	JComboBox minuteList;
	JComboBox timeList;
	JPanel panel;
    WatchPanel() {

		setLayout(null);
		addAlarm=new JButton("Add alarm");
		addAlarm.setBounds(10,60,120,40);
		addAlarm.addActionListener(this);

		add(addAlarm);
		
		String[] times =new String[24];
		for(int j=0;j<times.length;j++){
			times[j]=""+j;
		}
		timeList=new JComboBox(times);
		timeList.setSelectedIndex(7);
		timeList.setBounds(50,130,40,20);
		add(timeList);
		
		JLabel hLabel=new JLabel("Hour: ");
		hLabel.setBounds(10,130,40,20);
		add(hLabel);
		
		JLabel mLabel=new JLabel("Minutes: ");
		mLabel.setBounds(110,130,80,20);
		add(mLabel);
		
		String[] minutes =new String[60];
		for(int j=0;j<minutes.length;j++){
			minutes[j]=""+j;
		}
		minuteList=new JComboBox(minutes);
		
		minuteList.setSelectedIndex(0);
		minuteList.setBounds(180,130,40,20);
		
		add(minuteList);
		
		JLabel snoozeLabel=new JLabel("Snooze time: ");
		snoozeLabel.setBounds(10,170,80,20);
		add(snoozeLabel);
		
		snoozeMinutes=new JTextField("30");
		snoozeMinutes.setBounds(100,170,30,20);
		add(snoozeMinutes);
		
		JLabel mNotifier=new JLabel(" minutes");
		mNotifier.setBounds(140,170,60,20);
		add(mNotifier);
		
			JLabel alarmTime=new JLabel("Alarm Time:");
		alarmTime.setBounds(10,200,80,40);
		add(alarmTime);
		System.out.println(new JLabel().getFont());
		JLabel snoozeTitle=new JLabel("Snooze (mins)");
		snoozeTitle.setBounds(100,200,120,40);
		add(snoozeTitle);
        if (runner == null) {
            runner = new Thread(this);
            runner.start();
        }
    }
	boolean isBeeping=false;
    public void run() {
        while (true) {
			if(isBeeping){
				Toolkit.getDefaultToolkit().beep();
				
			} 
			for(int j=0;!isBeeping&&j<listOfAlarms.size();j++){
				if(!listOfAlarms.get(j).isSnoozing&&listOfAlarms.get(j).hour==today.getHours()&&listOfAlarms.get(j).minute==today.getMinutes()){
					listOfAlarms.get(j).isON=true;
					isBeeping=true;
					break;
				}else if(listOfAlarms.get(j).isSnoozing){
					long laterTime=today.getTime();
					long beforeTime=listOfAlarms.get(j).dateReserved.getTime();
					if(laterTime-beforeTime<60000){
						break;
					}
					long difference=(laterTime-beforeTime)%(listOfAlarms.get(j).snoozeMinutes*60000);
					if(difference<60000){
						isBeeping=true;
						break;
					}
				}
			}
            repaint();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
    }
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==addAlarm){
			String sHour=""+timeList.getSelectedIndex();
			if(sHour.length()<2){
				sHour="0"+sHour;
			}
			
			String sMinute=""+minuteList.getSelectedIndex();
			if(sMinute.length()<2){
				sMinute="0"+sMinute;
			}
			
			
			String sTime=sHour+":"+sMinute+"      "+Integer.parseInt(snoozeMinutes.getText());
			AlarmTime aT=new AlarmTime(timeList.getSelectedIndex(),minuteList.getSelectedIndex(),Integer.parseInt(snoozeMinutes.getText()),sTime);
			listOfAlarms.add(aT);
			aT.minusButton.setBounds(200,240+(listOfAlarms.size()-1)*30,50,20);
			aT.minusButton.addActionListener(this);
			add(aT.minusButton);
			
			aT.snoozeButton.setBounds(270,240+(listOfAlarms.size()-1)*30,100,20);
			aT.snoozeButton.addActionListener(this);
			add(aT.snoozeButton);
		}
		for(int j=0;j<listOfAlarms.size();j++){
			if(e.getSource()==listOfAlarms.get(j).minusButton){
				listOfAlarms.get(j).minusButton.setVisible(false);
				listOfAlarms.get(j).snoozeButton.setVisible(false);
				for(int k=j+1;k<listOfAlarms.size();k++){
					listOfAlarms.get(k).minusButton.setBounds(200,170+k*30,50,20);
					listOfAlarms.get(k).snoozeButton.setBounds(270,170+k*30,100,20);
				}
				listOfAlarms.get(j).isON=false;
				listOfAlarms.remove(j);
				j--;
			}else if(e.getSource()==listOfAlarms.get(j).snoozeButton&&listOfAlarms.get(j).isON){
			
				listOfAlarms.get(j).isON=false;
				listOfAlarms.get(j).isSnoozing=true;
			}
			
			
			
		}
		int countAlarms=0;
		for(AlarmTime alarmT:listOfAlarms){
			if(alarmT.isON){
				countAlarms++;
			}
		}
		if(countAlarms==0){
			isBeeping=false;
		}
		repaint();
		
	}
	Date today=new Date();
    public void paintComponent(Graphics comp) {
        Graphics2D comp2D = (Graphics2D)comp;
        Font type = new Font("Serif", Font.BOLD, 24);
        comp2D.setFont(type);
        comp2D.setColor(getBackground());
        comp2D.fillRect(0, 0, getSize().width, getSize().height);

		today=new Date();
		String hours=""+today.getHours();
		if(hours.length()<2){
			hours="0"+hours;
		}
		String minutes=""+today.getMinutes();
		if(minutes.length()<2){
			minutes="0"+minutes;
		}
		String seconds=""+today.getSeconds();
		if(seconds.length()<2){
			seconds="0"+seconds;
		}
		String time=hours+":"+minutes+":"+seconds;
        comp2D.setColor(Color.black);
        comp2D.drawString(time, 5, 25);
		
		for(int k=0;k<listOfAlarms.size();k++){
			comp.drawString(listOfAlarms.get(k).label,10,260+k*30);
		}
    }
	

}

