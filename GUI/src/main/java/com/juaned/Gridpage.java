package com.juaned;

import static com.juaned.ProcessingSketch.Blobs;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Gridpage extends JPanel {

	private static final long serialVersionUID = 1L;

	public static Gridpage gridPage;

	/* 
	 * [Swing Elements]
	 */
	/// #cleaned [Swing Elements]
	JPanel pagePanel;
	JLabel pageNumLabel;
	JButton turnPageNext, turnPageBack;
	JButton gridButtons[];
	public static Font commodoreFont;
	Font textInputFont;
	//#endregion

	/* 
	 * [Grid Variables]
	 */
	/// #cleaned [Grid Variables]
	int pageNum = 0;
	int rowscols = 0;
	int maxRowsCols = 6;
	int lastButtonPressed = -1;
	Border defaultBorder = UIManager.getBorder("Button.border");
	//#endregion

	/* 
	 * [Handlers]
	 */
	/// #cleaned [Handlers]
	GridButtonHandler gridButtonHandler = new GridButtonHandler();
	PageButtonHandler pageButtonHandler = new PageButtonHandler();
	//#endregion

	/* 
	 * [ArrayLists]
	 */
	/// #cleaned [ArrayLists]
	ArrayList<Blob> sortedBlobs;
	//#endregion

	public Gridpage() {
		gridPage = this;
		this.setBackground(Color.BLACK);
		this.setLayout(null);
		this.setVisible(true);

		/* 
		 * [Swing Elements]
		 */
		/// #cleaned [Swing Elements]

		/// [Fonts]
		InputStream fontInputStream = getClass().getResourceAsStream("/fonts/com.ttf");
		try {
			// create a font from the .tff file of a commodore 64 font.
			commodoreFont = Font.createFont(Font.TRUETYPE_FONT, fontInputStream);
			// adjust the sizes for each font.
			textInputFont = commodoreFont.deriveFont(24f);
		} catch (FontFormatException e1) {
			System.out.println("FontFormatException");
			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Font IO Exception");
			e1.printStackTrace();
		}

		/// [pagePanel]
		/// user will be able to turn the pages of button grid if there are more than 36 options.
		pagePanel = new JPanel(new BorderLayout());
		// set up the background of pagePanel.
		pagePanel.setBackground(Color.WHITE);

		/// [pageNumLabel]
		/// label displaying the current page number.
		// set up the page number label.
		pageNumLabel = new JLabel();
		pageNumLabel.setFont(textInputFont);
		pageNumLabel.setText("");

		/// [turnPageNext]
		/// buttons that will move forward and back in the number of pages.
		// set up turnPageNext.
		turnPageNext = new JButton(">");
		// set up the background and foreground.
		turnPageNext.setBackground(Color.WHITE);
		turnPageNext.setForeground(Color.BLACK);
		// set up Font.
		turnPageNext.setFont(textInputFont);
		// set up the Border.
		Border pageButtonPadding = BorderFactory.createEmptyBorder(0, 7, 0, 7);
		turnPageNext.setBorder(pageButtonPadding);
		// do not make it focusable.
		turnPageNext.setFocusable(false);
		// will take off the focus box when pressed.
		turnPageNext.setFocusPainted(false);
		// set up for ActionListener.
		turnPageNext.setActionCommand("next");
		turnPageNext.addActionListener(pageButtonHandler);
		// do not make it visible until enough options are obtained.
		turnPageNext.setVisible(false);

		/// [turnPageBack]
		turnPageBack = new JButton("<");
		turnPageBack.setBackground(Color.WHITE);
		turnPageBack.setForeground(Color.BLACK);
		turnPageBack.setFont(textInputFont);
		turnPageBack.setBorder(pageButtonPadding);
		turnPageBack.setFocusable(false);
		turnPageBack.setFocusPainted(false);
		turnPageBack.setActionCommand("back");
		turnPageBack.addActionListener(pageButtonHandler);
		turnPageBack.setVisible(false);

		/// [add pageLabel elements to the pagePanel]
		pagePanel.add(pageNumLabel, BorderLayout.CENTER);
		pagePanel.add(turnPageBack, BorderLayout.WEST);
		pagePanel.add(turnPageNext, BorderLayout.EAST);

		//#endregion

		/* 
		 * [ArrayLists]
		 */
		/// #cleaned [ArrayLists]
		sortedBlobs = new ArrayList<Blob>();
		//#endregion
	}

	void createGridButtons(int rows_, int cols_, int index_) { /// [DONE]
		/* 
		 * this handles the creation of buttons in the grid
		 * as well as the look and feel.
		 */

		// create button array
		gridButtons = new JButton[rows_ * cols_];

		// go through current # of rows and cols to draw buttons.
		int count = 0;
		for (int col = 0; col < cols_; col++) {
			for (int row = 0; row < rows_; row++) {
				int boxMinX = row * (GUI.myProcessingSketch.buttonCanvas.width / rows_);
				int boxMinY = col * (GUI.myProcessingSketch.buttonCanvas.height / cols_);
				System.out.println("row: " + row + " col: " + col);

				// draw the index number in corner.
				if (count > sortedBlobs.size() - 1 - index_) {
					GUI.myProcessingSketch.buttonCanvas.fill(255, 120, 50);
				} else {
					GUI.myProcessingSketch.buttonCanvas.fill(255);
				}
				GUI.myProcessingSketch.buttonCanvas.textAlign(PApplet.CENTER);
				GUI.myProcessingSketch.buttonCanvas.text(count + index_, boxMinX + 10, boxMinY + 15);

				// instantiate button in array.
				gridButtons[count] = new JButton("button" + count);

				// slice image in PGraphics buttonCanvas of what will be button icon.
				PImage pimg = GUI.myProcessingSketch.buttonCanvas.get(boxMinX - 10, boxMinY, (GUI.myProcessingSketch.buttonCanvas.width / rows_) + 10, (GUI.myProcessingSketch.buttonCanvas.height / cols_));
				BufferedImage bufimg = (BufferedImage) pimg.getNative();
				ImageIcon pic = new ImageIcon(bufimg);

				// set up button look and feel.
				gridButtons[count].setBounds(boxMinX, boxMinY, (GUI.myProcessingSketch.buttonCanvas.width / rows_), (GUI.myProcessingSketch.buttonCanvas.height / cols_));
				gridButtons[count].setEnabled(true);
				gridButtons[count].setBorderPainted(true);
				gridButtons[count].setFocusable(false);
				gridButtons[count].setIcon(pic);
				gridButtons[count].addActionListener(gridButtonHandler);
				gridButtons[count].setActionCommand(Integer.toString(count + index_));
				// disabled buttons.
				if (count > sortedBlobs.size() - 1 - index_) {
					gridButtons[count].setDisabledIcon(pic);
					gridButtons[count].setEnabled(false);
				}

				this.add(gridButtons[count]);

				count++;

				if (count >= gridButtons.length) {
					break;
				}
			}
			if (count >= gridButtons.length) {
				break;
			}
		}

	}

	public void addBlobsToGrid() { /// [DONE]
		/* 
			* copy the blobs in the Blobs array to add to the
		 * sortedBlobs array. Clear the Blobs array when finished.
		 * create the appropriate GUI elements and connections.
		 */

		pagePanel.setVisible(true);

		/// #cleaned [copyToSortedBlobs()]
		for (Blob b : Blobs) {
			Blob sortedB = new Blob(b.getX(), b.getY(), b.getAvgG(), b.getAvgG(), b.getAvgB());
			sortedB.setPoints(b.getPoints());
			sortedB.setBorder(b.getMinX(), b.getMaxX(), b.getMinY(), b.getMaxY());
			sortedB.setAvgCols(b.getAvgR(), b.getAvgG(), b.getAvgB());
			GUI.gridPage.sortedBlobs.add(sortedB);
		}
		// clear Blobs
		Blobs.clear();
		System.out.println("sortedBlob Size!: " + GUI.gridPage.sortedBlobs.size());
		//#endregion

		/// #cleaned [determineGridSize()]
		int rows = 0;
		int cols = 0;
		for (int i = 1; i <= maxRowsCols; i++) {
			rows = i;
			cols = i;

			// if sortedBlob array size is greater than current numRows squared, then break
			if (sortedBlobs.size() <= (i * i) && sortedBlobs.size() > (i - 1) * (i - 1)) {
				break;
			}
		}
		//#endregion

		/// #cleaned [addBlobsToGrid()]

		// if more than 36 entries, then show the page label.
		if (sortedBlobs.size() > 36 && !turnPageNext.isVisible()) {
			pageNumLabel.setText("Page " + (pageNum + 1) + " of " + ((int) Math.ceil(sortedBlobs.size() / (maxRowsCols * maxRowsCols)) + 1));
			turnPageNext.setVisible(true);
			turnPageBack.setVisible(true);
		}

		// add another page if it spills over.
		if (sortedBlobs.size() % (maxRowsCols * maxRowsCols) == 1 && turnPageNext.isVisible()) {
			pageNumLabel.setText("Page " + (pageNum + 1) + " of " + ((int) Math.ceil(sortedBlobs.size() / (maxRowsCols * maxRowsCols)) + 1));
			pageNumLabel.setVisible(true);
		}

		int boxWH = 0;
		int index = pageNum * (maxRowsCols * maxRowsCols);
		// draw the blob in the appropriate place in grid.
		for (int col = 0; col < cols; col++) {
			for (int row = 0; row < rows; row++) {

				// finding the location to move blob in box
				int centerX = (row * (GUI.myProcessingSketch.buttonCanvas.width / rows));
				int centerY = (col * (GUI.myProcessingSketch.buttonCanvas.height / cols));
				boxWH = (GUI.myProcessingSketch.buttonCanvas.width / rows);


				// current blob that is being added to grid
				Blob sortedB = sortedBlobs.get(index);
				sortedB.inGrid = true;


				// calculate scale and move factor.
				if (sortedB.getWidth() > boxWH) {
					float scaleFactor = (float) boxWH / (float) sortedB.getWidth();
					float moveFactor = sortedB.minX * scaleFactor;
					sortedB.changeBlobScale(scaleFactor);
					sortedB.changeBlobPosition(moveFactor);
				}
				// scale down blob if height is bigger than box height
				if (sortedB.getHeight() > boxWH) {
					float scaleFactor = (float) boxWH / (float) sortedB.getHeight();
					float moveFactor = sortedB.minX * scaleFactor;
					sortedB.changeBlobScale(scaleFactor);
					sortedB.changeBlobPosition(moveFactor);
				}

				/// move points.
				int changeX = centerX - sortedB.minX;
				int changeY = centerY - sortedB.minY;
				for (PVector pv : sortedB.points) {
					pv.x += changeX;
					pv.y += changeY;
				}
				/// move border.
				sortedB.minX += changeX;
				sortedB.maxX += changeX;
				sortedB.minY += changeY;
				sortedB.maxY += changeY;

				/// center blob to button box.
				float boxWHCenter = boxWH / 2;
				float BlobWidthCenter = sortedB.getWidth() / 2;
				float BlobHeightCenter = sortedB.getHeight() / 2;
				// points.
				for (PVector pv : sortedB.points) {
					pv.x += PApplet.floor(boxWHCenter - BlobWidthCenter);
					pv.y += PApplet.floor(boxWHCenter - BlobHeightCenter);
				}
				// border.
				sortedB.minX += PApplet.floor(boxWHCenter - BlobWidthCenter);
				sortedB.minY += PApplet.floor(boxWHCenter - BlobHeightCenter);
				sortedB.maxX += PApplet.floor(boxWHCenter - BlobWidthCenter);
				sortedB.maxY += PApplet.floor(boxWHCenter - BlobHeightCenter);

				index++;
				if (index >= sortedBlobs.size()) {
					break;
				}
			}
			if (index >= sortedBlobs.size()) {
				break;
			}
		}
		//#endregion

		/// #cleaned [drawOnButtonCanvas()]
		GUI.myProcessingSketch.buttonCanvas.beginDraw();

		// draw the sortedBlobs
		GUI.myProcessingSketch.buttonCanvas.background(0);
		for (Blob b : sortedBlobs) {
			if (b.inGrid == true) {
				b.drawOnButton(GUI.myProcessingSketch, GUI.myProcessingSketch.buttonCanvas);
			}
			b.inGrid = false;
		}

		// draw the grid
		// GUI.myProcessingSketch.buttonCanvas.stroke(255);
		// for (int row = 1; row < rows; row++) {
		// 	for (int col = 1; col < cols; col++) {
		// 		GUI.myProcessingSketch.buttonCanvas.line(col * (GUI.myProcessingSketch.buttonCanvas.height / cols), 0, col * (GUI.myProcessingSketch.buttonCanvas.height / cols), GUI.myProcessingSketch.buttonCanvas.height);
		// 	}
		// 	//@ GUI.myProcessingSketch.buttonCanvas.line(0, (GUI.myProcessingSketch.buttonCanvas.width / rows) * row, GUI.myProcessingSketch.buttonCanvas.width, (GUI.myProcessingSketch.buttonCanvas.width / rows) * row);
		// }

		// GUI.myProcessingSketch.buttonCanvas.endDraw();
		//#endregion

		// remove all previous buttons from panel 
		this.removeAll();

		// redraw updated buttons
		createGridButtons(rows, cols, pageNum * (maxRowsCols * maxRowsCols));

		// revalidate and repaint GUI.
		GUI.processingFrame.revalidate();
		GUI.processingFrame.repaint();

		// OSC: update any listening program of the array size in grid.
		GUI.myProcessingSketch.sendSortedBlobAmount();
	}

	void turnPageRedrawGrid() { /// [DONE]
		/* 
		 * the grid will be redrawn if there is an update to the grid contents.
		 */

		//determineGridSize();
		for (int i = 1; i <= maxRowsCols; i++) {
			rowscols = i;

			// if sortedBlob array size is greater than current numRows squared, then break
			if (sortedBlobs.size() <= (i * i) && sortedBlobs.size() > (i - 1) * (i - 1)) {
				break;
			}
		}

		// add the corresponding blobs to current page, and recalculate their size if too big.
		/// #cleaned [addBlobsToGrid()]
		int boxWH = 0;
		int index = pageNum * (maxRowsCols * maxRowsCols);
		for (int col = 0; col < rowscols; col++) {
			for (int row = 0; row < rowscols; row++) {

				// finding the location to move blob in box
				int centerX = (row * (GUI.myProcessingSketch.buttonCanvas.width / rowscols));
				int centerY = (col * (GUI.myProcessingSketch.buttonCanvas.height / rowscols));
				boxWH = (GUI.myProcessingSketch.buttonCanvas.width / rowscols);

				// current blob that is being added to grid
				Blob sortedB = sortedBlobs.get(index);
				sortedB.inGrid = true;

				// calculate scale and move factor.
				if (sortedB.getWidth() > boxWH) {
					float scaleFactor = (float) boxWH / (float) sortedB.getWidth();
					float moveFactor = sortedB.minX * scaleFactor;
					sortedB.changeBlobScale(scaleFactor);
					sortedB.changeBlobPosition(moveFactor);
				}
				// scale down blob if height is bigger than box height
				if (sortedB.getHeight() > boxWH) {
					float scaleFactor = (float) boxWH / (float) sortedB.getHeight();
					float moveFactor = sortedB.minX * scaleFactor;
					sortedB.changeBlobScale(scaleFactor);
					sortedB.changeBlobPosition(moveFactor);
				}

				// move all the points and border to point top left corner of box.
				/// move points.
				int changeX = centerX - sortedB.minX;
				int changeY = centerY - sortedB.minY;
				for (PVector pv : sortedB.points) {
					pv.x += changeX;
					pv.y += changeY;
				}
				/// move border.
				sortedB.minX += changeX;
				sortedB.maxX += changeX;
				sortedB.minY += changeY;
				sortedB.maxY += changeY;

				/// center blob to button box.
				float boxWHCenter = boxWH / 2;
				float BlobWidthCenter = sortedB.getWidth() / 2;
				float BlobHeightCenter = sortedB.getHeight() / 2;
				// points.
				for (PVector pv : sortedB.points) {
					pv.x += PApplet.floor(boxWHCenter - BlobWidthCenter);
					pv.y += PApplet.floor(boxWHCenter - BlobHeightCenter);
				}
				// border.
				sortedB.minX += PApplet.floor(boxWHCenter - BlobWidthCenter);
				sortedB.minY += PApplet.floor(boxWHCenter - BlobHeightCenter);
				sortedB.maxX += PApplet.floor(boxWHCenter - BlobWidthCenter);
				sortedB.maxY += PApplet.floor(boxWHCenter - BlobHeightCenter);

				index++;
				if (index >= sortedBlobs.size()) {
					break;
				}
			}
			if (index >= sortedBlobs.size()) {
				break;
			}
		}
		//#endregion

		// draw the blobs in their grid position to the buttonCanvas.
		/// #cleaned [drawOnButtonCanvas()]
		GUI.myProcessingSketch.buttonCanvas.beginDraw();

		// draw the sortedBlobs
		GUI.myProcessingSketch.buttonCanvas.background(0);
		for (Blob b : sortedBlobs) {
			if (b.inGrid == true) {
				b.drawOnButton(GUI.myProcessingSketch, GUI.myProcessingSketch.buttonCanvas);
			}
			b.inGrid = false;
		}

		// draw the grid
		GUI.myProcessingSketch.buttonCanvas.stroke(255);
		for (int row = 1; row < rowscols; row++) {
			for (int col = 1; col < rowscols; col++) {
				GUI.myProcessingSketch.buttonCanvas.line(col * (GUI.myProcessingSketch.buttonCanvas.height / rowscols), 0, col * (GUI.myProcessingSketch.buttonCanvas.height / rowscols), GUI.myProcessingSketch.buttonCanvas.height);
			}
			GUI.myProcessingSketch.buttonCanvas.line(0, (GUI.myProcessingSketch.buttonCanvas.width / rowscols) * row, GUI.myProcessingSketch.buttonCanvas.width, (GUI.myProcessingSketch.buttonCanvas.width / rowscols) * row);
		}

		GUI.myProcessingSketch.buttonCanvas.endDraw();
		//#endregion

		// remove all previous buttons from panel.
		this.removeAll();

		// redraw updated buttons. 
		createGridButtons(rowscols, rowscols, pageNum * (maxRowsCols * maxRowsCols));

		// revalidate and repaint.
		GUI.mainGUI.revalidate();
		GUI.mainGUI.repaint();
	}

	public class GridButtonHandler implements ActionListener { /// [DONE]
		public void actionPerformed(ActionEvent event) {

			// get the button in the grid that was clicked.
			String clickedButton = event.getActionCommand();
			int index_ = Integer.parseInt(clickedButton);

			//@ Do Something. Display Style.
			if (!GUI.myProcessingSketch.displayStyle.equals("")) {
				switch (GUI.myProcessingSketch.displayStyle) {

					case "random":
						GUI.myProcessingSketch.blobRandom(index_);
						break;

				}
			} else {
				// there is no display style selected.
				GUI.menu.menuMode(3);
				GUI.notification.setMessage("select a display style first", GUI.notification.warning, false);
				GUI.notification.charIndex = GUI.notification.message.length();
			}

			// if user clicks on a highlighted button, redraw it to default border.
			if (index_ >= (pageNum * 36) && index_ < ((pageNum + 1) * 36)) {
				// get the button number.
				int buttonNum = index_ % (maxRowsCols * maxRowsCols);
				// redraw the default border.
				JButton jb = gridButtons[buttonNum];
				jb.setBorder(defaultBorder);
			}

		}
	}

	public class PageButtonHandler implements ActionListener { /// [DONE]
		public void actionPerformed(ActionEvent event) {

			// get the button that was clicked.
			String clickedButton = event.getActionCommand();

			// determine whether the button clicked was the "next" button or "back" button.
			switch (clickedButton) {
				case "next":

					// if there newest page is completely filled up and the user is on the last page.
					if (sortedBlobs.size() % (maxRowsCols * maxRowsCols) == 0 && (pageNum + 1) == ((int) Math.ceil(sortedBlobs.size() / (maxRowsCols * maxRowsCols)))) {
						return;
					}

					// if the newest page is completely filled up and the user is NOT on the last page.
					if (sortedBlobs.size() % (maxRowsCols * maxRowsCols) == 0) {
						pageNum++;
						pageNumLabel.setText("Page " + (pageNum + 1) + " of " + ((int) Math.ceil(sortedBlobs.size()) / (maxRowsCols * maxRowsCols)));
						turnPageRedrawGrid();
						return;
					}

					// constrain gridPage number to not be lower than 0 or the max amount of possible pages.
					pageNum++;
					if (pageNum > sortedBlobs.size() / (maxRowsCols * maxRowsCols)) {
						pageNum--;
					}

					// redrawing the grid to be in the appropriate place.
					turnPageRedrawGrid();
					// set the newPageLabel with the new page number that the user is currently on.
					pageNumLabel.setText("Page " + (pageNum + 1) + " of " + ((int) Math.ceil(sortedBlobs.size() / (maxRowsCols * maxRowsCols)) + 1));
					break;

				case "back":

					// if there newest page is completely filled up and the user is on the first page.
					if (sortedBlobs.size() % (maxRowsCols * maxRowsCols) == 0 && pageNum == 0) {
						return;
					}

					// if there newest page is completely filled up and the user is NOT on the first page.
					if (sortedBlobs.size() % (maxRowsCols * maxRowsCols) == 0) {
						pageNum--;
						pageNumLabel.setText("Page " + (pageNum + 1) + " of " + ((int) Math.ceil(sortedBlobs.size() / (maxRowsCols * maxRowsCols))));
						turnPageRedrawGrid();
						return;
					}

					// constrain gridPage number to not be lower than 0 or the max amount of possible pages.
					pageNum--;
					if (pageNum < 0) {
						pageNum++;
					}
					// redrawing the grid to be in the appropriate place.
					turnPageRedrawGrid();
					// set the newPageLabel with the new page number that the user is currently on.
					pageNumLabel.setText("Page " + (pageNum + 1) + " of " + ((int) Math.ceil(sortedBlobs.size() / (maxRowsCols * maxRowsCols)) + 1));
					break;
			}

		}

	}
}
