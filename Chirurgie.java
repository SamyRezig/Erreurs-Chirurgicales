import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;

public class Chirurgie implements Comparable<Chirurgie> {
	private int identifiant;					// Identifiant de la chirurgie
	private IntervalleTemps datesOperation;		// Dates de debut et fin de la chirrugie
	private Salle salle;						// La salle ou l'operation a lieu
	private Chirurgien chirurgien;				// Le chirurgien qui opere
    private boolean urgence;					// Pour savoir s'il s'agit d'une urgence

	/**
	  * @param id l'identifiant de la chirurgie
	  * @param datesOp l'intervalle de temps entre la date de debut et de fin
	  * @param salle la salle de la chirurgie
	  * @param chirurgien le chirurgien qui opere
	  */
	public Chirurgie(int id, IntervalleTemps datesOp, Salle salle, Chirurgien chirurgien) {
		this.identifiant = id;
		this.datesOperation = datesOp;
		this.salle = salle;
		this.chirurgien = chirurgien;
        this.urgence = this.salle.estUrgence();
	}

	/**
	  * Getter pour l'identifiant
	  * @return l'identifiant de la chirurgie
	  */
	public int getId() {
		return this.identifiant;
	}

	/**
	  * Getter pour savoir si la chirrugie est urgente
	  * @return true si la chrirugie est urgente et false sinon
	  */
    public boolean estUrgente(){
        return this.urgence;
    }

	/**
	  * Getter pour la salle
	  * @return la salle utilisee
	  */
	public Salle getSalle() {
		return this.salle;
	}

	/**
	  * Seter pour la salle
	  * @param s salle de remplacement
	  */
	public void setSalle(Salle s) {
		this.salle = s;
	}

	/**
	  * Getter pour le chirurgien
	  * @return le chirurgien qui opere
	  */
	public Chirurgien getChirurgien() {
		return this.chirurgien;
	}

	/**
	  * Setter pour le chirurgien
	  * @param ch le chirurgien de remplacement
	  */
	public void setChirurgien(Chirurgien ch) {
		this.chirurgien = ch;
	}

	/**
	  * Getter pour l'intervalle de temps de la chirurgie
	  * @return l'intervalle de temps de la chirurgie
	  */
	public IntervalleTemps getDatesOperation() {
		return this.datesOperation;
	}

	/**
	  * @return un conflit avec la chirurgie donnee s'il y a lieu. Le conflit
	  * ne peut qu'etre une ubiquite, une interference ou un chevauchement.
	  *
	  */
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

	/**
	  * @return la duree de la chirurgie
	  */
	public long duree() {
		return this.datesOperation.duree();
	}

	/**
	  * @return true si la chirurgie courante commence strictement avant celle donnee et false sinon.
	  */
	public boolean commenceAvant(Chirurgie autre) {
		return this.datesOperation.getDateDebut().isBefore(autre.datesOperation.getDateDebut());
	}

	/**
	  * Reduire la duree de la chirurgie par la fin
	  * @param biaisMinutes quantite de minutes a reduire.
	  */
	public void reduireFin(long biaisMinutes) {
		this.datesOperation.reduireFin(biaisMinutes);
	}

	/**
	  * Reduire la duree de la chirurgie par le debut
	  * @param biaisMinutes quantite de minutes a reduire.
	  */
	public void reduireDebut(long biaisMinutes) {
		this.datesOperation.reduireDebut(biaisMinutes);
	}

	/**
	  * @return true si la chirurgie courante donne un chevauchement avec celle
	  * donnee et false sinon.
	  * @param second la chirurgie a tester .
	  */
	public boolean estChevauchement(Chirurgie second) {
		if (this.datesOperation.intersect(second.datesOperation) && this.salle.equals(second.salle)
				&& this.chirurgien.equals(second.chirurgien)) {
			return true;
		}
		return false;
	}

	/**
	  * @return true si la chirurgie courante donne une interference avec celle
	  * donnee et false sinon.
	  * @param second la chirurgie a tester .
	  */
	public boolean estInterference(Chirurgie second) {
		if (this.datesOperation.intersect(second.datesOperation) && this.salle.equals(second.salle)) {
			return true;
		}
		return false;
	}

