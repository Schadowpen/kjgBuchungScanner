package kjgBuchungScanner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import swing.JLabelWithLineBreaks;
import swing.ShrinkLayout;

/**
 * Panel mit einer Eingabemaske zum Einloggen.
 */
public class LoginPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -7808952037661327855L;
	private Main main;
	private JTextField usernameField;
	private JTextField passwordField;
	private JLabel loginLabel;
	private JButton loginButton;

	public LoginPanel(Main main) {
		super();
		
		this.main = main;
		this.setLayout(new ShrinkLayout(5));
		this.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		JLabel label1 = new JLabelWithLineBreaks(
				"Bitte melde dich mit den Nutzerdaten an, wie du dich auch auf der Webseite anmelden würdest.");
		label1.setFont(new Font(null, Font.PLAIN, 20));
		label1.setHorizontalTextPosition(JLabel.CENTER);
		label1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		this.add(label1);

		JPanel panel2 = new JPanel();
		GridLayout layout = new GridLayout(0, 2);
		layout.setHgap(10);
		layout.setVgap(10);
		panel2.setLayout(layout);
		this.add(panel2);

		JLabel label2 = new JLabel("Nutzername:");
		label2.setFont(Main.FONT);
		panel2.add(label2);

		usernameField = new JTextField();
		usernameField.setFont(Main.FONT);
		panel2.add(usernameField);

		JLabel label3 = new JLabel("Passwort:");
		label3.setFont(Main.FONT);
		panel2.add(label3);

		passwordField = new JPasswordField();
		passwordField.setFont(Main.FONT);
		panel2.add(passwordField);

		loginLabel = new JLabel();
		loginLabel.setPreferredSize(new Dimension(150, 30));
		loginLabel.setFont(Main.FONT);
		panel2.add(loginLabel);

		loginButton = new JButton("Einloggen");
		loginButton.setFont(Main.FONT);
		loginButton.addActionListener(this);
		loginButton.setPreferredSize(new Dimension(150, 30));
		panel2.add(loginButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Zeige, dass Login läuft
		loginButton.setEnabled(false);
		loginLabel.setForeground(Color.black);
		loginLabel.setText("Login ...");

		// Login in parallelem Thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Versuche Login
				WebConnection webConnection = WebConnection.login(usernameField.getText(), passwordField.getText());

				if (webConnection != null) {
					// login Erfolgreich!
					main.loginSuccessful(webConnection);
				} else {
					// login Fehlgeschlagen
					loginLabel.setForeground(Color.red);
					loginLabel.setText("Login fehlgeschlagen");
					loginButton.setEnabled(true);
				}
			}

		}).start();
	}
}
