package net.robig.stlab.gui.web;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPasswordField;

import net.robig.logging.Logger;
import net.robig.net.WebAccess;
import net.robig.stlab.StLab;
import net.robig.stlab.StLabConfig;
import net.robig.stlab.gui.DeviceFrame;
import net.robig.stlab.model.WebPreset;
import net.robig.stlab.model.WebPresetList;

import javax.swing.JTextArea;

public class WebControlFrame extends javax.swing.JFrame {

	private Logger log = new Logger(this);
	private static WebControlFrame instance=null;
	{
		instance=this;
	}
	public static WebControlFrame getInstance(){
		if(instance==null) new WebControlFrame();
		return instance;
	}
	
	private static final long serialVersionUID = 1L;
	private WebAccess web=new WebAccess();  //  @jve:decl-index=0:
	private net.robig.stlab.util.config.StringValue savedUsername=StLabConfig.getWebUsername();

	private JTabbedPane jTabbedPane = null;
	private JPanel searchPanel = null;
	private JScrollPane jScrollPane = null;
	private JTable presetTable = null;
	private JPanel searchControlsPanel = null;
	private JLabel searchTextLabel = null;
	private JTextField searchTextField = null;
	private JButton startSearchButton = null;
	private JPanel loginTabPanel = null;
	private JLabel loginUsernameLabel = null;
	private JLabel loginPasswordLabel = null;
	private JTextField loginUsernameTextField = null;
	private JPasswordField loginPasswordField = null;
	private JButton loginButton = null;
	private JButton loginRegisterNewButton = null;
	private JLabel loginInfoLabel = null;
	private JPanel aPanel = null;
	private JPanel topPresetsPanel = null;
	private JPanel sharePanel = null;
	private ActionListener loginActionListener;
	private JLabel shareTopLabel = null;
	private JPanel sharePropertiesPanel = null;
	private JPanel sharePanel2 = null;
	private JLabel shareTitleLabel = null;
	private JTextField shareTitleTextField = null;
	private JLabel jLabel = null;
	private JTextArea shareDescriptionTextArea = null;
	private JLabel shareTagsLabel = null;
	private JTextArea shareTagsTextArea = null;
	private JComponent shareSetupPanel = null;
	private JLabel shareSetupLabel = null;
	private JButton sharePublishButton = null;

