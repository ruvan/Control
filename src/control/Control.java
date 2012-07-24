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
    XBee XBee = new XBee();

    /**
     * Runs initialization methods based on given args then enters infinite loop
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        initializeXBee(XBee);


        while (true) {
        }
    }

    /**
     * opens the XBee COM port
     *
     * @todo Ensure a means of opening the correct COM port
     *
     * @param XBeeComPort the COM port the XBee communicates on.
     */
    public void initializeXBee(XBee XBee) {
        try {
            XBee.open("COM" + XBeeComPort, 9600);
        } catch (XBeeException e) {
            //didn't open COM port
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
    public Boolean[] pollXBeePins(int[] XBeePinNumbers) {
        Boolean[] XBeePinValues = new Boolean[XBeePinNumbers.length];

        // begin XBee read
        try {
            RxResponseIoSample ioSample = (RxResponseIoSample) XBee.getResponse();
            System.out.println("We received a sample from " + ioSample.getSourceAddress());

            if (ioSample.containsDigital()) {
                for (int i = 0; i <= 3; i++) {
                    System.out.println("Pin " + i + " is " + ioSample.getSamples()[0].isDigitalOn(i));
                }
            }
        } catch (XBeeException e) {
            //didn't get response
        }
        // end XBee read  

        return XBeePinValues;
    }
}
