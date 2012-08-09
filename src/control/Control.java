package control;

import com.rapplogic.xbee.api.*;
import com.rapplogic.xbee.api.wpan.*;
import gnu.io.RXTXCommDriver;
import java.io.FileOutputStream; //Kept for rewriting config file
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Ruvan Muthu-Krishna
 */
public class Control {

//  Variables which can be altered    
    static int[] XBeePins;
    int XBeeComPort;
    XBee XBee;
    
    

    /**
     * Runs initialization methods based on given args then enters infinite loop
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Control ctrl = new Control();
        ctrl.loadConfig();
        
       /* 
       while (true) {
          Boolean[] XBeePinValues = ctrl.pollXBeePins(XBeePins);
            for(int i=0; i<=XBeePinValues.length-1; i++) {
                System.out.println("Pin number " + i + " is " + XBeePinValues[i]);
            }
        }
        * 
        */
    }
    
    /**
     * Loads configuration file config.properties and assigns specified values to variables
     * once required values have been set the file is close so as to not cause file lock problems.
     * 
     * 
     */
    public void loadConfig() {
        Properties prop = new Properties();
        
    	try {
               // load the properties file
                FileInputStream propertiesFile = new FileInputStream("config.properties");
    		prop.load(propertiesFile);
 
                // XBee vars
                // TODO: Allow pin to be digital or analogue, input or output. Current digital input.
                if(prop.getProperty("XBee").equals("true")) {
                    XBeeComPort = Integer.parseInt(prop.getProperty("XBeeComPort"));
                    XBeePins = new int[prop.getProperty("XBeePins").split(",").length];
                    for(int i=0; i<=prop.getProperty("XBeePins").split(",").length-1; i++){
                        XBeePins[i] = Integer.parseInt(prop.getProperty("XBeePins").split(",")[i]);
                    }
                    XBee = initializeXBee(); 
                }
               
                
                
                // close the properties file
                propertiesFile.close();
                
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
    }

    /**
     * opens the XBee COM port
     *
     * @todo Ensure a means of opening the correct COM port
     * @todo Need to allow this method to produce an error;
     *
     * @param XBeeComPort the COM port the XBee communicates on.
     */
    public XBee initializeXBee() {
        XBee tempXBee = new XBee();
        try {
            tempXBee.open("COM" + XBeeComPort, 9600);
            return tempXBee;
        } catch (XBeeException e) {
            //didn't open COM port
            return tempXBee; // TODO remove this line when adding error statement.
        }
    }
    
    

    /**
     * receives the numbers of the pins on the XBee to poll and returns their
     * digital value.
     *
     * @todo Ask whether this method should be altered to allow analog reads.
     *
     * @param XBeePins the number of the pins to query.
     */
    public Boolean[] pollXBeePins(int[] XBeePins) {
        Boolean[] XBeePinValues = new Boolean[XBeePins.length];

        try {
            RxResponseIoSample ioSample = (RxResponseIoSample) XBee.getResponse();
            System.out.println("We received a sample from " + ioSample.getSourceAddress());

            if (ioSample.containsDigital()) {
                for (int i = 0; i <= XBeePins.length-1; i++) {
                    XBeePinValues[i] = ioSample.getSamples()[0].isDigitalOn(XBeePins[i]);
                }
            }
        } catch (XBeeException e) {
            //didn't get response
        }

        return XBeePinValues;
    } 
}
