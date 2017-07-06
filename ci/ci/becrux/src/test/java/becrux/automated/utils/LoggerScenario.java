//package becrux.automated.utils;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class LoggerScenario {
//
//	private String startDate = "";
//
//	public LoggerScenario(){
//		startDate=dateToString();
//	}
//
//	public void addLog(String resultOfTests){
//		try
//		{
//		    String filename= "/proj/ims_lu/cba_cde/int_ci/jenkins/testLog";
//		    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
//		    fw.write("\n\nStart Automated Scenarios : "+startDate+"\n"+resultOfTests+"\nStop Automated Scenarios"+dateToString()+"\n");//appends the string to the file
//		    fw.close();
//		}
//		catch(IOException ioe)
//		{
//		    System.err.println("IOException: " + ioe.getMessage());
//		}
//	}
//
//	private String dateToString(){
//		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//		Date now = new Date();
//		String reportDate = df.format(now);
//		return reportDate;
//	}
//}
