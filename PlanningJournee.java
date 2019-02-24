import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

public class PlanningJournee {

	private List<Chirurgie> listeChirurgies;
	private Ressources disponibilites;
	private List<Conflit> listeConflits;
	public static int cpt = 0;

	private List<Integer> nombresUbiquite;

	public PlanningJournee(List<Chirurgie> lc,List<Salle> ls, List<Salle> lsu,  List<Chirurgien> lch) {
		this.listeChirurgies = lc;
		Collections.sort(this.listeChirurgies);		// Trie les chirurgies par date de debut du plus tot au plus tard
		this.disponibilites = new Ressources(lch, ls, lsu);
		this.listeConflits = new ArrayList<>();
		this.nombresUbiquite = new ArrayList<>();
	}

	public List<Chirurgie> getListeChirurgies(){
		return this.listeChirurgies;
	}

	public List<Integer> getNombresUbiquite() {
		return this.nombresUbiquite;
	}

	public List<Conflit> getListeConflits() {
		return this.listeConflits;
	}

	public void setConflits() {
		// Vider la liste de conflit actuelle
		this.listeConflits.clear();

		Conflit nouveauConflit; // Sauvegarde le nouveau conflit, est null s'il y en a pas

		for (int i = 0; i < this.listeChirurgies.size(); i++) {
			for (int j = i + 1; j < this.listeChirurgies.size(); j++) {
				// Creer un nouveau conflit s'il y a lieu ou retourne null, reinitialisation de
				// nouveauConflit
				nouveauConflit = this.listeChirurgies.get(i).enConflit(this.listeChirurgies.get(j));

				if (nouveauConflit != null) {
					this.listeConflits.add(nouveauConflit);
					Statistiques.recenser(nouveauConflit);
				}

			}
		}

	}

	public void montrerConflits() {
		this.listeConflits.stream()
							.forEach(System.out::println);
	}

	public void resoudreConflits() {

		for(Conflit conflitCourant : this.listeConflits) {

                        if (conflitCourant.getPremiereChirurgie().estUrgente()){
							conflitCourant.resoudreConflit(this.disponibilites.getListeChirurgiens(), this.disponibilites.getListeSallesUrgence());

						} else {
							conflitCourant.resoudreConflit(this.disponibilites.getListeChirurgiens(), this.disponibilites.getListeSalles());
                        }
						if (PlanningJournee.cpt++ >= 15) {
							this.disponibilites.trierListes3(this.listeChirurgies);	// On reordonne les listes des salles et des chirurgiens disponibles
						} else if (PlanningJournee.cpt != 23) {
							this.disponibilites.trierListes2(this.listeChirurgies);
						} else {
							this.disponibilites.trierListes(this.listeChirurgies);
						}
				}
	}

	public void visualiserConflits() {
		for (Conflit conflitCourant : this.listeConflits) {
			conflitCourant.visualiser();
		}
	}

	public void visualiser() {
		for (Chirurgie chrg : this.listeChirurgies) {
			System.out.print(chrg + " : ");
			chrg.visualisation();
		}
	}

	public void verifierChirurgies() {
		Set<Chirurgie> enConflit = this.chirurgiesEnConflit();

		for (Chirurgie chrg : this.listeChirurgies) {
			if (chrg.incoherente()) {
				if (enConflit.contains(chrg))	System.out.println("EN CONFLIT : ");
				System.out.println(chrg);
				chrg.visualisation();
			}
		}
	}

	private Set<Chirurgie> chirurgiesEnConflit() {
		Set<Chirurgie> enConflit = new HashSet<>();
		List<Conflit> tousConflits  = this.listeConflits;

		for (Conflit c : tousConflits) {
			enConflit.add(c.getPremiereChirurgie());
			enConflit.add(c.getSecondeChirurgie());
		}

		return enConflit;
	}

	public Chirurgie derniereChirurgie() {
		return this.listeChirurgies.get( this.listeChirurgies.size() - 1 );
	}

	public long dureeTotale() {
		Chirurgie premiere = this.listeChirurgies.get(0);
		Chirurgie derniere = this.derniereChirurgie();

		return Duration.between(premiere.getDatesOperation().getDateDebut(), derniere.getDatesOperation().getDateFin()).toMinutes();
	}

	public boolean travaille(Chirurgien medecin) {
		for (Chirurgie operation : this.listeChirurgies) {
			if (operation.getChirurgien().equals(medecin)) {
				return true;
			}
		}
		return false;
	}

	public boolean occupe(Salle bloc) {
		for (Chirurgie operation : this.listeChirurgies) {
			if (operation.getSalle().equals(bloc)) {
				return true;
			}
		}
		return false;
	}

	public int nbChirurgies() {
		return this.listeChirurgies.size();
	}

	public int nbChirurgiensDispos() {
		return this.disponibilites.nbChirurgiensDispos();
	}

	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();

		strb.append(listeChirurgies.toString());
		strb.append(this.disponibilites.toString());

		return strb.toString();
	}
}
