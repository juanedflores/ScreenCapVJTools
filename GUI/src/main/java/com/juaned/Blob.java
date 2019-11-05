package com.juaned;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

class Blob {

	ArrayList<PVector> points;
	ArrayList<PVector> originalPoints;
	int x, y;
	float r, g, b;
	int alpha = 255;
	int minX, minY, maxX, maxY;
	int originalMinX, originalMinY, originalMaxX, originalMaxY;
	float avgR, avgG, avgB;
	public static float dBlobThreshold = 30;
	public static float cBlobThreshold = 160;
	int minOffset = 5;
	boolean inGrid = false;
	int pixelSize = 1;
	float scale = 1;
	boolean showOriginal;

	public Blob(int x_, int y_, float r_, float g_, float b_) {

		// save the point of pixel being added.
		x = x_;
		y = y_;

		// start to add dimensional values.
		minX = x_;
		maxX = x_;
		minY = y_;
		maxY = y_;

		// start to form color values for blob.
		r = r_;
		g = g_;
		b = b_;
		avgR = r_;
		avgG = g_;
		avgB = b_;

		// add the first point to the points ArrayList.
		points = new ArrayList<PVector>();
		points.add(new PVector(x_, y_));
	}

	void showBlobAtPoint(PApplet p, PGraphics pg_, int translateX_, int translateY_) { // DISPLAY: [DONE]
		/*
		 * [DISPLAY STYLE] show all points of a blob on the output canvas.
		 */

		int moveX;
		int moveY;
		if (scale < 1 && showOriginal) {
			// get the center of the original dimensions.
			int originalCenterX = (originalMaxX - originalMinX) / 2 + originalMinX;
			int originalCenterY = (originalMaxY - originalMinY) / 2 + originalMinY;
			moveX = translateX_ - originalCenterX;
			moveY = translateY_ - originalCenterY;

			// translate all pixel points the same translation factor.
			for (PVector pv : originalPoints) {
				pv.x += moveX;
				pv.y += moveY;
			}

			// draw all points (the blob) on the canvas.
			for (PVector pv : originalPoints) {
				pg_.fill(avgR, avgG, avgB, alpha);
				pg_.noStroke();
				pg_.rectMode(PApplet.CENTER);
				pg_.rect(pv.x, pv.y, pixelSize, pixelSize);
			}

			// move all pixel points back.
			for (PVector pv : originalPoints) {
				pv.x += -moveX;
				pv.y += -moveY;
			}
		} else {
			// get the center point of blob.
			int centerX = (maxX - minX) / 2 + minX;
			int centerY = (maxY - minY) / 2 + minY;
			moveX = translateX_ - centerX;
			moveY = translateY_ - centerY;

			// translate all pixel points the same translation factor.
			for (PVector pv : points) {
				pv.x += moveX;
				pv.y += moveY;
			}

			// draw all points (the blob) on the canvas.
			for (PVector pv : points) {
				pg_.fill(avgR, avgG, avgB, alpha);
				pg_.noStroke();
				pg_.rectMode(PApplet.CENTER);
				pg_.rect(pv.x, pv.y, pixelSize, pixelSize);
			}

			// move all pixel points back.
			for (PVector pv : points) {
				pv.x += -moveX;
				pv.y += -moveY;
			}
		}
	}

	void drawOnButton(PApplet p, PGraphics pg_) { /// [DONE] (for button grid)
		/*
		 * show all points of a blob on a PGraphics canvas. 
		 * (for drawing blobs on GUI grid buttons)
		 */

		// draw every point in blob as a small rect.
		for (PVector pv : points) {
			pg_.fill(avgR, avgG, avgB, alpha);
			pg_.noStroke();
			pg_.rectMode(PApplet.CENTER);
			pg_.rect(pv.x, pv.y, 1, 1);
		}

		// draw the boundary of blob.
		pg_.strokeWeight(1);
		pg_.stroke(255, 0, 0);
		pg_.noFill();
		pg_.rectMode(PApplet.CORNERS);
		pg_.rect(minX, minY, maxX, maxY);
	}

	void addToBlob(int x_, int y_, float r_, float g_, float b_) { /// [DONE]
		/* 
		 * add a qualified point to the blob.
		 */

		// add point to points array.
		points.add(new PVector(x_, y_));
		// update the MinXY and MaxXY
		minX = PApplet.min(minX, x_);
		minY = PApplet.min(minY, y_);
		maxX = PApplet.max(maxX, x_);
		maxY = PApplet.max(maxY, y_);
		// add rgbs to get the current avgRGB.
		r += r_;
		g += g_;
		b += b_;
		avgR = r / points.size();
		avgG = g / points.size();
		avgB = b / points.size();
	}