	/**
	 * This method initializes 
	 * 
	 */
	public WebControlFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new Dimension(586, 620));
        this.setTitle("StLab Web");
        this.setContentPane(getJTabbedPane());
			
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("Login", null, getLoginTabPanel(), null);
			jTabbedPane.addTab("Search", null, getSearchPanel(), "Search for presets");
			jTabbedPane.addTab("Top 10", null, getTopPresetsPanel(), null);
			jTabbedPane.addTab("Share", null, getSharePanel(), null);
			jTabbedPane.setEnabledAt(3, false);
			jTabbedPane.setEnabledAt(2, false); //TODO enable top 10
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes searchPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSearchPanel() {
		if (searchPanel == null) {
			searchPanel = new JPanel();
			searchPanel.setLayout(new BorderLayout());
			searchPanel.setEnabled(false);
			searchPanel.add(getJScrollPane(), BorderLayout.CENTER);
			searchPanel.add(getSearchControlsPanel(), BorderLayout.NORTH);
		}
		return searchPanel;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getPresetTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes presetTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getPresetTable() {
		if (presetTable == null) {
			presetTable = new JTable();
		}
		return presetTable;
	}

	/**
	 * This method initializes searchControlsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSearchControlsPanel() {
		if (searchControlsPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.weightx = 1.0;
			searchTextLabel = new JLabel();
			searchTextLabel.setText("Search for keyword:");
			searchControlsPanel = new JPanel();
			searchControlsPanel.setLayout(new GridBagLayout());
			searchControlsPanel.add(searchTextLabel, new GridBagConstraints());
			searchControlsPanel.add(getSearchTextField(), gridBagConstraints);
			searchControlsPanel.add(getStartSearchButton(), new GridBagConstraints());
		}
		return searchControlsPanel;
	}

	/**
	 * This method initializes searchTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getSearchTextField() {
		if (searchTextField == null) {
			searchTextField = new JTextField();
			searchTextField.setPreferredSize(new Dimension(200,20));
		}
		return searchTextField;
	}

	/**
	 * This method initializes startSearchButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getStartSearchButton() {
		if (startSearchButton == null) {
			startSearchButton = new JButton();
			startSearchButton.setText("Find");
			startSearchButton.setToolTipText("Find a preset by keyword");
			startSearchButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					onSearch();
				}
			});
		}
		return startSearchButton;
	}
	
	protected void onSearch() {
		List<WebPreset> result=web.find(new TextSearchCondition(getSearchTextField().getText().trim()));
		if(result!=null){
			presetTable.setModel(new WebPresetList(result));
		}else{
			log.error("search failed "+web.getMessage());
		}
	}

	/**
	 * This method initializes loginTabPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLoginTabPanel() {
		if (loginTabPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 2;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 0;
			loginInfoLabel = new JLabel();
			loginInfoLabel.setText("Please login:");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 3;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 3;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 2;
			loginPasswordLabel = new JLabel();
			loginPasswordLabel.setText("Password:");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			loginUsernameLabel = new JLabel();
			loginUsernameLabel.setText("Username:");
			loginTabPanel = new JPanel();
			loginTabPanel.setLayout(new GridBagLayout());
			loginTabPanel.add(loginInfoLabel, gridBagConstraints7);
			loginTabPanel.add(loginUsernameLabel, gridBagConstraints1);
			loginTabPanel.add(loginPasswordLabel, gridBagConstraints2);
			loginTabPanel.add(getLoginUsernameTextField(), gridBagConstraints3);
			loginTabPanel.add(getLoginButton(), gridBagConstraints5);
			loginTabPanel.add(getLoginRegisterNewButton(), gridBagConstraints6);
			loginTabPanel.add(getAPanel(), gridBagConstraints8);
		}
		return loginTabPanel;
	}

	/**
	 * This method initializes loginUsernameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getLoginUsernameTextField() {
		if (loginUsernameTextField == null) {
			loginUsernameTextField = new JTextField();
			loginUsernameTextField.setPreferredSize(new Dimension(300, 20));
			if(savedUsername!=null)
				loginUsernameTextField.setText(savedUsername.getValue());
			loginUsernameTextField.addActionListener(getLoginActionListener());
		}
		return loginUsernameTextField;
	}

	/**
	 * This method initializes loginPasswordField	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getLoginPasswordField() {
		if (loginPasswordField == null) {
			loginPasswordField = new JPasswordField();
			loginPasswordField.setText("08150835");
			loginPasswordField.setPreferredSize(new Dimension(300,20));
			loginPasswordField.addActionListener(getLoginActionListener());
		}
		return loginPasswordField;
	}
	
	private ActionListener getLoginActionListener(){
		if(loginActionListener==null){
			loginActionListener=new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onLogin();
				}
			};
		}
		return loginActionListener;
	}

	/**
	 * This method initializes loginButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLoginButton() {
		if (loginButton == null) {
			loginButton = new JButton();
			loginButton.setText("Login");
			loginButton.addActionListener(getLoginActionListener());
		}
		return loginButton;
	}

	protected void onLogin() {
		String user=getLoginUsernameTextField().getText().trim();
		String pass=new String(getLoginPasswordField().getPassword());
		boolean success=web.login(user, pass);
		if(success){
			getLoginButton().setEnabled(false);
			getLoginUsernameTextField().setEnabled(false);
			getLoginPasswordField().setEnabled(false);
			getLoginRegisterNewButton().setEnabled(false);
			savedUsername.setValue(user);
			loginInfoLabel.setText("Successfully logged in.");
			jTabbedPane.setEnabledAt(3, true);
		}else{
			loginInfoLabel.setText("Login failed! "+web.getMessage());
		}
	}

	/**
	 * This method initializes loginRegisterNewButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLoginRegisterNewButton() {
		if (loginRegisterNewButton == null) {
			loginRegisterNewButton = new JButton();
			loginRegisterNewButton.setText("Register");
		}
		return loginRegisterNewButton;
	}

	/**
	 * This method initializes aPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAPanel() {
		if (aPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints4.gridy = -1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridx = -1;
			aPanel = new JPanel();
			aPanel.setLayout(new BorderLayout());
			aPanel.add(getLoginPasswordField(), BorderLayout.NORTH);
		}
		return aPanel;
	}

	/**
	 * This method initializes topPresetsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTopPresetsPanel() {
		if (topPresetsPanel == null) {
			topPresetsPanel = new JPanel();
			topPresetsPanel.setLayout(new GridBagLayout());
		}
		return topPresetsPanel;
	}

	/**
	 * This method initializes sharePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSharePanel() {
		if (sharePanel == null) {
			jLabel = new JLabel();
			jLabel.setText("Description:");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridy = 1;
			shareTopLabel = new JLabel();
			shareTopLabel.setText("Share your current preset/settings with the world:");
			sharePanel = new JPanel();
			sharePanel.setLayout(new GridBagLayout());
			sharePanel.setEnabled(false);
			sharePanel.add(shareTopLabel, new GridBagConstraints());
			sharePanel.add(getSharePropertiesPanel(), new GridBagConstraints());
			sharePanel.add(getSharePanel2(), gridBagConstraints9);
		}
		return sharePanel;
	}
	
	/**
	 * This method initializes sharePropertiesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSharePropertiesPanel() {
		if (sharePropertiesPanel == null) {
			sharePropertiesPanel = new JPanel();
//			sharePropertiesPanel.
		}
		return sharePropertiesPanel;
	}

	/**
	 * This method initializes sharePanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSharePanel2() {
		if (sharePanel2 == null) {
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 1;
			gridBagConstraints17.gridy = 4;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.gridy = 3;
			shareSetupLabel = new JLabel();
			shareSetupLabel.setText("My Setup:");
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 1;
			gridBagConstraints15.gridy = 3;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = GridBagConstraints.BOTH;
			gridBagConstraints14.gridy = 2;
			gridBagConstraints14.weightx = 1.0;
			gridBagConstraints14.weighty = 1.0;
			gridBagConstraints14.gridx = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.gridy = 2;
			shareTagsLabel = new JLabel();
			shareTagsLabel.setText("Searchable tags:");
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.BOTH;
			gridBagConstraints12.gridy = 1;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.weighty = 1.0;
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints10.weightx = 1.0;
			shareTitleLabel = new JLabel();
			shareTitleLabel.setText("Preset Title/Name:");
			sharePanel2 = new JPanel();
			sharePanel2.setLayout(new GridBagLayout());
			sharePanel2.add(shareTitleLabel, new GridBagConstraints());
			sharePanel2.add(getShareTitleTextField(), gridBagConstraints10);
			sharePanel2.add(jLabel, gridBagConstraints11);
			sharePanel2.add(getShareDescriptionTextArea(), gridBagConstraints12);
			sharePanel2.add(shareTagsLabel, gridBagConstraints13);
			sharePanel2.add(getShareTagsTextArea(), gridBagConstraints14);
			sharePanel2.add(getShareSetupPanel(), gridBagConstraints15);
			sharePanel2.add(shareSetupLabel, gridBagConstraints16);
			sharePanel2.add(getSharePublishButton(), gridBagConstraints17);
		}
		return sharePanel2;
	}

	/**
	 * This method initializes shareTitleTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getShareTitleTextField() {
		if (shareTitleTextField == null) {
			shareTitleTextField = new JTextField();
			shareTitleTextField.setPreferredSize(new Dimension(290, 20));
		}
		return shareTitleTextField;
	}

	/**
	 * This method initializes shareDescriptionTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getShareDescriptionTextArea() {
		if (shareDescriptionTextArea == null) {
			shareDescriptionTextArea = new JTextArea();
			shareDescriptionTextArea.setTabSize(4);
			shareDescriptionTextArea.setRows(5);
		}
		return shareDescriptionTextArea;
	}

	/**
	 * This method initializes shareTagsTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getShareTagsTextArea() {
		if (shareTagsTextArea == null) {
			shareTagsTextArea = new JTextArea();
			shareTagsTextArea.setRows(4);
		}
		return shareTagsTextArea;
	}

	/**
	 * This method initializes shareAuthorPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JComponent getShareSetupPanel() {
		if (shareSetupPanel == null) {
			shareSetupPanel=new JPanel();
//TODO			shareSetupPanel = StLab.getSetupPreferences().getComponent();
		}
		return shareSetupPanel;
	}

	/**
	 * This method initializes sharePublishButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSharePublishButton() {
		if (sharePublishButton == null) {
			sharePublishButton = new JButton();
			sharePublishButton.setText("Publish Preset");
			sharePublishButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					onPublish();
				}
			});
		}
		return sharePublishButton;
	}
	
	protected void onPublish(){
		WebPreset preset = new WebPreset();
		preset.setTitle(getShareTitleTextField().getText().trim());
		preset.setDescription(getShareDescriptionTextArea().getText().trim());
		preset.setTags(getShareTagsTextArea().getText().trim());
		preset.setData(DeviceFrame.getInctance().requestPreset());
		boolean success=web.publish(preset);
		if(success){
			JOptionPane.showMessageDialog(this, "Published successfully");
		}else{
			JOptionPane.showMessageDialog(this, "sharing preset failed! "+web.getMessage());
		}
	}

	public static void main(String[] args) {
		JFrame frame=new WebControlFrame();
		frame.show();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
