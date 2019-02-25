import java.util.Map;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import java.util.List;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class Graphique extends Application {
    private double largeur = 700;
    private double hauteur = 400;
    public static Map<String, List<Integer>> valeurs;
    private LineChart graphe;
    public static int iterationMax;
    public static int  conflitMax;

    private Group root;

    public int getIterationMax() {
    	return Graphique.iterationMax;
    }
    
    public int getConflitMax() {
    	return Graphique.conflitMax;
    }
    
    public void afficher(String [] args, int nbIteration, int nbConflit) {
    	this.setIterationMax(nbIteration);
    	this.setConflitMax(nbConflit);
        Application.launch();
    }

    public void setIterationMax(int nb) {
    	Graphique.iterationMax = nb;
    }
    
    public void setConflitMax(int nb) {
    	Graphique.conflitMax = nb;
    }
    
    @SuppressWarnings("unchecked")
    private void initialiserAxes(Group root) {
    	
    	
    	
        System.out.println("Initialisation des axes");
        // Definir l'axe x
        NumberAxis xAxe = new NumberAxis(0, this.iterationMax+1, 1);

        xAxe.setLabel("Iterations");

        // Definir l'axe y
        NumberAxis yAxe = new NumberAxis(0, this.conflitMax, 10);
        
        yAxe.setLabel("Nombre de conflits");

        this.graphe = new LineChart(xAxe, yAxe);

        root.getChildren().add(xAxe);
        root.getChildren().add(yAxe);
        root.getChildren().add(this.graphe);
    }

    @SuppressWarnings("unchecked")
    private void tracer(List<Integer> nuagePoints, String nom) {
        System.out.println("Tracer une courbe");
        Series series = new XYChart.Series();
        series.setName(nom);

        for (int i = 0; i < nuagePoints.size(); i++) {
            series.getData().add(new XYChart.Data(i, nuagePoints.get(i)));
        }

        // Ajouter les data au graphique
        this.graphe.getData().add(series);
    }

    public Group getGroup() {
        return this.root;
    }

    public void initGroup() {
        Group root = new Group();

        this.initialiserAxes(root);
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
  
        this.initGroup();

        if (this.graphe != null) {
        	graphe.setPrefHeight(this.hauteur);
        	graphe.setPrefWidth(this.largeur);
            root = this.getGroup();
            System.out.println("Root initialise !");
        } else {
            System.out.println("Impossible");
        }


        Scene sc = new Scene(root, this.largeur, this.hauteur);
        primaryStage.setScene(sc);
        primaryStage.show();
    }
    
    

}
