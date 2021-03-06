package net.robig.stlab.midi.commands;

/**
 * Midi command for switching to another preset
 * @author robegroe
 *
 */
public class SwitchPresetCommand extends AbstractMidiCommand {

	int index = 0;
	
	public SwitchPresetCommand(int idx) {
		functionCode="4e";
		index = idx;
		expectData=true;
		expectedReturnCode="23";
	}
	
	@Override
	public void run() {
		sendData("00"+toHexString(index));
	}

}
