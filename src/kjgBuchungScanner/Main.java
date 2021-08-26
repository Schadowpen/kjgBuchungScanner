package kjgBuchungScanner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import data.PlatzStatus;
import data.QRCodeContent;
import data.Status;
import data.Vorgang;
import swing.JLabelWithLineBreaks;

/**
 * Hauptklasse mit dem Hauptfenster, welches alles verbindet
 */
public class Main implements ActionListener {

	public static final Font FONT = new Font(null, Font.PLAIN, 15);

	/**
	 * Startet das Programm und öffnet das Hauptfenster
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Setze Werte für Swing
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		new Main();
	}

	private Gson gson = new Gson();
	private JFrame frame;
	private JComponent visibleComponent;
	private QRScannerConnection qrScannerConnection;
	private WebConnection webConnection;

	/**
	 * Erzeugt ein neues Main, mit einer Maske zum Einloggen
	 */
	public Main() {
		// Erzeuge Fenster für Login
		frame = new JFrame("kjg Buchung Scanner");
		frame.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {
				if (qrScannerConnection != null)
					qrScannerConnection.close();
				System.exit(0);
			}
			public void windowClosed(WindowEvent arg0) {}
			public void windowActivated(WindowEvent arg0) {}
		});
		frame.setLocationRelativeTo(null);
		frame.setSize(new Dimension(350, 350));

		// Finde Ports
		try {
			String[] ports = QRScannerConnection.getAvailablePorts();
			if (ports.length == 0) {
				visibleComponent = new JLabelWithLineBreaks(
						"Es sind keine COM-Ports auffindbar, an denen der QR Code Scanner angeschlossen sein könnte");
				visibleComponent.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				visibleComponent.setFont(Main.FONT);
				frame.add(visibleComponent);

			} else if (ports.length == 1) {
				try {
					qrScannerConnection = new QRScannerConnection(ports[0]);
					scannerConnectSuccessful(qrScannerConnection);
				} catch (Exception e) {
					showException(e);
				}
			} else {
				visibleComponent = new SerialPortSelectPanel(this, ports);
				frame.add(visibleComponent);
			}
		} catch (Exception ex) {
			this.showException(ex);
		}

		frame.setVisible(true);
	}

	/**
	 * Zeigt eine Exception als visibleComponent
	 * 
	 * @param e
	 */
	public void showException(Exception e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		JTextArea textArea = new JTextArea(errors.toString());
		textArea.setForeground(Color.red);

		if (visibleComponent != null)
			frame.remove(visibleComponent);

		visibleComponent = new JScrollPane(textArea);
		frame.add(visibleComponent);
		frame.setVisible(true);
	}

	/**
	 * Function to call when the QR Code Scanner was found successfully. This
	 * function inits the next step, the online login.
	 * 
	 * @param qrScannerConnection Working connection to QR Code Scanner
	 */
	public void scannerConnectSuccessful(QRScannerConnection qrScannerConnection) {
		this.qrScannerConnection = qrScannerConnection;
		this.qrScannerConnection.setActionListener(this);

		if (visibleComponent != null)
			frame.remove(visibleComponent);
		visibleComponent = new LoginPanel(this);
		frame.add(visibleComponent);

		frame.setVisible(true);
	}

	/**
	 * Function to call when the online login was successful
	 * 
	 * @param webConnection Working connection after successful login
	 */
	public void loginSuccessful(WebConnection webConnection) {
		this.webConnection = webConnection;

		if (visibleComponent != null)
			frame.remove(visibleComponent);
		visibleComponent = new MainPanel();
		frame.add(visibleComponent);

		frame.setVisible(true);
	}

	/**
	 * This Method is triggered from the QRScannerConnection
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		System.out.println("QR CODE GELESEN: " + event.getActionCommand());
		if (visibleComponent instanceof MainPanel) {
			MainPanel mainPanel = (MainPanel) visibleComponent;

			// Verarbeite QR Code
			try {
				// Lese QR Code ein
				QRCodeContent q = null;
				try {
					q = gson.fromJson(event.getActionCommand(), QRCodeContent.class);
				} catch (JsonSyntaxException e) {
					mainPanel.showFailure("Inhalt des QR Code konnte nicht verarbeitet werden");
					return;
				}
				if (q == null)
					throw new Exception("No QR Code Content received");
				final QRCodeContent qrCodeContent = q;

				// Zeige QR Code an
				mainPanel.showWorking();
				mainPanel.displayQRCodeContent(qrCodeContent);

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							// Lade Vorgang und überprüfe auf Korrektheit
							String responseText = webConnection.getVorgang(qrCodeContent.vorgangsNr);
							if (responseText == null) {
								mainPanel.showFailure("Server Anfrage lieferte keine Antwort");
								return;
							}
							if (responseText.startsWith("Error:")) {
								mainPanel.showFailure(responseText);
								return;
							}
							Vorgang vorgang = null;
							try {
								vorgang = gson.fromJson(responseText, Vorgang.class);
							} catch (JsonSyntaxException e) {
								mainPanel.showFailure(
										"Antwort des Servers für getVorgang konnte nicht verarbeitet werden");
								return;
							}
							if (vorgang == null) {
								mainPanel.showFailure("Der Vorgang für die Theaterkarte existiert nicht");
								return;
							}
							if (qrCodeContent.bezahlung != vorgang.bezahlung) {
								mainPanel.showFailure("Auf der Theaterkarte steht als Bezahlung "
										+ qrCodeContent.bezahlung + ", online jedoch " + vorgang.bezahlung);
								return;
							}
							if (qrCodeContent.preis != vorgang.preis) {
								DecimalFormat decimalFormat = new DecimalFormat("#.00");
								mainPanel.showFailure("Auf der Theaterkarte steht ein Kartenpreis von "
										+ decimalFormat.format(qrCodeContent.preis) + "{, online jedoch "
										+ decimalFormat.format(vorgang.preis) + "€");
								return;
							}

							// Markiere Platz als anwesend
							PlatzStatus uploadPlatzStatus = new PlatzStatus(qrCodeContent.date, qrCodeContent.time,
									qrCodeContent.block, qrCodeContent.reihe, qrCodeContent.platz, Status.anwesend,
									qrCodeContent.vorgangsNr);
							String uploadText = gson.toJson(uploadPlatzStatus);
							responseText = webConnection.setPlatzStatus(uploadText);
							if (responseText == null) {
								mainPanel.showFailure("Server Anfrage lieferte keine Antwort");
								return;
							}
							if (responseText.startsWith("Error:")) {
								mainPanel.showFailure(responseText);
								return;
							}
							PlatzStatus downloadPlatzStatus = null;
							try {
								downloadPlatzStatus = gson.fromJson(responseText, PlatzStatus.class);
							} catch (JsonSyntaxException e) {
								mainPanel.showFailure(
										"Antwort des Servers für setPlatzStatus konnte nicht verarbeitet werden");
								return;
							}
							if (!uploadPlatzStatus.equals(downloadPlatzStatus)) {
								mainPanel.showFailure(
										"Der hochgeladene PlatzStatus gleicht nicht dem zurückerhaltenen PlatzStatus");
								return;
							}

							// Zeige Erfolg an
							switch (qrCodeContent.bezahlung) {
							case bezahlt:
								mainPanel.showOk();
								break;
							case offen:
								mainPanel.showPaymentNeeded("Wurde noch nicht bezahlt");
								break;
							case Abendkasse:
								mainPanel.showPaymentNeeded("Zahlt an der Abendkasse");
								break;
							}
						} catch (Exception e) {
							showException(e);
						}
					}
				}).start();
			} catch (Exception ex) {
				mainPanel.showFailure(ex);
			}

		} else {
			// Wenn noch nicht eingeloggt, zeige zumindest dass QR Code gelesen wurde
			try {
				QRCodeContent qrCodeContent = gson.fromJson(event.getActionCommand(), QRCodeContent.class);
				if (qrCodeContent == null)
					throw new Exception("No QR Code Content received");
				JOptionPane.showMessageDialog(frame, "Theaterkarte gescannt für " + qrCodeContent.block + ", "
						+ qrCodeContent.reihe + qrCodeContent.platz);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(frame, "QR Code konnte nicht gelesen werden");
			}
		}
	}
}
