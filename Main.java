import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.util.Scanner;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.time.LocalTime;

public class Main {

	public static void main(String [] args) throws IOException {
		Agenda a = new Agenda("Chirurgies_v2.csv");

		/*a.getListeChirurgies().stream()
								.filter( x->x.dansJournee() && !x.estUrgente())
								.forEach(System.out::print);*/

        a.resolution();

        // Description statistique
		a.descriptionCourante();

		// Evolution des conflits
		a.stats.afficherNombreCorrections();

		a.creerNouveauFichier();

		// Liste avec les nombres de conflits a chaque iteration
		a.afficherConflitsTotaux();

		// Le nombre de conflits restant par journee
		a.afficherJoursConflit();

		//a.verifierChirurgies();
		a.afficherGraphique(args);

	}

}
