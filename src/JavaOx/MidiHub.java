/**
 * Author: Donya Quick
 * Last modified: 19-Nov-2014
 */
package JavaOx;

import java.util.List;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * MidiHub implementation for JavaOx
 * 
 * This class is intended to serve as a "middle man" between MIDI input and 
 * output devices to allow merging and splitting streams between multiple 
 * devices. 
 * 
 * @author Donya Quick
 */
public class MidiHub {
    
    private MidiUnit[] inputs; // all input devices and their status settings
    private MidiUnit[] outputs; // all output devices and their status settings
    private OffReceiver offReceiver = new OffReceiver();
    private OnReceiver onReceiver; // is initialized later
    
    /**
     * Default constructor. If used, must call setDevices later.
     */
    public MidiHub() {
    }
    
    /**
     * Sets all input and output devices.
     * @param ins All input devices (to interface to MidiUtils.getInputDevices())
     * @param outs All output devices (to interface to MidiUtils.getOutputDevices())
     */
    public void setDevices(List<MidiDevice> ins, List<MidiDevice> outs) {
        inputs = new MidiUnit[ins.size()];
        outputs = new MidiUnit[outs.size()];
        
        // build MidiUnits for inputs
        for (int i=0; i<inputs.length; i++) {
            inputs[i] = new MidiUnit(ins.get(i));
        }
        
        // build MidiUnits for outputs
        for (int i=0; i<outputs.length; i++) {
            outputs[i] = new MidiUnit(outs.get(i));
        }
        
        // start the middle man "on" receiver
        onReceiver = new OnReceiver();
    }
    
    /**
     * Primary constructor.
     * @param ins All input devices (to interface to MidiUtils.getInputDevices())
     * @param outs All output devices (to interface to MidiUtils.getOutputDevices())
     */
    public MidiHub(List<MidiDevice> ins, List<MidiDevice> outs) {
        setDevices(ins,outs);
    }
    
    /**
     * Opens all devices.
     */
    public void openAll() {
        // open the inputs
        for (int i=0; i<inputs.length; i++) {
            try {
                // open device
                inputs[i].device.open();
                // send it to the "off" receiver by default
                inputs[i].device.getTransmitter().setReceiver(offReceiver);
                System.out.println("Input device "+i+" is open.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        
        // open all outputs
        for (int i=0; i<outputs.length; i++) {
            try {
                // open device
                outputs[i].device.open();
                System.out.println("Output device "+i+" is open");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    /**
     * Closes all devices.
     */
    public void closeAll() {
        for (int i=0; i<inputs.length; i++) {
            try {
                inputs[i].device.close();
                System.out.println("Input device "+i+" closed.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        for (int i=0; i<outputs.length; i++) {
            try {
                outputs[i].device.close();
                System.out.println("Output device "+i+" closed.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    /**
     * Update the "on" status of a MIDI input device.
     * @param i The index of the device to update.
     * @param b Whether data should be received from the device (true = "on").
     */
    public void setInputStatus(int i, boolean b) {
        if (i>=0 && i<=inputs.length) {
            inputs[i].status = b;
            try {
                updateTransmitters(inputs[i].device, b);
                if (b) {
                    System.out.println("Input #"+i+" enabled.");
                } else {
                    System.out.println("Input #"+i+" disabled.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Bad input device index: "+i);
        }
    }
    
    /**
     * Transmitter adjustment.
     * @param device
     * @param on 
     */
    private void updateTransmitters(MidiDevice device, boolean on) {
        //get all transmitters
        List<Transmitter> transmitters = device.getTransmitters();
        //and for each transmitter

        for(int j = 0; j<transmitters.size();j++) {
            //create a new receiver
            try {
                if(on) {
                    transmitters.get(j).setReceiver(onReceiver);
                } else {
                    transmitters.get(j).setReceiver(offReceiver);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    /**
     * For enabling/disabling outputs.
     * @param i The index of the device.
     * @param b Whether it should receive data (true = "on").
     */
    public void setOutputStatus(int i, boolean b) {
        if (i>=0 && i<=outputs.length) {
            outputs[i].status = b;
            if (b) {
                System.out.println("Output #"+i+" enabled.");
            } else {
                System.out.println("Output #"+i+" disabled.");
            }
        } else {
            System.out.println("Bad output device index: "+i);
        }
    }
    
    /**
     * Private class for handling device/status pairs.
     */
    class MidiUnit {
        public MidiDevice device;
        public boolean status;
        public MidiUnit() {}
        public MidiUnit(MidiDevice d) {
            device = d;
            status = false;
        }
    }
    
    /**
     * "Do nothing" receiver class.
     */
    private class OffReceiver implements Receiver {
        public OffReceiver() {
            // do nothing
        }
        public void send(MidiMessage m, long timeStamp) {
            // do nothing
        }
        public void close() {
            // do nothing
        }
    }
    
    /**
     * Broadcasting receiver class.
     */
    private class OnReceiver implements Receiver  {
      public OnReceiver() {
         // do nothing
      }
       
      @Override
      public void send(MidiMessage message, long timeStamp) {
         for (int i=0; i<outputs.length; i++) {
             if (outputs[i].status) {
                try {
                    outputs[i].device.getReceiver().send(message, timeStamp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
             }
         }
      }
 
      @Override
      public void close() {
         // do nothing - closeAll must be called manually
      }
   }
}