	boolean isNeighbor(int x_, int y_) { /// [DONE]
		/* 
		 * determines if a pixel is close enough to blob.
		 */

		// get the closest XY by clamping the MinXY and MaxXY values.
		float closestX = PApplet.max(PApplet.min(x_, maxX), minX);
		float closestY = PApplet.max(PApplet.min(y_, maxY), minY);
		// calculate the distance between both points.
		float d = distSq(closestX, closestY, x_, y_);

		// if the distance is less than distanceThreshold, add to blob.
		if (d <= dBlobThreshold * dBlobThreshold) {
			return true;
		} else {
			return false;
		}
	}

	boolean isSameColor(float r_, float g_, float b_) { /// [DONE]
		/* 
		 * determines if a blobs average color is close enough to a pixel color
		 * being reviewed.
		 */

		// calculate the distance between pixel color and blob avgColor.
		float dColor = distSq(r_, g_, b_, avgR, avgG, avgB);

		// if the distance is less than the color threshold, return true.
		if (dColor <= cBlobThreshold * cBlobThreshold) {
			return true;
		} else {
			return false;
		}
	}

	void changeBlobScale(float scaleFactor_) { /// [DONE]
		/* 
		 * change the scale of the blob without drawing.
		 */

		// if this is first time modifying the blob. save an original blob copy.
		if (getOriginalPoints() == null) {
			originalPoints = new ArrayList<PVector>();
			for (PVector p : points) {
				// add point to points array.
				originalPoints.add(new PVector(p.x, p.y));
			}
			originalMinX = minX;
			originalMinY = minY;
			originalMaxX = maxX;
			originalMaxY = maxY;
		}

		scale *= scaleFactor_;
		System.out.println("scale is: " + scale);

		if (scale >= 2) {
			pixelSize = 2;
		} else if (scale >= 4) {
			pixelSize = 4;
		} else if (scale >= 6) {
			pixelSize = 6;
		}

		// scale all points.
		for (PVector pv : points) {
			pv.x *= scaleFactor_;
			pv.y *= scaleFactor_;
		}

		// scale all border values.
		minX *= scaleFactor_;
		minY *= scaleFactor_;
		maxX *= scaleFactor_;
		maxY *= scaleFactor_;

	}

	void changeBlobPosition(float moveFactor_) { /// [DONE]
		/* 
		 * change the position of the blob without drawing.
		 */

		// move all points.
		for (PVector pv : points) {
			pv.x += moveFactor_;
			pv.y += moveFactor_;
		}

		// move all border values.
		minX += moveFactor_;
		minY += moveFactor_;
		maxX += moveFactor_;
		maxY += moveFactor_;
	}

	/// #cleaned [SETS]

	void setOpacity(int opacity_) { // EDIT: [DONE]
		/* 
		 * [EDIT] changes the opacity value of the blob.
		 */
		alpha = opacity_;
	}

	void setPoints(ArrayList<PVector> points_) { /// [DONE]
		/* 
		 * set all points of blob to be the new imported ArrayList of points.
		 */
		for (PVector p : points_) {
			points.add(new PVector(p.x, p.y));
		}
	}

	void setBorder(int minX_, int maxX_, int minY_, int maxY_) {
		/* 
		 * set all border values.
		 */
		minX = minX_;
		maxX = maxX_;
		minY = minY_;
		maxY = maxY_;
	}

	void setAvgCols(float avgR_, float avgG_, float avgB_) {
		/* 
		 * set all avgRGB values.
		 */
		avgR = avgR_;
		avgG = avgG_;
		avgB = avgB_;
	}
	//#endregion

	/// #cleaned [GETS]
	float getAvgR() {
		return PApplet.round(avgR);
	}

	float getAvgG() {
		return PApplet.round(avgG);
	}

	float getAvgB() {
		return PApplet.round(avgB);
	}

	int getX() {
		return x;
	}

	int getY() {
		return y;
	}

	int getMinX() {
		return minX;
	}

	int getMaxX() {
		return maxX;
	}

	int getMinY() {
		return minY;
	}

	int getMaxY() {
		return maxY;
	}

	float getArea() {
		float area = (maxX - minX) * (maxY - minY);
		return area;
	}

	int getWidth() {
		return maxX - minX;
	}

	int getHeight() {
		return maxY - minY;
	}

	ArrayList<PVector> getPoints() {
		return points;
	}

	ArrayList<PVector> getOriginalPoints() {
		return originalPoints;
	}
	//#endregion

	/// #cleaned [Calculations]
	public static float distSq(float x1, float y1, float z1, float x2, float y2, float z2) {
		float d = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) + (z2 - z1) * (z2 - z1);
		return d;
	}// end of "float distSq()"

	public static float distSq(float x1, float y1, float x2, float y2) {
		float d = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
		return d;
	}// end of "float distSq()"
		//#endregion
}