	/**
	  * @return true si la chirurgie courante donne une ubiquite avec celle
	  * donnee et false sinon.
	  * @param second la chirurgie a tester .
	  */
	public boolean estUbiquite(Chirurgie second) {
		if (this.datesOperation.intersect(second.datesOperation)
				&& this.getChirurgien().equals(second.getChirurgien())) {
			return true;
		}
		return false;
	}

	/**
	  * Visualiser la chirurgie par tranche de 10 mins
	  */
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

	/**
	  * @return true si la chirurgie est plus courte que la moyenne des chirurgie
	  * manuellement renseignee et false sinon.
	  */
	public boolean courte() {
		return this.datesOperation.duree() < 61;	// Duree moyenne de la grande BD 61
	}

	/**
	  * @return true si la duree de la chirurgie est plus longue que le dernier
	  * quartile manuellement renseignee.
	  */
	public boolean dureeSuspecte() {
		return this.duree() > 134;	// dernier quartile de la grande base de donnees 134
	}

	/**
	  * Calculer le taux de suspection de la chirurgie. Il represente un indice
	  * d'irrealisme par rapport a la duree de chevauchement donnee.
	  * Plus le taux est grand, plus on suspecte cette chirurgie d'etre fautive
	  * dans un conflit qui se regle par decoupage.
	  * @return le taux de suspection
	  * @param chevauchement duree du chevauchement
	  */
	public double tauxSuspect(long chevauchement) {
		return 1 - ((double) chevauchement / (double) this.datesOperation.duree());
	}

	/**
	  * @return true si la chirurgie courante est imbrique dans celle donnee et
	  * false sinon.
	  */
    public boolean estImbrique(Chirurgie chg){
        if ( (chg.getDatesOperation().getDateDebut().isBefore(this.getDatesOperation().getDateDebut())
			|| chg.getDatesOperation().getDateDebut().equals(this.getDatesOperation().getDateDebut()))

              && (chg.getDatesOperation().getDateFin().isAfter(this.getDatesOperation().getDateFin())
			  || chg.getDatesOperation().getDateFin().isAfter(this.getDatesOperation().getDateFin())) ) {
            return true;
        }
        return false;
    }

	/**
	  * @return la duree intersectee entre la chirurgie courante et celle
	  * donnee.
	  */
    public long dureeIntersection(Chirurgie chg){
        long dureeInter = 0;

        if (this.estImbrique(chg)){
            return this.duree();

        } else if (chg.estImbrique(this)){
        return chg.duree();

        } else {
            dureeInter = Duration.between(chg.getDatesOperation().getDateDebut()
						, this.getDatesOperation().getDateFin())
						.toMinutes();
            return dureeInter;
        }
    }

	/**
	  * @return true si la chirurgie courante semble peu realiste par sa duree et
	  * false sinon.
	  */
    public boolean incoherente() {
    	if (this.datesOperation.getDateDebut().isAfter(this.datesOperation.getDateFin())) {
    		return true;
    	} else if (this.duree() <= 0) {
    		return true;
    	} else if (this.duree() > 60 * 5) {
			return true;
		} else if (this.datesOperation.getDateFin().toLocalTime().equals( LocalTime.of(0, 0) )) {
			return true;
		}
    	return false;
    }

	/**
	  * @return true si la chirurgie courante a lieu entre 7h et 20h, false sinon.
	  */
	public boolean dansJournee() {
		LocalTime debutJournee = LocalTime.of(6, 59);
		LocalTime finJournee = LocalTime.of(20, 0);
		LocalTime debutOperation = this.getDatesOperation().getDateDebut().toLocalTime();

		return (debutOperation.isAfter(debutJournee) && debutOperation.isBefore(finJournee));
	}

	@Override
	/**
	  * Comaraison par rapport a la date de duebut de la chirurgie.
	  */
	public int compareTo(Chirurgie autre) {
		return this.datesOperation.getDateDebut().compareTo(autre.datesOperation.getDateDebut());
	}

	@Override
	/**
	  * Egalite sur l'identifiant de la chirurgie.
	  */
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
