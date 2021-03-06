package net.robig.stlab.gui;

import net.robig.stlab.model.DeviceInfo;
import net.robig.stlab.model.StPreset;

public interface IDeviceController {
	public DeviceInfo getDeviceInfo();
	public StPreset initialize() throws Exception;
	public void findAndConnect() throws Exception;
	public StPreset getCurrentParameters() throws Exception;
	public StPreset getPresetParameters(int number) throws Exception;
	public void activateParameters(StPreset preset) throws Exception;
	public void savePreset(StPreset preset, int pid);
//	public void nextPreset() throws Exception;
//	public void prevPreset() throws Exception;
	public void selectPreset(int i) throws Exception;
	public void disconnect();
	public void addDeviceListener(IDeviceListener l);
}
