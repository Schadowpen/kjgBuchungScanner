package kjgBuchungScanner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import javax.net.ssl.HttpsURLConnection;

public class WebConnection {
	
	private static final String LOGIN_URL = "https://kjg-theater.de/login/";
	private static final String RANDOM_PAGE_URL = "https://kjg-theater.de/login/ticketing/";
	private static final String API_ROOT = "https://kjg-theater.de/custom-code/data/";
	/**
	 * Schluessel, der für alle API-Anfragen genutzt werden muss
	 */
	private String apiKey;

	protected WebConnection(String apiKey) {
		this.apiKey = apiKey;
	}


	/**
	 * Versucht einen Login mit Nutzername und Passwort
	 * @param username Nutzername in Simple Wordpress Membership
	 * @param password Passwort in Simple Wordpress Membership
	 * @return Wenn der Login erfolgreich war eine WebConnection, ansonsten null
	 */
	public static WebConnection login(String username, String password) {
		try {
			// Setup Cookies (where Simple Wordpress Membership stores the session)
			CookieManager cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);

			// Login Request
			String urlParameters = "swpm_user_name=" + URLEncoder.encode(username, "UTF-8") + "&swpm_password=" + URLEncoder.encode(password, "UTF-8") + "&swpm-login=Einloggen";
			sendPostRequest(LOGIN_URL, "application/x-www-form-urlencoded", urlParameters);
			
			// Request Page for getting the Api Key
			String page = sendGetRequest(RANDOM_PAGE_URL);
			
			// Extract Api Key from page
			final String beginInJavascript = "apiSchluessel=\"";
			final String endInJavascript = "\"";
			int startIndex = page.indexOf(beginInJavascript);
			if (startIndex == -1)
				throw new Exception("API Key not found in Website response");
			startIndex += beginInJavascript.length();
			int endIndex = page.indexOf(endInJavascript, startIndex);
			if (endIndex == -1)
				throw new Exception("API Key not found in Website response");
			String apiKey = page.substring(startIndex, endIndex);
			
			// Create new WebConnection with this ApiKey
			System.out.println("API Key obtained: " + apiKey);
			return new WebConnection(apiKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public String getVorgang(long vorgangsNummer) throws NoSuchAlgorithmException, IOException {
		return sendGetRequest(API_ROOT + "getVorgang.php?key=" + getKey() + "&nummer=" + vorgangsNummer);
	}
	
	public String setPlatzStatus(String platzStatusJson) throws NoSuchAlgorithmException, IOException {
		return sendPostRequest(API_ROOT + "setPlatzStatus.php?key=" + getKey(), "application/json", platzStatusJson);
	}
	
	
	/**
	 * Generates the key for the API requests.
	 * It shall be passed as an URL Parameter named "key"
	 * @return key for API-Requests
	 * @throws NoSuchAlgorithmException If SHA256 is not supported
	 */
	protected String getKey() throws NoSuchAlgorithmException {
		Instant instant = Instant.now();
		String plainString = apiKey + instant.toString().subSequence(0, 16);
		
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(plainString.getBytes(StandardCharsets.UTF_8));
		return bytesToHex(hash).toLowerCase();
	}
	
	/**
	 * Converts Array of Bytes to its hexadecimal representation
	 * @param bytes
	 * @return Hexadecimal representation as string
	 */
	protected static String bytesToHex(byte[] bytes) {
		final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
	    byte[] hexChars = new byte[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars, StandardCharsets.UTF_8);
	}
	
	
	/**
	 * Sendet einen HTTPS POST mit einem Body
	 * @param url Url der anzufragenden Ressource
	 * @param contentType Typ des zu sendenden Body
	 * @param body Inhalt des POST Body
	 * @return Den Inhalt der Antwort, oder null wenn die Anfrage fehlschlug.
	 * @throws IOException 
	 */
	protected static String sendPostRequest(String url, String contentType, String body) throws IOException {
		System.out.println("Sende POST "+url);
		HttpsURLConnection connection = null;
		try {
			// setup Connection
			URL urlObject = new URL(url);
			connection = (HttpsURLConnection) urlObject.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));
			
			connection.setUseCaches(false);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(body);
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
			
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * Sendet ein HTTPS GET 
	 * @param url Url der anzufragenden Ressource
	 * @return Den Inhalt der Antwort, oder null wenn die Anfrage fehlschlug.
	 * @throws IOException 
	 */
	protected static String sendGetRequest(String url) throws IOException {
		System.out.println("Sende GET "+url);
		HttpsURLConnection connection = null;
		try {
			// setup Connection
			URL urlObject = new URL(url);
			connection = (HttpsURLConnection) urlObject.openConnection();
			connection.setRequestMethod("GET");
			
			connection.setUseCaches(false);
			connection.setDoOutput(true);

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
			
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
