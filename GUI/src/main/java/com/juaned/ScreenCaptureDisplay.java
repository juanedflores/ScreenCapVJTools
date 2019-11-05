package com.juaned;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ScreenCaptureDisplay extends JPanel {

	private static final long serialVersionUID = 1L;

	/*
	 * [Robot]
	 */
	/// #cleaned [Robot]
	Robot robotForScreenShot;
	Robot robotForLive;
	//#endregion

	/*
	 * [Screenshot]
	 */
	/// #cleaned [Handle Screenshot]
	// BufferedImage of the capture.
	BufferedImage fullscreenImg;
	BufferedImage sliceImg;
	BufferedImage origSlice;
	// change the scale of the image.

	float scaleFactorDown = 1; /// value to multiply values to scale down to GUI size.
	float scaleFactorUp = 1;  /// value to multiply values to scale up to full monitor size.
	int monitorIndex = 0;      /// storing the index of the monitor in our current GraphicsEnvironment.
	int startingX = 0;         /// an offset of X representing top left corner X location of monitor.
	int startingXLive = 0;
	//#endregion

	/*
	 * [GUI Rectangles]
	 */
	/// #cleaned [GUI Rectangles]
	// dimensions of fullscreen screep capture.
	Rectangle drawnRect;
	Rectangle scaledUpRect;
	Rectangle savedScaledUpRect;
	// Rectangle liveCapDrawnRect;
	Rectangle liveCapRect;
	int minLX, minLY;
	int maxLX, maxLY;
	int liveMonitor;
	int liveOutputX, liveOutputY;
	int livePointX, livePointY;
	boolean choosingLivePoint;
	boolean scaleToOutput = true;
	int userScaleFactor = 100;
	// draw rectangle
	int minX, minY;
	int maxX, maxY;
	int strokeSize;
	//#endregion

	/*
	 * [Swing Elements]
	 */
	/// #cleaned [Swing Elements]
	JButton goToScreenShtButton;
	JButton stopMainLiveButton;
	//#endregion

	public ScreenCaptureDisplay() {

		/*
		 * [Setup]
		 */
		/// #cleaned [Setup]
		setBackground(Color.BLACK);
		setBorder(null);
		addMouseMotionListener(new MyMouseMotionListener());
		addMouseListener(new MouseClickHandler());
		//#endregion

		/*
		 * [goToScreenShtButton]
		 */
		/// #cleaned [goToScreenShtButton]
		goToScreenShtButton = new JButton("screenshot");
		goToScreenShtButton.setBackground(Color.BLACK);
		goToScreenShtButton.setForeground(Color.WHITE);
		goToScreenShtButton.setFocusable(false);
		goToScreenShtButton.setFocusPainted(false);
		goToScreenShtButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// jump to screenshot mode.
				// change label.
				GUI.menu.modeLabel.setText("[screenshot]");
				GUI.menu.modeLabel.setForeground(GUI.menu.drawCol);
				// take the screenshot
				try {
					GUI.screenCapDisplay.getFullScreenImg(0);
				} catch (AWTException e1) {
					e1.printStackTrace();
				}
				GUI.screenShotPanel.repaint();
				GUI.screenShotPanel.setVisible(true);

				GUI.notification.setVisible(false);
				GUI.menu.centerTopPanel.remove(goToScreenShtButton);
				GUI.menu.centerTopPanel.add(GUI.menu.goToMenuButton, BorderLayout.EAST);
			}
		});
		//#endregion

		/*
		* [Swing Elements]
		*/
		/// #cleaned [stopLiveButton]
		stopMainLiveButton = new JButton("stop live");
		stopMainLiveButton.setBackground(Color.RED);
		stopMainLiveButton.setForeground(Color.BLACK);
		stopMainLiveButton.setFocusable(false);
		stopMainLiveButton.setFocusPainted(false);
		stopMainLiveButton.setVisible(false);
		stopMainLiveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUI.myProcessingSketch.noLoop();
				// set any live boolean to false;
				GUI.myProcessingSketch.goLive = false;
				GUI.myProcessingSketch.livechroma = false;

				// enable the goLiveButton
				GUI.screenCapInfo.goLiveButton.setBackground(Color.RED);
				GUI.screenCapInfo.goLiveButton.setEnabled(true);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				// show user a notifications.
				GUI.notification.setMessage("no longer live", Color.GREEN, false);

				// stop live button visibility is false.
				stopMainLiveButton.setVisible(false);

				// redraw liveCapture canvas.
				GUI.myProcessingSketch.liveCapture.beginDraw();
				GUI.myProcessingSketch.liveCapture.background(0, 0);
				GUI.myProcessingSketch.liveCapture.endDraw();
				GUI.myProcessingSketch.loop();
			}
		});
		//#endregion

	}

	public void getFullScreenImg(int monitorIndex_) throws AWTException { /// [DONE]
		/*
		 * takes a screenshot of the whole monitor and scales it down to be displayed in
		 * the GUI panel.
		 */

		/// #cleaned [takeFullScreenShot()]
		// store what monitor is being used to screenshot.
		monitorIndex = monitorIndex_;
		// get the current screens being used.
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screens = ge.getScreenDevices();
		// the screen that is being used to take a screenshot of.
		GraphicsDevice screen = screens[monitorIndex_];

		// instantiate the BufferedImage object that will hold the screenshot.
		BufferedImage screenShot;

		// instantiate a Robot with the screen that is being considered.
		try {
			robotForScreenShot = new Robot(screen);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}

		// get the dimensions info and create a Rectangle object.
		Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
		screenBounds.x = screen.getDefaultConfiguration().getBounds().x;

		// take the screenshot and save it globally as a BufferedImage.
		screenShot = robotForScreenShot.createScreenCapture(screenBounds);
		//#endregion

		/// #cleaned [scaleDownGUI()]
		// get the width and height of the full screencapture.
		int w = screenShot.getWidth();
		int h = screenShot.getHeight();

		// calculate how much to scale down. (for first monitor)
		scaleFactorDown = (float) this.getHeight() / (float) screen.getDefaultConfiguration().getBounds().height;
		// calculate how much to scale up. (for first monitor)
		scaleFactorUp = (float) screen.getDefaultConfiguration().getBounds().height / (float) this.getHeight();

		// if other monitors are connected, use the width.
		if (monitorIndex_ > 0) {
			scaleFactorDown = (float) (screen.getDefaultConfiguration().getBounds().width / 2) / (float) screen.getDefaultConfiguration().getBounds().width;
			scaleFactorUp = (float) (screen.getDefaultConfiguration().getBounds().width * 2) / (float) screen.getDefaultConfiguration().getBounds().width;
		}

		// calculate the new dimensions of the image being places in GUI.
		int w2 = (int) (w * scaleFactorDown);
		int h2 = (int) (h * scaleFactorDown);

		// create the new BufferedImage screenshot that is scaled to fit in GUI .
		BufferedImage afterScaleUp = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
		// set up the new scale.
		AffineTransform scaleInstance = AffineTransform.getScaleInstance(scaleFactorDown, scaleFactorDown);
		AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);
		// get the image as a Graphics2D.
		Graphics2D g2 = (Graphics2D) afterScaleUp.getGraphics();

		// apply the scale change and draw to fullscreenImg.
		g2.drawImage(screenShot, scaleOp, 0, 0);
		// dispose.
		g2.dispose();

		// get the image assigned to fullscreenImg.
		fullscreenImg = afterScaleUp;
		//#endregion

	}

	public BufferedImage scaleDownGUI(BufferedImage bufImg_) { /// [DONE]
		/* 
		 * this method is used to receive any BufferedImage to
		 * scale down to fit in this panel.
		 */

		// get the width and height of the full screencapture.
		int w = bufImg_.getWidth();
		int h = bufImg_.getHeight();

		// calculate how much to scale down. (for first monitor)
		scaleFactorDown = (float) this.getHeight() / (float) bufImg_.getHeight();

		// calculate the new dimensions of the image being places in GUI.
		int w2 = (int) (w * scaleFactorDown);
		int h2 = (int) (h * scaleFactorDown);

		// create the new BufferedImage screenshot that is scaled to fit in GUI .
		BufferedImage afterScaleDown = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
		// set up the new scale.
		AffineTransform scaleInstance = AffineTransform.getScaleInstance(scaleFactorDown, scaleFactorDown);
		AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);
		// get the image as a Graphics2D.
		Graphics2D g2 = (Graphics2D) afterScaleDown.getGraphics();

		// apply the scale change and draw to fullscreenImg.
		g2.drawImage(bufImg_, scaleOp, 0, 0);
		// dispose.
		g2.dispose();

		// get the image assigned to fullscreenImg.
		return afterScaleDown;
	}

	public BufferedImage scaleUpGUI(BufferedImage bufImg_) { /// [DONE]
		/* 
		 * this method is used to receive any BufferedImage to
		 * scale up to fit in this panel.
		 */

		// get the width and height of the full screencapture.
		int w = bufImg_.getWidth();
		int h = bufImg_.getHeight();

		// calculate how much to scale down. (for first monitor)
		scaleFactorUp = (float) this.getHeight() / (float) bufImg_.getHeight();
		System.out.println("scaleUP: " + scaleFactorUp);

		// calculate the new dimensions of the image being places in GUI.
		int w2 = (int) (w * scaleFactorUp);
		int h2 = (int) (h * scaleFactorUp);

		// create the new BufferedImage screenshot that is scaled to fit in GUI .
		BufferedImage afterScaleUp = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
		// set up the new scale.
		AffineTransform scaleInstance = AffineTransform.getScaleInstance(scaleFactorDown, scaleFactorDown);
		AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);
		// get the image as a Graphics2D.
		Graphics2D g2 = (Graphics2D) afterScaleUp.getGraphics();

		// apply the scale change and draw to fullscreenImg.
		g2.drawImage(bufImg_, scaleOp, 0, 0);
		// dispose.
		g2.dispose();

		// get the image assigned to fullscreenImg.
		return afterScaleUp;
	}

	public void getSliceImg() { /// [DONE]
		/*
		 * will return a BufferedImage screenshot of the sliceImg that the user has draw
		 * on the GUI. (should be called after a drawn rectangle is stored)
		 */

		/// #cleaned [scaleUpScreenShotSlice()]
		// calculate the values for the scaledUpRect.
		scaledUpRect = new Rectangle();
		scaledUpRect.x = (int) ((float) drawnRect.x * scaleFactorUp);
		scaledUpRect.y = (int) ((float) drawnRect.y * scaleFactorUp);
		scaledUpRect.width = (int) ((float) drawnRect.width * scaleFactorUp);
		scaledUpRect.height = (int) ((float) drawnRect.height * scaleFactorUp);

		//@ added a saved scaled rect, to continue collecting from Max/MSP.
		if (savedScaledUpRect == null) {
			// calculate the values for the scaledUpRect.
			savedScaledUpRect = new Rectangle();
			savedScaledUpRect.x = (int) ((float) drawnRect.x * scaleFactorUp);
			savedScaledUpRect.y = (int) ((float) drawnRect.y * scaleFactorUp);
			savedScaledUpRect.width = (int) ((float) drawnRect.width * scaleFactorUp);
			savedScaledUpRect.height = (int) ((float) drawnRect.height * scaleFactorUp);
		}
		//#endregion


		/// #cleaned [getStartingX()]
		// once again get the graphics environment.
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screens = ge.getScreenDevices();
		// we want to get the starting position of X for any other external monitors.
		startingX = 0;
		int counter = 0;
		for (GraphicsDevice screen : screens) {

			// the DisplayMode dimensions have the values we need.
			DisplayMode mode = screen.getDisplayMode();
			startingX += mode.getWidth();
			counter++;

			if (counter == monitorIndex) {
				break;
			}
		}
		if (monitorIndex == 0) {
			startingX = 0;
		}
		//#endregion

		/// #cleaned [getSliceImage()]
		Rectangle rect;
		if (monitorIndex == 0) {
			rect = new Rectangle(savedScaledUpRect.x, savedScaledUpRect.y, savedScaledUpRect.width, savedScaledUpRect.height);
			sliceImg = robotForScreenShot.createScreenCapture(rect);
		} else {
			rect = new Rectangle(startingX + savedScaledUpRect.x, savedScaledUpRect.y, savedScaledUpRect.width, savedScaledUpRect.height);
			sliceImg = robotForScreenShot.createScreenCapture(rect);
		}
		origSlice = sliceImg;
		//#endregion
	}

	public BufferedImage liveScreenshot() { /// [DONE] 
		/*
		 * return a screenshot of the drawnRect everytime this method is called.
		 */

		BufferedImage liveImg;

		Rectangle rect = new Rectangle(startingXLive + liveCapRect.x, liveCapRect.y, liveCapRect.width, liveCapRect.height);
		sliceImg = robotForLive.createScreenCapture(rect);

		if (scaleToOutput) {
			liveImg = scaleToOutput(sliceImg);
		} else {
			liveImg = scaleToUserScaleFactor(sliceImg);
		}

		return liveImg;
	}

	public BufferedImage scaleToUserScaleFactor(BufferedImage img_) {
		/* 
		 * scale the sliced image to a value received from the user.
		 */

		// get the dimensions.
		int originalWidth = img_.getWidth();
		int originalHeight = img_.getHeight();

		// calculate the new dimensions of the image being places in GUI.
		int w = (int) (originalWidth * userScaleFactor / 100);
		int h = (int) (originalHeight * userScaleFactor / 100);

		// resize.
		Image tmp = img_.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return resized;
	}

	public BufferedImage scaleToOutput(BufferedImage img_) { /// [DONE]
		/* 
		 * scale the sliced image to fit in the Processing sketch.
		 */

		// get the dimensions.
		int originalWidth = img_.getWidth();
		int originalHeight = img_.getHeight();
		int mainCWidth = GUI.myProcessingSketch.foregroundCanvas.width;
		int mainCHeight = GUI.myProcessingSketch.foregroundCanvas.height;

		// get the scale factor value.
		float scaleFactorUpOutput;
		if (originalWidth > originalHeight) {
			scaleFactorUpOutput = (float) mainCWidth / (float) originalWidth;
		} else {
			scaleFactorUpOutput = (float) mainCHeight / (float) originalHeight;
		}

		// update the value of the scaleFactorSpinner.
		GUI.screenCapInfo.scaleFactorSpinner.setValue(scaleFactorUpOutput * 100);

		// calculate the new dimensions of the image being places in GUI.
		int w = (int) ((float) originalWidth * scaleFactorUpOutput);
		int h = (int) ((float) originalHeight * scaleFactorUpOutput);

		// resize.
		Image tmp = img_.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return resized;
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		g.drawImage(fullscreenImg, 0, 0, null);

		if (monitorIndex != -1) {
			drawRect(g, minX, minY, maxX, maxY, strokeSize, Color.RED);
		}

		if (liveCapRect != null && liveMonitor == monitorIndex) {
			drawRect(g, minLX, minLY, maxLX, maxLY, 3, Color.BLUE);
		}

		if (choosingLivePoint) {
			drawRect(g, livePointX, livePointY, livePointX, livePointY, 10, Color.GREEN);
		}
	}

	public void drawRect(Graphics g, int minX_, int minY_, int maxX_, int maxY_, int strokeSize_, Color color_) { /// [DONE]
		/*
		 * draw a rectangle with the X and Y values being generated by user.
		 */

		int px = Math.min(minX_, maxX_);
		int py = Math.min(minY_, maxY_);
		int pw = Math.abs(minX_ - maxX_);
		int ph = Math.abs(minY_ - maxY_);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(color_);
		g2.setStroke(new BasicStroke(strokeSize_));
		g2.drawRect(px, py, pw, ph);
	}

	class MouseClickHandler extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {

			// show what point was clicked.
			GUI.screenCapInfo.xcoordLabel.setText("X: " + e.getX());
			GUI.screenCapInfo.ycoordLabel.setText("Y: " + e.getY());

			if (choosingLivePoint) {

				// scale up to mainCanvas.
				float scaleFactorUp = (float) GUI.myProcessingSketch.foregroundCanvas.width / (float) GUI.screenCapDisplay.getWidth();

				liveOutputX = (int) ((float) e.getX() * scaleFactorUp);
				liveOutputY = (int) ((float) e.getY() * scaleFactorUp);
				livePointX = e.getX();
				livePointY = e.getY();

				// redraw liveCapture canvas.
				GUI.myProcessingSketch.liveCapture.beginDraw();
				GUI.myProcessingSketch.liveCapture.background(0);
				GUI.myProcessingSketch.liveCapture.endDraw();
			}
		}

		public void mousePressed(MouseEvent e) {

			// set the start point.
			minX = e.getX();
			minY = e.getY();
			strokeSize = 1;

			//@ if notification is visible make it not visible.
			if (GUI.notification.isVisible()) {
				GUI.notification.setVisible(false);
			}
		}

		public void mouseDragged(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
			// set the end point.
			maxX = e.getX();
			maxY = e.getY();

			// save the rectangle.
			int px = Math.min(minX, maxX);
			int py = Math.min(minY, maxY);
			int pw = Math.abs(minX - maxX);
			int ph = Math.abs(minY - maxY);
			Rectangle rect = new Rectangle(px, py, pw, ph);
			drawnRect = rect;

			// change the strokeSize;
			strokeSize = 3;

			// repaint panel.
			repaint();

			if (choosingLivePoint) {
				// update the choosingLivePoint fullscreenImg when mouse is released.
				BufferedImage bufimg = GUI.myProcessingSketch.getTotalCanvasImg();
				GUI.screenCapDisplay.fullscreenImg = GUI.screenCapDisplay.scaleDownGUI(bufimg);
				repaint();
			}

			//@ enable the collect "add selection to grid" and "save live selection" button.
			if (drawnRect.width > 10 && drawnRect.height > 10 && !choosingLivePoint) {
				GUI.screenCapInfo.collectButton.setEnabled(true);
				GUI.screenCapInfo.saveLiveRectButton.setEnabled(true);
			} else {
				GUI.screenCapInfo.collectButton.setEnabled(false);
				GUI.screenCapInfo.saveLiveRectButton.setEnabled(false);
			}
		}
	}

	class MyMouseMotionListener extends MouseMotionAdapter {

		public void mouseDragged(MouseEvent e) {

			if (choosingLivePoint) {
				// scale up to mainCanvas.
				float scaleFactorUp = (float) GUI.myProcessingSketch.foregroundCanvas.width / (float) GUI.screenCapDisplay.getWidth();
				// update values.
				liveOutputX = (int) ((float) e.getX() * scaleFactorUp);
				liveOutputY = (int) ((float) e.getY() * scaleFactorUp);
				livePointX = e.getX();
				livePointY = e.getY();
				// update panel.
				repaint();
			} else {
				// set the end point.
				maxX = e.getX();
				maxY = e.getY();
				// update panel.
				repaint();
			}
		}
	}
}
