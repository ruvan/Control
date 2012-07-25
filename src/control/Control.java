package control;

import com.rapplogic.xbee.api.*;
import com.rapplogic.xbee.api.wpan.*;
import gnu.io.RXTXCommDriver;

/**
 *
 * @author Ruvan Muthu-Krishna
 */
public class Control {

//  Variables which can be altered    
    static int[] XBeePinNumbers = {0, 1, 2, 3};
    static int XBeeComPort = 4;
    static XBee XBee;

    /**
     * Runs initialization methods based on given args then enters infinite loop
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        XBee = initializeXBee();
        
        while (true) {
            Boolean[] XBeePinValues = pollXBeePins(XBeePinNumbers);
            for(int i=0; i<=XBeePinValues.length-1; i++) {
                System.out.println("Pin number " + i + " is " + XBeePinValues[i]);
            }
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
    public static XBee initializeXBee() {
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
     * @param XBeePinNumbers the number of the pins to query.
     */
    public static Boolean[] pollXBeePins(int[] XBeePinNumbers) {
        Boolean[] XBeePinValues = new Boolean[XBeePinNumbers.length];

        try {
            RxResponseIoSample ioSample = (RxResponseIoSample) XBee.getResponse();
            System.out.println("We received a sample from " + ioSample.getSourceAddress());

            if (ioSample.containsDigital()) {
                for (int i = 0; i <= XBeePinNumbers.length-1; i++) {
                    XBeePinValues[i] = ioSample.getSamples()[0].isDigitalOn(i);
                }
            }
        } catch (XBeeException e) {
            //didn't get response
        }

        return XBeePinValues;
    }
}
