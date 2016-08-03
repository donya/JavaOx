/**
 * Author: Donya Quick
 * Last modified: 19-Nov-2014
 */
package JavaOx;

import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;



/**
 * MidiUtils implementation for JavaOx
 * 
 * Functions for fetching device lists and device information.
 * 
 * @author Donya Quick
 */
public class MidiUtils {

    /*
    The following two global variables are meant to get rid of "garbage" 
    devices that are either Java-specific or that usually don't behave 
    properly. For example, the default MS synth on Windows produces two 
    entires: the MIDI mapper and the GS Wavetable synth, but only one should
    be used (usually the MIDI mapper).
    */
    private static boolean filterDevices = false; 
    private static String[] badDevs = new String[] {
        "Gervill",
        "Microsoft GS Wavetable Synth",
        "Real Time Sequencer",
    };
    
    public static boolean okDevName(String s) {
        boolean ok = true;
        if (filterDevices) {
            int i=0;
            while (ok && i<badDevs.length) {
                if (s.equals(badDevs[i])){
                    ok = false;
                }
                i++;
            }
        }
        return ok;
    }
    
    /**
     * Get information about all available MIDI input devices.
     * @return 
     */
    public static List<MidiDevice.Info> getInputDeviceInfo() {
        MidiDevice.Info[] allDevices = MidiSystem.getMidiDeviceInfo();
        List<MidiDevice.Info> inDevs = new ArrayList<>();
        for (int i = 0; i < allDevices.length; i++) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(allDevices[i]);
                // Does the device send MIDI messages?
                if (device.getMaxTransmitters() != 0) {
                    if (okDevName(allDevices[i].getName())) {
                        inDevs.add(allDevices[i]);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return inDevs;
    }
    
    /**
     * Get a list of all MIDI input devices.
     * @return 
     */
    public static List<MidiDevice> getInputDevices() {
        MidiDevice.Info[] allDevices = MidiSystem.getMidiDeviceInfo();
        List<MidiDevice> inDevs = new ArrayList<>();
        for (int i = 0; i < allDevices.length; i++) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(allDevices[i]);
                // Does the device send MIDI messages?
                if (device.getMaxTransmitters() != 0) {
                    if (okDevName(allDevices[i].getName())) {
                        inDevs.add(device);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return inDevs;
    }
    
    /**
     * Get information about all available MIDI output devices.
     * @return 
     */
    public static List<MidiDevice.Info> getOutputDeviceInfo() {
        MidiDevice.Info[] allDevices = MidiSystem.getMidiDeviceInfo();
        List<MidiDevice.Info> outDevs = new ArrayList<>();
        for (int i = 0; i < allDevices.length; i++) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(allDevices[i]);
                // Does the device receive MIDI messages?
                if (device.getMaxReceivers() != 0) {
                    if (okDevName(allDevices[i].getName())) {
                        outDevs.add(allDevices[i]);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return outDevs;
    }
    
    /**
     * Get all available MIDI output devices.
     * @return 
     */
    public static List<MidiDevice> getOutputDevices() {
        MidiDevice.Info[] allDevices = MidiSystem.getMidiDeviceInfo();
        List<MidiDevice> outDevs = new ArrayList<>();
        for (int i = 0; i < allDevices.length; i++) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(allDevices[i]);
                // Does the device Receive MIDI messages?
                if (device.getMaxReceivers() != 0) {
                    if (okDevName(allDevices[i].getName())) {
                        outDevs.add(device);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return outDevs;
    }
    
    /**
     * Fetches the names for a list of MidiDevices (from their Info)
     * @param devInfos list of device information (intended for use with 
     * getInputDeviceInfo() or getOutputDeviceInfo())
     * @return an array of device names as strings
     */
    public static String[] deviceNames(List<MidiDevice.Info> devInfos) {
        String[] s = new String[devInfos.size()];
        for (int i=0; i<s.length; i++) {
            s[i] = devInfos.get(i).getName();
        }
        return s;
    }
    
    /**
     * Print a list of MIDI device information.
     * @param devs List of devices information to print.
     */
    public static void printDeviceInfo(List<MidiDevice.Info> devs) {
        String[] s = deviceNames(devs);
        for (int i=0; i<s.length; i++) {
            System.out.println(s[i]);
        }
    }
}
