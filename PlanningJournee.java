import java.util.List;

public class PlanningJournee {
	
	private List<Chirurgie> listChirurgie;
	private List<Salle> listSalle;
	private List<Chirurgien> listChirurgien;
	
	public PlanningJournee(List<Chirurgie> lc,List<Salle> ls, List<Chirurgien> lch) {
		this.listChirurgie=lc;
		this.listSalle=ls;
		this.listChirurgien=lch;
	}
}
