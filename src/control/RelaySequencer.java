/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Ruvan Muthu-Krishna
 */
public class RelaySequencer {

    SerialPort relaySerialPort;
    InputStream relayInputStream;
    OutputStream relayOutputStream;

    public RelaySequencer(SerialPort relaySerialPort, InputStream relayInputStream, OutputStream relayOutputStream) {
        this.relaySerialPort = relaySerialPort;
        this.relayInputStream = relayInputStream;
        this.relayOutputStream = relayOutputStream;
    }
    
    public void send(int i) {
        try {
            relayOutputStream.write(i);
        } catch (IOException ex) {
            ex.getMessage();
        }
    }

    public void runSequence() {
        System.out.println("Starting relay loop");
        send(254);
        send(8);
    }
}
