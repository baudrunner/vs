package lab1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Log {

	FileWriter writer;
	File logfile;
	
	public Log() throws IOException{
		logfile = new File("MessageServiceLog_" + new Date().getTime()+ ".txt");
		writer = new FileWriter(logfile ,true);// falls die Datei bereits existiert
        									   // werden die Bytes an das Ende der Datei geschrieben
	}
	  
	public void append(String txt){
    
	     try {
	       // Text wird in den Stream geschrieben
	       writer.write(new Date() + txt);
	       
	       // Platformunabh�ngiger Zeilenumbruch
	       writer.write(System.getProperty("line.separator"));
   
	       writer.flush();
	      
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  }
	
	public void quitLogger(){
		
		// Schliesst den Stream
	    try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}