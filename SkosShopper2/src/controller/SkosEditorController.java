package controller;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceRequiredException;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * @author MARDJ-Project
 *
 */

public class SkosEditorController implements Initializable {

	// Define FXML Elements
	@FXML
	private Accordion accordionpane;
	@FXML
	private TitledPane acc_addIndi;
	@FXML
	private TitledPane acc_editLabel;
	@FXML
	private TitledPane acc_addCollection;
	@FXML
	private TitledPane acc_editCollection;
	@FXML
	private Label label_uri;
	@FXML
	private Label label_uri2;
	@FXML
	private ListView<String> listview_classes;
	@FXML
	private ListView<String> listview_indi;
	@FXML
	private ListView<String> listview_objprop;
	@FXML
	private ListView<String> listview_dataprop;
	@FXML
	private Button btn_show;
	@FXML
	private Button btn_addIndividual;
	@FXML
	private TextField txtfield_individiaulname;
	@FXML
	private TextField txtfield_IndiLabel;
	@FXML
	private TextField txtfield_editLabel;
	@FXML
	private Button btn_editLabel;
	@FXML
	private Label selectedIndiLocalname;
	@FXML 
	private Label labelCollectionFromText;
	@FXML 
	private TextField textfieldCollectionName;
	@FXML
	private TextField textfieldCollectionLabelName;
	@FXML
	private ChoiceBox<Individual> choiseBoxCollectionFilter;
	@FXML
	private ListView listviewCollectionChoise;
	@FXML
	private ListView listviewCollectionSelected;
	
	private static final String COLLECTION = "Collection";

	// local j4log logger
	public static final Logger log = Logger
			.getLogger(SkosEditorController.class);
	
	//String constant only to make arkadius happy
	public static final String PREFIXLABEL ="LabelFor";
	
	//Variables for the Ontology Class-Listview
	private ObservableList<String> classes = FXCollections.observableArrayList();;
	private ArrayList<OntClass> liste_classes = new ArrayList<OntClass>();
	
	//Variables for the ListView, Individuals of a Class
	private ObservableList<String> items = FXCollections.observableArrayList();
	private ArrayList<Individual> liste_indi = new ArrayList<Individual>();
	
	private ObservableList<Individual> liste_choicedindis = FXCollections.observableArrayList();
	
	//Variables for the Dropdownmenu, ObjectProperties
//	private ObservableList<String> props = FXCollections.observableArrayList();
//	private ArrayList<String> propNS = new ArrayList<String>();
	
	//Variables for the Dropdownmenu, Individuals
	private ArrayList<String> indiNS = new ArrayList<String>();
	private ObservableList<Individual> indis = FXCollections.observableArrayList();
	
	//In this class used Ontology Model
	private static OntModel model;
	
	//Selected OntClass and Individual
	private OntClass selectedOntClass;
	private Individual selectedIndividual;
	
	//Namespaces of the OntModel
	private String OntClassNS = "";
	private String baseNS = "";
	private String skosNS = "";
	private String skosxlNS = "";
	
	//Localized Resource Bundle
	private ResourceBundle localizedBundle;

	
	public void initialize(URL location, ResourceBundle resources) {
	
		label_uri2.setText("");
		label_uri.setText(OntClassNS);
		
		listview_indi.setCursor(Cursor.HAND);
		listview_classes.setCursor(Cursor.HAND);
		

		listview_indi.setItems(items);
		listview_classes.setItems(classes);
		localizedBundle = resources;
		
		choiseBoxCollectionFilter.setItems(indis);
		listviewCollectionChoise.setItems(liste_choicedindis);
		

	}

