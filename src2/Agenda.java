import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Agenda {
	private List<Chirurgie> listChirurgies;
	private List<Conflit> listConflit;
        
	
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
	
	public int sizeCsv() {
		return this.listChirurgies.size();
	}
	
	public List<Conflit> identifierConflit(){
		
            List<LocalDate> ld = this.listChirurgies.stream()
                                .map( x -> x.getDatesOperation().getDateDebut().toLocalDate())
                                .distinct()
                                .collect(Collectors.toList());
            
		//Creer une liste de chirurgie par jour
		//Verifier l'heure de dÃ©but et de fin 
		//Verifier chirurgien
		//Verifier salle
		//Si chirurgien et salle = true -> new Conflit = new Interference et on ajoute a la liste de conflit
		//Sinon si chirurgien -> new Conflit = new ubiquite et on ajoute a la liste de conflit
		//Sinon si salle -> new Conflit = new Chevauchement et on ajoute a la liste de conflit
		//Sinon on ajoute rien
		//Redefinir la liste au jour suivant
		
		
		return null;
	}
	
        public List<Chirurgie> getChirurgieJournee(LocalDate l){
            List<Chirurgie> chirurgieJournee = new ArrayList<>();
            for(Chirurgie c : this.listChirurgies){
                if(c.getDatesOperation().getDateDebut().toLocalDate().equals(l)){
                    chirurgieJournee.add(c);
                }
            }
            
            return chirurgieJournee;
        }
        
        
        
	public void resoudreConflit(List<Chirurgie> listCh , List<Conflit> listConflit) {
		
	}
	
	
	/**
	 * Ubiquite : (17,18) , (21,22)
	 * Chevauchement : (9,10)
	 * Interference : (9,10,11) , (13,14)
	 * 
	 */
}