import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.DayOfWeek;
import java.util.stream.Collectors;

import java.util.Scanner;
import java.time.LocalDate;

public class Chirurgien {
	private String nom;
	private Map<DayOfWeek, Long> frequencesTravail;
	private List<LocalDate> joursTravail;

	public Chirurgien(String nom) {
		this.nom = nom;
		this.frequencesTravail = new HashMap<>();
		this.joursTravail = new ArrayList<>();
	}

	public String getNom() {
		return this.nom;
	}

	public void definirFrequencesTravail(List<Chirurgie> listeChirurgies) {
		Long frequence;

		for (DayOfWeek jour : DayOfWeek.values()) {
			// Compte le nombre de fois que le chirurgien a travaille ce jour-ci
			frequence = listeChirurgies.stream()
										.filter( x -> x.getDatesOperation().getDateDebut().getDayOfWeek().equals(jour) )
										.count();

			this.frequencesTravail.put(jour, frequence);
		}

		/*System.out.println(this + " : " + this.frequencesTravail);
		(new Scanner(System.in)).nextLine();*/
	}

	public void definirJoursTravail(List<Chirurgie> listeChirurgies) {

		// Definition les frequences de travail
		this.definirFrequencesTravail(listeChirurgies);

		// On extrait les chirurgies operees par ce chirurgien
		List<Chirurgie> chirurgiesConcernees = listeChirurgies.stream()
													.filter( x->x.getChirurgien().equals(this))
													.collect(Collectors.toList());
		List<Chirurgie> chirurgiesSemaine = new ArrayList<>();
		LocalDate jour;
		IntervalleTemps semaine = null;
		IntervalleTemps semainePrec = null;

		for (Chirurgie operation : chirurgiesConcernees) {
			jour = operation.getDatesOperation().getDateDebut().toLocalDate();

			// Definition des dates extremes d'une semaine
			semainePrec = semaine;
			semaine = IntervalleTemps.enSemaine(jour);

			// Si on a change de semaine
			if (semainePrec != null && !semaine.equals(semainePrec)) {
				this.etudier(chirurgiesSemaine, semainePrec);
				chirurgiesSemaine.clear();			// Vider la liste pour la prochaine semaine
			}

			// On ajoute la chirurgie quoi qu'il arrive
			chirurgiesSemaine.add(operation);

		}
	}

	private void etudier(List<Chirurgie> chirurgiesSemaine, IntervalleTemps semaine) {
		System.out.println(chirurgiesSemaine);
		LocalDate max;
		List<LocalDate> joursRien = semaine.listeLocalDateEntre();
		List<LocalDate> joursTravail = chirurgiesSemaine.stream()
												.map( x->x.getDatesOperation().getDateDebut().toLocalDate() )
												.distinct()
												.collect( Collectors.toList() );
		// Si le chirurgien a travaille moins de 5 jours
		// mais strictement plus que 1 jour
		while (1 < joursTravail.size() && joursTravail.size() < 5) {
			joursRien.removeAll(joursTravail);

			// Completer par le jour de la semaine les plus frequents
			max = null;
			for (LocalDate jour : joursRien) {
				if (max == null || this.frequencesTravail.get(max.getDayOfWeek()) < this.frequencesTravail.get(jour.getDayOfWeek())) {
					max = jour;
				}
			}
			joursRien.remove(max);
			joursTravail.add(max);

			/*System.out.println(joursTravail + " -- " + joursRien);
			(new Scanner(System.in)).nextLine();*/
		}

		/*System.out.println(joursTravail);
		(new Scanner(System.in)).nextLine();*/

		this.joursTravail.addAll(joursTravail);
	}

	public boolean censeTravailler(LocalDate jour) {
		return this.joursTravail.contains(jour);
	}

	@Override
	public int hashCode() {
		return this.nom.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this.getClass() == o.getClass()) {
			// Egalite par le nom du chirurgien
			return this.nom.equals( ( (Chirurgien) o).nom );
		}
		return false;
	}

	@Override
	public String toString() {
		return this.nom;
	}
}
