import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IntervalleTemps {
	private LocalDateTime dateDebut;
	private LocalDateTime dateFin;

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

	public LocalDateTime getDateDebut() {
		return this.dateDebut;
	}
	
	
	public LocalDateTime getDateFin() {
		return this.dateFin;
	}

	public boolean intersect(IntervalleTemps secondInterv) {
		if (this.dateFin.compareTo(secondInterv.dateDebut) > 0 && this.dateDebut.compareTo(secondInterv.dateFin) < 0) {
			return true;

		} else if (secondInterv.dateFin.compareTo(this.dateDebut) > 0
				&& secondInterv.dateDebut.compareTo(this.dateFin) < 0) {
			return true;

		} else {
			return false;
		}
	}

	public long duree() {
		return Duration.between(this.dateDebut, this.dateFin).toMinutes();
	}

	public void translater(long biaisMinutes) {
		this.dateDebut.plusMinutes(biaisMinutes);
		this.dateFin.plusMinutes(biaisMinutes);
	}

	public void reduireFin(long biaisMinutes) {
		this.dateFin.plusMinutes(-biaisMinutes);
	}

	public void reduireDebut(long biaisMinutes) {
		this.dateDebut.plusMinutes(-biaisMinutes);
	}

	@Override
	public String toString() {
		return (this.dateDebut + "--" + this.dateFin);
	}
}