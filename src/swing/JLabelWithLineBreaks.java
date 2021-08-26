package swing;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * A normal JLabel, but it allows line breaks inside
 */
public class JLabelWithLineBreaks extends JLabel {

	private static final long serialVersionUID = -13642456913768573L;
	
	public JLabelWithLineBreaks() {
		super();
	}

	public JLabelWithLineBreaks(String text) {
		super(text);
	}

	public JLabelWithLineBreaks(Icon image) {
		super(image);
	}

	public JLabelWithLineBreaks(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public JLabelWithLineBreaks(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public JLabelWithLineBreaks(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public void setText(String text) {
		super.setText("<html>" + text + "</html>");
	}
	
	/*public String getText() {
		String text = super.getText();
		return text.substring(9, text.length() - 11);
	}*/
}
