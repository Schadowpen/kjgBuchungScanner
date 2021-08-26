package kjgBuchungScanner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import data.QRCodeContent;
import swing.JLabelWithLineBreaks;

public class MainPanel extends JPanel {

	private static final long serialVersionUID = -8574396404798999594L;
	private JLabelWithLineBreaks bigStatusLabel;
	private JLabel vorstellungLabel;
	private JLabel blockLabel;
	private JLabel reiheLabel;
	private JLabel platzLabel;
	private JLabel preisLabel;
	private JLabel bezahlungLabel;
	private JLabel vorgangsNummerLabel;

	public MainPanel() {
		super();
		this.setLayout(new BorderLayout());

		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(0, 2));
		panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.add(panel1, BorderLayout.NORTH);
		JLabel label;

		label = new JLabel("Vorstellung:");
		label.setFont(Main.FONT);
		panel1.add(label);

		vorstellungLabel = new JLabel();
		vorstellungLabel.setFont(Main.FONT);
		panel1.add(vorstellungLabel);

		label = new JLabel("Block:");
		label.setFont(Main.FONT);
		panel1.add(label);

		blockLabel = new JLabel();
		blockLabel.setFont(Main.FONT);
		panel1.add(blockLabel);

		label = new JLabel("Reihe:");
		label.setFont(Main.FONT);
		panel1.add(label);

		reiheLabel = new JLabel();
		reiheLabel.setFont(Main.FONT);
		panel1.add(reiheLabel);

		label = new JLabel("Platz:");
		label.setFont(Main.FONT);
		panel1.add(label);

		platzLabel = new JLabel();
		platzLabel.setFont(Main.FONT);
		panel1.add(platzLabel);

		label = new JLabel("Preis:");
		label.setFont(Main.FONT);
		panel1.add(label);

		preisLabel = new JLabel();
		preisLabel.setFont(Main.FONT);
		panel1.add(preisLabel);

		label = new JLabel("Bezahlung:");
		label.setFont(Main.FONT);
		panel1.add(label);

		bezahlungLabel = new JLabel();
		bezahlungLabel.setFont(Main.FONT);
		panel1.add(bezahlungLabel);

		label = new JLabel("Vorgangsnummer:");
		label.setFont(Main.FONT);
		panel1.add(label);

		vorgangsNummerLabel = new JLabel();
		vorgangsNummerLabel.setFont(Main.FONT);
		panel1.add(vorgangsNummerLabel);

		bigStatusLabel = new JLabelWithLineBreaks("Warte auf Scan");
		bigStatusLabel.setHorizontalAlignment(JLabel.CENTER);
		bigStatusLabel.setFont(Main.FONT);
		bigStatusLabel.setBackground(Color.white);
		bigStatusLabel.setOpaque(true);
		this.add(bigStatusLabel, BorderLayout.CENTER);
	}

	public void displayQRCodeContent(QRCodeContent qrCodeContent) {
		vorstellungLabel.setText(qrCodeContent.getDateGerman() + ", " + qrCodeContent.time + " Uhr");
		blockLabel.setText(qrCodeContent.block);
		reiheLabel.setText(qrCodeContent.reihe);
		platzLabel.setText(qrCodeContent.platz + "");
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		preisLabel.setText(decimalFormat.format(qrCodeContent.preis) + "€");
		switch (qrCodeContent.bezahlung) {
		case offen:
			bezahlungLabel.setText("offen");
			break;
		case bezahlt:
			bezahlungLabel.setText("bezahlt");
			break;
		case Abendkasse:
			bezahlungLabel.setText("zahlt an der Abendkasse");
			break;
		}
		long vNr = qrCodeContent.vorgangsNr;
		vorgangsNummerLabel.setText(((vNr / 1000000) % 1000) + " " + ((vNr / 1000) % 1000) + " " + (vNr % 1000));
	}

	public void showOk() {
		bigStatusLabel.setText("OK");
		bigStatusLabel.setFont(new Font(null, Font.BOLD, bigStatusLabel.getWidth() / 5));
		bigStatusLabel.setBackground(Color.green);
	}

	public void showPaymentNeeded(String message) {
		bigStatusLabel.setText(message);
		bigStatusLabel.setFont(new Font(null, Font.BOLD, bigStatusLabel.getWidth() / 20));
		bigStatusLabel.setBackground(Color.yellow);
	}

	public void showFailure(Exception e) {
		e.printStackTrace();
		showFailure(e.getMessage());
	}

	public void showFailure(String message) {
		bigStatusLabel.setText("Es ist ein Fehler aufgetreten: <br/>" + message + "");
		bigStatusLabel.setFont(new Font(null, Font.BOLD, bigStatusLabel.getWidth() / 20));
		bigStatusLabel.setBackground(Color.red);
	}

	public void showWorking() {
		bigStatusLabel.setText("Verarbeite ...");
		bigStatusLabel.setFont(Main.FONT);
		bigStatusLabel.setBackground(Color.white);
	}
}
