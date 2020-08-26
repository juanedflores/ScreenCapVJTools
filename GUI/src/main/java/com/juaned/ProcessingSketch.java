package com.juaned;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.BorderFactory;
//import netP5.NetAddress;
//import oscP5.OscMessage;
//import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class ProcessingSketch extends PApplet {

	private static final long serialVersionUID = 1L;

	ProcessingSketch sketch;
	int frameW = 1080;
	int frameH = 720;

	/*
	 * [ArrayLists]
	 */
	/// #cleaned [ArrayLists]
	public static ArrayList<Blob> Blobs;
	public static ArrayList<Blob> liveCapBlobs;
	//#endregion

	/*
	 * [drawtText]
	 */
	/// #cleaned [drawText]
	PFont font;
	int fontSize = 90;
	//#endregion

	/*
	 * [PGraphics]
	 */
	/// #cleaned [PGraphics]
	PGraphics foregroundCanvas;
	PGraphics backgroundCanvas;
	PGraphics liveCapture;
	PGraphics maskingCanvas;
	PGraphics drawTextCanvas;
	PGraphics buttonCanvas;
	int whichLayerToDraw = 1;
	int canvasMinX = 0;
	int canvasMinY = 0;
	//#endregion

	/*
	 * [OSC]
	 */
	/// #cleaned [OSC]
	//OscP5 oscP5;
	//NetAddress myRemoteLocation;
	//#endregion

	/*
	 * [Live]
	 */
	/// #cleaned [Live]
	boolean goLive = false;
	boolean livenormal = true;
	boolean livechroma = false;
	boolean chromaRedraw = true;
	//#endregion

	/* 
	 * [Display Style]
	 */
	/// #cleaned [Display Style]
	String displayStyle = "";
	Boolean accumulate = true;
	//#endregion

	/* 
	 * [Scan Values]
	 */
	/// #cleaned [Scan Values]
	float avgColThresholdScan = 150; /// background threshold.
	float avgColThresholdChroma = 100; /// background threshold.
	boolean avgColInverted = false;
	float darknessThreshold = 100;
	//#endregion


	public void setup() {
		size(1080, 720);

		/*
		 * [ArrayLists]
		 */
		/// #cleaned [ArrayLists]
		Blobs = new ArrayList<Blob>();
		//#endregion

		/*
		 * [drawText]
		 */
		/// #cleaned [drawText]
		font = createFont("Georgia", 32);
		//#endregion

		/*
		 * [PGraphics]
		 */
		/// #cleaned [PGraphics]
		drawTextCanvas = createGraphics(600, 600); /// to draw a text offscreen.
		buttonCanvas = createGraphics(600, 600); /// to organize our blobs.
		foregroundCanvas = createGraphics(frameW, frameH); /// to draw the final output in fullscreen.
		liveCapture = createGraphics(frameW, frameH);
		backgroundCanvas = createGraphics(frameW, frameH);
		maskingCanvas = createGraphics(frameW, frameH);

		drawTextCanvas.beginDraw();
		drawTextCanvas.background(0);
		drawTextCanvas.endDraw();

		foregroundCanvas.beginDraw();
		foregroundCanvas.background(0, 0);
		foregroundCanvas.endDraw();

		backgroundCanvas.beginDraw();
		backgroundCanvas.background(0);
		backgroundCanvas.endDraw();

		buttonCanvas.beginDraw();
		buttonCanvas.background(0);
		buttonCanvas.endDraw();

		liveCapture.beginDraw();
		liveCapture.background(0, 0);
		liveCapture.endDraw();

		maskingCanvas.beginDraw();
		maskingCanvas.background(0, 0);
		maskingCanvas.endDraw();
		//#endregion

		/*
		 * [OSC]
		 */
		/*
		/// #cleaned [OSC]
		oscP5 = new OscP5(this, 8000); /// receive osc messages.
		myRemoteLocation = new NetAddress("127.0.0.1", 9000); /// send osc messages.
		// set up any osc message received to send to the appropriate methods.
		oscP5.plug(this, "changeBlobOpacity", "/blobOpacity");
		oscP5.plug(this, "changeBlobScale", "/blobScale");
		oscP5.plug(this, "makeOriginal", "/makeOriginal");
		oscP5.plug(this, "receiveBangGrid", "/bangGrid");
		oscP5.plug(this, "receiveCanvasToDraw", "/canvas");
		oscP5.plug(this, "receiveLiveChromaValues", "/liveChroma");
		oscP5.plug(this, "receiveBangLiveCapture", "/bangLive");
		oscP5.plug(this, "receiveLiveStyle", "/liveStyle");
		oscP5.plug(this, "receiveBangRedrawLive", "/redrawLiveBack");
		oscP5.plug(this, "receiveIndexForArea", "/getArea");
		oscP5.plug(this, "specifyLocation", "/setLocation");
		oscP5.plug(this, "requestFrameDimensions", "/getDimensions");
		oscP5.plug(this, "collectLiveBlobs", "/collectBlobs");
		oscP5.plug(this, "pauseSketch", "/pause");
		oscP5.plug(this, "resumeSketch", "/resume");
		oscP5.plug(this, "collectOnly", "/collectOnly");
		//#endregion
		 * 
		 */
	}

	public void draw() {

		image(backgroundCanvas, canvasMinX, canvasMinY);
		image(liveCapture, canvasMinX, canvasMinY);
		image(foregroundCanvas, canvasMinX, canvasMinY);
		image(maskingCanvas, 0, 0);

		if (goLive) {
			liveCapture();
		} else {
			noLoop();
		}
	}

	void liveCapture() { // DRAWING: [DONE]
		/* 
		 * constantly take a screenshot of a drawn rectangle and get the
		 * img to be displayed in the liveCapture PGraphics canvas.
		 */

		// get a screenshot of the determined rectangle of screen.
		PImage liveImg = convertToPImage(GUI.screenCapDisplay.liveScreenshot());

		/// #cleaned [if livenormal]
		if (livenormal) {
			// if there is liveOutputX value other than 0, then it is a custom point.
			if (GUI.screenCapDisplay.liveOutputX != 0) {
				// draw on a point the user has indicated.
				liveCapture.beginDraw();
				liveCapture.background(0, 0);
				liveCapture.image(liveImg, GUI.screenCapDisplay.liveOutputX, GUI.screenCapDisplay.liveOutputY);
				liveCapture.endDraw();
			} else {
				// if no point has been indicated, the default is at point (0,0).
				liveCapture.beginDraw();
				liveCapture.background(0, 0);
				liveCapture.image(liveImg, 0, 0);
				liveCapture.endDraw();
			}
		}
		//#endregion

		/// #cleaned [if livechroma]
		if (livechroma) {
			// get the chromakeyedImg of the current liveImg.
			PImage chromaKeyedImg = chromaKeyImage(liveImg);
			// if there is liveOutputX value other than 0, then it is a custom point.
			if (GUI.screenCapDisplay.liveOutputX != 0) {
				// draw on a point the user has indicated.
				liveCapture.beginDraw();
				if (chromaRedraw) {
					liveCapture.background(0, 0);
				}
				liveCapture.image(chromaKeyedImg, GUI.screenCapDisplay.liveOutputX, GUI.screenCapDisplay.liveOutputY);
				liveCapture.endDraw();
			} else {
				// if no point has been indicated, the default is at point (0,0).
				liveCapture.beginDraw();
				if (chromaRedraw) {
					liveCapture.background(0, 0);
				}
				liveCapture.image(chromaKeyedImg, 0, 0);
				liveCapture.endDraw();
			}
		}
		//#endregion
	}

	void drawTextGrid(String text_) { // DRAWING: [DONE]

		// draw the text to the drawTextCanvas.
		/// #cleaned [drawText()]
		// random RGB values
		float textR = random(255);
		float textG = random(255);
		float textB = random(255);
		// draw to the canvas;
		drawTextCanvas.beginDraw();

		drawTextCanvas.background(0, 0);
		drawTextCanvas.textFont(font);
		drawTextCanvas.textSize(fontSize);
		drawTextCanvas.fill(textR, textG, textB);
		drawTextCanvas.textAlign(CENTER);
		drawTextCanvas.text(text_, 600 / 2, (600 / 4) * 3);

		drawTextCanvas.endDraw();
		//#endregion

		// scan the canvas and store them in the array as Blobs.
		scan(drawTextCanvas);

		// copy all the scanned blobs to the sortedBlobs array and add to the grid.
		GUI.gridPage.addBlobsToGrid();
	}

	void collectScreenshotBlbs() { // DRAWING: [DONE]
		/*
		 * scan screenshot for blobs and add to grid.
		 */

		// convert the BufferedImage slice to a PImage.
		PImage scrnshot = convertToPImage(GUI.screenCapDisplay.sliceImg);
		// scan
		scan(scrnshot);
	}

	void blobRandom(int arrayIndex_) { // DISPLAY: [DONE]
		/*
		 * draws in a random location in the canvas.
		 */

		// get a random location in canvas.
		int randomX = (int) random(foregroundCanvas.width);
		int randomY = (int) random(foregroundCanvas.height);

		// get the blob we want.
		Blob b = GUI.gridPage.sortedBlobs.get(arrayIndex_);

		//@ choosing which layer to draw in.
		if (whichLayerToDraw == 0) {
			backgroundCanvas.beginDraw();
			b.showBlobAtPoint(this, backgroundCanvas, randomX, randomY);
			backgroundCanvas.endDraw();
		}
		if (whichLayerToDraw == 1) {
			foregroundCanvas.beginDraw();
			b.showBlobAtPoint(this, foregroundCanvas, randomX, randomY);
			foregroundCanvas.endDraw();
		}

		loop();
	}

	void specifyLocation(int arrayIndex_, int x_, int y_) { // DISPLAY: 

		// get the blob we want.
		Blob b = GUI.gridPage.sortedBlobs.get(arrayIndex_);

		//@ choosing which layer to draw in.
		if (whichLayerToDraw == 0) {
			backgroundCanvas.beginDraw();
			b.showBlobAtPoint(this, backgroundCanvas, x_, y_);
			backgroundCanvas.endDraw();
		}
		if (whichLayerToDraw == 1) {
			foregroundCanvas.beginDraw();
			b.showBlobAtPoint(this, foregroundCanvas, x_, y_);
			foregroundCanvas.endDraw();
		}

		loop();
	}

	void changeBlobOpacity(int arrayIndex_, int opacity_) { // EDIT: [DONE] 
		/* 
		 * changes the alpha value of any individual blob. 
		 */

		// get the blob we want. 
		Blob b = GUI.gridPage.sortedBlobs.get(arrayIndex_);
		// change its opacity.
		b.setOpacity(opacity_);
	}

	void changeBlobScale(int arrayIndex_, int scale_) { // EDIT: [DONE] 
		/* 
		 * changes the scale value of any individual blob. 
		 */
		// get the blob we want.  
		Blob b = GUI.gridPage.sortedBlobs.get(arrayIndex_);

		// change its opacity.
		b.changeBlobScale(scale_);
	}

	void makeOriginal(int arrayIndex_) { // EDIT [DONE]
		/* 
		 * reverts back to the original blob. 
		 */

		// get the blob we want.  
		Blob b = GUI.gridPage.sortedBlobs.get(arrayIndex_);

		// if there is a originalPoints array saved, transfer back to points array.
		if (b.getOriginalPoints() != null) {
			b.points = new ArrayList<PVector>();
			for (PVector p : b.originalPoints) {
				// add point to points array.
				b.points.add(new PVector(p.x, p.y));
			}
			b.minX = b.originalMinX;
			b.minY = b.originalMinY;
			b.maxX = b.originalMaxX;
			b.maxY = b.originalMaxY;
			b.pixelSize = 1;
		}
	}

	void changeCanvasSize(int width_, int height_) { // GENERAL: [DONE]
		/*
		 * the JFrame containing the Processing Sketch has changed size. This method
		 * will change the size of the mainCanvas with the new dimensions.
		 */

		noLoop(); /// stop loop just to be sure we don't run into animationThread errors.

		backgroundCanvas = createGraphics(width_, height_);
		backgroundCanvas.beginDraw();
		backgroundCanvas.background(0);
		backgroundCanvas.endDraw();

		liveCapture = createGraphics(width_, height_);
		liveCapture.beginDraw();
		liveCapture.background(0, 0);
		liveCapture.endDraw();

		foregroundCanvas = createGraphics(width_, height_);
		foregroundCanvas.beginDraw();
		foregroundCanvas.background(0, 0);
		foregroundCanvas.endDraw();

		// @ [DEBUGGING] print the dimensions of the new mainCanvas.
		System.out.println("\ncreated a new mainCanvas with dimensions:\nwidth: " + width_ + "\nheight: " + height_);

		loop();
	}

	void createMask(int[] x_, int[] y_) {  // GENERAL: [DONE]

		maskingCanvas.beginDraw();
		maskingCanvas.background(0);
		maskingCanvas.noStroke();
		maskingCanvas.fill(255);
		maskingCanvas.smooth();
		maskingCanvas.beginShape();
		for (int i = 0; i < x_.length; i++) {
			maskingCanvas.vertex(x_[i], y_[i]);
		}
		maskingCanvas.endShape(CLOSE);
		maskingCanvas.endDraw();

		// change all white pixels to transparent.
		maskingCanvas.loadPixels();
		for (int x = 0; x < maskingCanvas.width; x++) {
			for (int y = 0; y < maskingCanvas.height; y++) {
				int index = maskingCanvas.width * y + x;
				int col = color(maskingCanvas.pixels[index]);
				float avgColDist = Blob.distSq(red(0), green(0), blue(0), red(col), green(col), blue(col));

				if (avgColDist > 150) {
					// make the pixel transparent.
					maskingCanvas.pixels[index] = color(0, 0);
				}
			}
		}
		maskingCanvas.updatePixels();

		loop();
	}

	void scan(PGraphics canvas) { //@ NEEDS WORK
		/*
		 * scan a PGraphics canvas to store temporarily in the Blobs array.
		 */

		canvas.loadPixels();
		for (int x = 0; x < canvas.width; x++) {
			for (int y = 0; y < canvas.height; y++) {
				int index = canvas.width * y + x;
				int col = color(canvas.pixels[index]);
				float r = red(col);
				float g = green(col);
				float b = blue(col);

				if (col != color(0)) {
					boolean found = false;
					for (Blob bl : Blobs) {
						if (bl.isNeighbor(x, y) && bl.isSameColor(r, g, b)) {
							bl.addToBlob(x, y, r, g, b);
							found = true;
							break;
						}
					}

					if (!found) {
						Blob newBlob = new Blob(x, y, r, g, b);
						Blobs.add(newBlob);
					}
				}

				// if blob is too small or avgCol too dark
				for (int i = 0; i < Blobs.size(); i++) {
					Blob currentBlob = Blobs.get(i);
					if (currentBlob.getArea() < 300 && i != 0) {
						Blobs.remove(i);
						i--;
					}
				}
			}
		}
	}

	void scan(PImage img_) { //@ NEEDS WORK
		/*
		 * scan a PImage to store temporarily in the Blobs array.
		 */

		int avgColor = avgColor(img_);

		img_.loadPixels();
		for (int x = 0; x < img_.width; x++) {
			for (int y = 0; y < img_.height; y++) {
				int index = img_.width * y + x;
				int col = color(img_.pixels[index]);
				float r = red(col);
				float g = green(col);
				float b = blue(col);
				float avgColDist = Blob.distSq(red(avgColor), green(avgColor), blue(avgColor), red(col), green(col), blue(col));

				if (avgColDist > avgColThresholdScan * avgColThresholdScan) {

					boolean found = false;
					for (Blob bl : Blobs) {
						if (bl.isNeighbor(x, y) && bl.isSameColor(r, g, b)) {
							bl.addToBlob(x, y, r, g, b);
							found = true;
							break;
						}
					}

					if (!found) {
						Blob newBlob = new Blob(x, y, r, g, b);
						Blobs.add(newBlob);
					}
				}
			}
		}

		// draws the sortedBlobs to grid.
		GUI.gridPage.addBlobsToGrid();
	}

	void collectLiveCapBlobs(PImage img_) { //@ NOT IMPLEMENTED YET

		int avgColor = avgColor(img_);
		img_.loadPixels();
		for (int x = 0; x < img_.width; x++) {
			for (int y = 0; y < img_.height; y++) {
				int index = img_.width * y + x;
				int col = color(img_.pixels[index]);
				float r = red(col);
				float g = green(col);
				float b = blue(col);
				float avgColDist = Blob.distSq(red(avgColor), green(avgColor), blue(avgColor), red(col), green(col), blue(col));

				if (avgColDist > avgColThresholdScan * avgColThresholdScan || (r + g + b) / 3 > darknessThreshold) {

					boolean found = false;
					for (Blob bl : Blobs) {
						if (bl.isNeighbor(x, y) && bl.isSameColor(r, g, b)) {
							bl.addToBlob(x, y, r, g, b);
							found = true;
							break;
						}
					}

					if (!found) {
						Blob newBlob = new Blob(x, y, r, g, b);
						Blobs.add(newBlob);
					}
				}

				// if blob is too small or avgCol too dark
				for (int i = 0; i < Blobs.size(); i++) {
					Blob currentBlob = Blobs.get(i);
					if (currentBlob.getArea() < 300 && i != 0) {
						Blobs.remove(i);
						i--;
					}
				}
			}
		}

		for (Blob b : Blobs) {
			Blob liveB = new Blob(b.getX(), b.getY(), b.getAvgG(), b.getAvgG(), b.getAvgB());
			liveB.setPoints(b.getPoints());
			liveB.setBorder(b.getMinX(), b.getMaxX(), b.getMinY(), b.getMaxY());
			liveB.setAvgCols(b.getAvgR(), b.getAvgG(), b.getAvgB());
			liveCapBlobs.add(liveB);
		}
		// clear Blobs
		Blobs.clear();
		System.out.println("sortedBlob Size: " + liveCapBlobs.size());
	}

	PImage chromaKeyImage(PImage img_) { /// [DONE]
		/* 
		 * make a target pixel (according to a dominant color threshold) transparent.
		 */

		int avgColor = avgColor(img_);

		img_.loadPixels();
		for (int x = 0; x < img_.width; x++) {
			for (int y = 0; y < img_.height; y++) {
				int index = img_.width * y + x;
				int col = color(img_.pixels[index]);
				float avgColDist = Blob.distSq(red(avgColor), green(avgColor), blue(avgColor), red(col), green(col), blue(col));

				// determine which pixels to make transparent.
				if (!avgColInverted) {
					if (avgColDist < avgColThresholdChroma * avgColThresholdChroma) {
						// make the pixel transparent.
						img_.pixels[index] = color(0, 0);
					}
				} else {
					if (avgColDist > avgColThresholdChroma * avgColThresholdChroma) {
						// make the pixel transparent.
						img_.pixels[index] = color(0, 0);
					}
				}
			}
		}
		img_.updatePixels();

		return img_;
	}

	int avgColor(PImage img_) { /// [DONE]
		/* 
		 * returns the average color of an image. (dominant color)
		 */

		float curR = 0;
		float curG = 0;
		float curB = 0;
		float commR = 0;
		float commG = 0;
		float commB = 0;
		for (int x = 0; x < img_.width; x++) {
			for (int y = 0; y < img_.height; y++) {
				int index = x + (y * img_.width);
				int cc = img_.pixels[index];
				curR = red(cc);
				curG = green(cc);
				curB = blue(cc);

				commR += curR;
				commG += curG;
				commB += curB;
			}
		}

		float avgR = commR / (img_.width * img_.height);
		float avgG = commG / (img_.width * img_.height);
		float avgB = commB / (img_.width * img_.height);
		int c = color(avgR, avgG, avgB);

		return c;
	}

	BufferedImage getTotalCanvasImg() { /// [DONE]
		/* 
		 * returns an image of the sum of all PGraphics layers.
		 */
		// initialize the result image.
		PImage totalImg = createImage(backgroundCanvas.width, backgroundCanvas.height, ARGB);

		totalImg.loadPixels();
		backgroundCanvas.loadPixels();
		foregroundCanvas.loadPixels();
		liveCapture.loadPixels();
		for (int x = 0; x < backgroundCanvas.width; x++) {
			for (int y = 0; y < backgroundCanvas.height; y++) {
				int index = backgroundCanvas.width * y + x;
				int pixColorBack = color(backgroundCanvas.pixels[index]);
				int pixColorFore = color(foregroundCanvas.pixels[index]);
				int pixColorLive = color(liveCapture.pixels[index]);

				// sum of all pixels.
				totalImg.pixels[index] += pixColorBack;
				totalImg.pixels[index] += pixColorLive;
				totalImg.pixels[index] += pixColorFore;
			}
		}
		totalImg.updatePixels();

		// return as BufferedImage
		BufferedImage totalBufImg = (BufferedImage) totalImg.getNative();

		return totalBufImg;
	}

	PImage convertToPImage(BufferedImage img_) { /// [DONE]
		/* 
		 * converts a entered BufferedImage into a PImage.
		 */
		try {
			BufferedImage bimg = img_;
			PImage img = new PImage(bimg.getWidth(), bimg.getHeight(), PConstants.ARGB);
			bimg.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
			img.updatePixels();
			return img;
		} catch (Exception e) {
			System.err.println("Can't create image from buffer");
			e.printStackTrace();
		}
		return null;
	}

	void receiveBangGrid(int index_) { // OSC: [Cleaned]
		/*
		 * an index is received by OSC activating an event that corresponds to that blob
		 * in the grid.
		 */

		// get the sortedBlob index being received to draw.
		int sortedIndex = index_;
		// get the appropriate button between 0 and 35 containing the index.
		int buttonNum = index_ % (GUI.gridPage.maxRowsCols * GUI.gridPage.maxRowsCols);

		// we want to reset the previously drawn blue border if there is any.
		if (GUI.gridPage.lastButtonPressed != -1) {
			GUI.gridPage.gridButtons[GUI.gridPage.lastButtonPressed].setBorder(GUI.gridPage.defaultBorder);
		}

		// only draw a blue border highlight if index received is in the page that the
		// user is currently on.
		if (sortedIndex >= (GUI.gridPage.pageNum * (GUI.gridPage.maxRowsCols * GUI.gridPage.maxRowsCols))
				&& sortedIndex < ((GUI.gridPage.pageNum + 1) * (GUI.gridPage.maxRowsCols * GUI.gridPage.maxRowsCols))) {
			GUI.gridPage.gridButtons[buttonNum].setBorder(BorderFactory.createLineBorder(Color.BLUE, 10));
		}
		// store the last button pressed into this global variable.
		GUI.gridPage.lastButtonPressed = buttonNum;
	}

	void receiveCanvasToDraw(int canvasNum_) { // OSC: [DONE]
		/*
		 * a number is received corresponding to a canvas that will
		 * later determine where the next output is going.
		 */
		whichLayerToDraw = canvasNum_;
	}

	void receiveLiveChromaValues(int avgColThreshold_, int invert_) { // OSC: [DONE]
		/*
		 * change the values that are used to scan or process live image
		 */
		avgColThresholdChroma = avgColThreshold_;
		if (invert_ == 1) {
			avgColInverted = true;
		} else {
			avgColInverted = false;
		}
	}

	void receiveBangLiveCapture(String bang_) { // OSC: [DONE]
		/*
		 * will receive a bang that will take and display the most current screenshot of a selected
		 * rectangle. will receive a warning if there is no rectangle selected yet.
		 */

		if (GUI.screenCapDisplay.sliceImg != null) {
			PImage liveImg = convertToPImage(GUI.screenCapDisplay.liveScreenshot());
			if (livenormal) {
				if (GUI.screenCapDisplay.liveOutputX != 0) {
					// draw on a point the user has indicated.
					liveCapture.beginDraw();
					liveCapture.background(0, 0);
					liveCapture.image(liveImg, GUI.screenCapDisplay.liveOutputX, GUI.screenCapDisplay.liveOutputY);
					liveCapture.endDraw();
				} else {
					// if no point has been indicated, the default is at point (0,0).
					liveCapture.beginDraw();
					liveCapture.background(0, 0);
					liveCapture.image(liveImg, 0, 0);
					liveCapture.endDraw();
				}
			}
			if (livechroma) {
				PImage chromaKeyedImg = chromaKeyImage(liveImg);
				if (GUI.screenCapDisplay.liveOutputX != 0) {
					// draw on a point the user has indicated.
					liveCapture.beginDraw();
					if (chromaRedraw) {
						liveCapture.background(0, 0);
					}
					liveCapture.image(chromaKeyedImg, GUI.screenCapDisplay.liveOutputX, GUI.screenCapDisplay.liveOutputY);
					liveCapture.endDraw();
				} else {
					// if no point has been indicated, the default is at point (0,0).
					liveCapture.beginDraw();
					if (chromaRedraw) {
						liveCapture.background(0, 0);
					}
					liveCapture.image(chromaKeyedImg, 0, 0);
					liveCapture.endDraw();
				}
			}
		} else {
			GUI.notification.setMessage("/bangLive: save a live rect first", GUI.notification.warning, false);
		}

		loop();
	}

	void receiveLiveStyle(int style_) { // OSC: [DONE]
		/*
		 * receive a value to change what live display style to be in.
		 */

		if (style_ == 0) {
			// change colors of buttons to update which is active.
			GUI.screenCapInfo.liveStyleNormalButton.setBackground(GUI.screenCapInfo.selectedLiveStyleColor);
			GUI.screenCapInfo.liveStyleChroma1Button.setBackground(Color.GRAY);
			GUI.screenCapInfo.liveStyleChroma2Button.setBackground(Color.GRAY);
			// set the appropriate live booleans.
			livechroma = false;
			chromaRedraw = true;
			livenormal = true;
		}
		if (style_ == 1) {
			// change colors of buttons to update which is active.
			GUI.screenCapInfo.liveStyleChroma1Button.setBackground(GUI.screenCapInfo.selectedLiveStyleColor);
			GUI.screenCapInfo.liveStyleNormalButton.setBackground(Color.GRAY);
			GUI.screenCapInfo.liveStyleChroma2Button.setBackground(Color.GRAY);
			// set the appropriate live booleans.
			livenormal = false;
			livechroma = true;
			chromaRedraw = true;
		}
		if (style_ == 2) {
			// change colors of buttons to update which is active.
			GUI.screenCapInfo.liveStyleChroma2Button.setBackground(GUI.screenCapInfo.selectedLiveStyleColor);
			GUI.screenCapInfo.liveStyleChroma1Button.setBackground(Color.GRAY);
			GUI.screenCapInfo.liveStyleNormalButton.setBackground(Color.GRAY);
			// set the appropriate live booleans.
			livenormal = false;
			livechroma = true;
			chromaRedraw = false;
		}
	}

	void receiveBangRedrawLive(String bang_) { // OSC: [DONE]
		/* 
		 * redraw the background of liveCapture PGraphics canvas.
		 */

		liveCapture.beginDraw();
		liveCapture.background(0, 0);
		liveCapture.endDraw();
		loop();
	}

	void receiveIndexForArea(int index_) { // OSC: [DONE]

		// get the blob being requested.
		//Blob b = GUI.gridPage.sortedBlobs.get(index_);

		// program listening should have the /blobArea header to receive the int.
		//OscMessage myMessage = new OscMessage("/blobArea");
		// add the area of the requested blob.
		//myMessage.add(b.getArea());
		// send the message.
		//oscP5.send(myMessage, myRemoteLocation);
	}

	void requestFrameDimensions(String bang_) { // OSC: [DONE]

		// program listening should have the /blobArea header to receive the int.
		//OscMessage myMessage = new OscMessage("/dimensions");
		// add the area of the requested blob.
		//myMessage.add(frameW);
		//myMessage.add(frameH);
		// send the message.
		//oscP5.send(myMessage, myRemoteLocation);
	}

	void collectLiveBlobs(String bang_) { // OSC: [DONE]

		/// #cleaned [clearBlobs]

		// clear all the blobs in array.
		GUI.gridPage.sortedBlobs.clear();
		// send via OSC the current state of blob array.
		sendSortedBlobAmount();
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
		//#endregion

		System.out.println("scaledRect: " + GUI.screenCapDisplay.scaledUpRect.x);

		/// #cleaned [collect]
		// scan the rectangle windows
		GUI.screenCapDisplay.getSliceImg();
		collectScreenshotBlbs();

		// disable all rect related buttons.
		GUI.screenCapInfo.collectButton.setEnabled(false);
		GUI.screenCapInfo.saveLiveRectButton.setEnabled(false);
		//#endregion
	}

	void pauseSketch(String bang_) { // OSC [DONE]
		noLoop();
	}

	void resumeSketch(String bang_) { // OSC [DONE]
		loop();
	}

	void collectOnly(String bang_) { // OSC [DONE]
		/// #cleaned [collect]
		// scan the rectangle windows
		GUI.screenCapDisplay.getSliceImg();
		collectScreenshotBlbs();

		// disable all rect related buttons.
		GUI.screenCapInfo.collectButton.setEnabled(false);
		GUI.screenCapInfo.saveLiveRectButton.setEnabled(false);
		//#endregion
	}

	void sendSortedBlobAmount() { // OSC: [DONE]
		/*
		 * this will updated a program listening through OSC what the current amount of
		 * blobs are contained in the grid. these are in the sortedBlobs array.
		 */
		// program listening should have the /sortedBlobs header to receive the int.
		//OscMessage myMessage = new OscMessage("/maxIndex");
		// add the size of the sortedBlobs array.
		//myMessage.add(GUI.gridPage.sortedBlobs.size());
		// send the message.
		//oscP5.send(myMessage, myRemoteLocation);
	}
}
