import java.util.Map;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import java.util.List;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class Graphique extends Application {

    private double largeur = 700;                       // Largeur de la fenetre
    private double hauteur = 400;                       // Hauteur de la fenetre
    // String : le type de conflit + total, Integer : les valeur associees.
    public static Map<String, List<Integer>> valeurs;   // Les donnees a afficher dans le graphique.
    private LineChart graphe;                           // Le graphique
    public static int iterationMax;                     // Determine la taille de l'axe des abscisses.
    public static int  conflitMax;                      // Determine la taille de l'axe des ordonnees.

    private Group root;                                 // Contient tous les elements a ajouter dans la fenetre : le graphe.

    /**
      * Getter pour avoir le nombre la taille de l'axe des abscisses.
      * @return l'entier maximale visible dans le graphe sur l'axe des abscisses.
      */
    public int getIterationMax() {
    	return Graphique.iterationMax;
    }

    /**
      * Getter pour avoir le nombre la taille de l'axe des ordonnees.
      * @return l'entier maximale visible dans le graphe sur l'axe des ordonnes.
      */
    public int getConflitMax() {
    	return Graphique.conflitMax;
    }

    /**
      * Afficher la fenetre contenant le graphque.
      * @param nbIteration valeur maximale visible sur l'axe des abscisses.
      * @param nbConflit valeur maximale visible sur l'axe des ordonnes.
      */
    public void afficher(String [] args, int nbIteration, int nbConflit) {
    	this.setIterationMax(nbIteration); // abscisses
    	this.setConflitMax(nbConflit);     // ordonnees.
        Application.launch();           // Lancement de la fenetre.
    }

    /**
      * Setter pour la taille de l'axe des abscisses.
      * @param nb le nombre maximale visible sur l'axe des abscisses.
      */
    public void setIterationMax(int nb) {
    	Graphique.iterationMax = nb;
    }

    /**
      * Setter pour la taille de l'axe des ordonnees.
      * @param nb le nombre maximale visible sur l'axe desordonnees..
      */
    public void setConflitMax(int nb) {
    	Graphique.conflitMax = nb;
    }

    @SuppressWarnings("unchecked")
    /**
      * Initialisation des axes du graphique et sa creation.
      * @param root affectation du graphique vide creer sur ce groupe.
      */
    private void initialiserAxes(Group root) {
        // Definir l'axe x
        NumberAxis xAxe = new NumberAxis(0, this.iterationMax + 1, 1);

        xAxe.setLabel("Iterations");

        // Definir l'axe y
        NumberAxis yAxe = new NumberAxis(0, this.conflitMax, 10);

        yAxe.setLabel("Nombre de conflits");

        // Definition du graphique.
        this.graphe = new LineChart(xAxe, yAxe);
        root.getChildren().add(xAxe);
        root.getChildren().add(yAxe);
        root.getChildren().add(this.graphe);
    }

    @SuppressWarnings("unchecked")
    /**
      * Tracer une courbe avec le nuage de points donne.
      * @param nom nom de la courbe.
      *@ @param nuagePoints le nuage de point a tracer.
      */
    private void tracer(List<Integer> nuagePoints, String nom) {
        Series series = new XYChart.Series();
        series.setName(nom);    // Titre de la courbe.

        // Ajout des points dans le graphe.
        for (int i = 0; i < nuagePoints.size(); i++) {
            series.getData().add(new XYChart.Data(i, nuagePoints.get(i)));
        }

        // Ajouter les data au graphique au groupe.
        this.graphe.getData().add(series);
    }

    /**
      * Setter pour le groupe.
      * @return le groupe contenant le graphique.
      */
    public Group getGroup() {
        return this.root;
    }

    /**
      * Initialiser le groupe. Mettre en place les axes et les courbes.
      */
    public void initGroup() {
        Group root = new Group();

        // Initialisation des axes
        this.initialiserAxes(root);

        // Tracage des courbes.
        this.tracer(Graphique.valeurs.get("Ubiquite"), "Ubiquite");
        this.tracer(Graphique.valeurs.get("Interference"), "Interference");
        this.tracer(Graphique.valeurs.get("Chevauchement"), "Chevauchement");
        this.tracer(Graphique.valeurs.get("Total"), "Total");

        this.root = root;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Nombre de conflits en fonction du temps");
        Group root = null;

        // Initialiser le groupe avec le graphique charge.
        this.initGroup();

        if (this.graphe != null) {
            // L'initialisation est reussie.
        	graphe.setPrefHeight(this.hauteur);    // Reglage de la hauteur du graphe
        	graphe.setPrefWidth(this.largeur);     // Reglage de la largeur du graphe
            root = this.getGroup();
        } else {
            System.out.println("Impossible d'afficher le graphique.");
        }

        Scene sc = new Scene(root, this.largeur, this.hauteur);
        primaryStage.setScene(sc);
        primaryStage.show();
    }

}
