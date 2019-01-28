
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;




public class Main {
	public static void main(String [] args) {
		IntervalleTemps intervalle = new IntervalleTemps("01/01/2019", "08:00:01", "01/01/2019", "11:00:00");
		System.out.println(intervalle);
		
		Agenda a = new Agenda("MiniBase(1).csv");
                
                /*DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/d");
                String date = "2019/01/01";
                LocalDate localDate = LocalDate.parse(date,df);
                
                
                System.out.println(a.getChirurgieJournee(localDate));
                
                a.identifierConflit();*/
		System.out.println(a.getListConflits());
		System.out.println(a.getChirurgiensDispos());
		
	}
}
