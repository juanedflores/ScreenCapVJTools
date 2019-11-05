package com.juaned;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class MenuEditorKit extends StyledEditorKit {
	/*
	 * [method helps to center text vertically]
	 */
	private static final long serialVersionUID = 1L;

	public ViewFactory getViewFactory() {
		return new StyledViewFactory();
	}

	public static class StyledViewFactory implements ViewFactory {

		public View create(Element elem) {
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					return new LabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					return new ParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new MenuBoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}
			return new LabelView(elem);
		}
	}
}


class MenuBoxView extends BoxView {
	/*
	 * [method helps to center text vertically]
	 */

	public MenuBoxView(Element elem, int axis) {
		super(elem, axis);
	}

	protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
		super.layoutMajorAxis(targetSpan, axis, offsets, spans);
		int textBlockHeight = 0;
		int offset = 0;
		for (int i = 0; i < spans.length; i++) {
			textBlockHeight = spans[i];
		}

		offset = (targetSpan - textBlockHeight) / 5;
		for (int i = 0; i < offsets.length; i++) {
			offsets[i] += offset;

		}
	}
}
