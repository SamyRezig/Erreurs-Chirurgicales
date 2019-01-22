import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IntervalleTemps {
	private LocalDateTime dateDebut;
	private LocalDateTime dateFin;
	
	public IntervalleTemps(String jourDebut, String horaireDebut, String jourFin, String horaireFin) {
		// Concatenation des String pour avoir deux dates de la forme dd/MM/yyyy HH:mm:ss distincts
		String dateDebut = jourDebut + " " + horaireDebut;
		String dateFin = jourFin + " " + horaireFin;
		
		// Definition du formatteur pour convertir les String en LocalDateTime
		DateTimeFormatter formatteur = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		
		// Conversion et setting
		this.dateDebut = LocalDateTime.parse(dateDebut, formatteur);
		this.dateFin = LocalDateTime.parse(dateFin,  formatteur);
	}
	
        public LocalDateTime getDateDebut(){
            return this.dateDebut;
        }
        
        public LocalDateTime getDateFin(){
            return this.dateFin;
        }
        
	@Override
	public String toString() {
		return (this.dateDebut + "--" + this.dateFin);
	}
	
}