	/**
	 * This function recognizes which ontology class the user has selected, displays the URI and
	 * call a function to get all Individual in this ontology class.
	 * 
	 * Also if no valid Class is selected you can't use buttons to add Individuals/Properties or Labels
	 * 
	 * @param e
	 */
	@FXML public void selectOntClass(MouseEvent e){
		if (!listview_classes.getSelectionModel().isEmpty())
		{
			txtfield_IndiLabel.setDisable(false);
			selectedOntClass = model.getOntClass(liste_classes.get(
					listview_classes.getSelectionModel().getSelectedIndex())
					.getURI());
			
				OntClassNS = liste_classes.get(
						listview_classes.getSelectionModel().getSelectedIndex())
						.getURI();
				label_uri.setText(OntClassNS);
				
				showIndividualsOfOntClass(selectedOntClass);
			switch(selectedOntClass.getLocalName()){
				case "Concept":
					acc_addIndi.setExpanded(true);
					break;
				case "Label":
					acc_editLabel.setExpanded(true);
					txtfield_IndiLabel.setDisable(true);
					btn_editLabel.setText(localizedBundle.getString("btnEditLabel"));
					break;
				case "OrderedCollection":
				case "Collection":
					acc_addCollection.setExpanded(true);
					break;
				default:
					break;
			}
				
			
		}
	}
	
	
	/**
	 * 
	 * This Method loads an Ontology which this Controller works with. Also it gets the Namespaces, Objectproperties and all Individuals of 
	 * the Ontology Model for the TreeView and Choiceboxes. 
	 * 
	 */
	public void loadOntology() {
//		if (model.isEmpty()) {
			try {
				// Path input =
				// Paths.get("C:\\Users\\VRage\\Documents\\SpiderOak Hive\\studium\\5_Semester\\projekt\\",
				// "test1.rdf");
				//
				// model.read(input.toUri().toString(), "RDF/XML");
//				model = ModelFacadeTEST.getOntModel();
				//model.read("file:///C:/Users/rd/git/SkosShopper2/ServerConnector/fuseki/Data/test.rdf");
//				 model = TripleModel.getAllTriples();
//				 Model m = FusekiModel.getDatasetAccessor().getModel();
//				 model.add(ModelFacadeTEST.getOntModel());

//				model = ModelFacadeTEST.getAktModel();
				
				baseNS = model.getNsPrefixURI("");
				log.info("Base NS set to: " + baseNS);
				skosNS = model.getNsPrefixURI("skos");
				log.info("Skos NS set to: " + skosNS);
				skosxlNS = model.getNsPrefixURI("skos-xl");
				log.info("Skosxl NS set to: " + skosxlNS);


//				showOPropertiesInChoicebox();
//				showAllIndividualsInChoicebox();
				showOntClassInTree();
				showAllIndividualsInChoicebox();
				log.info("Ontologie loaded");
			} catch (Exception e) {
				log.error(e, e);
			}
//		}
	}

	
	/**
	 * 
	 * All skos and skosxl OntologyClasses will be shown in the TreeView by this method
	 * 
	 */
	public void showOntClassInTree() {
		classes.clear();
		liste_classes.clear();
		selectedOntClass = null;
		
		ExtendedIterator oclasslist = model.listClasses();
		while (oclasslist.hasNext()) {

				OntClass oclass = (OntClass) oclasslist.next();
				if(oclass.toString().contains(skosNS)||oclass.toString().contains(skosxlNS)){
					liste_classes.add(oclass);
					classes.add(oclass.getLocalName());
				}
			
		}
		log.info("class added to List");

	}

