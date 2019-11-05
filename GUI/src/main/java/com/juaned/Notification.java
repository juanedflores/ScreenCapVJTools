package com.juaned;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Notification extends JTextPane {

	private static final long serialVersionUID = 1L;

	// a public variable of the notification object.
	public static Notification notification;

	/*
	 * [Font]
	 */
	/// #cleaned [Font]
	Font commodoreFont;    /// font that is imported from a .ttf file.
	Font notificationFont; /// font that is used for the notification text.
	//#endregion

	/*
	 * [Handle Drawing of Text]
	 */
	/// #cleaned [Drawing Text]
	String message;           /// variable to store text that will be drawn.
	int charIndex = 0;        /// counts through the chars in text.
	Timer timer;              /// timer used to draw text.
	boolean punctuationPause; /// will let Timer know when to pause drawing after punctuation char.
	//#endregion

	/*
	 * [Notification Colors]
	 */
	/// #cleaned [Notification Colors]
	Color warning = new Color(214, 130, 124);
	Color success = new Color(123, 212, 146);
	//#endregion

	/*
	 * [Handlers]
	 */
	/// #cleaned [Handlers]
	NotificationsInputHandler notificationsInput = new NotificationsInputHandler();
	TimerAction timerAction = new TimerAction();
	//#endregion

	public Notification() {

		/*
		 * [Set up Fonts]
		 */
		/// #cleaned [Font]
		InputStream fontInputStream = getClass().getResourceAsStream("/fonts/com.ttf");
		try {
			// create a font from the .ttf file of a commodore 64 font.
			commodoreFont = Font.createFont(Font.TRUETYPE_FONT, fontInputStream);
			// adjust the size for the font.
			notificationFont = commodoreFont.deriveFont(18f);
		} catch (FontFormatException e1) {
			System.out.println("FontFormatException");
			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Font IO Exception");
			e1.printStackTrace();
		}
		//#endregion

		/*
		 * [Set up textbox]
		 */
		/// #cleaned [Setup]
		notification = this;
		this.setBackground(new Color(45, 45, 45));
		this.setForeground(Color.white);
		this.setFont(notificationFont);
		this.setHighlighter(null);
		this.setEditable(false);
		this.setBorder(new AdvancedBevelBorder(new Color(100, 100, 100), new Color(200, 200, 200), new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), 5));
		this.setFocusable(false);
		this.setBounds(50, 45, 500, 60);

		// text will be centered.
		try {
			this.setEditorKit(new NotificationEditorKit());
			SimpleAttributeSet attrs = new SimpleAttributeSet();
			StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_CENTER);
			StyledDocument doc = (StyledDocument) this.getDocument();
			doc.setParagraphAttributes(0, doc.getLength() - 1, attrs, false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//#endregion
	}

	public void setMessage(String text, Color textcolor, boolean animate) { /// [DONE]
		/*
		 * [Draws a message as a notification for user]
		 */

		if (animate == true) {
			// update text and timer.
			timer = new Timer(55, timerAction);
			timer.start();
			message = text;
			charIndex = 0;
		} else {
			notification.setText(text);
		}

		// set a color.
		try {
			Thread.sleep(55);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.setForeground(textcolor);

		// make text guide visible.
		this.setVisible(true);
	}

	// HANDLER: NotificationsHandler
	public class NotificationsInputHandler implements ActionListener { /// [DONE]
		/*
		 * [method will handle notification box behavior with user input]
		 */
		public void actionPerformed(ActionEvent e) {

			// if timer is running and a user input is received, skip to animation and draw the rest of the message.
			if (notification.isVisible() && timer.isRunning()) {
				charIndex = message.length();
			}
			// if timer is done and window is visible, a enter press by the user will make it disappear.
			if (notification.isVisible() && !timer.isRunning()) {
				notification.setVisible(false);
			}
		}
	}

	// HANDLER: TimerAction
	public class TimerAction implements ActionListener { /// [DONE]
		/*
		 * [method will handle the timer that draws the text letter by letter]
		 */
		public void actionPerformed(ActionEvent ae) {

			// if found a punctuation, pause for a slight amount of time.
			if (punctuationPause) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// if charIndex is textLength, then stop Timer and display full text.
			if (charIndex == message.length()) {
				timer.stop();
				notification.setText(message);
			}

			// if we have not reached the end, we must progress.
			if (charIndex < message.length()) {
				// draws text letter by letter
				String textToDraw = message.substring(0, charIndex);
				// look at each individual char
				String textChar = "";
				if (textToDraw.length() > 0) {
					textChar = textToDraw.substring(textToDraw.length() - 1);
				}
				if (textToDraw.length() == 0) {
					textChar = textToDraw;
				}

				notification.setText(textToDraw);

				// if char is punctuation, pause slightly
				if (textChar.matches("[.,!?]")) {
					punctuationPause = true;
				} else {
					punctuationPause = false;
				}

				// index to continue to next letter
				charIndex++;
			}
		}
	}

}
