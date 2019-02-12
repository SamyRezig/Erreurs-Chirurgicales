import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Chirurgie {
	private int identifiant;
	private IntervalleTemps datesOperation;
	private Salle salle;
	private Chirurgien chirurgien;
        private boolean urgence;

	public Chirurgie(int id, IntervalleTemps datesOp, Salle salle, Chirurgien chirurgien) {
		this.identifiant = id;
		this.datesOperation = datesOp;
		this.salle = salle;
		this.chirurgien = chirurgien;
        this.urgence = this.salle.estUrgence();
	}

    public boolean estUrgente(){
        return this.urgence;
    }

	public Salle getSalle() {
		return this.salle;
	}

	public void setSalle(Salle s) {
		this.salle = s;
	}

	public Chirurgien getChirurgien() {
		return this.chirurgien;
	}

	public void setChirurgien(Chirurgien ch) {
		this.chirurgien = ch;
	}

	public IntervalleTemps getDatesOperation() {
		return this.datesOperation;
	}

	public Conflit enConflit(Chirurgie secondChg) {

		if (this.estChevauchement(secondChg)) {
			return new Chevauchement(this, secondChg);

		} else if (this.estInterference(secondChg)) {
			return new Interference(this, secondChg);

		} else if (this.estUbiquite(secondChg)) {
			return new Ubiquite(this, secondChg);
		}

		return null;
	}

	public long duree() {
		return this.datesOperation.duree();
	}

	public void reduireFin(long biaisMinutes) {
		this.datesOperation.reduireFin(biaisMinutes);
	}

	public void reduireDebut(long biaisMinutes) {
		this.datesOperation.reduireDebut(biaisMinutes);
	}

	public boolean estChevauchement(Chirurgie second) {
		if (this.datesOperation.intersect(second.datesOperation) && this.salle.equals(second.salle)
				&& this.chirurgien.equals(second.chirurgien)) {
			System.out.println("CHEVAUCHEMENT");
			return true;
		}
		return false;
	}

	public boolean estInterference(Chirurgie second) {
		if (this.datesOperation.intersect(second.datesOperation) && this.salle.equals(second.salle)) {
			System.out.println("INTERFERENCE");
			return true;
		}
		return false;
	}

	public boolean estUbiquite(Chirurgie second) {
		if (this.datesOperation.intersect(second.datesOperation)
				&& this.getChirurgien().equals(second.getChirurgien())) {
			System.out.println("UBIQUITE");
			return true;
		}
		return false;
	}

	public void visualisation() {
		List<LocalDateTime> ref = new ArrayList<>();
		LocalDateTime fin = this.datesOperation.getDateFin();
		LocalDateTime debut = this.datesOperation.getDateDebut();
		LocalDateTime base = debut.minusSeconds(debut.getSecond())
									.minusMinutes(debut.getMinute())
									.minusHours(debut.getHour());
		// Construction des marquages de temps
		for (int i = 0; i < 24 * 4; i++) {
			ref.add(base);
			base = base.plusMinutes(15);
		}

		for (LocalDateTime time : ref) {
			if (time.isAfter(debut) && time.isBefore(fin)) {
				System.out.print("*");

			} else {
				System.out.print("-");
			}
		}
		// Chirurgie sur deux jours successifs
		if (fin.getDayOfYear() != (debut.getDayOfYear())) {
			System.out.print(">>");		// La suite de la chirugie se trouve sur le lendemain
		}
		System.out.println();

	}
	
	public boolean dureeSuspecte() {
		return this.duree() > 134;	// dernier quartile de la grande base de donnees
	}
        
        public boolean heureSuspecte() {
            return this.heureDebutSuspecte() || this.heureFinSuspecte();
        }
	
	public boolean heureDebutSuspecte() {
		List<LocalDateTime> heuresSuspectes = new ArrayList<>();
		for (LocalDateTime heure : heuresSuspectes) {
			if (this.datesOperation.getDateDebut().equals(heure)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean heureFinSuspecte() {
		List<LocalDateTime> heuresSuspectes = new ArrayList<>();
		for (LocalDateTime heure : heuresSuspectes) {
			if (this.datesOperation.getDateFin().equals(heure)) {
				return true;
			}
		}
		return false;
	}
	
	public double tauxSuspectDebut(long chevauchement) {
		long duree = this.datesOperation.duree();
		int heuresSusp = (this.heureDebutSuspecte()) ? 2 : 0;
		double certitude = Math.pow((double)duree / (double)1440, (double)chevauchement + heuresSusp);
                
		return 1 - certitude;
	}
	
	public double tauxSuspectFin(long chevauchement) {
		long duree = this.datesOperation.duree();
		int heuresSusp = (this.heureFinSuspecte()) ? 2 : 0;
		double certitude = Math.pow((double)chevauchement / (double)1440, (double)duree + heuresSusp);
		
		return 1 - certitude;
	}

	
	@Override
	public Chirurgie clone() {
		Chirurgie tmp = this; 
		return tmp;
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
		strb.append("\n");
		return strb.toString();
	}

}