	/**
	 * 
	 * Method which will be executed after clicked on button btn_addIndi.
	 * Catches the User input and creates an Individual with the base namespace in the current selected Ontology Class
	 * 
	 * @param event
	 */
	@FXML
	private void addIndi(ActionEvent event) {
		if (!txtfield_individiaulname.getText().isEmpty()) {
			log.info("start method addIndi");
			String antwort = txtfield_individiaulname.getText();
			if(model.getIndividual(baseNS + (antwort)) == null){
				model.createIndividual(baseNS + (antwort), selectedOntClass);
				
				if(!txtfield_IndiLabel.getText().isEmpty()){
					createLabelRecipe("", txtfield_IndiLabel.getText(), model.getIndividual(baseNS + (antwort)));
				}
				int length = baseNS.length();
				String indinamespace = model.getIndividual(
						baseNS + (antwort)).getNameSpace();
				
				/*
				 * Very complicated string operations, probably PhD needed to understand.
				 * 
				 * Recipe to add multiple Individuals with one string input. Furthermore adding "has narrower" to the created Individuals.
				 * 
				 */
				if (!(length == indinamespace.length())) {
					String superindi = indinamespace.substring(length,
							indinamespace.length() - 1);
					log.info(superindi);
					String[] stringarray = superindi.split("/");
					String newindi = baseNS;
					for (String ss : stringarray) {
						Individual tempindi = model.getIndividual(newindi + ss);
						if (tempindi == null) {
							model.createIndividual(newindi + ss, selectedOntClass);
							newindi = newindi + ss + "/";
							log.info("new individual added: " + ss);
	
						} else {
							newindi = tempindi.getURI() + "/";
						}
						log.info(ss);
					}
					newindi = baseNS;
					if (stringarray.length > 0) {
						for (int i = 0; i < stringarray.length; i++) {
							Individual tempindi = model.getIndividual(newindi
									+ stringarray[i]);
							ObjectProperty oProp = model.getObjectProperty(skosNS
									+ "narrower");
							if (i < stringarray.length - 1) {
								Individual nextindi = model
										.getIndividual(newindi + stringarray[i]
												+ "/" + stringarray[i + 1]);
								model.add(tempindi, oProp, nextindi);
							} else {
								Individual nextindi = model.getIndividual(baseNS
										+ ((String) antwort));
								model.add(tempindi, oProp, nextindi);
							}
							newindi = newindi + stringarray[i] + "/";
						}
					}
				}
				showAllIndividualsInChoicebox();
				showIndividualsOfOntClass(selectedOntClass);
				
				txtfield_IndiLabel.clear();
				txtfield_individiaulname.clear();
			}else{
				log.info("Individual already exist");
			}
		}
	}

	
	/**
	 * 
	 * Shows all Individuals of selected Ontology Class in a Listview
	 * 
	 * @param oclass
	 */
	public void showIndividualsOfOntClass(OntClass oclass) {
		
		if(oclass != null){			
			items.clear();
			liste_indi.clear();
			selectedIndividual = null;
			
			ExtendedIterator<? extends OntResource> indilist = oclass.listInstances();
			while (indilist.hasNext()) {
				Individual indivi = (Individual) indilist.next();
				liste_indi.add(indivi);
				items.add(indivi.getLocalName());
	
			}
		}else{
			items.clear();
			liste_indi.clear();
			selectedIndividual = null;
		}

	}

