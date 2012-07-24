/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;
    import com.rapplogic.xbee.api.*;
    import com.rapplogic.xbee.api.wpan.*;
    import gnu.io.RXTXCommDriver;
/**
 *
 * @author mk.ruvan
 */
public class Control {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       // begin XBee Initilization
       XBee xbee = new XBee();         
       try {
            xbee.open("COM4", 9600);
       } catch(XBeeException e) {
           //didn't open COM port
       }
       // end XBee Initilization
       
        while (true) {
            // begin XBee read
            try { 
                RxResponseIoSample ioSample = (RxResponseIoSample) xbee.getResponse();
                System.out.println("We received a sample from " + ioSample.getSourceAddress());     
                        
                if (ioSample.containsDigital()) {
                    for (int i=0; i<=3; i++) {
                        System.out.println("Pin " + i + " is " + ioSample.getSamples()[0].isDigitalOn(i));
                    }
                }
            } catch(XBeeException e) {
                //didn't get response
            }
            // end XBee read

            
        }
    }
}
