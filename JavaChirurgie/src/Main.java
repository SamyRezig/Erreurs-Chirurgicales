
public class Main {
	public static void main(String [] args) {
		IntervalleTemps intervalle = new IntervalleTemps("01/01/2019", "08:00:01", "01/01/2019", "11:00:00");
		System.out.println(intervalle);
		
		Agenda a = new Agenda("MiniBase(1).csv");
	}
}
