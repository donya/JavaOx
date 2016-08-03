/**
 * Author: Donya Quick
 * Last modified: 19-Nov-2014
 * 
 * GUI and main function implementation for JavaOx.
 */
package JavaOx;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.midi.MidiDevice;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * GUI and main class for JavaOx.
 * @author Donya Quick
 */
public class GUI extends JFrame implements ActionListener{
    // global variables
    private JCheckBox[] inBoxes;
    private JCheckBox[] outBoxes;
    private List<MidiDevice.Info> inDevInfo;
    private List<MidiDevice.Info> outDevInfo;
    private List<MidiDevice> inDevs;
    private List<MidiDevice> outDevs;
    private int maxNum = 0;
    private MidiHub hub;
    
    /**
     * Main function
     * @param args No arguments are taken.
     */
    public static void main(String[] args) {
        GUI g = new GUI();        
        g.setVisible(true);
    }
    
    
    /**
     * GUI constructor. Builds the entire GUI and handles all MIDI preparation.
     */
    public GUI() {
        // build panels
        JPanel p = makeInputPanels();
        this.add(p);
        // calculate best size
        int header = 40; // to allow for title bar
        int ppf = 30; // pixels per field (height)
        int width = 500;
        int height = maxNum * ppf + header;
        this.setSize(width,height);
        this.setTitle("JavaOx");
        
        // initialize MIDI connections
        hub = new MidiHub(inDevs, outDevs);
        
        // set up handling of window closing to close MIDI devices
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                hub.closeAll();
                System.exit(0);
            }
        });
        
        // open all devices
        hub.openAll();
    }
    
    /**
     * Builds the left and right MIDI input checkbox panels.
     * @return the JPanel containing both sub-panels.
     */
    public JPanel makeInputPanels() {
        String s = "";
        JPanel pIn = new JPanel();
        JPanel pOut = new JPanel();
        JPanel pAll = new JPanel();
        pAll.setLayout(new GridLayout(1,2));
        inDevInfo = MidiUtils.getInputDeviceInfo();
        outDevInfo = MidiUtils.getOutputDeviceInfo();
        inDevs = MidiUtils.getInputDevices();
        outDevs = MidiUtils.getOutputDevices();
        int numIn = inDevs.size();
        int numOut = outDevs.size();
        pIn.add(new JLabel("  MIDI Inputs"));
        pOut.add(new JLabel("  MIDI Outputs"));
        maxNum = Math.max(numIn, numOut); // maximum number of devices
        inBoxes = new JCheckBox[maxNum];
        outBoxes = new JCheckBox[maxNum];
        pIn.setLayout(new GridLayout(maxNum+1,1));
        pOut.setLayout(new GridLayout(maxNum+1,1));
        
        // initialize the check boxes for input
        for (int i=0; i<numIn; i++) {
            inBoxes[i] = new JCheckBox(inDevInfo.get(i).getName(), false);
            pIn.add(inBoxes[i]);
            inBoxes[i].addActionListener(this);
            inBoxes[i].setActionCommand("In "+i);
        }
        // add empty space as needed
        for (int i=numIn; i<maxNum; i++) {
            pIn.add(new JLabel(""));
        }
        
        // initialize the check boxes for output
        for (int i=0; i<numOut; i++) {
            outBoxes[i] = new JCheckBox(outDevInfo.get(i).getName(), false);
            pOut.add(outBoxes[i]);
            outBoxes[i].addActionListener(this);
            outBoxes[i].setActionCommand("Out "+i);
        }
        
        // add empty space as needed
        for (int i=numOut; i<maxNum; i++) {
            pOut.add(new JLabel(""));
        }
        
        // set borders and add sub-panels to larger panel
        pIn.setBorder(new BevelBorder(BevelBorder.LOWERED));
        pOut.setBorder(new BevelBorder(BevelBorder.LOWERED));
        pAll.add(pIn);
        pAll.add(pOut);
        return pAll;
    }
    
    /**
     * Event handling for the GUI. Triggers whenever a checkbox is ticked/unticked.
     * @param event 
     */
    public void actionPerformed(ActionEvent event) {
        String s = event.getActionCommand();
        int i;
        // Is it an input checkbox?
        if (s.startsWith("In")) {
            i = Integer.parseInt(s.substring(3));
            // Is the checkbox ticked? Set status accordingly.
            if (inBoxes[i].isSelected()) {
                hub.setInputStatus(i, true); 
            } else {
                hub.setInputStatus(i, false);
            }
        } else
        // Is it an output checkbox?
        if (s.startsWith("Out")) {
            i = Integer.parseInt(s.substring(4));
            // Is the checkbox ticked? Set status accordingly.
            if (outBoxes[i].isSelected()) {
                hub.setOutputStatus(i, true);
            } else {
                hub.setOutputStatus(i, false);
            }
        } else {
            System.out.println("Unrecognized command: "+s);
        }
    }
}
