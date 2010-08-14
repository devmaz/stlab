package net.robig.stlab;

import javax.swing.JOptionPane;
import net.robig.logging.Logger;
import net.robig.stlab.gui.DeviceFrame;
import net.robig.stlab.gui.SplashWindow;
import net.robig.stlab.midi.DeviceController;
import net.robig.stlab.midi.AbstractMidiController;
import net.robig.stlab.midi.MidiControllerFactory;

public class StLab {
	static Logger log = new Logger(StLab.class); 
    public static void main(String[] args) throws Exception {

    	//Display Menu in MacOS Menubar:
		System.getProperties().setProperty("apple.laf.useScreenMenuBar", "true");
		System.getProperties().setProperty("com.apple.macos.useScreenMenuBar","true");
    	
    	//show loading screen:
    	SplashWindow splash = new SplashWindow("img/stlab.png",null);
    	
    	// Initialize Conifg:
    	new StLabConfig();
    	
    	try {
			final AbstractMidiController midiController=MidiControllerFactory.create();
			midiController.findAndConnectToVOX();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error initializing Midi system: "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace(log.getErrorPrintWriter());
			System.exit(1);
		}
		
		// main window:
		DeviceFrame deviceFrame = new DeviceFrame(new DeviceController());
		deviceFrame.setVisible(true);
		
		// Close splash screen
		splash.close();
		
    }
}
