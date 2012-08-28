package control;

import com.rapplogic.xbee.api.*;
import com.rapplogic.xbee.api.wpan.*;
import gnu.io.RXTXCommDriver;
import java.io.FileOutputStream; //Kept for rewriting config file
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.sound.midi.*;
import java.io.*;
import gnu.io.*;

//TODO: add Serial
/**
 *
 * @author Ruvan Muthu-Krishna
 */
public class Control {

//  Variables which can be altered    
    String programName;
    // XBee
    static int[] XBeePins;
    int XBeeComPort;
    XBee XBee;
    // MIDI
    static Boolean MIDI;
    Synthesizer synth;
    static Receiver rcvr;
    static String configPath;
    // Relay
    static Boolean relay = false;
    SerialPort relaySerialPort;
    InputStream relayInputStream;
    static OutputStream relayOutputStream;

    /**
     * Runs initialization methods based on given args then enters infinite loop
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Control ctrl = new Control();
        configPath = args[0];
        String midiPath = args[1];
        ctrl.loadConfig();

        while (true) {

            // Relay Section
            if (relay == true) {
                ctrl.playRelay();
            }

            // MIDI
            // note,channel,pitch,on/off,pause
            // cc,channel,cc#,value,pause
            // Microsoft GS Wavetable SW Synth

            if (MIDI == true) {
                try {
                    FileInputStream fstream = new FileInputStream(midiPath);
                    //FileInputStream fstream = new FileInputStream("midi.txt");
                    // Get the object of DataInputStream
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String midiLine;
                    // Read each line in the file
                    while ((midiLine = br.readLine()) != null) {
                        if (midiLine.charAt(0) != (char) '#') {
                            String[] line = midiLine.split(",");
                            if (line[0].equals("note")) {
                                int channel = Integer.parseInt(line[1]);
                                int pitch = Integer.parseInt(line[2]);
                                ShortMessage msg = new ShortMessage();
                                if (line[3].equals("on")) {
                                    msg.setMessage(ShortMessage.NOTE_ON, channel, pitch, 60);
                                    System.out.println("playing " + pitch + " on channel " + channel);
                                } else {
                                    msg.setMessage(ShortMessage.NOTE_OFF, channel, pitch, 60);
                                    System.out.println("stoping " + pitch + " on channel " + channel);
                                }
                                rcvr.send((MidiMessage) msg, 0);
                            } else { // else we got a CC
                                int channel = Integer.parseInt(line[1]);
                                int cc = Integer.parseInt(line[2]);
                                int ccValue = Integer.parseInt(line[3]);
                                ShortMessage msg = new ShortMessage();
                                msg.setMessage(ShortMessage.CONTROL_CHANGE, channel, cc, ccValue);
                                rcvr.send((MidiMessage) msg, 0);
                                System.out.println("sending cc " + cc + "value " + ccValue);
                            }

                            if (Integer.parseInt(line[4]) != 0) {
                                try {
                                    Thread.currentThread().sleep(Integer.parseInt(line[4]));
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                    //Close the input stream
                    in.close();
                } catch (Exception e) {//Catch exception if any
                    System.err.println("Error: " + e.getMessage());
                }
                System.out.println("Looping...");
            }

        }
        /*
         * XBee while (true) { Boolean[] XBeePinValues =
         * ctrl.pollXBeePins(XBeePins); for(int i=0; i<=XBeePinValues.length-1;
         * i++) { System.out.println("Pin number " + i + " is " +
         * XBeePinValues[i]); } }
         *
         */
    }

    /**
     * Loads configuration file config.properties and assigns specified values
     * to variables once required values have been set the file is close so as
     * to not cause file lock problems.
     *
     *
     */
    public void loadConfig() {
        Properties prop = new Properties();

        try {
            // load the properties file
            //FileInputStream propertiesFile = new FileInputStream("C:\\Control\\src\\control\\config.properties");
            FileInputStream propertiesFile = new FileInputStream(configPath);
            prop.load(propertiesFile);

            programName = prop.getProperty("ProgramName");

            // XBee vars
            // TODO: Allow pin to be digital or analogue, input or output. Current digital input.
            if (prop.getProperty("XBee").equals("true")) {
                XBeeComPort = Integer.parseInt(prop.getProperty("XBeeComPort"));
                XBeePins = new int[prop.getProperty("XBeePins").split(",").length];
                for (int i = 0; i <= prop.getProperty("XBeePins").split(",").length - 1; i++) {
                    XBeePins[i] = Integer.parseInt(prop.getProperty("XBeePins").split(",")[i]);
                }
                XBee = initializeXBee();
            }

            // MIDI vars
            if (prop.getProperty("MIDI").equals("true")) {
                initializeMidi(prop.getProperty("MIDIDeviceName"));
                MIDI = true;
            } else {
                MIDI = false;
            }

            // Relay vars
            if (prop.getProperty("Relay").equals("true")) {
                relay = true;
                relaySerialPort = initializeSerial(prop.getProperty("RelayComPort"), Integer.parseInt(prop.getProperty("RelayBaud")));
                relayInputStream = relaySerialPort.getInputStream();
                relayOutputStream = relaySerialPort.getOutputStream();
            }



            // close the properties file
            propertiesFile.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Open a serial connection note: assumes databits=8, stopbits=1, no parity
     * and no flow control.
     */
    public SerialPort initializeSerial(String port, int baud) throws IOException {
        SerialPort serialPort;
        try {
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(port);
            serialPort = (SerialPort) portId.open(programName, 5000);
            serialPort.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        } catch (NoSuchPortException ex) {
            throw new IOException("No such port");
        } catch (PortInUseException ex) {
            throw new IOException("Port in use");
        } catch (UnsupportedCommOperationException ex) {
            throw new IOException("Unsupported serial port parametes");
        }
        return serialPort;
    }

    /**
     * Open the MIDI connection
     */
    public void initializeMidi(String MIDIDeviceName) {
        try {
            MidiDevice MIDIDevice;
            MidiDevice.Info[] MIDIDevices = MidiSystem.getMidiDeviceInfo();
            System.out.println("Devices found:");
            for (int i = 0; i < MIDIDevices.length; i++) {
                System.out.println(MIDIDevices[i].getName());
            }
            for (int i = 0; i < MIDIDevices.length; i++) {
                if (MIDIDevices[i].getName().equalsIgnoreCase(MIDIDeviceName)) {
                    MIDIDevice = MidiSystem.getMidiDevice(MIDIDevices[i]);
                    if (MidiSystem.getMidiDevice(MIDIDevices[i]).getMaxReceivers() != 0) {
                        MIDIDevice = MidiSystem.getMidiDevice(MIDIDevices[i]);
                        System.out.println("Attempting to play on " + MIDIDevices[i].getName());
                        MIDIDevice.open();
                        rcvr = MIDIDevice.getReceiver();
                    }
                }
            }
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * opens the XBee COM port
     *
     * @todo Ensure a means of opening the correct COM port @todo Need to allow
     * this method to produce an error;
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
                for (int i = 0; i <= XBeePins.length - 1; i++) {
                    XBeePinValues[i] = ioSample.getSamples()[0].isDigitalOn(XBeePins[i]);
                }
            }
        } catch (XBeeException e) {
            //didn't get response
        }

        return XBeePinValues;
    }
    
    /**
     * Allows Geoffrey to add his own relay code.
     * 
     */
    public void playRelay(){
        System.out.println("Starting relay loop");
                try {
                    relayOutputStream.write(254);
                    relayOutputStream.write(8);
                } catch (IOException ex) {
                    ex.getMessage();
                }
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                try {
                    relayOutputStream.write(254);
                    relayOutputStream.write(0);
                } catch (IOException ex) {
                    ex.getMessage();
                }
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
    }
}
