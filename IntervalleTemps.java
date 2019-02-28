import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;
import java.util.ArrayList;

public class IntervalleTemps {
	private LocalDateTime dateDebut;
	private LocalDateTime dateFin;
	private String jourDeb;

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

	public IntervalleTemps(LocalDateTime debut, LocalDateTime fin) {
		if (debut.isBefore(fin)) {
			this.dateDebut = debut;
			this.dateFin = fin;
		} else {
			this.dateDebut = fin;
			this.dateFin = debut;
		}
	}

	public LocalDateTime getDateDebut() {
		return this.dateDebut;
	}


	public LocalDateTime getDateFin() {
		return this.dateFin;
	}

	public boolean intersect(IntervalleTemps secondInterv) {
		if (this.dateFin.compareTo(secondInterv.dateDebut) >= 0 && this.dateDebut.compareTo(secondInterv.dateFin) <= 0) {
			return true;

		} else if (secondInterv.dateFin.compareTo(this.dateDebut) >= 0
				&& secondInterv.dateDebut.compareTo(this.dateFin) <= 0) {
			return true;

		} else {
			return false;
		}
	}

	public long duree() {
		return Duration.between(this.dateDebut, this.dateFin).toMinutes();
	}

	public void translater(long biaisMinutes) {
		this.dateDebut = this.dateDebut.plusMinutes(biaisMinutes);
		this.dateFin = this.dateFin.plusMinutes(biaisMinutes);
	}

	public void reduireFin(long biaisMinutes) {
		this.dateFin = this.dateFin.plusMinutes(-biaisMinutes);
	}

	public void reduireDebut(long biaisMinutes) {
		this.dateDebut = this.dateDebut.plusMinutes(biaisMinutes);
	}

	public List<LocalDate> listeLocalDateEntre() {
		LocalDate jourCourant = this.dateDebut.toLocalDate();
		List<LocalDate> listeJours = new ArrayList<>();

		while (this.dateDebut.toLocalDate().minusDays(1).isBefore(jourCourant) && jourCourant.isBefore(this.dateFin.toLocalDate().plusDays(1))) {
			listeJours.add(jourCourant);
			jourCourant = jourCourant.plusDays(1);
		}

		return listeJours;
	}

	public static IntervalleTemps enSemaine(LocalDate jour) {
		LocalDate jourCourant;
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
	public boolean equals(Object o) {
		if (o != null && o.getClass().equals(this.getClass())) {
			IntervalleTemps interv = (IntervalleTemps) o;
			return ( this.dateDebut.equals(interv.dateDebut) && this.dateFin.equals(interv.dateFin) );
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.dateDebut.hashCode() + this.dateFin.hashCode();
	}

	@Override
	public String toString() {
		return (this.dateDebut + "--" + this.dateFin);
	}
}
