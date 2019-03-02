import java.util.List;

/**
  * Classe representant une interference entre deux chirurgies. Cela signifie que
  * les deux chirurgies s'intersectent et partagent la meme salle.
  * @author Samy Rezig
  * @author Yves Tran
  * @see Conflit
  * @see Ubiquite
  * @see Chevauchement
  */
public class Interference extends Conflit {

    /**
      * Appelle le constructeur de la classe Conflit seulement
      * @param first premiere chirurgie en interference
      * @param second seconde chirurgie en interference
      */
    public Interference(Chirurgie first, Chirurgie second) {
	super(first, second);
    }

    @Override
	public boolean persiste() {
		return this.getPremiereChirurgie().estInterference(this.getSecondeChirurgie());
	}

    @Override
    public boolean ressourcesSuffisantes(List<Chirurgien> lc, List<Salle> ls) {
		return (ls.size() >= 1);  // La liste de salles n'est plus censee contenir la salle qu'on veut changer
	}

    @Override
    /**
	  * Cherche la salle de remplcement depuis la liste donne.
	  * @param ls la liste de salles utilisables.
	  */
    public void modifierChirurgie(List<Chirurgien> lc, List<Salle> ls) {
    	Salle tmpSalle = null;
        for (Salle s : ls) {
            if (!this.getPremiereChirurgie().getSalle().equals(s)) {
                tmpSalle = s;
                break;
            }
        }
        if (tmpSalle != null)	Correcteur.changerSalle(this.getSecondeChirurgie(), tmpSalle);
    }

}
