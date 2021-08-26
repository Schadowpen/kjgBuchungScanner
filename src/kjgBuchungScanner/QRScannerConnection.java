package kjgBuchungScanner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jssc.*;

/**
 * Connector, der sich mit dem Barcode Scanner verbindet. Es wird angenommen,
 * dass der Barcode Scanner den Inhalt der QR-Codes im Klartext übermittelt und
 * mit einem Line Feed beendet.
 */
public class QRScannerConnection extends Thread {

	/**
	 * Liefert eine Liste mit allen auf dem Gerät verfügbaren Ports. Einer dieser
	 * Ports wird wahrscheinlich der QR-Code Scanner sein.
	 * 
	 * @return Liste mit allen verfügbaren Ports
	 */
	public static String[] getAvailablePorts() {
		return SerialPortList.getPortNames();
	}

	private byte[] buffer = new byte[1024];
	private int tail = 0;
	private ActionListener actionListener = null;
	private SerialPort serialPort;
	private boolean portOpen = true;

	/**
	 * Erzeugt einen Connector auf dem angegebenen Seriellen Port
	 * 
	 * @param portName Name des Ports, mit dem der Connector sich verbinden soll
	 * @throws Exception Wenn die Verbindung nicht hergestellt werden kann
	 */
	public QRScannerConnection(String portName) throws Exception {
		serialPort = new SerialPort(portName);
		serialPort.openPort();
		serialPort.setParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

		this.start();
	}

	/**
	 * Interne run-Methode, die andauernd auf neue Daten abfragt
	 */
	public void run() {
		try {
			while (portOpen) {
				// read Port. If something was scanned, readBytes will return all Bytes.
				byte[] readBytes = serialPort.readBytes();
				// send if something scanned
				if (readBytes != null) {
					String message = new String(readBytes);
					actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, message));
				}
				// wait 10ms when stream is broken and check again
				sleep(10);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setzt den ActionListener, der immer aufgerufen wird, nachdem ein QR-Code
	 * gescannt wurde
	 * 
	 * @param actionListener
	 */
	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	/**
	 * Schließt die Verbindung zu dem QR Code Scanner
	 */
	public void close() {
		portOpen = false;
		try {
			serialPort.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
}
