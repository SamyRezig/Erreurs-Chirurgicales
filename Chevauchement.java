import java.time.Duration;
import java.util.List;

public class Chevauchement extends Conflit {

	public Chevauchement(Chirurgie first, Chirurgie second) {
		super(first, second);
	}

	@Override
	public void resoudreConflit(List<Chirurgien> lc, List<Salle> ls) {
		System.out.println(this);
            	long dureeChevauchement = Duration.between(this.getPremiereChirurgie().getDatesOperation().getDateFin(),
														this.getSecondeChirurgie().getDatesOperation().getDateDebut()).toMinutes();

                if(lc.size() > 1){
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
                            }
                    }
                    Correcteur.changerChirurgien(this.getSecondeChirurgie(), tmpChirurgien);
                    Correcteur.changerSalle(this.getSecondeChirurgie(), tmpSalle);
                }else{
					System.out.println("Decalage et translation");
                    Correcteur.translater(this.getSecondeChirurgie(), dureeChevauchement + 30);
                }
				System.out.println(this);


	}

}
