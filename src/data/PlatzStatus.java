package data;

public class PlatzStatus {
	public PlatzStatus(String date, String time, String block, String reihe, int platz, Status status,
			long vorgangsNr) {
		super();
		this.date = date;
		this.time = time;
		this.block = block;
		this.reihe = reihe;
		this.platz = platz;
		this.status = status;
		this.vorgangsNr = vorgangsNr;
	}
	public String date;
	public String time;
	public String block;
	public String reihe;
	public int platz;
	public Status status;
	public long vorgangsNr;
	
	@Override
	public boolean equals(Object another) {
		if (this.getClass() != another.getClass())
			return false;
		
		return date.equals(((PlatzStatus)another).date)
				&& time.equals(((PlatzStatus)another).time)
				&& block.equals(((PlatzStatus)another).block)
				&& reihe.equals(((PlatzStatus)another).reihe)
				&& platz == ((PlatzStatus)another).platz
				&& status == ((PlatzStatus)another).status
				&& vorgangsNr == ((PlatzStatus)another).vorgangsNr;
	}
}
