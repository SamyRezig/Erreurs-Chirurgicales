import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;

public class Chirurgie implements Comparable<Chirurgie> {
	private int identifiant;
	private IntervalleTemps datesOperation;
	private Salle salle;
	private Chirurgien chirurgien;
    private boolean urgence;
    private boolean corrige;

	public Chirurgie(int id, IntervalleTemps datesOp, Salle salle, Chirurgien chirurgien) {
		this.identifiant = id;
		this.datesOperation = datesOp;
		this.salle = salle;
		this.chirurgien = chirurgien;
        this.urgence = this.salle.estUrgence();
        this.corrige = false;
	}

	public int getId() {
		return this.identifiant;
	}

	public void setCorrige() {
		this.corrige = true;
	}

	public boolean getCorrige() {
		return this.corrige;
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

	public boolean commenceAvant(Chirurgie autre) {
		return this.datesOperation.getDateDebut().isBefore(autre.datesOperation.getDateDebut());
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
			return true;
		}
		return false;
	}

	public boolean estInterference(Chirurgie second) {
		if (this.datesOperation.intersect(second.datesOperation) && this.salle.equals(second.salle)) {
			return true;
		}
		return false;
	}

	public boolean estUbiquite(Chirurgie second) {
		if (this.datesOperation.intersect(second.datesOperation)
				&& this.getChirurgien().equals(second.getChirurgien())) {
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

	public boolean courte() {
		return this.datesOperation.duree() < 61;	// Duree moyenne de la grande BD

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

	public double tauxSuspect(long chevauchement) {
		return 1 - ((double) chevauchement / (double) this.datesOperation.duree());
	}

        public boolean estImbrique(Chirurgie chg){
            if ( (chg.getDatesOperation().getDateDebut().isBefore(this.getDatesOperation().getDateDebut())
				|| chg.getDatesOperation().getDateDebut().equals(this.getDatesOperation().getDateDebut()))

                  && (chg.getDatesOperation().getDateFin().isAfter(this.getDatesOperation().getDateFin())
				  || chg.getDatesOperation().getDateFin().isAfter(this.getDatesOperation().getDateFin())) ) {
                return true;
            }
            return false;
        }

        public long dureeIntersection(Chirurgie chg){
            long dureeInter = 0;
            if (this.estImbrique(chg)){
                return this.duree();
            } else if (chg.estImbrique(this)){
                return chg.duree();
            } else {
                dureeInter = Duration.between(chg.getDatesOperation().getDateDebut(), this.getDatesOperation().getDateFin())
			.toMinutes();
                return dureeInter;
            }
        }

    public boolean incoherente() {
    	if (this.datesOperation.getDateDebut().isAfter(this.datesOperation.getDateFin())) {
    		return true;
    	} else if (this.duree() <= 0) {
    		return true;
    	} else if (this.duree() >= 60 * 5) {
			return true;
		} else if (this.datesOperation.getDateFin().toLocalTime().equals( LocalTime.of(0, 0) )) {
			return true;
		}
    	return false;
    }

	@Override
	public int compareTo(Chirurgie autre) {
		return this.datesOperation.getDateDebut().compareTo(autre.datesOperation.getDateDebut());
	}

	@Override
	public boolean equals(Object o) {
		if (o.getClass().equals(this.getClass()) ) {
			Chirurgie chr = (Chirurgie) o;
			return chr.getId() == this.getId();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getId();
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
