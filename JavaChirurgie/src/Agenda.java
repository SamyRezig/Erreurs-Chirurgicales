import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Agenda {
	private List<Chirurgie> listChirurgies;
	//List<Conflit>
	
	private Agenda() {
		this.listChirurgies = new ArrayList<>();
	}
	
	public Agenda(String nomFichier) {
		this();
		this.remplirDepuisFichier(nomFichier);
	}
	
	private void remplirDepuisFichier(String nomFichier) {
		BufferedReader fluxTexte = null;
		String ligne;
		Chirurgie operation;
		
		try {
			// Ouverture du flux sur le fichier
			fluxTexte = new BufferedReader(new InputStreamReader(new FileInputStream(nomFichier)));
			// Lecture de la premiere ligne
			fluxTexte.readLine();
			
			// Lecture de la 2e ligne jusqu'a la fin du fichier
			while ((ligne = fluxTexte.readLine()) != null) {
				System.out.println(ligne);
				
				operation = creationChirurgie(ligne.split(";"));
				this.listChirurgies.add(operation);
				
			}
			System.out.println("Fin");
			System.out.println(listChirurgies);
			
			
		} catch (IOException e) {
			System.out.println("Pas de fichier trouve");
		}
	}
	
	public Chirurgie creationChirurgie(String [] infoSeparees) {
		int identifiant = Integer.parseInt(infoSeparees[0]);
		IntervalleTemps datesOperation = new IntervalleTemps(infoSeparees[1], infoSeparees[2], infoSeparees[1], infoSeparees[3]);
		Salle bloc = this.trouverSalle(infoSeparees[4]);
		Chirurgien chirurgien = this.trouverChirurgien(infoSeparees[5]);
		
		return new Chirurgie(identifiant, datesOperation, bloc, chirurgien);
	}
	
	private Salle trouverSalle(String nomSalle) {
		for (Chirurgie operation : this.listChirurgies) {
			if (operation.getSalle().getNom().equals(nomSalle)) {
				return operation.getSalle();
			}
		}
		return new Salle(nomSalle);
	}
	
	private Chirurgien trouverChirurgien(String nomChirurgien) {
		for (Chirurgie operation : this.listChirurgies) {
			if (operation.getChirurgien().getNom().equals(nomChirurgien)) {
				return operation.getChirurgien();
			}
		}
		return new Chirurgien(nomChirurgien);
	}
}
