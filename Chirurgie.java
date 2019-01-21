import java.util.Date;

public class Chirurgie {
	private int identifiant;
	private IntervalleTemps datesOperation;
	private String salle;
	private Chirurgien chirurgien;
	
	private Chirurgie(int id, IntervalleTemps datesOp, String salle, Chirurgien chirurgien) {
		this.identifiant = id;
		this.datesOperation = datesOp;
		this.salle = salle;
		this.chirurgien = chirurgien;
	}
	
	
}
