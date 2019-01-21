
public class Chirurgie {
	private int identifiant;
	private IntervalleTemps datesOperation;
	private Salle salle;
	private Chirurgien chirurgien;
	
	public Chirurgie(int id, IntervalleTemps datesOp, Salle salle, Chirurgien chirurgien) {
		this.identifiant = id;
		this.datesOperation = datesOp;
		this.salle = salle;
		this.chirurgien = chirurgien;
	}
	
	public Salle getSalle() {
		return this.salle;
	}
	
	public Chirurgien getChirurgien() {
		return this.chirurgien;
	}
	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append(this.identifiant);
		strb.append("|");
		strb.append(datesOperation);
		strb.append("|");
		strb.append(this.salle);
		strb.append("|");
		strb.append(this.chirurgien);
		return strb.toString();
	}
	
}