	/**
	 * 
	 * Debug Method, saves Ontology into a File (fuseki/Data/outputfromProgramm.owl)
	 * Will be deleted in deployed version.
	 * 
	 * @param event
	 */
	@FXML
	private void printToConsole(ActionEvent event) {
		try {
			File file = new File("./fuseki/Data/outputfromProgramm.owl");
			FileOutputStream out = new FileOutputStream(file);
			model.write(out, "RDF/XML");
			log.info("label hinzufügen");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	
	/**
	 * 
	 * Adds Label to the selected Individual. Executed after clicking the button btn_addLabel.
	 * 
	 * Recipe: 	Creates Individual in Label-Class and adding DataProperty "literalForm" with the Userinput as literal.
	 * 			Creates Object property "preferred Label" between created Label individual and selected individual.
	 * 			
	 * 
	 * @param event
	 */
	@FXML
	private void addLabel(ActionEvent event) {
		if ((selectedOntClass.getLocalName().equals("Concept"))
				&& (selectedIndividual != null)) {

			Object antwort = JOptionPane.showInputDialog(null,
					localizedBundle.getString("EnterLabel"), localizedBundle.getString("InputWindow"),
					JOptionPane.INFORMATION_MESSAGE, null, null, null);
			if (antwort != null) {
				String name = (String) antwort;
				model.getOntClass(skosxlNS + "Label").createIndividual(
						baseNS + "LabelFor" + name);
				Individual indi = model.getIndividual(baseNS + "LabelFor"
						+ name);
				DatatypeProperty dprop = model.getDatatypeProperty(skosxlNS
						+ "literalForm");
				log.info("datatypeProp" + dprop.getLocalName());
				indi.addProperty(dprop, model.createLiteral(name, "de"));
				ObjectProperty Oprop = model.getObjectProperty(skosxlNS
						+ "prefLabel");
				model.add(selectedIndividual, Oprop, indi);
			}
		}

	}

	/**
	 * 
	 * Method started from MouseClick on Individual of a Ontology Class ListView.
	 * Get the clicked Individual set it to the selectedIndividual and displays object and data property.
	 * Double click gives you the possibility to delete a Individual.
	 * 
	 * @param event
	 */
	@FXML
	private void selectIndividualOfOntClass(MouseEvent event) {
		if (!listview_indi.getSelectionModel().isEmpty()) {
			selectedIndividual = model.getIndividual(liste_indi.get(
					listview_indi.getSelectionModel().getSelectedIndex())
					.getURI());
			label_uri2.setText(liste_indi.get(
					listview_indi.getSelectionModel().getSelectedIndex())
					.getURI());

			showObjectProperties(selectedIndividual);
			showDataProperties(selectedIndividual);
			liste_choicedindis.add(selectedIndividual );
			txtfield_individiaulname.setText(selectedIndividual.getURI().substring(baseNS.length())+"/");
			if(selectedOntClass.getLocalName().contains("Label")){
				selectedIndiLocalname.setText(selectedIndividual.getLocalName());
				txtfield_editLabel.setText(getDatapropertyFromLabel(selectedIndividual).getString());
			}else if(selectedOntClass.getLocalName().contains("Concept")){
				btn_editLabel.setText(localizedBundle.getString("btnAddLabel"));
				if(getIndividualbyObjectProperty(selectedIndividual,"prefLabel")!=null){
					btn_editLabel.setText(localizedBundle.getString("btnEditLabel"));
					selectedIndiLocalname.setText(getIndividualbyObjectProperty(selectedIndividual,"prefLabel").getLocalName());
					if(getDatapropertyFromLabel(getIndividualbyObjectProperty(selectedIndividual,"prefLabel"))!=null){
						txtfield_editLabel.setText(getDatapropertyFromLabel(getIndividualbyObjectProperty(selectedIndividual,"prefLabel")).getString());
					}
				}else{
					selectedIndiLocalname.setText("");
					txtfield_editLabel.setText("");
				}
			}
			if (event.getClickCount() == 2) {
				String selected = model.getIndividual(
						liste_indi.get(
								listview_indi.getSelectionModel()
										.getSelectedIndex()).getURI())
						.getLocalName();
				int delete = JOptionPane.showConfirmDialog(null,
						localizedBundle.getString("delIndi")  + selected,
						localizedBundle.getString("delIndiWindow"), JOptionPane.YES_NO_OPTION);
				if (delete == JOptionPane.YES_OPTION) {
					model.getIndividual(
							liste_indi.get(
									listview_indi.getSelectionModel()
											.getSelectedIndex()).getURI())
							.remove();
					showIndividualsOfOntClass(selectedOntClass);
					showObjectProperties(selectedIndividual);
					showDataProperties(selectedIndividual);
				}
			}
		}
	}

	/**
	 * 
	 * Method of Button btn_addprop.
	 * Set a object property between selected Individual and selected individual of a choicebox.
	 * 
	 * @param event
	 */
	@FXML
	private void showSubIndividualinListView(MouseEvent event) {
		

	}

	/**
	 * 
	 * Show all Object properties in a choicebox from loaded Ontology
	 * 
	 */
//	private void showOPropertiesInChoicebox() {
//		ExtendedIterator list_prop = model.listObjectProperties();
//		while (list_prop.hasNext()) {
//			ObjectProperty prop = (ObjectProperty) list_prop.next();
//			props.add(prop.getLabel("en"));
//			propNS.add(prop.getURI());
//			log.info("property added: " + prop.getLocalName());
//		}
//	}

	private void showAllIndividualsInChoicebox() {
		indis.clear();

		ExtendedIterator list_indis = model.listIndividuals();
		while (list_indis.hasNext()) {
			Individual indi = (Individual) list_indis.next();
			indis.add(indi);
			log.info("Individual added: " + indi.getLocalName());
			
		}
	}

	/**
	 * 
	 * Displays all object properties of the selected Individual in a Listview.
	 * 
	 * @param selectedIndividual
	 */
	private void showObjectProperties(Individual selectedIndividual) {
		// Property Window ID: listview_dataprop
		listview_dataprop.setItems(null);
		if (selectedIndividual != null) {
			StmtIterator iterProperties = selectedIndividual.listProperties();
			ObservableList<String> items = FXCollections.observableArrayList();
			String predicate = "";
			String object = "";
			while (iterProperties.hasNext()) {
				Statement nextProperty = iterProperties.next();
				if (nextProperty.getPredicate().getNameSpace().equals(skosNS)
						|| nextProperty.getPredicate().getNameSpace()
								.equals(skosxlNS)) {
					try {

						if (nextProperty.getObject().isResource()) {
							predicate = model.getObjectProperty(
									nextProperty.getPredicate().getURI())
									.getLabel("en");
							object = nextProperty.getObject().asResource()
									.getLocalName();
							items.add("'" + predicate + "'" + "  " + object
									+ "\n\n");
						}

					} catch (ResourceRequiredException e) {
						log.error(e, e);
					}
				}
				if (!items.isEmpty()) {
					listview_dataprop.setItems(items);
				}
				// System.out.println(nextProperty);
				// System.out.println(nextProperty.getPredicate().getLocalName());
				// System.out.println(nextProperty.getObject().asResource().getLocalName());
				// System.out.println(skosNS);
			}
		}
	}

	/**
	 * 
	 * Displays all data properties of the selected Individual in a Listview.
	 * 
	 * @param selectedIndividual
	 */
	private void showDataProperties(Individual selectedIndividual) {
		// Property Window ID: listview_objprop
		listview_objprop.setItems(null);
		if (selectedIndividual != null) {
			StmtIterator iterProperties = selectedIndividual.listProperties();
			ObservableList<String> items = FXCollections.observableArrayList();
			String predicate = "";
			String object = "";
			while (iterProperties.hasNext()) {
				Statement nextProperty = iterProperties.next();
				if (nextProperty.getPredicate().getNameSpace().equals(skosNS)
						|| nextProperty.getPredicate().getNameSpace()
								.equals(skosxlNS)) {
					try {

						if (nextProperty.getObject().isLiteral()) {
							predicate = nextProperty.getPredicate()
									.getLocalName();
							object = nextProperty.getObject().asLiteral().toString();
							items.add("'" + predicate + "'" + " " + object + "\n\n");
						}
					}
						catch (ResourceRequiredException e){
							log.error(e, e);
						}
					}
					if(!items.isEmpty()){
						listview_objprop.setItems(items);
					}
				}
			}
		}
	/** Tries to create a new Collection with the given name as long as the
	 * 	name doesn't already exist.
	 */
	@FXML public void createCollection(){

		String collectionString = "/" + COLLECTION + "/";
		String collectionNameString = baseNS 
				+ collectionString 
				+ textfieldCollectionName.getText();
		String collectionLabelString = baseNS 
				+ "LabelForCollection" 
				+ textfieldCollectionName.getText();
		log.info(selectedOntClass == null);
		if(model.getIndividual(collectionNameString) == null 
				&& selectedOntClass != null){
			
			//Name must not be empty! 
			if(!textfieldCollectionName.getText().equals("")){
				//Collection must be seleted!
				if(selectedOntClass.getLocalName().equals(COLLECTION)){
					log.info("Collection selected = true");
					// Add Individual
					model.createIndividual(collectionNameString, selectedOntClass);
					
					//optional Label
					if(!textfieldCollectionLabelName.getText().isEmpty()){
						model.getOntClass(skosxlNS + "Label").createIndividual(
								collectionLabelString);
					}
					else{
						log.info("No Label given");
					}	
				}
				//not implemented yet!
				else if (selectedOntClass.getLocalName().equals("OrderedCollection")){
					log.info("Ordered Collection selected = true \n not implemented yet!");
				}
				else{
					log.error("Neither Collection nor Ordered Collection selected!");
				}// end if-else case collection or ordered collection selected
			}
			else{
				log.error("Namefield Empty!");
			}// end if-case textfield empty check
		}
		else{
			log.error("Name for Collection already taken or No Ontclass selected!");
		} // end if-else-case collection name already exist
	}

	@FXML public void insertToCollection(){
		
	}
	@FXML public void deleteFromCollection(){
		
	} 

	public void createLabelRecipe(String name, String description, Individual toindividual){
		String labelname = toindividual.getLocalName();
		model.getOntClass(skosxlNS + "Label").createIndividual(
				baseNS + PREFIXLABEL+name +labelname);
		Individual indi = model.getIndividual(baseNS + PREFIXLABEL
				+name +labelname);
		DatatypeProperty dprop = model.getDatatypeProperty(skosxlNS
				+ "literalForm");
		log.info("datatypeProp" + dprop.getLocalName());
		indi.addProperty(dprop, model.createLiteral(description, "de"));
		ObjectProperty Oprop = model.getObjectProperty(skosxlNS
				+ "prefLabel");
		model.add(toindividual, Oprop, indi);
		
	}
	
	@FXML public void editLabel(ActionEvent e){
		log.info("in editLabelmethod");
		if(selectedOntClass !=null && selectedIndividual != null){
			log.info("no null Class or Individual");
			log.info(selectedOntClass.getLocalName());
			if(selectedOntClass.getLocalName().contains("Concept")){
				log.info("test");
				if(getIndividualbyObjectProperty(selectedIndividual,"prefLabel")!=null){
					
					if(getDatapropertyFromLabel(getIndividualbyObjectProperty(selectedIndividual,"prefLabel"))!=null){
						getDatapropertyFromLabel(getIndividualbyObjectProperty(selectedIndividual,"prefLabel")).changeObject(txtfield_editLabel.getText(),"de");
					}
				}else{
					createLabelRecipe("", txtfield_editLabel.getText(), selectedIndividual);
				}
			}else if(selectedOntClass.getLocalName().contains("Label")){
				if(getDatapropertyFromLabel(selectedIndividual)!=null){
					getDatapropertyFromLabel(selectedIndividual).changeObject(txtfield_editLabel.getText(),"de");
				}
			}
		}
	}
	
	private Resource getIndividualbyObjectProperty(Individual individual, String objectproperty){
		if(individual != null){
			StmtIterator iter = individual.listProperties();
			while(iter.hasNext()){
				Statement s = iter.next();
				if(s.getPredicate().getLocalName().equals(objectproperty)){
					return s.getObject().asResource();
				}
			}
		}
		return null;
	}
	
	private Statement getDatapropertyFromLabel(Resource label){
		StmtIterator iter = label.listProperties();
		while(iter.hasNext()){
			Statement s = iter.next();
			log.info(s.getPredicate().getLocalName());
			if(s.getPredicate().getLocalName().equals("literalForm")){
				return s;
			}
		}
		return null;
	}

	public static void startSKOSController(OntModel ontModel) {
		model = ontModel;
	}

	public static OntModel endSKOSController() {
		return model;
	}
}
