package com.juaned;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class SketchMapper extends JPanel {

	private static final long serialVersionUID = 1L;

	float scaleFactorUp;

	/// #cleaned [Canvas] // [Cleaned]
	boolean isEditingCanvas;
	boolean hasMovedCanvas;
	boolean isDraggingMinXY, isDraggingMaxXY, isDraggingAll;
	int mapperMinX, mapperMinY, mapperMaxX, mapperMaxY, mapperCenterX, mapperCenterY;
	int lastPressedX, lastPressedY;
	int savedMapperMinX, savedMapperMinY, savedMapperMaxX, savedMapperMaxY, savedMapperCenterX, savedMapperCenterY;;
	int processingMinX, processingMinY, processingMaxX, processingMaxY, canvasW, canvasH;
	int scaledDownProcessingW, scaledDownProcessingH;
	//#endregion

	/// #cleaned [Masking] // [Cleaned]
	boolean isEditingMask;
	boolean hasCreatedMask;
	boolean isDraggingPoint;
	int pointBeingDraggedIndex;
	List<Integer> polyPointsX = new ArrayList<>();
	List<Integer> polyPointsY = new ArrayList<>();
	int x[];
	int y[];
	int scaledX[];
	int scaledY[];
	int createdMaskX[];
	int createdMaskY[];
	int numberofpoints = 0;
	//#endregion

	/// #cleaned [Swing Elements]
	JPanel sketchMapperPanel, sketchMapperInfoPanel, sketchMapperCanvasPanel, sketchMapperCanvasButtonPanel, sketchMapperMaskPanel, sketchMapperMaskButtonPanel;
	JButton createCanvasButton, editCanvasButton, saveCanvasButton, loadCanvasButton;
	JButton createMaskButton, editMaskButton, resetMaskButton, saveMaskButton, loadMaskButton;
	JButton goToMenuButton;
	JLabel canvasLabel, maskLabel;
	Border labelOutline = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY);
	Border buttonPanelOutline = BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK);
	//#endregion

	public SketchMapper() {
		setSize(0, GUI.sketchMapperPanel.getHeight());
		setBackground(Color.BLACK);
		addMouseMotionListener(new MyMouseMotionListener());
		addMouseListener(new MouseClickHandler());

		/// #cleaned [Exit Button (Top Panel)]
		// a button that will go to menu.
		goToMenuButton = new JButton("Exit");
		goToMenuButton.setBackground(Color.BLACK);
		goToMenuButton.setForeground(Color.WHITE);
		goToMenuButton.setFocusable(false);
		goToMenuButton.setFocusPainted(false);
		goToMenuButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// jump to menu mode.
				GUI.menu.menuMode(GUI.menu.menuPage);

				/// #cleaned [GUI Maintanence]

				// make this panel not visible.
				GUI.sketchMapperPanel.setVisible(false);

				// since we are using a transparent PGraphics, we should redraw so it doesn't stick.
				GUI.myProcessingSketch.maskingCanvas.beginDraw();
				GUI.myProcessingSketch.maskingCanvas.background(0, 0);
				GUI.myProcessingSketch.maskingCanvas.endDraw();
				GUI.myProcessingSketch.loop();
				if (hasCreatedMask) {
					// create the mask and display in processing.
					GUI.myProcessingSketch.createMask(createdMaskX, createdMaskY);
				}

				// enable userInput JTextField in case it was disabled.
				GUI.menu.userInput.setEnabled(true);

				// put away notification if it is visible.
				GUI.notification.setVisible(false);

				// remove the menu button and replace it with the goToScreenShtButton.
				GUI.menu.centerTopPanel.remove(goToMenuButton);
				GUI.menu.centerTopPanel.add(GUI.screenCapDisplay.goToScreenShtButton, BorderLayout.EAST);
				//#endregion

				GUI.menu.centerTopPanel.repaint();
			}
		});
		//#endregion

		/// #cleaned [Panel Setup and Labels]
		sketchMapperInfoPanel = new JPanel();
		sketchMapperInfoPanel.setBackground(Color.BLACK);
		sketchMapperInfoPanel.setLayout(new GridLayout(2, 1));

		sketchMapperCanvasPanel = new JPanel();
		sketchMapperCanvasPanel.setBackground(new Color(93, 138, 194));
		sketchMapperCanvasPanel.setLayout(new BorderLayout());
		sketchMapperCanvasButtonPanel = new JPanel();
		sketchMapperCanvasButtonPanel.setBackground(new Color(93, 138, 194));
		sketchMapperCanvasButtonPanel.setLayout(new GridLayout(4, 1));
		sketchMapperCanvasButtonPanel.setBorder(buttonPanelOutline);

		sketchMapperMaskPanel = new JPanel();
		sketchMapperMaskPanel.setBackground(new Color(93, 138, 194));
		sketchMapperMaskPanel.setLayout(new BorderLayout());
		sketchMapperMaskButtonPanel = new JPanel();
		sketchMapperMaskButtonPanel.setBackground(new Color(93, 138, 194));
		sketchMapperMaskButtonPanel.setLayout(new GridLayout(5, 1));
		sketchMapperMaskButtonPanel.setBorder(buttonPanelOutline);

		// the label that indicates the buttons below are for canvas.
		canvasLabel = new JLabel("Canvas Size & Position", SwingConstants.CENTER);
		canvasLabel.setBorder(labelOutline);
		canvasLabel.setForeground(Color.RED);
		canvasLabel.setOpaque(true);
		canvasLabel.setBackground(Color.BLACK);
		// the label that indicates the button below are for masking.
		maskLabel = new JLabel("Masking", SwingConstants.CENTER);
		maskLabel.setBorder(labelOutline);
		maskLabel.setForeground(Color.RED);
		maskLabel.setOpaque(true);
		maskLabel.setBackground(Color.BLACK);
		//#endregion

		/// #cleaned [Create Canvas]
		createCanvasButton = new JButton("Create Canvas");
		createCanvasButton.setBackground(new Color(120, 120, 120));
		createCanvasButton.setForeground(Color.WHITE);
		createCanvasButton.setFocusable(false);
		createCanvasButton.setFocusPainted(false);
		createCanvasButton.setEnabled(true);
		createCanvasButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (mapperMaxX > 0 && mapperMaxY > 0) {

					//@ draw to Processing.
					// update to the new canvas.
					GUI.myProcessingSketch.canvasMinX = processingMinX;
					GUI.myProcessingSketch.canvasMinY = processingMinY;
					GUI.myProcessingSketch.noStroke();
					GUI.myProcessingSketch.changeCanvasSize(canvasW, canvasH);

					GUI.myProcessingSketch.maskingCanvas.beginDraw();
					GUI.myProcessingSketch.maskingCanvas.background(0, 0);
					GUI.myProcessingSketch.maskingCanvas.endDraw();
					GUI.myProcessingSketch.loop();

					// show user a notifications.
					GUI.notification.setMessage("created a new canvas", GUI.notification.success, false);
				} else {
					// show user a notifications.
					GUI.notification.setMessage("create a canvas first", GUI.notification.warning, false);
				}
			}
		});
		//#endregion

		/// #cleaned [Edit Canvas]
		editCanvasButton = new JButton("Edit Canvas");
		editCanvasButton.setBackground(new Color(148, 53, 94));
		editCanvasButton.setForeground(Color.WHITE);
		editCanvasButton.setFocusable(false);
		editCanvasButton.setFocusPainted(false);
		editCanvasButton.setEnabled(true);
		editCanvasButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				isEditingCanvas = true;
				isEditingMask = false;

				if (!hasMovedCanvas) {
					// default to fully scaled box.
					mapperMinX = 0;
					mapperMinY = 0;
					mapperMaxX = scaledDownProcessingW;
					mapperMaxY = scaledDownProcessingH;
					mapperCenterX = ((mapperMaxX - mapperMinX) + mapperMinX) / 2;
					mapperCenterY = ((mapperMaxY - mapperMinY) + mapperMinY) / 2;
				}

				//@ draw in processing.
				// calculate how much to scale up. (to processing)
				int scaledUpMinX = (int) ((float) mapperMinX * scaleFactorUp);
				int scaledUpMinY = (int) ((float) mapperMinY * scaleFactorUp);
				int scaledUpMaxX = (int) ((float) mapperMaxX * scaleFactorUp);
				int scaledUpMaxY = (int) ((float) mapperMaxY * scaleFactorUp);

				processingMinX = scaledUpMinX;
				processingMinY = scaledUpMinY;
				processingMaxX = scaledUpMaxX;
				processingMaxY = scaledUpMaxY;
				canvasW = (int) ((float) Math.abs(processingMinX - processingMaxX));
				canvasH = (int) ((float) Math.abs(processingMinY - processingMaxY));
				GUI.myProcessingSketch.maskingCanvas.beginDraw();
				GUI.myProcessingSketch.maskingCanvas.background(0);
				GUI.myProcessingSketch.maskingCanvas.fill(255);
				GUI.myProcessingSketch.maskingCanvas.noStroke();
				GUI.myProcessingSketch.maskingCanvas.rect(ProcessingSketch.constrain(processingMinX, 0, GUI.myProcessingSketch.maskingCanvas.width), ProcessingSketch.constrain(processingMinY, 0, GUI.myProcessingSketch.maskingCanvas.height), canvasW, canvasH);
				GUI.myProcessingSketch.maskingCanvas.endDraw();
				GUI.myProcessingSketch.loop();

				repaint();

				// set notification
				GUI.notification.setMessage("you can now edit canvas", GUI.notification.success, false);
			}
		});
		//#endregion

		/// #cleaned [Save Canvas]
		saveCanvasButton = new JButton("Save Canvas");
		saveCanvasButton.setBackground(new Color(111, 80, 140));
		saveCanvasButton.setForeground(Color.WHITE);
		saveCanvasButton.setFocusable(false);
		saveCanvasButton.setFocusPainted(false);
		saveCanvasButton.setEnabled(true);
		saveCanvasButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//@ check to see if a canvas has already been saved. if saved, replace those lines.
				boolean hasSavedCanvas = false;

				Path file = Paths.get("savedcanvas.txt");
				List<String> fileContent = new ArrayList<>();
				try {
					fileContent = new ArrayList<>(Files.readAllLines(file, StandardCharsets.UTF_8));
				} catch (IOException e2) {
					e2.printStackTrace();
				}

				for (int i = 0; i < fileContent.size(); i++) {
					String[] words = fileContent.get(i).split("\\s");

					if (words[0].equals("mapperMinX:")) {
						fileContent.set(i, "mapperMinX: " + mapperMinX);
					}
					if (words[0].equals("mapperMinY:")) {
						fileContent.set(i, "mapperMinY: " + mapperMinY);
					}
					if (words[0].equals("mapperMaxX:")) {
						fileContent.set(i, "mapperMaxX: " + mapperMaxX);
					}
					if (words[0].equals("mapperMaxY:")) {
						fileContent.set(i, "mapperMaxY: " + mapperMaxY);
					}
					if (words[0].equals("mapperCenterX:")) {
						fileContent.set(i, "mapperCenterX: " + mapperCenterX);
					}
					if (words[0].equals("mapperCenterY:")) {
						fileContent.set(i, "mapperCenterY: " + mapperCenterY);
						hasSavedCanvas = true;
						break;
					}
				}

				if (hasSavedCanvas) {
					try {
						Files.write(file, fileContent, StandardCharsets.UTF_8);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					if (canvasW > 0 && canvasH > 0 && mapperMaxX > 0 && mapperMaxY > 0) {

						List<String> savedValuesList = new ArrayList<>();

						boolean foundMaskValues = false;
						for (int i = 0; i < fileContent.size(); i++) {
							String[] words = fileContent.get(i).split("\\s");
							if (words[0].equals("numberofpoints:")) {
								foundMaskValues = true;
							}
						}

						savedValuesList.add("mapperMinX: " + mapperMinX);
						savedValuesList.add("mapperMinY: " + mapperMinY);
						savedValuesList.add("mapperMaxX: " + mapperMaxX);
						savedValuesList.add("mapperMaxY: " + mapperMaxY);
						savedValuesList.add("mapperCenterX: " + mapperCenterX);
						savedValuesList.add("mapperCenterY: " + mapperCenterY);

						if (foundMaskValues) {
							for (int i = 0; i < fileContent.size(); i++) {
								savedValuesList.add(fileContent.get(i));
							}
						}

						try {
							writeToFile(savedValuesList);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}

			}
		});
		//#endregion

		/// #cleaned [Load Canvas]
		loadCanvasButton = new JButton("Load Canvas");
		loadCanvasButton.setBackground(new Color(80, 103, 140));
		loadCanvasButton.setForeground(Color.WHITE);
		loadCanvasButton.setFocusable(false);
		loadCanvasButton.setFocusPainted(false);
		loadCanvasButton.setEnabled(true);
		loadCanvasButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// jump straight into edit mode for canvas.
				isEditingCanvas = true;
				isEditingMask = false;

				// read the file.
				readFile();

				// show user a notifications.
				GUI.notification.setMessage("successfully loaded a canvas", GUI.notification.success, false);
			}
		});
		//#endregion

		/// #cleaned [Create Mask]
		createMaskButton = new JButton("Create Mask");
		createMaskButton.setBackground(new Color(120, 120, 120));
		createMaskButton.setForeground(Color.WHITE);
		createMaskButton.setFocusable(false);
		createMaskButton.setFocusPainted(false);
		createMaskButton.setEnabled(true);
		createMaskButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// let the program know that user has created a mask.
				hasCreatedMask = true;

				GUI.myProcessingSketch.maskingCanvas.beginDraw();
				GUI.myProcessingSketch.maskingCanvas.background(0, 0);
				GUI.myProcessingSketch.maskingCanvas.endDraw();
				GUI.myProcessingSketch.loop();

				//@ draw in processing.
				// calculate how much to scale up. (to processing)
				createdMaskX = new int[numberofpoints];
				createdMaskY = new int[numberofpoints];
				for (int i = 0; i < numberofpoints; i++) {
					createdMaskX[i] = (int) ((float) x[i] * scaleFactorUp);
					createdMaskY[i] = (int) ((float) y[i] * scaleFactorUp);
				}
				// create the mask and display in processing.
				GUI.myProcessingSketch.createMask(GUI.sketchMapper.createdMaskX, GUI.sketchMapper.createdMaskY);

				// set notification
				GUI.notification.setMessage("created a new mask", GUI.notification.success, false);
			}
		});
		//#endregion

		/// #cleaned [Edit Mask]
		editMaskButton = new JButton("Edit Mask");
		editMaskButton.setBackground(new Color(148, 53, 94));
		editMaskButton.setForeground(Color.WHITE);
		editMaskButton.setFocusable(false);
		editMaskButton.setFocusPainted(false);
		editMaskButton.setEnabled(true);
		editMaskButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				isEditingMask = true;
				isEditingCanvas = false;

				//@ draw in processing.
				// calculate how much to scale up. (to processing)
				scaledX = new int[numberofpoints];
				scaledY = new int[numberofpoints];
				for (int i = 0; i < numberofpoints; i++) {
					scaledX[i] = (int) ((float) x[i] * scaleFactorUp);
					scaledY[i] = (int) ((float) y[i] * scaleFactorUp);
				}

				GUI.myProcessingSketch.maskingCanvas.beginDraw();
				GUI.myProcessingSketch.maskingCanvas.background(0);

				GUI.myProcessingSketch.maskingCanvas.beginShape();
				GUI.myProcessingSketch.maskingCanvas.noStroke();
				GUI.myProcessingSketch.maskingCanvas.fill(255);
				for (int i = 0; i < numberofpoints; i++) {
					GUI.myProcessingSketch.maskingCanvas.vertex(scaledX[i], scaledY[i]);
				}
				GUI.myProcessingSketch.maskingCanvas.endShape(ProcessingSketch.CLOSE);

				GUI.myProcessingSketch.maskingCanvas.endDraw();
				GUI.myProcessingSketch.loop();

				repaint();

				// set notification
				GUI.notification.setMessage("add points to start drawing a mask", GUI.notification.success, false);
			}
		});
		//#endregion

		/// #cleaned [Reset Mask]
		resetMaskButton = new JButton("Reset Mask");
		resetMaskButton.setBackground(new Color(125, 75, 87));
		resetMaskButton.setForeground(Color.WHITE);
		resetMaskButton.setFocusable(false);
		resetMaskButton.setFocusPainted(false);
		resetMaskButton.setEnabled(true);
		resetMaskButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				numberofpoints = 0;
				polyPointsX = new ArrayList<>();
				polyPointsY = new ArrayList<>();
				x = new int[0];
				y = new int[0];

				// repaint the panel
				repaint();

				//@ draw to processing.
				GUI.myProcessingSketch.maskingCanvas.beginDraw();
				GUI.myProcessingSketch.maskingCanvas.background(0, 0);
				GUI.myProcessingSketch.maskingCanvas.endDraw();
				GUI.myProcessingSketch.loop();

				// set notification
				GUI.notification.setMessage("mask has been reset", GUI.notification.success, false);
			}
		});
		//#endregion

		/// #cleaned [Save Mask]
		saveMaskButton = new JButton("Save Mask");
		saveMaskButton.setBackground(new Color(111, 80, 140));
		saveMaskButton.setForeground(Color.WHITE);
		saveMaskButton.setFocusable(false);
		saveMaskButton.setFocusPainted(false);
		saveMaskButton.setEnabled(true);
		saveMaskButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//@ check to see if a canvas has already been saved. if saved, replace those lines.

				Path file = Paths.get("savedcanvas.txt");
				List<String> fileContent = new ArrayList<>();
				List<String> savedValuesList = new ArrayList<>();
				try {
					fileContent = new ArrayList<>(Files.readAllLines(file, StandardCharsets.UTF_8));
				} catch (IOException e2) {
					e2.printStackTrace();
				}

				for (int i = 0; i < fileContent.size(); i++) {
					String[] words = fileContent.get(i).split("\\s");

					if (fileContent.get(i).length() > 0) {
						if (words[0].substring(0, 6).equals("vertex") || words[0].equals("numberofpoints:")) {
							fileContent.set(i, "");
						}
					}
				}

				for (int i = 0; i < fileContent.size(); i++) {
					if (fileContent.get(i).length() > 0) {
						savedValuesList.add(fileContent.get(i));
					}
				}

				if (numberofpoints > 0) {

					savedValuesList.add("numberofpoints: " + numberofpoints);
					for (int i = 0; i < numberofpoints; i++) {
						savedValuesList.add("vertex[" + i + "]:" + " X: " + x[i] + " Y: " + y[i]);
					}

					try {
						writeToFile(savedValuesList);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				// set notification
				GUI.notification.setMessage("mask has been saved", GUI.notification.success, false);
			}
		});
		//#endregion

		/// #cleaned [Load Mask]
		loadMaskButton = new JButton("Load Mask");
		loadMaskButton.setBackground(new Color(80, 103, 140));
		loadMaskButton.setForeground(Color.WHITE);
		loadMaskButton.setFocusable(false);
		loadMaskButton.setFocusPainted(false);
		loadMaskButton.setEnabled(true);
		loadMaskButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// jump straight into edit mode for mask.
				isEditingMask = true;
				isEditingCanvas = false;

				// read the file.
				readFile();

				// set notification
				GUI.notification.setMessage("mask has been successfully loaded", GUI.notification.success, false);
			}
		});
		//#endregion

		sketchMapperCanvasButtonPanel.add(editCanvasButton);
		sketchMapperCanvasButtonPanel.add(createCanvasButton);
		sketchMapperCanvasButtonPanel.add(saveCanvasButton);
		sketchMapperCanvasButtonPanel.add(loadCanvasButton);
		sketchMapperMaskButtonPanel.add(editMaskButton);
		sketchMapperMaskButtonPanel.add(createMaskButton);
		sketchMapperMaskButtonPanel.add(resetMaskButton);
		sketchMapperMaskButtonPanel.add(saveMaskButton);
		sketchMapperMaskButtonPanel.add(loadMaskButton);

		sketchMapperCanvasPanel.add(canvasLabel, BorderLayout.NORTH);
		sketchMapperCanvasPanel.add(sketchMapperCanvasButtonPanel, BorderLayout.CENTER);
		sketchMapperMaskPanel.add(maskLabel, BorderLayout.NORTH);
		sketchMapperMaskPanel.add(sketchMapperMaskButtonPanel, BorderLayout.CENTER);

		sketchMapperInfoPanel.add(sketchMapperCanvasPanel);
		sketchMapperInfoPanel.add(sketchMapperMaskPanel);
	}

	public void startUpSketchMapper() { /// [Cleaned]

		/// #cleaned [GUI Maintanence]
		// make the main panel visible.
		GUI.sketchMapperPanel.setVisible(true);

		// change the label.
		GUI.menu.modeLabel.setForeground(Color.RED);
		GUI.menu.modeLabel.setText("[mapping]");

		// make sure the goToMenuButton is visible.
		if (GUI.screenCapDisplay.goToScreenShtButton.isVisible()) {
			GUI.menu.centerTopPanel.remove(GUI.screenCapDisplay.goToScreenShtButton);
		}
		if (GUI.menu.goToMenuButton.isVisible()) {
			GUI.menu.centerTopPanel.remove(GUI.menu.goToMenuButton);
		}
		GUI.menu.centerTopPanel.add(goToMenuButton, BorderLayout.EAST);
		GUI.menu.centerTopPanel.repaint();

		// disable the userInput to prevent bugs.
		GUI.menu.userInput.setEnabled(false);
		//#endregion

		/// #cleaned [scaleDownProcessingFrame]
		// calculate how much to scale down. 
		float scaleFactorDown = (float) this.getHeight() / (float) GUI.myProcessingSketch.frameH;
		// these values will determine size of GUI panel.
		scaledDownProcessingW = (int) ((float) GUI.myProcessingSketch.frameW * scaleFactorDown);
		scaledDownProcessingH = (int) ((float) GUI.myProcessingSketch.frameH * scaleFactorDown);
		// get the scaleFactorUp to processing.
		scaleFactorUp = (float) GUI.myProcessingSketch.frameH / (float) scaledDownProcessingH;
		//#endregion

		/// #cleaned [handle component resizing]
		// handle the panel component sizes.
		GUI.sketchMapperPanel.removeAll();
		setBounds(0, 0, scaledDownProcessingW, scaledDownProcessingH);
		sketchMapperInfoPanel.setBounds(scaledDownProcessingW, 0, GUI.sketchMapperPanel.getWidth() - scaledDownProcessingW, this.getHeight());

		// add all to panel and repaint.
		GUI.sketchMapperPanel.add(this, BorderLayout.CENTER);
		GUI.sketchMapperPanel.add(sketchMapperInfoPanel, BorderLayout.EAST);
		GUI.sketchMapperPanel.revalidate();
		GUI.sketchMapperPanel.repaint();
		//#endregion
	}

	@Override
	public void paintComponent(Graphics g) { /// [DONE]
		super.paintComponent(g);

		if (isEditingCanvas || isEditingMask) {
			// draw the canvas bound box.
			drawRect(g, mapperMinX, mapperMinY, mapperMaxX, mapperMaxY, 3, Color.GREEN, false);

			if (isEditingCanvas) {
				// draw minX and minY.
				drawRect(g, mapperMinX, mapperMinY, mapperMinX + 50, mapperMinY + 50, 2, Color.RED, true);

				// draw maxX and maxY.
				drawRect(g, mapperMaxX - 50, mapperMaxY - 50, mapperMaxX, mapperMaxY, 2, Color.RED, true);

				// draw centerX and centerY.
				drawRect(g, mapperCenterX - 25, mapperCenterY - 25, mapperCenterX + 25, mapperCenterY + 25, 25, Color.RED, true);
			}
		}

		// draw a mask if any.
		if (numberofpoints > 0 && isEditingMask) {
			drawPolygon(g, x, y, numberofpoints, Color.PINK);
			for (int i = 0; i < numberofpoints; i++) {
				int pointX = polyPointsX.get(i);
				int pointY = polyPointsY.get(i);
				drawRect(g, pointX, pointY, pointX, pointY, 20, Color.WHITE, false);
			}
		}
	}

	public void drawRect(Graphics g, int minX_, int minY_, int maxX_, int maxY_, int strokeSize_, Color color_, boolean fill_) { /// [DONE]
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
		if (fill_) {
			g2.fillRect(px, py, pw, ph);
		} else {
			g2.drawRect(px, py, pw, ph);
		}
	}

	public void drawPolygon(Graphics g, int[] x_, int[] y_, int numberofpoints_, Color color_) { /// [DONE]
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));
		g2.setColor(color_);
		g2.drawPolygon(x_, y_, numberofpoints_);
	}

	public void writeToFile(List<String> values_) throws IOException { /// [DONE]
		Path file = Paths.get("savedcanvas.txt");
		Files.write(file, values_, StandardCharsets.UTF_8);
	}

	public void readFile() { /// [DONE]

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("savedcanvas.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		int vertexCount = 0;
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				String[] words = line.split("\\s");

				if (isEditingCanvas) {
					if (words[0].equals("mapperMinX:")) {
						mapperMinX = Integer.parseInt(words[1]);
					}
					if (words[0].equals("mapperMinY:")) {
						mapperMinY = Integer.parseInt(words[1]);
					}
					if (words[0].equals("mapperMaxX:")) {
						mapperMaxX = Integer.parseInt(words[1]);
					}
					if (words[0].equals("mapperMaxY:")) {
						mapperMaxY = Integer.parseInt(words[1]);
					}
					if (words[0].equals("mapperCenterX:")) {
						mapperCenterX = Integer.parseInt(words[1]);
					}
					if (words[0].equals("mapperCenterY:")) {
						mapperCenterY = Integer.parseInt(words[1]);
					}
				}

				if (isEditingMask) {
					if (words[0].equals("numberofpoints:")) {
						numberofpoints = Integer.parseInt(words[1]);
						x = new int[numberofpoints];
						y = new int[numberofpoints];
						polyPointsX = new ArrayList<>();
						polyPointsY = new ArrayList<>();

						for (int i = 0; i < numberofpoints; i++) {
							polyPointsX.add(0);
							polyPointsY.add(0);
						}
					}
					if (words[0].substring(0, 6).equals("vertex")) {
						polyPointsX.set(vertexCount, Integer.parseInt(words[2]));
						polyPointsY.set(vertexCount, Integer.parseInt(words[4]));
						x[vertexCount] = Integer.parseInt(words[2]);
						y[vertexCount] = Integer.parseInt(words[4]);
						vertexCount++;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// repaint the panel with loaded values.
		repaint();

		//@ draw to processing the loaded values.
		if (isEditingCanvas) {
			/// #cleaned [getProcessingValues & Draw]
			// get the scaleFactor
			scaleFactorUp = (float) GUI.myProcessingSketch.frameH / (float) scaledDownProcessingH;
			// calculate how much to scale up. (to processing)
			processingMinX = (int) ((float) mapperMinX * scaleFactorUp);
			processingMinY = (int) ((float) mapperMinY * scaleFactorUp);
			processingMaxX = (int) ((float) mapperMaxX * scaleFactorUp);
			processingMaxY = (int) ((float) mapperMaxY * scaleFactorUp);
			canvasW = (int) ((float) Math.abs(processingMinX - processingMaxX));
			canvasH = (int) ((float) Math.abs(processingMinY - processingMaxY));
			GUI.myProcessingSketch.maskingCanvas.beginDraw();
			GUI.myProcessingSketch.maskingCanvas.background(0);
			GUI.myProcessingSketch.maskingCanvas.fill(255);
			GUI.myProcessingSketch.maskingCanvas.rect(ProcessingSketch.constrain(processingMinX, 0, GUI.myProcessingSketch.maskingCanvas.width), ProcessingSketch.constrain(processingMinY, 0, GUI.myProcessingSketch.maskingCanvas.height), canvasW, canvasH);
			GUI.myProcessingSketch.maskingCanvas.endDraw();
			GUI.myProcessingSketch.loop();
			//#endregion
		}
		if (isEditingMask) {
			/// #cleaned [getProcessingValues & Draw]
			// calculate how much to scale up. (to processing)
			scaledX = new int[numberofpoints];
			scaledY = new int[numberofpoints];
			for (int i = 0; i < numberofpoints; i++) {
				scaledX[i] = (int) ((float) x[i] * scaleFactorUp);
				scaledY[i] = (int) ((float) y[i] * scaleFactorUp);
			}

			GUI.myProcessingSketch.maskingCanvas.beginDraw();
			GUI.myProcessingSketch.maskingCanvas.background(0);
			GUI.myProcessingSketch.maskingCanvas.beginShape();
			GUI.myProcessingSketch.maskingCanvas.noStroke();
			GUI.myProcessingSketch.maskingCanvas.fill(255);
			for (int i = 0; i < numberofpoints; i++) {
				GUI.myProcessingSketch.maskingCanvas.vertex(scaledX[i], scaledY[i]);
			}
			GUI.myProcessingSketch.maskingCanvas.endShape(ProcessingSketch.CLOSE);
			GUI.myProcessingSketch.maskingCanvas.endDraw();
			GUI.myProcessingSketch.loop();
			//#endregion
		}
	}

	class MouseClickHandler extends MouseAdapter { /// [Cleaned]

		public void mouseClicked(MouseEvent e) {

			if (isEditingMask && !isDraggingPoint) {

				// add the point to the ArrayLists.
				polyPointsX.add(e.getX());
				polyPointsY.add(e.getY());

				// number of points has increased.
				numberofpoints++;

				// create the arrays.
				x = new int[numberofpoints];
				y = new int[numberofpoints];

				// transfer to arrays.
				for (int i = 0; i < numberofpoints; i++) {
					x[i] = polyPointsX.get(i);
					y[i] = polyPointsY.get(i);
				}

				//@ draw in processing.
				/// #cleaned [Draw To Processing]
				// calculate how much to scale up. (to processing)
				scaledX = new int[numberofpoints];
				scaledY = new int[numberofpoints];
				for (int i = 0; i < numberofpoints; i++) {
					scaledX[i] = (int) ((float) x[i] * scaleFactorUp);
					scaledY[i] = (int) ((float) y[i] * scaleFactorUp);
				}

				// draw to processing.
				GUI.myProcessingSketch.maskingCanvas.beginDraw();
				GUI.myProcessingSketch.maskingCanvas.background(0);
				GUI.myProcessingSketch.maskingCanvas.beginShape();
				GUI.myProcessingSketch.maskingCanvas.strokeWeight(4);
				GUI.myProcessingSketch.maskingCanvas.fill(255);
				GUI.myProcessingSketch.maskingCanvas.stroke(255, 120, 150);
				for (int i = 0; i < numberofpoints; i++) {
					GUI.myProcessingSketch.maskingCanvas.vertex(scaledX[i], scaledY[i]);
				}
				GUI.myProcessingSketch.maskingCanvas.endShape(ProcessingSketch.CLOSE);
				GUI.myProcessingSketch.maskingCanvas.endDraw();
				GUI.myProcessingSketch.loop();
				//#endregion

				// repaint panel.
				repaint();
			}
		}

		public void mousePressed(MouseEvent e) { /// [Cleaned]

			/// #cleaned [GUI Maintanence]
			// take away any notification message.
			if (GUI.notification.isVisible()) {
				GUI.notification.setVisible(false);
			}
			//#endregion

			if (isEditingCanvas) {
				// user has touched the canvas, so values will be stored instead of using defaults.
				hasMovedCanvas = true;
				editingCanvasPressed(e.getX(), e.getY());
			}

			if (isEditingMask) {
				for (int i = 0; i < numberofpoints; i++) {
					// get the dimensions of each vertex point.
					int pointMinX = polyPointsX.get(i) - 10;
					int pointMinY = polyPointsY.get(i) - 10;
					int pointMaxX = polyPointsX.get(i) + 10;
					int pointMaxY = polyPointsY.get(i) + 10;

					// check to see if user pressed inside vertex point.
					if (e.getX() >= pointMinX && e.getY() >= pointMinY && e.getX() <= pointMaxX && e.getY() <= pointMaxY) {
						isDraggingPoint = true;
						pointBeingDraggedIndex = i;
					}
				}
			}

			// repaint the panel.
			repaint();
		}

		public void mouseDragged(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) { /// [Cleaned]

			if (isEditingCanvas) {
				// reset all booleans to false.
				isDraggingMinXY = false;
				isDraggingMaxXY = false;
				isDraggingAll = false;
			}

			if (isEditingMask) {
				// reset isDraggingPoint to false.
				isDraggingPoint = false;
			}

		}
	}

	class MyMouseMotionListener extends MouseMotionAdapter { /// [DONE]

		public void mouseDragged(MouseEvent e) {

			if (isEditingCanvas) {
				editingCanvasDragged(e.getX(), e.getY());
			}

			if (isEditingMask) {

				if (isDraggingPoint) {
					// set the point being dragged to the new coordinate as it is being dragged.
					polyPointsX.set(pointBeingDraggedIndex, e.getX());
					polyPointsY.set(pointBeingDraggedIndex, e.getY());

					// update the shape as point is being dragged.
					for (int i = 0; i < numberofpoints; i++) {
						x[i] = polyPointsX.get(i);
						y[i] = polyPointsY.get(i);
					}

					//@ draw in processing.
					/// #cleaned [Draw in Processing]
					// calculate how much to scale up. (to processing)
					scaledX = new int[numberofpoints];
					scaledY = new int[numberofpoints];
					for (int i = 0; i < numberofpoints; i++) {
						scaledX[i] = (int) ((float) x[i] * scaleFactorUp);
						scaledY[i] = (int) ((float) y[i] * scaleFactorUp);
					}

					GUI.myProcessingSketch.maskingCanvas.beginDraw();
					GUI.myProcessingSketch.maskingCanvas.background(0);

					GUI.myProcessingSketch.maskingCanvas.beginShape();
					GUI.myProcessingSketch.maskingCanvas.strokeWeight(5);
					GUI.myProcessingSketch.maskingCanvas.fill(255);
					GUI.myProcessingSketch.maskingCanvas.stroke(255, 50, 50);
					for (int i = 0; i < numberofpoints; i++) {
						GUI.myProcessingSketch.maskingCanvas.vertex(scaledX[i], scaledY[i]);
					}
					GUI.myProcessingSketch.maskingCanvas.endShape(ProcessingSketch.CLOSE);

					GUI.myProcessingSketch.maskingCanvas.endDraw();
					GUI.myProcessingSketch.loop();
					//#endregion
				}
			}

			// repaint panel.
			repaint();
		}
	}

	public void editingCanvasPressed(int x_, int y_) { /// [DONE]
		//@ [isDraggingAll] user is translating the whole rectangle.
		if (x_ >= mapperMinX && x_ <= mapperMaxX && y_ >= mapperMinY && y_ <= mapperMaxY) {
			// save the x and y coordinates that was first pressed.
			lastPressedX = x_;
			lastPressedY = y_;

			// save the box values for reference before translation.
			savedMapperMinX = mapperMinX;
			savedMapperMinY = mapperMinY;
			savedMapperMaxX = mapperMaxX;
			savedMapperMaxY = mapperMaxY;
			savedMapperCenterX = mapperCenterX;
			savedMapperCenterY = mapperCenterY;

			// update which one of the modes the user is doing.
			isDraggingAll = true;
			isDraggingMaxXY = false;
			isDraggingMinXY = false;
		}

		//@ [isDragginMinXY] user is dragging from top left corner.
		if (x_ >= mapperMinX && x_ <= mapperMinX + 50 && y_ >= mapperMinY && y_ <= mapperMinY + 50) {
			// automatically move the corner to where the user pressed.
			mapperMinX = x_;
			mapperMinY = y_;

			/// protect against corners and center overlapping.
			if (mapperMinX + 50 >= mapperCenterX - 25) {
				mapperMinX = mapperCenterX - 75;
			}
			if (mapperMinY + 50 >= mapperCenterY - 25) {
				mapperMinY = mapperCenterY - 75;
			}

			// update which one of the modes the user is doing.
			isDraggingMinXY = true;
			isDraggingMaxXY = false;
			isDraggingAll = false;
		}

		//@ [isDraggingMaxXY] user is dragging from bottom right corner.
		if (x_ >= mapperMaxX - 50 && y_ <= mapperMaxX && y_ >= mapperMaxY - 50 && y_ <= mapperMaxY) {
			// automatically move the corner to where the user pressed.
			mapperMaxX = x_;
			mapperMaxY = y_;

			/// protect against corners and center overlapping.
			if (mapperMaxX - 50 <= mapperCenterX + 25) {
				mapperMaxX = mapperCenterX + 75;
			}
			if (mapperMaxY - 50 <= mapperCenterY + 25) {
				mapperMaxY = mapperCenterY + 75;
			}

			// update which one of the modes the user is doing.
			isDraggingMaxXY = true;
			isDraggingMinXY = false;
			isDraggingAll = false;
		}
		// update center point.
		mapperCenterX = (mapperMaxX + mapperMinX) / 2;
		mapperCenterY = (mapperMaxY + mapperMinY) / 2;

		// repaint panel.
		repaint();
	}

	public void editingCanvasDragged(int x_, int y_) { /// [DONE]

		//@ [isDragginMinXY]
		if (isDraggingMinXY) {
			// update MinX and MinY to current point.
			mapperMinX = x_;
			mapperMinY = y_;

			// constrain the values to fit in panel.
			if (mapperMinX <= 0) {
				mapperMinX = 0;
			}
			if (mapperMinY <= 0) {
				mapperMinY = 0;
			}
			if (mapperMaxX >= scaledDownProcessingW) {
				mapperMaxX = scaledDownProcessingW;
			}
			if (mapperMaxY >= scaledDownProcessingH) {
				mapperMaxY = scaledDownProcessingH;
			}

			// protect against corners and center overlapping
			if (mapperMinX + 50 >= mapperCenterX - 25) {
				mapperMinX = mapperCenterX - 75;
			}
			if (mapperMinY + 50 >= mapperCenterY - 25) {
				mapperMinY = mapperCenterY - 75;
			}
		}

		//@ [isDraggingMaxXY]
		if (isDraggingMaxXY) {
			// update MaxX and MaxY to current point.
			mapperMaxX = x_;
			mapperMaxY = y_;

			// constrain the values to fit in panel.
			if (mapperMinX <= 0) {
				mapperMinX = 0;
			}
			if (mapperMinY <= 0) {
				mapperMinY = 0;
			}
			if (mapperMaxX >= scaledDownProcessingW) {
				mapperMaxX = scaledDownProcessingW;
			}
			if (mapperMaxY >= scaledDownProcessingH) {
				mapperMaxY = scaledDownProcessingH;
			}

			// protect against corners and center overlapping
			if (mapperMaxX - 50 <= mapperCenterX + 25) {
				mapperMaxX = mapperCenterX + 75;
			}
			if (mapperMaxY - 50 <= mapperCenterY + 25) {
				mapperMaxY = mapperCenterY + 75;
			}
		}

		//@ [isDraggingAll]
		if (isDraggingAll) {

			int translateFactorX = 0;
			int translateFactorY = 0;

			// take into account which direction user is moving.
			if (lastPressedX < x_) {
				translateFactorX = x_ - lastPressedX;
				mapperMinX = savedMapperMinX + translateFactorX;
				mapperMaxX = savedMapperMaxX + translateFactorX;
				mapperCenterX = mapperCenterX + translateFactorX;
			}
			if (lastPressedY < y_) {
				translateFactorY = y_ - lastPressedY;
				mapperMinY = savedMapperMinY + translateFactorY;
				mapperMaxY = savedMapperMaxY + translateFactorY;
				mapperCenterY = savedMapperCenterY + translateFactorY;
			}
			if (lastPressedX > x_) {
				translateFactorX = (lastPressedX - x_) * -1;
				mapperMinX = translateFactorX + savedMapperMinX;
				mapperMaxX = translateFactorX + savedMapperMaxX;
				mapperCenterX = translateFactorX + savedMapperCenterX;
			}
			if (lastPressedY > y_) {
				translateFactorY = (lastPressedY - y_) * -1;
				mapperMinY = translateFactorY + savedMapperMinY;
				mapperMaxY = translateFactorY + savedMapperMaxY;
				mapperCenterY = translateFactorY + savedMapperCenterY;
			}
			if (lastPressedX == x_) {
				mapperMinX = savedMapperMinX;
				mapperMaxX = savedMapperMaxX;
				mapperCenterX = savedMapperCenterX;
			}
			if (lastPressedY == y_) {
				mapperMinY = savedMapperMinY;
				mapperMaxY = savedMapperMaxY;
				mapperCenterY = savedMapperCenterY;
			}

			// constrain the values to fit in panel.
			if (mapperMinX <= 0) {
				mapperMinX = 0;
			}
			if (mapperMinY <= 0) {
				mapperMinY = 0;
			}
			if (mapperMaxX >= scaledDownProcessingW) {
				mapperMaxX = scaledDownProcessingW;
			}
			if (mapperMaxY >= scaledDownProcessingH) {
				mapperMaxY = scaledDownProcessingH;
			}

			// keep the box from translating outside of panel.
			if (mapperMinX <= 0 && mapperMaxX <= mapperMinX + 150) {
				mapperMinX = 0;
				mapperMaxX = 150;
			}
			if (mapperMinY <= 0 && mapperMaxY <= mapperMinY + 150) {
				mapperMinY = 0;
				mapperMaxY = 150;
			}
			if (mapperMaxX >= scaledDownProcessingW && mapperMaxX - mapperMinX <= 150) {
				mapperMaxX = scaledDownProcessingW;
				mapperMinX = scaledDownProcessingW - 150;
			}
			if (mapperMaxY >= scaledDownProcessingH && mapperMaxY - mapperMinY <= 150) {
				mapperMaxY = scaledDownProcessingH;
				mapperMinY = scaledDownProcessingH - 150;
			}
		}

		// update center point.
		mapperCenterX = (mapperMaxX + mapperMinX) / 2;
		mapperCenterY = (mapperMaxY + mapperMinY) / 2;

		//@ draw in processing.
		/// #cleaned [Draw To Processing]
		// calculate how much to scale up. (to processing)
		processingMinX = (int) ((float) mapperMinX * scaleFactorUp);
		processingMinY = (int) ((float) mapperMinY * scaleFactorUp);
		processingMaxX = (int) ((float) mapperMaxX * scaleFactorUp);
		processingMaxY = (int) ((float) mapperMaxY * scaleFactorUp);
		canvasW = (int) ((float) Math.abs(processingMinX - processingMaxX));
		canvasH = (int) ((float) Math.abs(processingMinY - processingMaxY));
		// draw to processing.
		GUI.myProcessingSketch.maskingCanvas.beginDraw();
		GUI.myProcessingSketch.maskingCanvas.background(0);
		GUI.myProcessingSketch.maskingCanvas.fill(255);
		GUI.myProcessingSketch.maskingCanvas.noStroke();
		GUI.myProcessingSketch.maskingCanvas.rect(ProcessingSketch.constrain(processingMinX, 0, GUI.myProcessingSketch.maskingCanvas.width), ProcessingSketch.constrain(processingMinY, 0, GUI.myProcessingSketch.maskingCanvas.height), canvasW, canvasH);
		GUI.myProcessingSketch.maskingCanvas.endDraw();
		GUI.myProcessingSketch.loop();
		//#endregion
	}
}
