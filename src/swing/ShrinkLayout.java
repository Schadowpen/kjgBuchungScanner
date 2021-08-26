package swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * Ordnet die Container untereinander an. Expandiert sie in X-Richtung, aber in
 * Y-Richtung bleibt die PreferredSize
 */
public class ShrinkLayout implements LayoutManager {
	private int verticalGap = 0;
	private int minWidth = 0, minHeight = 0;
	private int preferredWidth = 0, preferredHeight = 0;
	private boolean sizeUnknown = true;

	public ShrinkLayout(int verticalGap) {
		this.verticalGap = verticalGap;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		// Required by LayoutManager

	}

	@Override
	public void removeLayoutComponent(Component comp) {
		// Required by LayoutManager
	}

	private void setSizes(Container parent) {
		int nComps = parent.getComponentCount();
		Dimension preferredSize = null;
		Dimension minimumSize = null;

		// Reset preferred/minimum width and height.
		preferredWidth = 0;
		preferredHeight = 0;
		minWidth = 0;
		minHeight = 0;

		for (int i = 0; i < nComps; i++) {
			Component c = parent.getComponent(i);
			if (c.isVisible()) {
				preferredSize = c.getPreferredSize();
				minimumSize = c.getMinimumSize();

				preferredWidth = Math.max(preferredSize.width, preferredWidth);
				if (i > 0)
					preferredHeight += verticalGap;
				preferredHeight += preferredSize.height;

				minWidth = Math.max(minimumSize.width, minWidth);
				minHeight += minimumSize.height;
			}
		}
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Dimension dim = new Dimension(0, 0);

		setSizes(parent);

		// Always add the container's insets!
		Insets insets = parent.getInsets();
		dim.width = preferredWidth + insets.left + insets.right;
		dim.height = preferredHeight + insets.top + insets.bottom;

		sizeUnknown = false;

		return dim;
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		Dimension dim = new Dimension(0, 0);

		// Always add the container's insets!
		Insets insets = parent.getInsets();
		dim.width = minWidth + insets.left + insets.right;
		dim.height = minHeight + insets.top + insets.bottom;

		sizeUnknown = false;

		return dim;
	}

	@Override
	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();
		int maxWidth = parent.getWidth() - (insets.left + insets.right);
		int maxHeight = parent.getHeight() - (insets.top + insets.bottom);
		int nComps = parent.getComponentCount();
		
		// Go through the components' sizes, if neither
		// preferredLayoutSize nor minimumLayoutSize has
		// been called.
		if (sizeUnknown) {
			setSizes(parent);
		}

		// calculate start y
		int y = insets.top + (maxHeight - preferredHeight) / 2;

		for (int i = 0; i < nComps; i++) {
			Component c = parent.getComponent(i);
			if (c.isVisible()) {
				Dimension d = c.getPreferredSize();

				// Set the component's size and position.
				c.setBounds(insets.left, y, maxWidth, d.height);
				y += d.height + verticalGap;
			}
		}
	}

}
