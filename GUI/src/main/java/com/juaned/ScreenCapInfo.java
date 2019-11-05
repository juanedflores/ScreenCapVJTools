package com.juaned;

import static com.juaned.Gridpage.commodoreFont;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.image.BufferedImage;

class ScreenCapInfo extends JPanel {

	private static final long serialVersionUID = 1L;

	/*
	 * [Swing Elements]
	 */
	/// #cleaned [Swing Elements]
	JPanel XYcoordsPanel, mainMonitorPanel, monitorButtonPanel, scaleFactorPanel, livePanel, liveStylePanel, chromaStylePanel;
	JPanel customScaleFactorPanel;
	JLabel xcoordLabel, ycoordLabel, monitorLabel, scaleFactorLabel, liveStyleLabel, customScaleFactorLabel;
	JButton[] monitorButton;
	JButton collectButton, saveLiveRectButton, chooseLiveLocationButton, scaleToProcessingButton, goLiveButton, liveStyleNormalButton;
	JButton liveStyleChroma1Button, liveStyleChroma2Button, canvasMapperButton;
	Color selectedLiveStyleColor = new Color(126, 87, 194);
	Border labelOutline = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY);
	JSpinner scaleFactorSpinner;
	SpinnerNumberModel scaleFactorNumModel = new SpinnerNumberModel(100, 20, 1000, 1);
	//#endregion

	/*
	 * [Handlers]
	 */
	/// #cleaned [Handlers]
	MonitorButtonHandler monitorButtonHandler = new MonitorButtonHandler();
	SpinnerHandler spinnerHandler = new SpinnerHandler();
	//#endregion

	public ScreenCapInfo() {

		setBackground(Color.GRAY);
		setPreferredSize(new Dimension(240, 600));
		setLayout(new GridLayout(10, 1));

		/*
		 * [Display Coordinates]
		 */
		/// #cleaned [Coordinates]
		XYcoordsPanel = new JPanel();
		XYcoordsPanel.setBackground(Color.BLACK);
		XYcoordsPanel.setLayout(new GridLayout(1, 2));
		// x and y coordinate labels.
		xcoordLabel = new JLabel("x");
		xcoordLabel.setForeground(Color.WHITE);
		xcoordLabel.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 0, Color.LIGHT_GRAY));
		xcoordLabel.setHorizontalAlignment(JLabel.CENTER);
		ycoordLabel = new JLabel("y");
		ycoordLabel.setForeground(Color.WHITE);
		ycoordLabel.setBorder(BorderFactory.createMatteBorder(3, 0, 3, 3, Color.LIGHT_GRAY));
		ycoordLabel.setHorizontalAlignment(JLabel.CENTER);
		// add to panel
		XYcoordsPanel.add(xcoordLabel);
		XYcoordsPanel.add(ycoordLabel);
		//#endregion

		/*
		 * [Monitor Panel]
		 */
		/// #cleaned [MonitorPanel]
		// get the current screens being used.
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screens = ge.getScreenDevices();

		// set the size of screens array to instantiate the button array.
		monitorButton = new JButton[screens.length];

		// this panel holds all the elements for this functionality.
		mainMonitorPanel = new JPanel();
		mainMonitorPanel.setBackground(Color.BLACK);
		mainMonitorPanel.setLayout(new BorderLayout());

		// this panel will hold all the buttons.
		monitorButtonPanel = new JPanel();
		monitorButtonPanel.setBackground(Color.BLACK);
		monitorButtonPanel.setLayout(new GridLayout(1, screens.length));

		// this label will indicate the user what the buttons below it are for.
		monitorLabel = new JLabel("screenshot monitor", SwingConstants.CENTER);
		Border labelPadding = BorderFactory.createEmptyBorder(4, 0, 8, 0);
		Border labelOutline = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY);
		Border compoundBorder = BorderFactory.createCompoundBorder(labelOutline, labelPadding);
		monitorLabel.setFont(commodoreFont.deriveFont(14f));
		monitorLabel.setBorder(compoundBorder);
		monitorLabel.setBackground(Color.GRAY);
		monitorLabel.setForeground(Color.WHITE);

		for (int i = 0; i < screens.length; i++) {

			monitorButton[i] = new JButton(Integer.toString(i));
			monitorButton[i].setBackground(Color.BLACK);
			monitorButton[i].setForeground(Color.YELLOW);
			monitorButton[i].setFocusable(false);
			monitorButton[i].setFocusPainted(false);
			monitorButton[i].setActionCommand(Integer.toString(i));
			monitorButton[i].addActionListener(monitorButtonHandler);

			monitorButtonPanel.add(monitorButton[i]);
		}
		// add the elements to the mainMonitorPanel
		mainMonitorPanel.add(monitorLabel, BorderLayout.NORTH);
		mainMonitorPanel.add(monitorButtonPanel, BorderLayout.CENTER);
		//#endregion

		/*
		 * [Collect Button]
		 */
		/// #cleaned [CollectButton]
		collectButton = new JButton("Scan Selection to Grid");
		collectButton.setBackground(new Color(251, 197, 49));
		collectButton.setForeground(Color.WHITE);
		collectButton.setFocusable(false);
		collectButton.setFocusPainted(false);
		collectButton.setEnabled(false);
		collectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// scan the rectangle windows
				GUI.screenCapDisplay.getSliceImg();
				GUI.myProcessingSketch.collectScreenshotBlbs();

				// disable all rect related buttons.
				collectButton.setEnabled(false);
				saveLiveRectButton.setEnabled(false);

				// show user a notifications.
				GUI.notification.setMessage("Collected", GUI.notification.success, true);
			}
		});

		//#endregion

		/*
		 * [Choose Live Location]
		 */
		/// #cleaned [LivePanel, saveLiveRectBtn, goLiveBtn]
		// this panel holds all the elements for this functionality.
		livePanel = new JPanel();
		livePanel.setBackground(Color.BLACK);
		livePanel.setLayout(new BorderLayout());

		// choose a live Rect.
		saveLiveRectButton = new JButton("Save Live Rect");
		saveLiveRectButton.setBackground(new Color(30, 55, 153));
		saveLiveRectButton.setForeground(Color.WHITE);
		saveLiveRectButton.setFocusable(false);
		saveLiveRectButton.setFocusPainted(false);
		saveLiveRectButton.setEnabled(false);
		saveLiveRectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (GUI.screenCapDisplay.drawnRect != null && GUI.screenCapDisplay.drawnRect.width > 0 && GUI.screenCapDisplay.drawnRect.height > 0) {

					// save where the rectangle is that is used to go live.
					GUI.screenCapDisplay.getSliceImg();

					// scaleToOutput is true. (default, if no custom scale is specified)
					GUI.screenCapDisplay.scaleToOutput = true;
					// save that box as the area to live capture.
					GUI.screenCapDisplay.liveCapRect = GUI.screenCapDisplay.scaledUpRect;

					// disable any saveRect button.
					collectButton.setEnabled(false);
					saveLiveRectButton.setEnabled(false);

					// enable the goLiveButton.
					if (!GUI.myProcessingSketch.goLive && goLiveButton.getBackground() != Color.RED && !GUI.screenCapDisplay.stopMainLiveButton.isVisible()) {
						// change goLiveButton background to red.
						goLiveButton.setBackground(Color.RED);
						goLiveButton.setEnabled(true);
					}

					// transfer the values of live capture values.
					GUI.screenCapDisplay.minLX = GUI.screenCapDisplay.minX;
					GUI.screenCapDisplay.maxLX = GUI.screenCapDisplay.maxX;
					GUI.screenCapDisplay.minLY = GUI.screenCapDisplay.minY;
					GUI.screenCapDisplay.maxLY = GUI.screenCapDisplay.maxY;

					// save which monitor the live rectangle was saved in.
					GUI.screenCapDisplay.liveMonitor = GUI.screenCapDisplay.monitorIndex;
					GUI.screenCapDisplay.startingXLive = GUI.screenCapDisplay.startingX;
					// get the graphics environment.
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice[] screens = ge.getScreenDevices();

					// create a new robot with that liveMonitor.
					try {
						GUI.screenCapDisplay.robotForLive = new Robot(screens[GUI.screenCapDisplay.liveMonitor]);
					} catch (AWTException e1) {
						e1.printStackTrace();
					}

					// repaint the panel.
					GUI.screenCapDisplay.repaint();

					// update user with a notification.
					GUI.notification.setMessage("saved live rect", GUI.notification.success, true);
				}
			}
		});

		// go Live button.
		goLiveButton = new JButton("Go Live");
		goLiveButton.setBackground(Color.GRAY);
		goLiveButton.setForeground(Color.WHITE);
		goLiveButton.setFocusable(false);
		goLiveButton.setFocusPainted(false);
		goLiveButton.setEnabled(false);
		goLiveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (GUI.screenCapDisplay.liveCapRect != null) {
					GUI.myProcessingSketch.noLoop();

					/// #cleaned [GUI Housekeeping]
					// change background to gray.
					goLiveButton.setBackground(Color.GRAY);
					// disable this button.
					goLiveButton.setEnabled(false);
					//#endregion

					/// #cleaned [Which Style Button is Highlighted]
					if (liveStyleNormalButton.getBackground() == selectedLiveStyleColor) {
						// set the appropriate live booleans.
						GUI.myProcessingSketch.livechroma = false;
						GUI.myProcessingSketch.chromaRedraw = true;
						GUI.myProcessingSketch.livenormal = true;
					} else if (liveStyleChroma1Button.getBackground() == selectedLiveStyleColor) {
						// set the appropriate live booleans.
						GUI.myProcessingSketch.livenormal = false;
						GUI.myProcessingSketch.livechroma = true;
						GUI.myProcessingSketch.chromaRedraw = true;
					} else if (liveStyleChroma2Button.getBackground() == selectedLiveStyleColor) {
						// set the appropriate live booleans.
						GUI.myProcessingSketch.livenormal = false;
						GUI.myProcessingSketch.livechroma = true;
						GUI.myProcessingSketch.chromaRedraw = false;
					}
					//#endregion

					GUI.myProcessingSketch.goLive = true;

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					// show user a notifications.
					GUI.notification.setMessage("we are live", Color.PINK, false);

					GUI.myProcessingSketch.liveCapture.background(0, 0);
					GUI.myProcessingSketch.loop();

					GUI.screenCapDisplay.stopMainLiveButton.setVisible(true);
				} else {
					// show user a notifications.
					GUI.notification.setMessage("no live capture saved", GUI.notification.warning, true);
				}
			}
		});
		livePanel.add(goLiveButton, BorderLayout.EAST);
		livePanel.add(saveLiveRectButton, BorderLayout.CENTER);
		//#endregion

		/*
		 * [Live Rect Button]
		 */
		/// #cleaned [chooseLiveLocationButton]
		chooseLiveLocationButton = new JButton("Choose Location");
		chooseLiveLocationButton.setBackground(new Color(7, 153, 146));
		chooseLiveLocationButton.setForeground(Color.YELLOW);
		chooseLiveLocationButton.setFocusable(false);
		chooseLiveLocationButton.setFocusPainted(false);
		chooseLiveLocationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// set that the user is going to choose a point for live location.
				GUI.screenCapDisplay.choosingLivePoint = true;

				// show user a notifications.
				GUI.notification.setMessage("Choose the minX & minY point for capture", GUI.notification.success, false);

				// get the Img of the Processing Sketch.
				BufferedImage bufimg = GUI.myProcessingSketch.getTotalCanvasImg();
				if (bufimg.getWidth() <= GUI.screenShotPanel.getWidth() && bufimg.getHeight() <= GUI.screenShotPanel.getHeight()) {
					System.out.println("it is smaller than GUI");
					GUI.screenCapDisplay.fullscreenImg = GUI.screenCapDisplay.scaleUpGUI(bufimg);
				} else {
					GUI.screenCapDisplay.fullscreenImg = GUI.screenCapDisplay.scaleDownGUI(bufimg);
				}

				// we are not displaying a screenshot of a monitor right now.
				GUI.screenCapDisplay.monitorIndex = -1;

				// disable any rect saving related button.
				GUI.screenCapInfo.collectButton.setEnabled(false);
				GUI.screenCapInfo.saveLiveRectButton.setEnabled(false);

				// update the panel.
				GUI.screenShotPanel.repaint();
			}
		});
		//#endregion

		/*
		 * [Scale Factor]
		 */
		/// #cleaned [ScaleFactor]
		// this panel holds all the elements for this functionality.
		scaleFactorPanel = new JPanel();
		scaleFactorPanel.setBackground(Color.BLACK);
		scaleFactorPanel.setLayout(new BorderLayout());

		// the label that shows what this section does.
		scaleFactorLabel = new JLabel("Scale Factor", SwingConstants.CENTER);
		scaleFactorLabel.setBorder(labelOutline);
		scaleFactorLabel.setForeground(Color.WHITE);

		customScaleFactorPanel = new JPanel();
		customScaleFactorPanel.setBackground(Color.BLACK);
		customScaleFactorPanel.setLayout(new BorderLayout());

		// the label that shows what this section does.
		customScaleFactorLabel = new JLabel("custom", SwingConstants.CENTER);
		customScaleFactorLabel.setForeground(Color.LIGHT_GRAY);
		customScaleFactorLabel.setBackground(Color.BLUE);

		// customScaleFactor Spinner.
		scaleFactorSpinner = new JSpinner(scaleFactorNumModel);
		scaleFactorSpinner.setName("scaleFactor");
		scaleFactorSpinner.addChangeListener(spinnerHandler);
		// center the number text.
		JComponent editor = scaleFactorSpinner.getEditor();
		JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
		spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);

		// add to the custom scale factor panel.
		customScaleFactorPanel.add(customScaleFactorLabel, BorderLayout.NORTH);
		customScaleFactorPanel.add(scaleFactorSpinner, BorderLayout.CENTER);

		// the button to press to scale to processing window.
		scaleToProcessingButton = new JButton("Scale to Processing");
		scaleToProcessingButton.setBackground(new Color(250, 152, 58));
		scaleToProcessingButton.setForeground(Color.WHITE);
		scaleToProcessingButton.setFocusable(false);
		scaleToProcessingButton.setFocusPainted(false);
		scaleToProcessingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				GUI.screenCapDisplay.scaleToOutput = true;

				// show user a notifications.
				GUI.notification.setMessage("scaled to processing", GUI.notification.success, false);

				GUI.myProcessingSketch.liveCapture.beginDraw();
				GUI.myProcessingSketch.liveCapture.background(0);
				GUI.myProcessingSketch.liveCapture.endDraw();
			}
		});

		scaleFactorPanel.add(scaleFactorLabel, BorderLayout.NORTH);
		scaleFactorPanel.add(customScaleFactorPanel, BorderLayout.EAST);
		scaleFactorPanel.add(scaleToProcessingButton, BorderLayout.CENTER);
		//#endregion

		/*
		 * [liveStylePanel]
		 */
		/// #cleaned [liveStylePanel]
		// this panel holds all the elements for this functionality.
		liveStylePanel = new JPanel();
		liveStylePanel.setBackground(Color.BLACK);
		liveStylePanel.setLayout(new BorderLayout());

		// the label that shows what this section does.
		liveStyleLabel = new JLabel("Live Display Style", SwingConstants.CENTER);
		liveStyleLabel.setBorder(labelOutline);
		liveStyleLabel.setForeground(Color.WHITE);

		liveStyleNormalButton = new JButton("Normal");
		liveStyleNormalButton.setBackground(selectedLiveStyleColor);
		liveStyleNormalButton.setForeground(Color.WHITE);
		liveStyleNormalButton.setFocusable(false);
		liveStyleNormalButton.setFocusPainted(false);
		liveStyleNormalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// change colors of buttons to update which is active.
				liveStyleNormalButton.setBackground(selectedLiveStyleColor);
				liveStyleChroma1Button.setBackground(Color.GRAY);
				liveStyleChroma2Button.setBackground(Color.GRAY);
				// set the appropriate live booleans.
				GUI.myProcessingSketch.livechroma = false;
				GUI.myProcessingSketch.chromaRedraw = true;
				GUI.myProcessingSketch.livenormal = true;
			}
		});

		liveStylePanel.add(liveStyleLabel, BorderLayout.NORTH);
		liveStylePanel.add(liveStyleNormalButton, BorderLayout.CENTER);
		//#endregion

		/*
		 * [liveStyleChromaPanel]
		 */
		/// #cleaned [liveStyleChromaPanel]
		// this panel holds all the elements for this functionality.
		chromaStylePanel = new JPanel();
		chromaStylePanel.setBackground(Color.BLACK);
		chromaStylePanel.setLayout(new GridLayout(1, 2));

		liveStyleChroma1Button = new JButton("Chroma1");
		liveStyleChroma1Button.setBackground(Color.GRAY);
		liveStyleChroma1Button.setForeground(Color.WHITE);
		liveStyleChroma1Button.setFocusable(false);
		liveStyleChroma1Button.setFocusPainted(false);
		liveStyleChroma1Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// change colors of buttons to update which is active.
				liveStyleChroma1Button.setBackground(selectedLiveStyleColor);
				liveStyleNormalButton.setBackground(Color.GRAY);
				liveStyleChroma2Button.setBackground(Color.GRAY);
				// set the appropriate live booleans.
				GUI.myProcessingSketch.livenormal = false;
				GUI.myProcessingSketch.livechroma = true;
				GUI.myProcessingSketch.chromaRedraw = true;
			}
		});

		liveStyleChroma2Button = new JButton("Chroma2");
		liveStyleChroma2Button.setBackground(Color.GRAY);
		liveStyleChroma2Button.setForeground(Color.WHITE);
		liveStyleChroma2Button.setFocusable(false);
		liveStyleChroma2Button.setFocusPainted(false);
		liveStyleChroma2Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// change colors of buttons to update which is active.
				liveStyleChroma2Button.setBackground(selectedLiveStyleColor);
				liveStyleChroma1Button.setBackground(Color.GRAY);
				liveStyleNormalButton.setBackground(Color.GRAY);
				// set the appropriate live booleans.
				GUI.myProcessingSketch.livenormal = false;
				GUI.myProcessingSketch.livechroma = true;
				GUI.myProcessingSketch.chromaRedraw = false;
			}
		});

		chromaStylePanel.add(liveStyleChroma1Button);
		chromaStylePanel.add(liveStyleChroma2Button);
		//#endregion

		// add all components to this panel.
		add(XYcoordsPanel);
		add(mainMonitorPanel);
		add(collectButton);
		add(livePanel);
		add(chooseLiveLocationButton);
		add(scaleFactorPanel);
		add(liveStylePanel);
		add(chromaStylePanel);
	}

	public class MonitorButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {

			// get the button in the grid that was clicked.
			String clickedButton = event.getActionCommand();
			int index_ = (int) Integer.parseInt(clickedButton);

			// switch to the monitor that was pressed by the user.
			try {
				GUI.screenCapDisplay.getFullScreenImg(index_);
			} catch (AWTException e1) {
				e1.printStackTrace();
			}

			// we are not choosing a point for the live window.
			GUI.screenCapDisplay.choosingLivePoint = false;

			// reset any drawn rectangle.
			GUI.screenCapDisplay.minX = 0;
			GUI.screenCapDisplay.minY = 0;
			GUI.screenCapDisplay.maxX = 0;
			GUI.screenCapDisplay.maxY = 0;

			GUI.screenShotPanel.repaint();
		}
	}

	public class SpinnerHandler implements ChangeListener {
		public void stateChanged(ChangeEvent event) {

			SpinnerModel spinnerModel = scaleFactorSpinner.getModel();
			Object source = event.getSource();
			if (spinnerModel instanceof SpinnerNumberModel) {

				JSpinner spinner = (JSpinner) source;
				String name = spinner.getName();
				if ("scaleFactor".equals(name)) {

					// get the value in the spinner.
					float value = ((SpinnerNumberModel) spinnerModel).getNumber().floatValue();

					// change the scaleFactor the value the user entered.
					GUI.screenCapDisplay.scaleToOutput = false;
					GUI.screenCapDisplay.userScaleFactor = (int) value;

				}
			}
		}
	}
}
