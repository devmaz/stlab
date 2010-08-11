package net.robig.stlab.gui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JMenuBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;

import net.robig.gui.HoldableImageSwitch;
import net.robig.gui.ImageButton;
import net.robig.gui.ImagePanel;
import net.robig.gui.ImageSwitch;
import net.robig.gui.IntegerValueKnob;
import net.robig.gui.LED;
import net.robig.logging.Logger;
import net.robig.stlab.model.StPreset;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DeviceFrame extends JFrame {

	protected Logger log = new Logger(this.getClass());
	private StPreset currentPreset=new StPreset();
	private GuiDeviceController device=null;
	private Boolean receiving = false;
	long lastUpdate = 0;
	int maxChangesPerSecond=1;
	private boolean optionMode=false;
	
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private ImagePanel devicePanel = null;
	private JToolBar toolBar = null;
	private JMenuBar menu = null;
	private JMenu fileMenu = null;
	private JMenuItem optionsMenuItem = null;
	private JMenuItem exitMenuItem = null;
	private ImagePanel back = devicePanel;
	
	//Controls:
	private IntegerValueKnob volumeKnob = new IntegerValueKnob();
	private IntegerValueKnob bassKnob = new IntegerValueKnob();
	private IntegerValueKnob middleKnob = new IntegerValueKnob();
	private IntegerValueKnob trebleKnob = new IntegerValueKnob();
	private IntegerValueKnob gainKnob = new IntegerValueKnob();
	private IntegerValueKnob ampKnob = new IntegerValueKnob();
	
	private class LittleKnob extends IntegerValueKnob {
		@Override
		protected String getImageFile() {
			return "img/lknob.png";
		}
	}
	
	private class LongButton extends ImageButton {
		public LongButton() {
			imageFile="img/button_long.png";
//			setBorder(new LineBorder(new Color(0,0,255)));
			init();
		}
	}
	
	private class SmallButton extends ImageButton {
		public SmallButton() {
			imageFile="img/button.png";
//			setBorder(new LineBorder(new Color(0,0,255)));
			init();
		}
	}
	
	private class PresetSwitch extends ImageButton {
		public PresetSwitch(){
			//setBorder(new LineBorder(new Color(0,0,255)));
			init();
		}
	}
	
	private LittleKnob pedalKnob = new LittleKnob();
	private LittleKnob pedalEditKnob = new LittleKnob();
	private LittleKnob delayKnob = new LittleKnob();
	private LittleKnob delayEditKnob = new LittleKnob();
	private LittleKnob reverbKnob = new LittleKnob();
	
	private PresetSwitch prevPreset = new PresetSwitch(){
		public void onClick() {
			device.prevPreset();
		};
	};
	private PresetSwitch nextPreset = new PresetSwitch(){
		public void onClick() {
			device.nextPreset();
		};
	};
	
	private LongButton ampModeSwitch = new LongButton(){
		
	};
	
	private LED cabinetLed = new LED();
	private HoldableImageSwitch cabinetOptionSwitch = new HoldableImageSwitch(cabinetLed){
		public void onClick() {
			if(isOptionMode()) setOptionMode(false);
			else {
				updatePreset();
				sendPresetChange(true);
			}
		};
		protected void onHold() {
			setOptionMode(!isOptionMode());
		};
	};
	
	private LED pedalLed = new LED();
	private LED delayLed = new LED();
	private LED reverbLed = new LED(); 
	private ImageSwitch pedalSwitch = new ImageSwitch(pedalLed){
		public void onClick() {
			updatePreset();
			sendPresetChange(true);
		};
	};
	private ImageSwitch delaySwitch = new ImageSwitch(delayLed){
		public void onClick() {
			updatePreset();
			sendPresetChange(true);
		};
	};
	private ImageSwitch reverbSwitch = new ImageSwitch(reverbLed){
		public void onClick() {
			updatePreset();
			sendPresetChange(true);
		};
	};
	
	private LED tapLed = new LED();
	private SmallButton tapButton = new SmallButton(); 
	
	//Display:
	private DisplayPanel display = new DisplayPanel();
	private JTextArea output;

	/**
	 * This is the default constructor
	 */
	public DeviceFrame(IDeviceController ctrl) {
		super();
		device=new GuiDeviceController(ctrl,this);
		initialize();
		initDevice();
	}
	
	public void initDevice(){
		setCurrentPreset(device.initialize());
	}
	
	/**
	 * update the preset data and the correnspondign GUI elements
	 * @param preset
	 */
	public void setCurrentPreset(StPreset preset){
		synchronized (currentPreset) {
			currentPreset=preset;
			updateGui();
		}
	}

	
	/**
	 * is receiving mode enabled?
	 */
	private boolean isReceiving() {
		synchronized (receiving) {
			return receiving;
		}
	}
	
	/**
	 * switch to Receive-mode where no control changes were processed
	 * @param r
	 */
	private void setReceiving(boolean r){
		synchronized (receiving) {
			receiving=r;
		}
	}
	
	public boolean isOptionMode() {
		return optionMode;
	}

	public void setOptionMode(boolean optionMode) {
		this.optionMode = optionMode;
	}

	/** 
	 * Gui contolls have changed, update the preset for later submitting to the device
	 */
	public void updatePreset() {
		setReceiving(true);
		currentPreset.setAmp(ampKnob.getValue());
		currentPreset.setGain(gainKnob.getValue());
		currentPreset.setTreble(trebleKnob.getValue());
		currentPreset.setMiddle(middleKnob.getValue());
		currentPreset.setBass(bassKnob.getValue());
		currentPreset.setVolume(volumeKnob.getValue());
		currentPreset.setPedalEffect(pedalKnob.getValue());
		currentPreset.setPedalEdit(pedalEditKnob.getValue());
		currentPreset.setDelayEffect(delayKnob.getValue());
		currentPreset.setReverbEffect(reverbKnob.getValue());
		//TODO: currentPreset.setAmpType(ampType)
		currentPreset.setCabinetEnabled(cabinetOptionSwitch.isActive());
		//TODO: currentPreset.setCabinet(cabinet)
		currentPreset.setDelayEnabled(delaySwitch.isActive());
		currentPreset.setPedalEnabled(pedalSwitch.isActive());
		currentPreset.setReverbEnabled(reverbSwitch.isActive());
		setReceiving(false);
	}
	
	public void updateGui(){
		setReceiving(true);
		
		ampKnob.setValue(currentPreset.getAmp());
		gainKnob.setValue(currentPreset.getGain());
		trebleKnob.setValue(currentPreset.getTreble());
		middleKnob.setValue(currentPreset.getMiddle());
		bassKnob.setValue(currentPreset.getBass());
		volumeKnob.setValue(currentPreset.getVolume());
		pedalKnob.setValue(currentPreset.getPedalEffect());
		pedalEditKnob.setValue(currentPreset.getPedalEdit());
		delayKnob.setValue(currentPreset.getDelayEffect());
		reverbKnob.setValue(currentPreset.getReverbEffect());
		
		cabinetOptionSwitch.setActive(currentPreset.isCabinetEnabled());
		pedalSwitch.setActive(currentPreset.isPedalEnabled());
		delaySwitch.setActive(currentPreset.isDelayEnabled());
		reverbSwitch.setActive(currentPreset.isReverbEnabled());
		
		display.setValue(currentPreset.getNumber());
		setReceiving(false);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		ampKnob.setBounds(new Rectangle(64, 165, 100, 100));
		gainKnob.setBounds(new Rectangle(194, 165, 100, 100));
		trebleKnob.setBounds(new Rectangle(292, 165, 100, 100));
		middleKnob.setBounds(new Rectangle(390, 165, 100, 100));
		bassKnob.setBounds(new Rectangle(489, 165, 100, 100));
		volumeKnob.setBounds(new Rectangle(587, 165, 100, 100));
		display.setBounds(new Rectangle(588,275,29*2,49));
		pedalKnob.setBounds(new Rectangle(65,311,65,65));
		pedalEditKnob.setBounds(new Rectangle(160,311,65,65));
		delayKnob.setBounds(new Rectangle(277,311,65,65));
		delayEditKnob.setBounds(new Rectangle(373,311,65,65));
		reverbKnob.setBounds(new Rectangle(469,311,65,65));
		
		prevPreset.setBounds(new Rectangle(201,543,32,32));
		nextPreset.setBounds(new Rectangle(465,543,32,32));
		
		ampModeSwitch.setBounds(new Rectangle(89,135,24,12));
		cabinetOptionSwitch.setBounds(new Rectangle(175,135,24,12));
		pedalSwitch.setBounds(new Rectangle(51,405,24,12));
		delaySwitch.setBounds(new Rectangle(264,405,24,12));
		reverbSwitch.setBounds(new Rectangle(459,405,24,12));
		
		pedalLed.setBounds(new Rectangle(82,406,12,12));
		delayLed.setBounds(new Rectangle(294,406,12,12));
		reverbLed.setBounds(new Rectangle(489,406,12,12));
		cabinetLed.setBounds(new Rectangle(205,136,12,12));
		
		tapLed.setBounds(new Rectangle(382,364,12,12));
		tapButton.setBounds(new Rectangle(394,385,28,28));
		
		
		this.setJMenuBar(getMenu());
		this.setContentPane(getJContentPane());
		this.setSize(940, 691);
		this.setTitle("Tonelab Device");
		this.setName("StLab");
		getLogOutput();
		
		volumeKnob.setName("Volume");
		bassKnob.setName("Bass");
		middleKnob.setName("Middle");
		trebleKnob.setName("Treble");
		gainKnob.setName("Gain");
		ampKnob.setName("AMP");
		ampKnob.setMaxValue(11);
		
		pedalKnob.setName("Pedal Effect");
		pedalEditKnob.setName("Pedal Edit");
		pedalKnob.setMaxValue(11);
		delayKnob.setName("Mod/Delay Effect");
		delayEditKnob.setName("Mod/Delay Edit");
		reverbKnob.setMaxValue(11);
		reverbKnob.setName("Reverb");
		
		prevPreset.setName("Previous Preset");
		nextPreset.setName("Next Preset");
		
		ampModeSwitch.setName("Switch AMP Type");
		cabinetOptionSwitch.setName("Cabinet/Option");
		pedalSwitch.setName("Pedal effect");
		delaySwitch.setName("Delay");
		reverbSwitch.setName("Reverb");

		pedalLed.setName("Pedal effect");
		delayLed.setName("Delay");
		reverbLed.setName("Reverb");
		
		initListeners();
	}
	
	// Init control listeners:
	private void initListeners(){
		//Listeners for the display change:
		for(IntegerValueKnob k: new IntegerValueKnob[]{
			ampKnob,
			gainKnob,
			trebleKnob,
			middleKnob,
			bassKnob,
			volumeKnob,
			//mini's:
			pedalKnob,
			pedalEditKnob,
			delayKnob,
			delayEditKnob,
			reverbKnob
		}){
			k.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					if(isReceiving()) return;
					IntegerValueKnob knob = (IntegerValueKnob) e.getSource();
					log.debug("Knob changed: "+knob.getName()+" value="+knob.getValue());
					display.setValue(knob.getValue());
					updatePreset();
					sendPresetChange(!knob.isDragging());
				}
			});
		}
		
		// CLose Button of window:
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                quit();
            }
        });
	}

	private void sendPresetChange(boolean exclusive) {
		long now=System.currentTimeMillis();
		if(exclusive || (now-lastUpdate) > 1000/maxChangesPerSecond){
			lastUpdate=now;
			device.activateParameters(currentPreset);
		}
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if(jContentPane==null){
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getDevicePanel(), BorderLayout.CENTER);
			
			
		}
		return jContentPane;
	}
	
	private JPanel getDevicePanel() {
		if (devicePanel == null) {
			devicePanel = new ImagePanel(ImagePanel.loadImage("img/TonelabST.png"));
			devicePanel.setSize(940, 671);
			// Controls:
			devicePanel.setLayout(null);
			devicePanel.add(getToolBar(), null);
			devicePanel.add(ampKnob, null);
			devicePanel.add(volumeKnob, null);
			devicePanel.add(bassKnob, null);
			devicePanel.add(middleKnob, null);
			devicePanel.add(trebleKnob, null);
			devicePanel.add(gainKnob, null);
			devicePanel.add(display, null);
			devicePanel.add(pedalKnob, null);
			devicePanel.add(pedalEditKnob, null);
			devicePanel.add(delayKnob, null);
			devicePanel.add(delayEditKnob, null);
			devicePanel.add(reverbKnob, null);
			devicePanel.add(nextPreset, null);
			devicePanel.add(prevPreset, null);
			devicePanel.add(ampModeSwitch, null);
			devicePanel.add(cabinetOptionSwitch, null);
			
			devicePanel.add(cabinetLed, null);
			devicePanel.add(pedalLed, null);
			devicePanel.add(delayLed, null);
			devicePanel.add(reverbLed, null);
			devicePanel.add(pedalSwitch, null);
			devicePanel.add(delaySwitch, null);
			devicePanel.add(reverbSwitch, null);
			
			devicePanel.add(tapLed, null);
			devicePanel.add(tapButton, null);
		}
		return devicePanel;
	}

	/**
	 * This method initializes toolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new JToolBar();
			toolBar.setBounds(new Rectangle(0, 0, 940, 4));
		}
		return toolBar;
	}

	/**
	 * quit the application
	 */
	public void quit() {
		device.disconnect();
		System.exit(0);
	}
	
	/**
	 * This method initializes the menu	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getMenu() {
		if (menu == null) {
			menu = new JMenuBar();
			fileMenu=new JMenu("File");
			menu.add(fileMenu);
			optionsMenuItem = new JMenuItem("Options");
			optionsMenuItem.setMnemonic(KeyEvent.VK_O);
			fileMenu.add(optionsMenuItem);
			exitMenuItem = new JMenuItem("Exit");
			exitMenuItem.setMnemonic(KeyEvent.VK_W);
			fileMenu.add(exitMenuItem);
			
			exitMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					quit();
				}
			});
		}
		return menu;
	}
	
	/**
	 * initialize info/error output panel
	 * @return
	 */
	private JTextArea getLogOutput() {
		if(output==null){
			output = new JTextArea();
		    output.setEditable(false);
		    output.setColumns(3);
		    output.setBackground(new Color(200,200,200));
		    output.setForeground(new Color(255,200,200));
		    output.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
		    JScrollPane scroller = new JScrollPane();
		    scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    scroller.getViewport().setView(output);
		    add(scroller,BorderLayout.SOUTH);
		}
		return output;
	}
	
	/**
	 * Output status information
	 * @param text
	 */
	public void output(String text){
		if(output==null) return;
		output.append(text+"\n");
		if(output.getLineCount()>3)
			try {
				output.setText(output.getText().substring(output.getLineStartOffset(output.getLineCount()-3)));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
  		output.setCaretPosition(output.getText().length()-1);
	}
	
	/**
	 * for testing with dummy controller (without device)
	 * @param args
	 */
	public static void main(String[] args) {
		System.getProperties().setProperty("apple.laf.useScreenMenuBar", "true");
		System.getProperties().setProperty("com.apple.macos.useScreenMenuBar","true");
		//TODO:System.getProperties().setProperty("com.apple.mrj.application.apple.menu.about.name","StLab");
		new DeviceFrame(new DummyDeviceController()).show();
		
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"