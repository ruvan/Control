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
    Boolean[][] relayTable;
    BigTriangle[][] bigTriangle;

    public RelaySequencer(SerialPort relaySerialPort, InputStream relayInputStream, OutputStream relayOutputStream, Boolean[][] relayTable, BigTriangle[][] bigTriangle) {
        this.relaySerialPort = relaySerialPort;
        this.relayInputStream = relayInputStream;
        this.relayOutputStream = relayOutputStream;
        this.bigTriangle = bigTriangle;
    }

    public void send(int i) {
        try {
            relayOutputStream.write(i);
        } catch (IOException ex) {
            ex.getMessage();
        }
    }

    public void sleep(int time) {
        try {
            Thread.currentThread().sleep(time);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void runSequence() {
        turnOn();
        System.out.println("Starting chase bloom");
        for(int level = 0; level < 3; level++){
            for(int triangle = 0; triangle < 12; triangle++){
                bigTriangle[level][triangle].allOn();
                updateRelays();
                sleep(2);
                bigTriangle[level][triangle].allOff();
                updateRelays();
            }
        }
    }

    public void turnOff() {
        //pull in all actuators
        for (int bank = 1; bank < 19; bank++) {
            for (int relay = 1; relay < 8; relay++) {
                relayTable[bank][relay] = false;
            }
        }
        updateRelays();

        // TODO: put in wait command, be weary of this pausing other aspects of totem.

        // turn off PSU's
        for (int bank = 0; bank < 19; bank++) {
            relayTable[bank][0] = false;
        }
        // relay coil 12V supply
        relayTable[0][1] = false;
        updateRelays();
    }

    public void turnOn() {
        // turn on all PSU's
        for (int bank = 0; bank < 19; bank++) {
            relayTable[bank][0] = true;
        }
        // relay coil 12V supply
        relayTable[0][1] = true;
        updateRelays();
    }

    public void updateRelays() {
        for (int bank = 0; bank < 19; bank++) {
            int command = 0;
            for (int relay = 0; relay < 8; relay++) {
                if (relayTable[bank][relay]) {
                    command += Math.pow(2, relay);
                }
            }
            send(254);
            send(140);
            send(command);
            send(bank + 1); // +1 because ProXR starts at 1
        }
    }
}
