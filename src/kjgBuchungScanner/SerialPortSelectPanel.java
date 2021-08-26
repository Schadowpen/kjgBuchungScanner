package kjgBuchungScanner;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import swing.JLabelWithLineBreaks;
import swing.ShrinkLayout;

/**
 * Panel for selecting serial ports
 */
public class SerialPortSelectPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -6845683359364227963L;
	private Main main;
	private JComboBox<String> portComboBox;
	private JLabel statusLabel;

	public SerialPortSelectPanel(Main main, String[] serialPorts) {
		super();
		this.main = main;
		this.setLayout(new ShrinkLayout(5));
		
		JLabelWithLineBreaks label1 = new JLabelWithLineBreaks("Einer der folgenden Ports könnte der QR Code Scanner sein. Viel Spaß beim Raten!");
		label1.setFont(Main.FONT);
		this.add(label1);
		
		portComboBox = new JComboBox<String>(serialPorts);
		portComboBox.setFont(Main.FONT);
		this.add(portComboBox);
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(0, 2));
		this.add(panel1);
		
		statusLabel = new JLabelWithLineBreaks();
		statusLabel.setFont(Main.FONT);
		statusLabel.setForeground(Color.red);
		panel1.add(statusLabel);
		
		JButton button1 = new JButton("Verbinden");
		button1.setFont(Main.FONT);
		button1.addActionListener(this);
		panel1.add(button1);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String portName = (String) portComboBox.getSelectedItem();
		try {
			QRScannerConnection c = new QRScannerConnection(portName);
			main.scannerConnectSuccessful(c);
		} catch (Exception e1) {
			main.showException(e1);
		}
	}

}
