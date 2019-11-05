package com.juaned;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Menu {

	/*
	 * [Fonts]
	 */
	/// #cleaned [Font]
	public static Font commodoreFont;
	Font textInputFont;
	Font menuFont;
	//#endregion

	/*
	 * [Swing elements and related variables]
	 */
	/// #cleaned [Swing Elements]
	JPanel bottomPanel, topPanel, centerTopPanel;
	JLabel modeLabel;
	JTextArea arrowT;
	JTextPane menuTextPane;
	JScrollPane menuScrollPane;
	JTextField userInput;
	JButton goToMenuButton;
	//#endregion

	/*
	* [Colors for Menu]
	*/
	/// #cleaned [Colors for Menu]
	Style unhighlightedStyle;
	SimpleAttributeSet highlightedStyle;
	Color backgroundColor = new Color(30, 30, 30);
	Color currentHeaderColor = new Color(255, 255, 255);
	Color generalCol = new Color(255, 255, 200);
	Color drawCol = new Color(100, 200, 255);
	Color editCol = new Color(100, 255, 140);
	Color displayCol = new Color(255, 200, 255);
	//#endregion

	/* 
	 * [Handle Menu]
	 */
	/// #cleaned [Handle Menu]
	int menuPage = 0;
	String menuHeader;
	String menuBody;
	ArrayList<String> choices = new ArrayList<String>();
	ArrayList<String> description = new ArrayList<String>();
	//#endregion

	/*
	 * [Handlers]
	 */
	/// #cleaned [Handlers]
	MenuInputHandler menuInputHandler = new MenuInputHandler();
	KeyHandler keyhandler = new KeyHandler();
	TextDrawer textdrawer = new TextDrawer();
	//#endregion

	public Menu() {

		/*
		 * [Set up Fonts]
		 */
		/// #cleaned [Fonts]
		InputStream fontInputStream = getClass().getResourceAsStream("/fonts/com.ttf");
		try {
			// create a font from the .tff file of a commodore 64 font.
			commodoreFont = Font.createFont(Font.TRUETYPE_FONT, fontInputStream);
			// adjust the sizes for each font.
			textInputFont = commodoreFont.deriveFont(24f);
			menuFont = commodoreFont.deriveFont(14f);
		} catch (FontFormatException e1) {
			System.out.println("FontFormatException");
			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Font IO Exception");
			e1.printStackTrace();
		}
		//#endregion

		/*
		 * [Set up Menu Display]
		 */
		/// #cleaned [Menu Display]
		// Set up menuTextPane
		menuTextPane = new JTextPane();
		// set up the background and foreground.
		menuTextPane.setBackground(new Color(30, 30, 30));
		menuTextPane.setForeground(Color.WHITE);
		// set up the Font.
		menuTextPane.setFont(commodoreFont);
		// make JTextPane not focusable or editable.
		menuTextPane.setHighlighter(null);
		menuTextPane.setEditable(false);
		menuTextPane.setFocusable(false);
		/// [menuScrollPane]
		menuScrollPane = new JScrollPane(menuTextPane);
		// set up the location and size.
		int cornerOffset = 15;
		int menuScrollPaneWidth = 600 - (cornerOffset + cornerOffset);
		int menuScrollPaneHeight = 600 - (cornerOffset + cornerOffset);
		menuScrollPane.setLocation(cornerOffset, cornerOffset);
		menuScrollPane.setSize(menuScrollPaneWidth, menuScrollPaneHeight);
		// set up the border.
		AdvancedBevelBorder advancedBorder = new AdvancedBevelBorder(new Color(191, 191, 191), new Color(112, 112, 112), new Color(142, 142, 142), new Color(210, 210, 210), new Color(101, 101, 101), 5);
		Border blackPadding = BorderFactory.createMatteBorder(10, 10, 10, 10, Color.black);
		Border outerBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black);
		Border preTextBoxBorder = BorderFactory.createCompoundBorder(advancedBorder, blackPadding);
		Border textBoxBorder = BorderFactory.createCompoundBorder(outerBorder, preTextBoxBorder);
		menuScrollPane.setBorder(textBoxBorder);
		/// [Style]
		// set up the default unhighlighted text look.
		unhighlightedStyle = menuTextPane.addStyle("Standard Style", null);
		StyleConstants.setBackground(unhighlightedStyle, backgroundColor);
		// set up how a highlighted menu option would look.
		highlightedStyle = new SimpleAttributeSet();
		StyleConstants.setFontSize(highlightedStyle, 18);
		StyleConstants.setItalic(highlightedStyle, true);
		StyleConstants.setBackground(highlightedStyle, backgroundColor);
		StyleConstants.setForeground(highlightedStyle, Color.YELLOW);
		/// [Align]
		// make the text align to center and start at 1/5 the way up at the top of the screen.
		try {
			menuTextPane.setEditorKit(new MenuEditorKit());
			SimpleAttributeSet attrs = new SimpleAttributeSet();
			StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_CENTER);
			StyledDocument doc = (StyledDocument) menuTextPane.getDocument();
			doc.setParagraphAttributes(0, doc.getLength() - 1, attrs, false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//#endregion

		/*
		 * [Set Up Bottom Panel]
		 */
		/// #cleaned [Bottom Panel]
		bottomPanel = new JPanel(new BorderLayout());

		/// [userInput]
		/// Set up the JTextField that will take user input.
		// set up user input text field.
		userInput = new JTextField();
		// set up background and foreground.
		userInput.setBackground(Color.BLACK);
		userInput.setForeground(Color.WHITE);
		// set up the Font.
		userInput.setFont(textInputFont);
		// set up the caret/cursor appearance.
		userInput.setCaret(new BlockCaret());
		userInput.setCaretColor(Color.WHITE);
		// set up the border for userInput.
		Border inputBorder = BorderFactory.createMatteBorder(4, 0, 0, 0, Color.GRAY);
		userInput.setBorder(inputBorder);
		// add the actionListener for userInput.
		userInput.addActionListener(menuInputHandler);
		userInput.addKeyListener(keyhandler);

		/// [arrowT]
		/// Set up input arrow for aesthetic reasons.
		arrowT = new JTextArea(">", 1, 1);
		// set up background and foreground.
		arrowT.setBackground(Color.BLACK);
		arrowT.setForeground(Color.WHITE);
		// set up the Font.
		arrowT.setFont(textInputFont);
		// set up the Border of arrowT.
		Border padding = BorderFactory.createEmptyBorder(30, 15, 30, 15);
		Border arrowTBorder = BorderFactory.createCompoundBorder(inputBorder, padding);
		arrowT.setBorder(arrowTBorder);
		// disable any function from arrowT.
		arrowT.setEditable(false);
		arrowT.setEnabled(false);
		arrowT.setDisabledTextColor(Color.WHITE);

		/// [add elements to bottom Panel]
		bottomPanel.add(arrowT, BorderLayout.WEST);
		bottomPanel.add(userInput, BorderLayout.CENTER);
		//#endregion

		/*
		 * [Set Up Top Panel]
		 */
		/// #cleaned [Top Panel]
		topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(Color.WHITE);
		// set up border for topPanel.
		Border topEmpty = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		Border topLine = BorderFactory.createMatteBorder(3, 0, 0, 0, Color.black);
		Border topPanelBorder = BorderFactory.createCompoundBorder(topLine, topEmpty);
		topPanel.setBorder(topPanelBorder);

		/// [modeLabel]
		/// a label that will indicate what current state/mode the program is in.
		modeLabel = new JLabel();
		modeLabel.setFont(textInputFont);

		/// [centerTopPanel]
		/// a simple panel that can hold elements.
		centerTopPanel = new JPanel(new BorderLayout());
		centerTopPanel.setBackground(Color.WHITE);
		centerTopPanel.add(GUI.screenCapDisplay.goToScreenShtButton, BorderLayout.EAST);
		centerTopPanel.add(GUI.screenCapDisplay.stopMainLiveButton, BorderLayout.WEST);

		/// [goToMenuButton]
		/// a button that will go to menu.
		goToMenuButton = new JButton("menu");
		goToMenuButton.setBackground(Color.BLACK);
		goToMenuButton.setForeground(Color.WHITE);
		goToMenuButton.setFocusable(false);
		goToMenuButton.setFocusPainted(false);
		goToMenuButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// jump to menu mode.
				menuMode(menuPage);

				/// #cleaned [GUI Maintanence]
				// if the screenshotPanel is visible, make it not visible.
				if (GUI.screenShotPanel.isVisible()) {
					GUI.screenShotPanel.setVisible(false);
				}
				// if the sketchMapperPanel is visible, make it not visible.
				if (GUI.sketchMapperPanel.isVisible()) {
					GUI.sketchMapperPanel.setVisible(false);

					// since we are using a transparent PGraphics, we should redraw so it doesn't stick.

				}
				// enable userInput JTextField in case it was disabled.
				userInput.setEnabled(true);

				// put away notification if it is visible.
				GUI.notification.setVisible(false);

				// remove and add appropriate listeners.
				userInput.removeActionListener(textdrawer);

				//@ is this choosingLivePoint necessary?
				GUI.screenCapDisplay.choosingLivePoint = false;

				// remove the menu button and replace it with the goToScreenShtButton.
				centerTopPanel.remove(goToMenuButton);
				centerTopPanel.add(GUI.screenCapDisplay.goToScreenShtButton, BorderLayout.EAST);
				//#endregion

				centerTopPanel.repaint();
			}
		});

		/// [add elements to the topPanel]
		topPanel.add(modeLabel, BorderLayout.WEST);
		topPanel.add(GUI.gridPage.pagePanel, BorderLayout.EAST);
		topPanel.add(centerTopPanel, BorderLayout.CENTER);
		//#endregion

		// start in the first page.
		menuMode(0);
	}

	/* 
	 * This will enable user to navigate through the menu by using top and down arrow keys.
	 * It will also add visual interacivity by constantly determining whether or not
	 * the user has a correct menu command currently typed in the JTextField, and
	 * highlighted that command in the menu.
	 */
	// HANDLER: KeyHandler
	public class KeyHandler implements KeyListener { /// [DONE]

		public void keyPressed(KeyEvent e) {

			// turn page to the right.
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				if (menuPage == 3) {
					menuPage = 0;
				} else {
					menuPage++;
				}
				menuMode(menuPage);
			}
			// turn page to the left.
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				if (menuPage == 0) {
					menuPage = 3;
				} else {
					menuPage--;
				}
				menuMode(menuPage);
			}

			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

				// clear the userInput if esc is pressed.
				userInput.setText("");
			}
		}

		public void keyTyped(KeyEvent e) {

		}

		public void keyReleased(KeyEvent e) {

			// get the entered text so far.
			JTextField textField = (JTextField) e.getSource();
			String enteredtext = textField.getText().toLowerCase();

			// a default value if nothing is found.
			int currentChoiceHighlight = -1;

			// check to see if entered text matches a choice.
			for (int i = 0; i < choices.size(); i++) {
				if (enteredtext.equals(choices.get(i).toLowerCase())) {
					currentChoiceHighlight = i;
					break;
				}
			}

			// redrawing menu with highlighted text.
			menuTextPane.setText("");

			// redraw the header and divider.
			setRegularText(menuHeader + "\n..................." + "\n\n", currentHeaderColor, 18);

			// highlight any text entered
			for (int j = 0; j < choices.size(); j++) {
				if (j == currentChoiceHighlight) {
					setHighlighted(" [" + choices.get(j) + "]" + description.get(j).substring(3) + "\n");
				} else {
					setRegularText("[" + choices.get(j) + "]" + description.get(j) + "\n", new Color(255, 255, 255), 16);
				}
			}
		}
	}

	// HANDLER: TextDrawer
	public class TextDrawer implements ActionListener {
		/*
		 * [method will handle user input when in notesMode]
		 */
		public void actionPerformed(ActionEvent e) {

			String inputVariable = userInput.getText();
			userInput.setText("");

			switch (inputVariable) {

				case "menu":
					// jump to menu mode.
					menuMode(menuPage);
					// if the screenshotPanel is visible, make it not visible.
					if (GUI.screenShotPanel.isVisible()) {
						GUI.screenShotPanel.setVisible(false);
					}

					// remove and add appropriate listeners.
					userInput.removeActionListener(textdrawer);
					userInput.addActionListener(menuInputHandler);

					GUI.notification.setVisible(false);
					GUI.menu.centerTopPanel.remove(goToMenuButton);
					GUI.menu.centerTopPanel.add(GUI.screenCapDisplay.goToScreenShtButton, BorderLayout.EAST);
					break;

				default:
					if (inputVariable.equals("") && GUI.gridPage.sortedBlobs.size() == 0) {
						break;
					}
					GUI.myProcessingSketch.drawTextGrid(inputVariable);
			}
		}
	}

	// HANDLER: MenuHandler
	public class MenuInputHandler implements ActionListener {
		/*
		 * [method will handle the JTextField user input]
		 */
		public void actionPerformed(ActionEvent e) {

			String enteredText = userInput.getText();

			userInput.setText("");

			switch (enteredText) {
				case "menu":          // GENERAL:   [DONE]
					// jump to menu mode.
					menuMode(menuPage);
					// if the screenshotPanel is visible, make it not visible.
					if (GUI.screenShotPanel.isVisible()) {
						GUI.screenShotPanel.setVisible(false);
					}
					// if the sketchMapperPanel is visible, make it not visible.
					if (GUI.sketchMapperPanel.isVisible()) {
						GUI.sketchMapperPanel.setVisible(false);
					}

					GUI.screenCapDisplay.choosingLivePoint = false;

					GUI.notification.setVisible(false);
					GUI.menu.centerTopPanel.remove(goToMenuButton);
					GUI.menu.centerTopPanel.add(GUI.screenCapDisplay.goToScreenShtButton, BorderLayout.EAST);
					break;
				case "fullscreen":    // GENERAL:   [DONE]
					/* 
					 * make the floating processingFrame fullscreen.
					 */
					GUI.makeProcessingFullScreen();
					GUI.menu.userInput.requestFocus();
					GUI.menu.userInput.requestFocusInWindow();
					break;
				// make processing sketch the default 1080 x 720 size.
				case "normalscreen":  // GENERAL:   [DONE]
					GUI.makeProcessingNormalScreen();
					GUI.mainGUI.revalidate();
					GUI.menu.userInput.requestFocusInWindow();

					break;
				// exit the entire program.
				case "pause":         // GENERAL:   [DONE]
					GUI.myProcessingSketch.noLoop();
					break;
				case "resume":        // GENERAL:   [DONE]
					GUI.myProcessingSketch.loop();
					break;
				case "redrawback":    // GENERAL:   [DONE]
					// clear the mainCanvas
					GUI.myProcessingSketch.backgroundCanvas.beginDraw();
					GUI.myProcessingSketch.backgroundCanvas.background(0);
					GUI.myProcessingSketch.backgroundCanvas.endDraw();
					GUI.myProcessingSketch.loop();

					// show user a notifications.
					GUI.notification.setMessage("Redrew the background", drawCol, true);
					break;
				case "redrawfore":    // GENERAL:   [DONE]
					// clear the mainCanvas
					GUI.myProcessingSketch.foregroundCanvas.beginDraw();
					GUI.myProcessingSketch.foregroundCanvas.background(0, 0);
					GUI.myProcessingSketch.foregroundCanvas.endDraw();
					GUI.myProcessingSketch.loop();

					// show user a notifications.
					GUI.notification.setMessage("Redrew the foreground", drawCol, true);
					break;
				case "clearblobs":    // GENERAL:   [DONE]
					// clear all the blobs in array.
					GUI.gridPage.sortedBlobs.clear();
					// send via OSC the current state of blob array.
					GUI.myProcessingSketch.sendSortedBlobAmount();
					// update the grid.
					GUI.gridPage.removeAll();
					GUI.mainGUI.revalidate();
					GUI.mainGUI.repaint();

					// update grid Page
					GUI.gridPage.pageNum = 0;
					GUI.gridPage.turnPageBack.setVisible(false);
					GUI.gridPage.turnPageNext.setVisible(false);
					GUI.gridPage.pageNumLabel.setVisible(false);

					GUI.gridPage.pagePanel.setVisible(false);

					// show user a notifications.
					GUI.notification.setMessage("Cleared all the blobs", editCol, true);
					break;
				case "map":           // GENERAL:   [DONE]
					GUI.sketchMapper.startUpSketchMapper();
					break;
				case "exit":          // GENERAL:   [DONE]
					System.exit(0);
					break;
				case "drawtext":      // DRAWING:   [DONE]
					// change label.
					modeLabel.setText("[drawtext]");
					modeLabel.setForeground(drawCol);
					// remove and add appropriate listeners.
					userInput.removeActionListener(menuInputHandler);
					userInput.addActionListener(textdrawer);

					// if the screenshotPanel is visible, make it not visible.
					if (GUI.screenShotPanel.isVisible()) {
						GUI.screenShotPanel.setVisible(false);
					}

					// make sure the goToMenuButton is visible.
					if (GUI.screenCapDisplay.goToScreenShtButton.isVisible()) {
						GUI.menu.centerTopPanel.remove(GUI.screenCapDisplay.goToScreenShtButton);
					}
					GUI.menu.centerTopPanel.add(goToMenuButton, BorderLayout.EAST);

					// show user a notifications.
					GUI.notification.setMessage("Type to enter blobs in grid", drawCol, false);
					break;
				// takes a screenshot of first monitor.
				case "screenshot":    // DRAWING:   [DONE]
					// change label.
					modeLabel.setText("[screenshot]");
					modeLabel.setForeground(drawCol);
					// take the screenshot
					try {
						GUI.screenCapDisplay.getFullScreenImg(0);
					} catch (AWTException e1) {
						e1.printStackTrace();
					}
					GUI.screenShotPanel.repaint();
					GUI.screenShotPanel.setVisible(true);

					GUI.notification.setVisible(false);
					GUI.menu.centerTopPanel.remove(GUI.screenCapDisplay.goToScreenShtButton);
					GUI.menu.centerTopPanel.add(goToMenuButton, BorderLayout.EAST);

					break;
				case "livenormal":    // DRAWING:   [DONE]
					if (GUI.screenCapDisplay.liveCapRect != null) {
						GUI.myProcessingSketch.noLoop();

						// if livechroma is on, set to false;
						if (GUI.myProcessingSketch.livechroma) {
							GUI.myProcessingSketch.livechroma = false;
						}
						// set the live style.
						GUI.myProcessingSketch.livenormal = true;
						// set live to true;
						GUI.myProcessingSketch.goLive = true;

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}

						// show user a notifications.
						GUI.notification.setMessage("we are live", Color.PINK, false);

						GUI.myProcessingSketch.loop();

						// make the stopMainLiveButton visible.
						GUI.screenCapDisplay.stopMainLiveButton.setVisible(true);
						// make the goLiveButton disabled.
						GUI.screenCapInfo.goLiveButton.setEnabled(false);
						GUI.screenCapInfo.goLiveButton.setBackground(Color.GRAY);
					} else {
						// show user a notifications.
						GUI.notification.setMessage("no live capture saved", GUI.notification.warning, true);
					}
					break;
				case "livechroma1":   // DRAWING:   [DONE]
					if (GUI.screenCapDisplay.liveCapRect != null) {
						GUI.myProcessingSketch.noLoop();

						// set live to true and chroma redraw to true.
						GUI.myProcessingSketch.chromaRedraw = true;
						GUI.myProcessingSketch.livechroma = true;
						// make every other live related variable false.
						GUI.myProcessingSketch.livenormal = false;
						// set live to true.
						GUI.myProcessingSketch.goLive = true;

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}

						// show user a notifications.
						GUI.notification.setMessage("we are live: chroma", Color.PINK, false);

						GUI.myProcessingSketch.loop();

						GUI.screenCapDisplay.stopMainLiveButton.setVisible(true);
						// make the goLiveButton disabled.
						GUI.screenCapInfo.goLiveButton.setEnabled(false);
						GUI.screenCapInfo.goLiveButton.setBackground(Color.GRAY);
					} else {
						// show user a notifications.
						GUI.notification.setMessage("no live capture saved", GUI.notification.warning, true);
					}
					break;
				case "livechroma2":   // DRAWING:   [DONE]
					if (GUI.screenCapDisplay.liveCapRect != null) {
						GUI.myProcessingSketch.noLoop();

						// set livechroma to true and chromaredraw to false,
						GUI.myProcessingSketch.chromaRedraw = false;
						GUI.myProcessingSketch.livechroma = true;
						// make every other live related variable false.
						GUI.myProcessingSketch.livenormal = false;
						// set live to true.
						GUI.myProcessingSketch.goLive = true;

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}

						// show user a notifications.
						GUI.notification.setMessage("we are live: chroma", Color.PINK, false);

						GUI.myProcessingSketch.loop();

						GUI.screenCapDisplay.stopMainLiveButton.setVisible(true);
						// make the goLiveButton disabled.
						GUI.screenCapInfo.goLiveButton.setEnabled(false);
						GUI.screenCapInfo.goLiveButton.setBackground(Color.GRAY);
					} else {
						// show user a notifications.
						GUI.notification.setMessage("no live capture saved", GUI.notification.warning, true);
					}
					break;
				case "random":        // DISPLAY:   [DONE]
					GUI.myProcessingSketch.displayStyle = enteredText;
					break;
				case "accumulate":    // DISPLAY:   [DONE]
					GUI.myProcessingSketch.accumulate = true;
					break;
				case "singlefile":    // DISPLAY:   [DONE]
					GUI.myProcessingSketch.accumulate = false;
					break;
				case "showOriginal":    // EDIT:   [DONE]
					for (Blob b : GUI.gridPage.sortedBlobs) {
						b.showOriginal = true;
					}
					break;
				case "showScaled":    // EDIT:   [DONE]
					for (Blob b : GUI.gridPage.sortedBlobs) {
						b.showOriginal = false;
					}
					break;
				case "graphics":      // DEBUGGING: [DONE]
					/*
						 * returns a list of the currently connected GraphicsDevices 
						 */

					// get the GraphicsEnvironment and place all devices in a list called allScreens. 
					GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();         /// returns the local GraphicsEnvironment
					GraphicsDevice[] allScreens = env.getScreenDevices();

					// print to the debugger/terminal.
					System.out.println("\nCurrent Connected Devices:");
					System.out.println("----------------------");
					for (int i = 0; i < allScreens.length; i++) {
						GraphicsDevice dev = allScreens[i];
						System.out.println("Device [" + i + "]: " + "= " + dev.getIDstring() + " (" + dev.getDefaultConfiguration().getBounds().height + " x " + dev.getDefaultConfiguration().getBounds().width + ")");
					}
					System.out.println("----------------------");
					break;
				default:
					// display error message if incorrect command is entered.
					if (!GUI.notification.timer.isRunning() && !enteredText.equals("")) {
						GUI.notification.setMessage("Did not understand input", GUI.notification.warning, true);
					}
			}
		}
	}

	// MODE: menu mode.
	public void menuMode(int menuPage) { /// [DONE]
		/*
		 * [method will draw the menu]
		 */


		/// #cleaned [GUI Maintenance]
		// reset any stored rects from screenCapDisplay.
		if (GUI.screenCapDisplay.drawnRect != null) {
			GUI.screenCapDisplay.drawnRect = null;
			GUI.screenCapDisplay.minX = 0;
			GUI.screenCapDisplay.minY = 0;
			GUI.screenCapDisplay.maxX = 0;
			GUI.screenCapDisplay.maxY = 0;
			GUI.screenCapInfo.collectButton.setEnabled(false);
			GUI.screenCapInfo.saveLiveRectButton.setEnabled(false);
		}
		//#endregion

		userInput.addActionListener(menuInputHandler);

		// clear the menu.
		menuTextPane.setText("");

		// update mode label.
		modeLabel.setText("[menu]");
		modeLabel.setForeground(new Color(0, 0, 0));

		// clear all the choices and descriptions.
		choices.clear();
		description.clear();

		// get the current menu page text file as a BufferedReader.
		/// #cleaned [BufferedReader]
		// BufferedReader set to null
		BufferedReader reader = null;
		// page number will determine what text files to read.
		if (menuPage == 0) {
			InputStream inputStreamGeneral = GUI.class.getResourceAsStream("/script/general.txt");
			InputStreamReader inputReaderGeneral = new InputStreamReader(inputStreamGeneral);
			BufferedReader readerGeneral = new BufferedReader(inputReaderGeneral);
			reader = readerGeneral;
			currentHeaderColor = generalCol;
		}
		if (menuPage == 1) {
			InputStream inputStreamDraw = GUI.class.getResourceAsStream("/script/drawing.txt");
			InputStreamReader inputReaderDraw = new InputStreamReader(inputStreamDraw);
			BufferedReader readerDraw = new BufferedReader(inputReaderDraw);
			reader = readerDraw;
			currentHeaderColor = drawCol;
		}
		if (menuPage == 2) {
			InputStream inputStreamEdit = GUI.class.getResourceAsStream("/script/edit.txt");
			InputStreamReader inputReaderEdit = new InputStreamReader(inputStreamEdit);
			BufferedReader readerEdit = new BufferedReader(inputReaderEdit);
			reader = readerEdit;
			currentHeaderColor = editCol;
		}
		if (menuPage == 3) {
			InputStream inputStreamDisplay = GUI.class.getResourceAsStream("/script/display.txt");
			InputStreamReader inputReaderDisplay = new InputStreamReader(inputStreamDisplay);
			BufferedReader readerDisplay = new BufferedReader(inputReaderDisplay);
			reader = readerDisplay;
			currentHeaderColor = displayCol;
		}
		//#endregion

		// get the Header and Menu Options
		/// #cleaned [Scan .txt File]
		// set up temp variables to go through text.
		int counter = 0;
		String currentline = null;
		// read the .txt file to find next line in script, choices, and replies
		try {
			while ((currentline = reader.readLine()) != null) {
				// find the menu header.
				if (counter == 0) {
					menuHeader = currentline;
				}
				// find the choices and their description.
				if (counter > 0) {
					choices.add(currentline.substring(0, currentline.indexOf(" ")));
					description.add(currentline.substring(currentline.indexOf(" "), currentline.length()));
				}
				counter++;
			}
		} catch (Exception e) {
			System.out.println("failed to read text");
		}
		//#endregion

		setRegularText(menuHeader + "\n..................." + "\n\n", currentHeaderColor, 18);
		for (int i = 0; i < choices.size(); i++) {
			setRegularText("[" + choices.get(i) + "]" + description.get(i) + "\n", new Color(255, 255, 255), 16);
		}
	}

	public void setHighlighted(String str) { /// [DONE]

		// insert String to doc.
		try {
			StyledDocument doc = (StyledDocument) menuTextPane.getDocument();
			doc.insertString(doc.getLength(), str, highlightedStyle);
		} catch (Exception ex) {
			System.out.println("caught");
		}
	}

	public void setRegularText(String str, Color color_, int fontSize_) { /// [DONE]

		// change color and font size whether it is header or one of the options.
		StyleConstants.setForeground(unhighlightedStyle, color_);
		StyleConstants.setFontSize(unhighlightedStyle, fontSize_);

		// insert String to doc.
		try {
			StyledDocument doc = (StyledDocument) menuTextPane.getDocument();
			doc.insertString(doc.getLength(), str, unhighlightedStyle);
		} catch (Exception ex) {
			System.out.println("caught");
		}
	}
}
