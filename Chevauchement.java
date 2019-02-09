import java.time.Duration;
import java.util.List;
import java.util.Random;

public class Chevauchement extends Conflit {

	public Chevauchement(Chirurgie first, Chirurgie second) {
		super(first, second);
	}

	@Override
	public boolean persiste() {
		return this.getPremiereChirurgie().estChevauchement(this.getSecondeChirurgie());
	}

	@Override
	public void resoudreConflit(List<Chirurgien> lc, List<Salle> ls) {
		if (!this.persiste())   return ;
		
		System.out.println(this);
		System.out.println(ls);
        long dureeChevauchement = Duration.between(this.getPremiereChirurgie().getDatesOperation().getDateFin(),
														this.getSecondeChirurgie().getDatesOperation().getDateDebut()).toMinutes();

        if(lc.size() != 1 && ls.size() != 1){
            Chirurgien tmpChirurgien = null;
            for(Chirurgien c : lc) {
                    if(!this.getPremiereChirurgie().getChirurgien().equals(c)) {
                            tmpChirurgien = c;
                            break;
                    }
            }
	        Salle tmpSalle = null;
	        for(Salle s : ls) {
	            if(!this.getPremiereChirurgie().getSalle().equals(s)) {
	                tmpSalle = s;
					break;
	            }
	        }
			if (tmpSalle != null)	Correcteur.changerSalle(this.getSecondeChirurgie(), tmpSalle);
            if (tmpChirurgien != null)	Correcteur.changerChirurgien(this.getSecondeChirurgie(), tmpChirurgien);

        	} else {
				System.out.println("Decalage et translation");
                Correcteur.translater(this.getSecondeChirurgie(), dureeChevauchement + 30);
            }
			System.out.println(this);

	}

}
