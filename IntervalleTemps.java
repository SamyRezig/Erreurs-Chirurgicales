import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;
import java.util.ArrayList;

/**
  * CLasse representant un intervalle de temps entre deux dates avec les horaires
  * precises.
  * @author Samy Rezig
  * @author Yves Tran
  * @see Chirurgie
  */
public class IntervalleTemps {

	private LocalDateTime dateDebut;	// Date et horaire de debut de l'intervalle
	private LocalDateTime dateFin;		// Date et horaire de fin de l'intervalle


	/**
	  * @param jourDebut chaine de caracteres representant la date de debut
	  * @param horaireDebut chaide de carateres representant l'horaire de debut
	  * @param jourFin chaine de caracteres representant la date de fin
	  * @param horaireFin chaide de carateres representant l'horaire de fin
	  */
	public IntervalleTemps(String jourDebut, String horaireDebut, String jourFin, String horaireFin) {
		// Concatenation des String pour avoir deux dates de la forme dd/MM/yyyy
		// HH:mm:ss distincts
		String dateDebut = jourDebut + " " + horaireDebut;
		String dateFin = jourFin + " " + horaireFin;

		// Definition du formatteur pour convertir les String en LocalDateTime
		DateTimeFormatter formatteur = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		// Conversion et setting
		this.dateDebut = LocalDateTime.parse(dateDebut, formatteur);
		this.dateFin = LocalDateTime.parse(dateFin, formatteur);

        // Si la date de début est apres la date de fin, on rajoute une journee a la date de fin
        if (this.dateFin.isBefore(this.dateDebut)) {
            this.dateFin = this.dateFin.plusDays(1);
        }
	}

	/**
	  * @param debut debut de l'intervalle
	  * @param fin fin de l'intervalle
	  */
	public IntervalleTemps(LocalDateTime debut, LocalDateTime fin) {
		// Ordonner les dates.
		if (debut.isBefore(fin)) {
			this.dateDebut = debut;
			this.dateFin = fin;
		} else {
			this.dateDebut = fin;
			this.dateFin = debut;
		}
	}

	/**
	  * Getter pour la date de debut.
	  * @return la date de debut.
	  */
	public LocalDateTime getDateDebut() {
		return this.dateDebut;
	}

	/**
	  * Getter pour la date de fin
	  * @return la date de fin.
	  */
	public LocalDateTime getDateFin() {
		return this.dateFin;
	}

	/**
	  * @return true si l'intervalle donne intersecte l'intervalle courant et
	  * false sinon.
	  */
	public boolean intersect(IntervalleTemps secondInterv) {
		// Cas de figure : ---------- (this)
		//						-----------
		if (this.dateFin.compareTo(secondInterv.dateDebut) >= 0 && this.dateDebut.compareTo(secondInterv.dateDebut) <= 0) {
			return true;

		// Cas de figure : 			-------------- (this)
		//					--------------
		} else if (secondInterv.dateFin.compareTo(this.dateDebut) >= 0
				&& secondInterv.dateDebut.compareTo(this.dateFin) <= 0) {
			return true;

		} else {
			return false;
		}
	}

	/**
	  * @return duree de l'intervalle en minutes.
	  */
	public long duree() {
		return Duration.between(this.dateDebut, this.dateFin).toMinutes();
	}

	/**
	  * Translater l'intervalle.
	  * @param biaisMinutes le nombre de minutes à translater.
	  */
	public void translater(long biaisMinutes) {
		this.dateDebut = this.dateDebut.plusMinutes(biaisMinutes);
		this.dateFin = this.dateFin.plusMinutes(biaisMinutes);
	}

	/**
	  * Reduire la fin de l'intervalle.
	  * @param biaisMinutes nombre de minutes a reduire.
	  */
	public void reduireFin(long biaisMinutes) {
		this.dateFin = this.dateFin.plusMinutes(-biaisMinutes);
	}

	/**
	  * Reduire l'intervalle par le debut.
	  * @param biaisMinutes nombre de nimutes a reduire.
	  */
	public void reduireDebut(long biaisMinutes) {
		this.dateDebut = this.dateDebut.plusMinutes(biaisMinutes);
	}

	/**
	  * @return une liste de jour entre qui commence dans l'intervalle courante.
	  */
	public List<LocalDate> listeLocalDateEntre() {
		LocalDate jourCourant = this.dateDebut.toLocalDate();
		List<LocalDate> listeJours = new ArrayList<>();

		while (this.dateDebut.toLocalDate().minusDays(1).isBefore(jourCourant) && jourCourant.isBefore(this.dateFin.toLocalDate().plusDays(1))) {
			listeJours.add(jourCourant);
			jourCourant = jourCourant.plusDays(1);
		}

		return listeJours;
	}

	/**
	  * @return l'intervalle de temps representant la semaine qui encadre le jour donne.
	  * @param jour le jour dont on veut obtenir la semaine.
	  */
	public static IntervalleTemps enSemaine(LocalDate jour) {
		LocalDate jourCourant;		// Les jours de la semaine.
		LocalDate debutSemaine;
		LocalDate finSemaine;

		// Avoir le debut de la semaine : lundi
		jourCourant = jour;
		while (!jourCourant.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
			jourCourant = jourCourant.minusDays(1);
		}
		debutSemaine = jourCourant;		// lundi

		//Avoir la fin de la semaine : dimanche
		jourCourant = jour;
		while (!jourCourant.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
			jourCourant = jourCourant.plusDays(1);
		}
		finSemaine = jourCourant;		// Dimanche

		return new IntervalleTemps(debutSemaine.atTime(0, 0, 0), finSemaine.atTime(0, 0, 0));
	}

	@Override
	/**
	  * Egalite sur les deux dates et horareis.
	  */
	public boolean equals(Object o) {
		if (o != null && o.getClass().equals(this.getClass())) {
			IntervalleTemps interv = (IntervalleTemps) o;
			return ( this.dateDebut.equals(interv.dateDebut) && this.dateFin.equals(interv.dateFin) );
		} else {
			return false;
		}
	}

	@Override
	/**
	  * @return somme des hashs des deux dates.
	  */
	public int hashCode() {
		return this.dateDebut.hashCode() + this.dateFin.hashCode();
	}

	@Override
	public String toString() {
		return (this.dateDebut + "--" + this.dateFin);
	}
}
