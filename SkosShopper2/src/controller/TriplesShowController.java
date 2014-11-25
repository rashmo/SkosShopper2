package controller;

import java.net.URL;
import java.util.ResourceBundle;

import model.FusekiModel;
import model.ModelFacade;

import org.apache.commons.lang.StringUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

public class TriplesShowController implements Initializable{
	
	
	public class TripleOwn {
		private final SimpleStringProperty object = new SimpleStringProperty("");
		private final SimpleStringProperty predicate = new SimpleStringProperty("");
		private final SimpleStringProperty subject = new SimpleStringProperty("");
		
		public TripleOwn() { this("", "", ""); }
		public TripleOwn(String o, String p, String s) { setObject(o); setPredicate(p); setSubject(s); }
		
		public String getObject() { return object.get(); }
		public String getPredicate() { return predicate.get(); }
		public String getSubject() { return subject.get(); }
		
		public void setObject(String text) { object.set(text); }
		public void setPredicate(String text) { predicate.set(text); }
		public void setSubject(String text) { subject.set(text); }
	}
	

	@FXML
	private Button loadTriplesFromServerBtn;
	@FXML
	private Label loadTriplesMsg;
	@FXML
	private TableView tableTriples;


	public void initialize(URL fxmlPath, ResourceBundle resources) {
		assert loadTriplesFromServerBtn != null : "fx:id=\"startStopFuseki\" was not injected: check your FXML file";
		assert loadTriplesMsg != null : "fx:id=\"fusekiStatus\" was not injected: check your FXML file";
	}
	
	
	public void loadTriplesFromServer(ActionEvent event)
	{
		boolean serverStarted = FusekiModel.getServerStatus();
		
		ObservableList<TripleOwn> items = tableTriples.getItems();
		
		Model model = ModelFacade.getAllTriples();
		String tString = model.toString();
		StmtIterator stmti = model.listStatements();
		
		while(stmti.hasNext())
		{
			Statement stmt = stmti.nextStatement();
			
			TripleOwn t = new TripleOwn();

			t.setObject(stmt.getObject().toString());
			t.setPredicate(stmt.getPredicate().toString());
			t.setSubject(stmt.getSubject().toString());
			
			items.add(t);
		}
		
		//System.out.println(model.listStatements());
		
//		String tString =  "{:book5 @dc:title \"Harry Potter and the Order of the Phoenix\"; :book5 @dc:creator \"J.K. Rowling\"; 6476c1fd6aa5e98887a51944fd902a2c @vcard:Family \"Rowling\"; 6476c1fd6aa5e98887a51944fd902a2c @vcard:Given \"Joanna\"; :book3 @dc:title \"Harry Potter and the Prisoner Of Azkaban\"; :book3 @dc:creator 6fca26fd7251e21d602215543e95c855; :book1 @dc:title \"Harry Potter and the Philosopher's Stone\"; :book1 @dc:creator \"J.K. Rowling\"; 6fca26fd7251e21d602215543e95c855 @vcard:FN \"J.K. Rowling\"; 6fca26fd7251e21d602215543e95c855 @vcard:N 6476c1fd6aa5e98887a51944fd902a2c; :book6 @dc:title \"Harry Potter and the Half-Blood Prince\"; :book6 @dc:creator \"J.K. Rowling\"; :book4 @dc:title \"Harry Potter and the Goblet of Fire\"; :book2 @dc:title \"Harry Potter and the Chamber of Secrets\"; :book2 @dc:creator 6fca26fd7251e21d602215543e95c855; :book7 @dc:title \"Harry Potter and the Deathly Hallows\"; :book7 @dc:creator \"J.K. Rowling\"}		<ModelCom   {:book5 @dc:title \"Harry Potter and the Order of the Phoenix\"; :book5 @dc:creator \"J.K. Rowling\"; 6476c1fd6aa5e98887a51944fd902a2c @vcard:Family \"Rowling\"; 6476c1fd6aa5e98887a51944fd902a2c @vcard:Given \"Joanna\"; :book3 @dc:title \"Harry Potter and the Prisoner Of Azkaban\"; :book3 @dc:creator 6fca26fd7251e21d602215543e95c855; :book1 @dc:title \"Harry Potter and the Philosopher's Stone\"; :book1 @dc:creator \"J.K. Rowling\"; 6fca26fd7251e21d602215543e95c855 @vcard:FN \"J.K. Rowling\"; 6fca26fd7251e21d602215543e95c855 @vcard:N 6476c1fd6aa5e98887a51944fd902a2c; :book6 @dc:title \"Harry Potter and the Half-Blood Prince\"; :book6 @dc:creator \"J.K. Rowling\"; :book4 @dc:title \"Harry Potter and the Goblet of Fire\"; :book2 @dc:title \"Harry Potter and the Chamber of Secrets\"; :book2 @dc:creator 6fca26fd7251e21d602215543e95c855; :book7 @dc:title \"Harry Potter and the Deathly Hallows\"; :book7 @dc:creator \"J.K. Rowling\"} |  [http://example.org/book/book5, http://purl.org/dc/elements/1.1/title, \"Harry Potter and the Order of the Phoenix\"] [http://example.org/book/book5, http://purl.org/dc/elements/1.1/creator, \"J.K. Rowling\"] [6476c1fd6aa5e98887a51944fd902a2c, http://www.w3.org/2001/vcard-rdf/3.0#Family, \"Rowling\"] [6476c1fd6aa5e98887a51944fd902a2c, http://www.w3.org/2001/vcard-rdf/3.0#Given, \"Joanna\"] [http://example.org/book/book3, http://purl.org/dc/elements/1.1/title, \"Harry Potter and the Prisoner Of Azkaban\"] [http://example.org/book/book3, http://purl.org/dc/elements/1.1/creator, 6fca26fd7251e21d602215543e95c855] [http://example.org/book/book1, http://purl.org/dc/elements/1.1/title, \"Harry Potter and the Philosopher's Stone\"] [http://example.org/book/book1, http://purl.org/dc/elements/1.1/creator, \"J.K. Rowling\"] [6fca26fd7251e21d602215543e95c855, http://www.w3.org/2001/vcard-rdf/3.0#FN, \"J.K. Rowling\"] [6fca26fd7251e21d602215543e95c855, http://www.w3.org/2001/vcard-rdf/3.0#N, 6476c1fd6aa5e98887a51944fd902a2c] [http://example.org/book/book6, http://purl.org/dc/elements/1.1/title, \"Harry Potter and the Half-Blood Prince\"] [http://example.org/book/book6, http://purl.org/dc/elements/1.1/creator, \"J.K. Rowling\"] [http://example.org/book/book4, http://purl.org/dc/elements/1.1/title, \"Harry Potter and the Goblet of Fire\"] [http://example.org/book/book2, http://purl.org/dc/elements/1.1/title, \"Harry Potter and the Chamber of Secrets\"] [http://example.org/book/book2, http://purl.org/dc/elements/1.1/creator, 6fca26fd7251e21d602215543e95c855] [http://example.org/book/book7, http://purl.org/dc/elements/1.1/title, \"Harry Potter and the Deathly Hallows\"] [http://example.org/book/book7, http://purl.org/dc/elements/1.1/creator, \"J.K. Rowling\"]>";
//		int tNum = StringUtils.countMatches(tString, ";");
//		String[] triples = tString.split(";");
//		
//		System.out.println("Triples:");
//		for(int i = 0; i < triples.length; i++) {
//			System.out.println("    "+triples[i]);
//		}
//			
//			
//			
//		System.out.println("Number of Semicolons: "+tNum);
//		System.out.println("Number of Elements in 'triples': "+triples.length);
		
		//System.out.println(tString);
		
//		TripleOwn t1 = new TripleOwn("Tree", "doesHave", "Leafs");
//		TripleOwn t2 = new TripleOwn("Goethe", "isA", "Poet");
//		
//		ObservableList<TripleOwn> items = tableTriples.getItems();
//		System.out.println("itemstostring: "+items.toString());
//		items.add(t1);
//		items.add(t2);
		
		
		if(serverStarted)
		{
			System.out.println("Load Triples from Server");
			loadTriplesMsg.setText("Loading Triples...");
			loadTriplesMsg.setTextFill(Color.web("#BBBB33"));
		} else if(!serverStarted) {
			System.out.println("Start Server first");
			loadTriplesMsg.setText("Please start the Server first!");
			loadTriplesMsg.setTextFill(Color.web("#BB3333"));
		}
		

	}
	
	

}
