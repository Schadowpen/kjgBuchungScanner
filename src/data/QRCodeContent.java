package data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QRCodeContent {
	public String date;
	public String time;
	public String block;
	public String reihe;
	public int platz;
	public float preis;
	public Bezahlung bezahlung;
	public long vorgangsNr;
	
	public String getDateGerman() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date d = sdf.parse(date);
			sdf.applyPattern("dd.MM.yyyy");
			return sdf.format(d);
		} catch (ParseException e) {
			// Fallback:
			return date;
		}
	}
}
