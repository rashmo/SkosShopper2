package controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import model.SKOSOntology;
import model.TempSparql;

import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

public class SKOSOntologyController implements Initializable{
	
	@FXML	private Label label_box1, label_box2, label_box3, label_box4, label_box5, label_box6, label_lang;

	// Buttons
	@FXML	private Button add1, add2, add3;
	@FXML	private Button print;
	@FXML	private MenuButton load_btn;
	
	// Menu Items
	@FXML	private MenuItem create_skos;
	@FXML	private MenuItem load_skos_from_storage;
	@FXML	private MenuItem load_from_fuseki;
	@FXML	private MenuItem load_from_web;
	
	@FXML	private TextField delete_box;
	@FXML	private TextField statement_box1;
	@FXML	private TextField statement_box2;
	@FXML	private ComboBox<String> statement_box3;
	@FXML	private ComboBox<String> statement_box4;
	@FXML	private ComboBox<String> statement_box5;
	@FXML	private ComboBox<String> statement_box6;
	@FXML	private ComboBox<String> option_box;
	@FXML	private TextField language;
	//@FXML	private TextArea assertion;
	

	@FXML	private TableView<TempSparql> sparql_field = new TableView<TempSparql>();
	@FXML	private TableView<String> table_concepts = new TableView<String>();
	@FXML	private TableView<String> table_labels = new TableView<String>();
	@FXML	private TableColumn<String, String> conceptListColumn = new TableColumn<String, String>();
	@FXML	private TableColumn<String, String> labelListColumn = new TableColumn<String, String>();
	@FXML	private TreeView<String> collection_tree = new TreeView<String>();
	@FXML	private TreeView<String> conceptSchemes_tree = new TreeView<String>();
	@FXML	private TreeView<String> ordCollection_tree = new TreeView<String>();
	@FXML	private TableColumn<TempSparql, String> predicateCol = new TableColumn<TempSparql, String>();
	@FXML	private TableColumn<TempSparql, String> objectCol = new TableColumn<TempSparql, String>();

	private SKOSOntology skos;
	private String baseURI;
	private ObservableList<TreeItem<String>> ord_col;
	private ObservableList<TreeItem<String>> col;
	private ObservableList<String> concepts;
	private ObservableList<String> conceptSchemes;
	private ObservableList<TempSparql> list = FXCollections.observableArrayList();
	private ObservableList<String> labels;
	private ObservableList<String> object_properties;
	private ObservableList<String> annot;
	private ObservableList<String> datatypes;
	private ObservableList<String> userchoice;
	private Model model = ModelFactory.createDefaultModel();
	private DatasetAccessor ds;
	private TreeItem<String> root1;
	private TreeItem<String> root2;
	private TreeItem<String> root3;
	private TreeItem<String> target_drag, source_drag;
	public void initialize(URL location, ResourceBundle resources) {
		createBoxSource();
	}
	
	/**
	 * This method is used to pop a Dialog where the user can create a new concept
	 * @param  event  Event when mouse is clicked
	 */
	@FXML public void clicked_concept(MouseEvent event) {
		// check if left mouse button was pressed
        if(event.getButton().equals(MouseButton.PRIMARY)){
        	// check if left mouse button was pressed twice
            if(event.getClickCount() == 2){
        		String input = (String)JOptionPane.showInputDialog(
                        null,
                        "Enter a new concept individual",
                        "Create New Concept individual",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null, null);
        		// user input, if user inputs nothing, nothing will happen
        		if(input == null) {}
        		else if(input.length() ==  0) {}
        		// user input, new individual is getting created
        		else {
        			skos.createConcept(input);
        			concepts.add(input);
        		}
            }
        }else if(event.getButton().equals(MouseButton.SECONDARY)) {
        	
        }
	}
	
	
	/**
	 * This method is used to pop a Dialog where the user can create a new label
	 * @param  event  Event when mouse is clicked
	 */
	@FXML public void clicked_label(MouseEvent event) {
		// check if left mouse button was pressed
        if(event.getButton().equals(MouseButton.PRIMARY)){
        	// check if left mouse button was pressed twice
            if(event.getClickCount() == 2){
        		String input = (String)JOptionPane.showInputDialog(
                        null,
                        "Enter a new Label individual",
                        "Create New Label individual",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null, null);
        		// user input, if user inputs nothing, nothing will happen
        		if(input == null) {}
        		else if(input.length() ==  0) {}
        		// user input, new individual is getting created
        		else {
        			skos.createLabel(input);
        			labels.add(input);
        		}
            }
        }
	}
	
	/**
	 * This method is used to pop a Dialog where the user can create a new collection
	 * @param  event  Event when mouse is clicked
	 */
	@FXML public void clicked_collection(MouseEvent event) {
		// check if left mouse button was pressed
        if(event.getButton().equals(MouseButton.PRIMARY)){
        	// check if left mouse button was pressed twice
            if(event.getClickCount() == 2){
        		String input = (String)JOptionPane.showInputDialog(
                        null,
                        "Enter a new Collection individual",
                        "Create New Collection individual",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null, null);
        		// user input, if user inputs nothing, nothing will happen
        		if(input == null) {}
        		else if(input.length() ==  0) {}
        		// user input, new individual is getting created
        		else {
        			skos.createCollection(input);
        			root1.getChildren().add(new TreeItem<String>(input));
        			col.add(new TreeItem<String>(input));
        		}
            }
        }
	}
	
	/**
	 * This method is used to pop a Dialog where the user can create a new ordered collection
	 * @param  event  Event when mouse is clicked
	 */
	@FXML public void clicked_ordCollection(MouseEvent event) {
		// check if left mouse button was pressed
        if(event.getButton().equals(MouseButton.PRIMARY)){
        	// check if left mouse button was pressed twice
            if(event.getClickCount() == 2){
        		String input = (String)JOptionPane.showInputDialog(
                        null,
                        "Enter a new Ordered Collection individual",
                        "Create New Ordered Collection individual",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null, null);
        		// user input, if user inputs nothing, nothing will happen
        		if(input == null) {}
        		else if(input.length() ==  0) {}
        		// user input, new individual is getting created
        		else {
        			skos.createOrderedCollection(input);
        			root2.getChildren().add(new TreeItem<String>(input));
        			ord_col.add(new TreeItem<String>(input));
        		}
            }
        }
	}
	
	/**
	 * This method is used to pop a Dialog where the user can create a new concept scheme
	 * @param  event  Event when mouse is clicked
	 */
	@FXML public void clicked_conceptScheme(MouseEvent event) {
		// check if left mouse button was pressed
        if(event.getButton().equals(MouseButton.PRIMARY)){
        	// check if left mouse button was pressed twice
            if(event.getClickCount() == 2){
        		String input = (String)JOptionPane.showInputDialog(
                        null,
                        "Enter a new Concept Scheme individual",
                        "Create New Concept Scheme individual",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null, null);
        		// user input, if user inputs nothing, nothing will happen
        		if(input == null) {}
        		else if(input.length() ==  0) {}
        		// user input, new individual is getting created
        		else {
        			skos.createConceptScheme(input);
        			root3.getChildren().add(new TreeItem<String>(input));
        		}
            }
        }
	}
	
	/**
	 * This method is used to detect a drag from the concept TableView
	 * @param  event  Event when an item is being dragged
	 */
	@FXML public void table_concept_drag(Event event) {
		Dragboard dragBoard = table_concepts.startDragAndDrop(TransferMode.COPY);
		ClipboardContent content = new ClipboardContent();
		content.putString(table_concepts.getSelectionModel().getSelectedItem());
		dragBoard.setContent(content);
		source_drag = new TreeItem<String>(dragBoard.getString());
        event.consume();
	}
	
	/**
	 * This method is used to let the concept TableView know that drag is done
	 * @param  event  Event when an drag transaction is done
	 */
	@FXML public void table_concept_drag_done(Event event) {
		table_concepts.setOnDragDone(new EventHandler<DragEvent>() {
		    public void handle(DragEvent event) {
		        event.consume();
		    }
		});
	}
	
	/**
	 * This method is used to detect a drag from the label TableView
	 * @param  event  Event when an item is being dragged
	 */
	@FXML public void table_label_drag(Event event) {
		Dragboard dragBoard = table_labels.startDragAndDrop(TransferMode.COPY);
		ClipboardContent content = new ClipboardContent();
		content.putString(table_labels.getSelectionModel().getSelectedItem());
		dragBoard.setContent(content);
		System.out.println(dragBoard.getString());   
        event.consume();
	}
	
	/**
	 * This method is used to let the label TableView know that drag is done
	 * @param  event  Event when an drag transaction is done
	 */
	@FXML public void table_label_drag_done(Event event) {
		table_labels.setOnDragDone(new EventHandler<DragEvent>() {
		    public void handle(DragEvent event) {
		        event.consume();
		    }
		});
	}
	
	/**
	 * This method is used to detect if something dragged is over the first statement box
	 * @param  event  Event when something is being dragged
	 */
	@FXML public void overBox1(DragEvent event) {
        if (event.getGestureSource() != statement_box1 &&
                event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
	}
	
	/**
	 * This method is used to detect if something has been dropped over the first statement box
	 * @param  event  Event when something is being dragged
	 */
	@FXML public void droppedOnBox1(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
        	statement_box1.setText(db.getString());
           success = true;
        }
        event.setDropCompleted(success);
        event.consume();
	}
	
	/**
	 * This method is used to detect if something dragged is over the second statement box
	 * @param  event  Event when something is being dragged
	 */
	@FXML public void droppedOnBox2(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
        	statement_box2.setText(db.getString());
           success = true;
        }
        event.setDropCompleted(success);
        event.consume();
	}
	
	/**
	 * This method is used to detect if something has been dropped over the second statement box
	 * @param  event  Event when something is being dragged
	 */
	@FXML public void overBox2(DragEvent event) {
        if (event.getGestureSource() != statement_box2 &&
                event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
	}

	/**
	 * This method uses a sparql update in order to delete an individual
	 * @param  event  Event when something is being dragged
	 */
	@FXML public void droppedDelete(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
        	String toRemove = db.getString();
        	delete_box.setText(toRemove);
        	success = true;
        	int result = JOptionPane.showConfirmDialog(null, "Delete: " + toRemove + "?",
        	        "alert", JOptionPane.OK_CANCEL_OPTION);
        	if(result == 0) {
        		skos.removeIndiv(skos.getBaseURI(), db.getString());
        		if(concepts.contains(toRemove)) {
    				concepts.remove(toRemove);
        		} 
        		
        		// delete from skos model works, but delete nodes does not work, needs check
//        		else if(collections.contains(toRemove)) {
//        			collections.remove(toRemove);
//        			TreeItem<String> node = new TreeItem<String>(toRemove);
//        			collection_tree.getRoot().getChildren().remove(node);
//        		} else if(orderedCollections.contains(toRemove)) {
//        			orderedCollections.remove(toRemove);
//        			TreeItem<String> node = new TreeItem<String>(toRemove);
//        			ordCollection_tree.getRoot().getChildren().remove(node);
//        		} else if(conceptSchemes.contains(toRemove)) {
//        			conceptSchemes.remove(toRemove);
//        			TreeItem<String> node = new TreeItem<String>(toRemove);
//        			conceptSchemes_tree.getRoot().getChildren().remove(node);
//        		} 
        		
        		else if(labels.contains(toRemove)) {
        			labels.remove(toRemove);
        		}
        	}
        }
        event.setDropCompleted(success);
        event.consume();
	}
	
	@FXML public void overBoxDelete(DragEvent event) {
        if (event.getGestureSource() != delete_box &&
                event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
	}
	
	/**
	 * This method is used to pop a dialog when the user wants to create a SKOS-Ontology from scratch
	 * @param  event  Event when button clicked
	 */
	@FXML public void create_from_scratch(ActionEvent event) {
		skos = new SKOSOntology();
		baseURI = (String)JOptionPane.showInputDialog(
                null,
                "Enter an URL for your new SKOS-File:\n"
                + "Example: http://www.example.org/ont",
                "Create New SKOS",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null, "http://");
		skos.createNewOntology(baseURI);
		fillTable();
	}
	
	/**
	 * This method is used to pop a dialog when the user wants to load a SKOS-Ontology from physical storage system
	 * @param  event  Event when button clicked
	 */
	@FXML public void load_from_storage(ActionEvent event) {
		skos = new SKOSOntology();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		File file = fileChooser.showOpenDialog(null);
		if(fileChooser != null) {
			String path = file.getAbsolutePath();
			skos.getSKOSOntologyFromLocal(path);
		}
		baseURI = skos.getModel().getNsPrefixURI("");
		fillTable();
	}
	
	/**
	 * This method is used to pop a dialog when the user wants to load a SKOS-Ontology from any web-server
	 * @param  event  Event when button clicked
	 */
	@FXML public void load_web(ActionEvent event) {
		skos = new SKOSOntology();
		baseURI = (String)JOptionPane.showInputDialog(
                null,
                "Enter an URL for your SKOS-File:\n"
                + "Example: http://www.w3.org/2008/05/skos-xl",
                "SKOS-URL",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null, "http://");
		skos.getSKOSOntologyFromServer(baseURI);
	}
	
	/**
	 * This method is used to pop a dialog when the user wants to load a SKOS-Ontology from fuseki-server
	 * @param  event  Event when button clicked
	 */
	@FXML public void load_fuseki(ActionEvent event) {
		// NOT IMPLEMENTED YET
	}
	
	/**
	 * This method is used to print the content of a SKOS-model
	 * @param  event  Event when button clicked
	 */
	@FXML public void printing(ActionEvent event) {
		skos.getModel().write(System.out);
	}
	
	/**
	 * This method is used to add an object property statement into a SKOS-model
	 * @param  event  Event when button clicked
	 */
	public void add_statement1(ActionEvent event) {
		System.out.println("pushed button1");
		if(option_box.getValue() == userchoice.get(0)) {
			if(statement_box1.getText().length() > 0 && statement_box2.getText().length() > 0 && statement_box3.getValue() != null) {
				for (int i = 0; i < object_properties.size(); i++) {
					if(statement_box3.getValue() == object_properties.get(i)) {
						if(!skos.contains(statement_box1.getText()) || !skos.contains(statement_box2.getText())) {
						    JOptionPane.showMessageDialog(null, "It seems your SKOS does not contain those individuals"
						    		+ "\nPlease create first individuals by double clicking the table.", "Warning",
						            JOptionPane.WARNING_MESSAGE);
							break;
						}
						skos.objPropAssertion(statement_box1.getText(), statement_box2.getText(), i);
						break;
					}
				}
			}
		}
		statement_box3.setValue(null); statement_box1.setText("");; statement_box2.setText("");
	}
	
	/**
	 * This method is used to add a datatyped label statement into a SKOS-model
	 * @param  event  Event when button clicked
	 */
	public void add_statement2(ActionEvent event) {
		if (option_box.getValue() == userchoice.get(1)) {
			if (statement_box1.getText().length() > 0 && statement_box2.getText().length() > 0) {
				if(statement_box4.getValue() != null) {
					for (int i = 0; i < datatypes.size(); i++) {
						if (datatypes.get(i) == statement_box4.getValue()) {
							skos.hasLiteralForm(statement_box1.getText(), statement_box2.getText(), "", i);
							break;
						}
					}
				} else {
					if(language.getText().length() > 0) {
						skos.hasLiteralForm(statement_box1.getText(), statement_box2.getText(), language.getText(), -1);
					}
				}
			}
		}
		statement_box4.setValue(null); statement_box1.setText("");; statement_box1.setText("");
		language.setText("");
	}
	
	/**
	 * This method is used to add a annotation statement into a SKOS-model
	 * @param  event  Event when button clicked
	 */
	public void add_statement3(ActionEvent event) {
		if (option_box.getValue() == userchoice.get(2)) {
			if (statement_box1.getText().length() > 0 && statement_box2.getText().length() > 0 && statement_box5.getValue() != null) {
				if(statement_box6.getValue() != null) {
					int i, j;
					for (i = 0; i < annot.size(); i++) {
						if (statement_box5.getValue() == annot.get(i)) {
							break;
						}
					}
					for (j = 0; j < datatypes.size(); j++) {
						if (statement_box6.getValue() == datatypes.get(j)) {
							break;
						}
					}
					skos.hasNote(statement_box1.getText(), statement_box2.getText(), i, null, j);
				} else {
					if(language.getText().length() > 0) {
						for (int i = 0; i < annot.size(); i++) {
							if (statement_box3.getValue() == annot.get(i)) {
								skos.hasNote(statement_box1.getText(), statement_box2.getText(), i, language.getText(), -1);
								break;
							}
						}
						
					}
				}
			}
		}
		statement_box5.setValue(null); statement_box6.setValue(null); statement_box1.setText(""); statement_box1.setText("");
		language.setText("");
	}
	
	
	/**
	 * This method is used to clear the text fields
	 * @param  event  Event when button clicked
	 */
	@FXML public void clear_fields(ActionEvent event) {
		// Not implemented yet
	}
	
	/**
	 * This method is used to send a model to the fuseki-server
	 * @param  event  Event when button clicked
	 */
	@FXML public void send_to_fuseki(ActionEvent event) {
		String serviceURI = "http://localhost:3030/ds/data";
		ds = DatasetAccessorFactory.createHTTP(serviceURI);
		model = ds.getModel();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		skos.getModel().write(out);
		model.write(out);
	}
	
	/**
	 * This method is used to get a model from the fuseki-server
	 * @param  event  Event when button clicked
	 */
	@FXML public void getFromFuseki(ActionEvent event) {
		String serviceURI = "http://localhost:3030/ds/data";
		ds = DatasetAccessorFactory.createHTTP(serviceURI);
		model = ds.getModel();
		model.write(System.out);
	}
	
	/**
	 * This method is used to change the view of the frame when switching options
	 * @param  event  Event when button clicked
	 */
	@FXML public void option_event(ActionEvent event) {
		if(option_box.getValue() == userchoice.get(0)) {
			if(language.isVisible()) { language.setVisible(false); }
			if(!label_box3.isVisible()) { label_box3.setVisible(true); }
			if(label_lang.isVisible()) { label_lang.setVisible(false); }
			if(label_box4.isVisible()) { label_box4.setVisible(false); }
			if(label_box5.isVisible()) { label_box5.setVisible(false); }
			if(label_box6.isVisible()) { label_box6.setVisible(false); }
			if(statement_box4.isVisible()) { statement_box4.setVisible(false); }
			if(statement_box5.isVisible()) { statement_box5.setVisible(false); }
			if(statement_box6.isVisible()) { statement_box6.setVisible(false); }
			if(!statement_box3.isVisible()) { statement_box3.setVisible(true); }
			if(!add1.isVisible()) { add1.setVisible(true); }
			if(add2.isVisible()) { add2.setVisible(false); }
			if(add3.isVisible()) { add3.setVisible(false); }
			
			label_box1.setText("Individual A");
			label_box2.setText("Individual B");

		} else if(option_box.getValue() == userchoice.get(1)) {
			if(!language.isVisible()) { language.setVisible(true); }
			if(label_box3.isVisible()) { label_box3.setVisible(false); }
			if(!label_lang.isVisible()) { label_lang.setVisible(true); }
			if(!label_box4.isVisible()) { label_box4.setVisible(true); }
			if(label_box5.isVisible()) { label_box5.setVisible(false); }
			if(label_box6.isVisible()) { label_box6.setVisible(false); }
			if(!statement_box4.isVisible()) { statement_box4.setVisible(true); }
			if(statement_box5.isVisible()) { statement_box5.setVisible(false); }
			if(statement_box6.isVisible()) { statement_box6.setVisible(false); }
			if(statement_box3.isVisible()) { statement_box3.setVisible(false); }
			if(add1.isVisible()) { add1.setVisible(false); }
			if(!add2.isVisible()) { add2.setVisible(true); }
			if(add3.isVisible()) { add3.setVisible(false); }
			label_box1.setText("Individual");
			label_box2.setText("has Label");

		} else if(option_box.getValue() == userchoice.get(2)) {
			if(!language.isVisible()) { language.setVisible(true); }
			if(label_box3.isVisible()) { label_box3.setVisible(false); }
			if(!label_lang.isVisible()) { label_lang.setVisible(true); }
			if(label_box4.isVisible()) { label_box4.setVisible(false); }
			if(!label_box5.isVisible()) { label_box5.setVisible(true); }
			if(!label_box6.isVisible()) { label_box6.setVisible(true); }
			if(statement_box4.isVisible()) { statement_box4.setVisible(false); }
			if(!statement_box5.isVisible()) { statement_box5.setVisible(true); }
			if(!statement_box6.isVisible()) { statement_box6.setVisible(true); }
			if(statement_box3.isVisible()) { statement_box3.setVisible(false); }
			if(add1.isVisible()) { add1.setVisible(false); }
			if(add2.isVisible()) { add2.setVisible(false); }
			if(!add3.isVisible()) { add3.setVisible(true); }
			
			label_box1.setText("Individual");
			label_box2.setText("has Annotation");
		}
	}
	
	/**
	 * This method is used to create statement boxes and fill in data
	 */
	public void createBoxSource() {
		// Fill the user choice box
		userchoice = FXCollections.observableArrayList("Object Properties", "Data Properties", "Annotation Properties");
		option_box.getItems().clear();
		option_box.setItems(userchoice);
		
		// Object Properties
		// SEMANTIC object_properties BOX
		object_properties = FXCollections.observableArrayList(
				"has member", 
				"has member list",
				"has preferred label", 
				"has alternative label", 
				"has hidden label",
				"has top concept", 
				"is top concept of", 
				"has broader",
				"has narrower",
				"has broad match", 
				"has narrow match",
				"has related",
				"has exact match",
				"has close match",
				"has related match", 
				"has related label", 
				"is in scheme with", 
				"has broader transitive", 
				"has narrower transitive",
				"is in semantic relation with",
				"is in mapping relation with");
		statement_box3.getItems().clear();
		statement_box3.setItems(object_properties);
		
		// Data Properties
		// DATATYPE BOX
		datatypes = FXCollections.observableArrayList("anyURI", "base64Binary", "boolean", "byte", "date",
				"dateTime", "decimal", "double", "duration", "ENTITY", "float", "gDay", "gMonth",
				"gMonthDay", "gYear", "gYearMonth", "hexBinary", "ID", "IDREF" , "int", "integer",
				"language", "long", "Name", "NCName", "negativeInteger", "NMTOKEN", "nonNegativeInteger",
				"nonPositiveInteger", "normalizedString", "NOTATION", "positiveInteger", "QName", "short",
				"string", "time", "token", "unsignedByte", "unsignedInt", "unsignedLong", "unsignedShort");
		statement_box4.getItems().clear();
		statement_box4.setItems(datatypes);
		statement_box6.getItems().clear();
		statement_box6.setItems(datatypes);
		
		// ANNOTATION BOX
		annot = FXCollections.observableArrayList(
				"has note", "has change note", "has definition", "has editorial note", "has example", "has history note", "has scope note");
		statement_box5.getItems().clear();
		statement_box5.setItems(annot);
	}
	
	/**
	 * This method creates a tree with a root and its children
	 * @param  root  Root of the tree
	 * @param  tree_list  lsit with the children of the tree
	 * @param  collection_tree2  The tree which will contain the root and children
	 */
	public void buildTree(TreeItem<String> root, ObservableList<String> tree_list, TreeView<String> collection_tree2) {
		root.setExpanded(true);
		for (int i = 0; i < tree_list.size(); i++) {
			TreeItem<String> node = new TreeItem<String>(tree_list.get(i));
			root.getChildren().add(node);
		}
		collection_tree2.setRoot(root);
	}
	
	/**
	 * This method finds all individuals of a SKOS-Ontology and fills all tables with data of the SKOS-model
	 */
	public void fillTable() {
		// find all individuals
		predicateCol.setCellValueFactory(cellData -> cellData.getValue().predicateProperty());
		objectCol.setCellValueFactory(cellData -> cellData.getValue().objectProperty());
		sparql_field.setItems(list);
		
		
		concepts = FXCollections.observableArrayList();
		conceptSchemes = FXCollections.observableArrayList();
		labels = FXCollections.observableArrayList();
		col = FXCollections.observableArrayList();
		ord_col = FXCollections.observableArrayList();
		
		skos.getCollections(col);
		skos.getOrderedCollections(ord_col);
	
		skos.getConcepts(concepts);
		skos.getConceptSchemes(conceptSchemes);
		//skos.getCollections(collections);
		//skos.getOrderedCollections(orderedCollections);
		skos.getLabels(labels);
		
		// Build all tree with a root
		root1 = new TreeItem<String>("Collections");
		root2 = new TreeItem<String>("Ordered Collections");
		root3 = new TreeItem<String>("Concept Schemes");
		
		
		// Will be change 
		for (int i = 0; i < col.size(); i++) {
			root1.getChildren().add(col.get(i));
		}
		root1.setExpanded(true);
		collection_tree.setRoot(root1);
		
		for (int i = 0; i < ord_col.size(); i++) {
			root2.getChildren().add(ord_col.get(i));
		}
		root2.setExpanded(true);
		ordCollection_tree.setRoot(root2);
		
		//buildTree(root1, collections, collection_tree);
		//buildTree(root2, orderedCollections, ordCollection_tree);
		buildTree(root3, conceptSchemes, conceptSchemes_tree);
		
		setCol(root1, collection_tree);
		setCol(root2, ordCollection_tree);
		//setCellF(root3, conceptSchemes_tree);
		
		// Build the table for Concepts and Labels
		setValueOfTable(conceptListColumn);
		setValueOfTable(labelListColumn);

		table_concepts.setItems(concepts);
		table_labels.setItems(labels);
	}
	
	
	public void setValueOfTable(TableColumn<String, String> col) {
		col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<String, String> param) {
					return new ReadOnlyObjectWrapper<String>(param.getValue());
			}
		});
		
		col.setCellFactory(new Callback<TableColumn<String,String>, TableCell<String,String>>() {
			public TableCell<String, String> call(TableColumn<String, String> param) {
				final TableCell<String, String> tablecell = new TableCell<String, String>() {
	                protected void updateItem(String item, boolean empty) {
	                    super.updateItem(item, empty);
	                    if (!empty && item != null) {
	                        setText(item);
	                    }else{
	                        setText(null);
	                        setGraphic(null);
	                    }
	                }
				};
				tablecell.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						if (event.getButton().equals(MouseButton.PRIMARY)) {
							if (tablecell.getItem() != null) {
								updateSparqlTable();
								skos.queryIndiv(skos.getBaseURI(), tablecell.getItem(), list);
							}
						}
					}
				});
				
				return tablecell;
			}
		});
	}
	
	public void setCol(final TreeItem<String> root, final TreeView<String> tree) {
		tree.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			public TreeCell<String> call(TreeView<String> param) {
				TreeCell<String> treecell = new TreeCell<String>() {
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (!empty && item != null) {
							setText(item);
							setGraphic(getTreeItem().getGraphic());
						} else {
							setText(null);
							setGraphic(null);
						}
					}
				};
				addDragAndDrop(treecell, root);
				addMouseAction(treecell, root);
				return treecell;
			}
		});
	}
	
	public void updateSparqlTable()	 {
		list.clear();
		sparql_field.getItems().clear();
		sparql_field.setItems(list);
	}
	
	public void addMouseAction(TreeCell<String> treecell, TreeItem<String> root) {
		treecell.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				
		        if(event.getButton().equals(MouseButton.PRIMARY)){
		        	if(treecell.getTreeItem() != null) {
		        		updateSparqlTable();
		        		skos.queryIndiv(skos.getBaseURI(), treecell.getTreeItem().getValue(), list);
		        	}
		            if(event.getClickCount() == 2 && treecell.getTreeItem() == null){
		        		String input = (String)JOptionPane.showInputDialog(
		                        null,
		                        "Enter a new Collection individual",
		                        "Create New Collection individual",
		                        JOptionPane.PLAIN_MESSAGE,
		                        null,
		                        null, null);
		        		// user input, if user inputs nothing, nothing will happen
		        		if(input == null) {}
		        		else if(input.length() ==  0) {}
		        		// user input, new individual is getting created
		        		else {
		        			skos.createCollection(input);
		        			root1.getChildren().add(new TreeItem<String>(input));
		        			col.add(new TreeItem<String>(input));
		        		}
		            } else if(event.getClickCount() == 2 && treecell.getTreeItem() != null) {
		        		String input = (String)JOptionPane.showInputDialog(
		                        null,
		                        "Enter a new Collection individual",
		                        "Create New Collection individual",
		                        JOptionPane.PLAIN_MESSAGE,
		                        null,
		                        null, null);
		        		// user input, if user inputs nothing, nothing will happen
		        		if(input == null) {}
		        		else if(input.length() ==  0) {}
		        		// user input, new individual is getting created
		        		else {
		        			skos.createCollection(input);
		        			treecell.getTreeItem().getChildren().add(new TreeItem<String>(input));
		        		}
		            }
		        }
				event.consume();
			}
		});
	}
	
	public void addDragAndDrop(TreeCell<String> treecell, TreeItem<String> root) {
		
		treecell.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
	            Dragboard db = treecell.startDragAndDrop(TransferMode.ANY);
	            ClipboardContent content = new ClipboardContent();
	            content.putString(treecell.getTreeItem().getValue());
	            db.setContent(content);
	            source_drag = deepcopy(treecell.getTreeItem());
	            System.out.println("Source: " + source_drag.hashCode());
	            System.out.println("Treecell: " + treecell.getTreeItem().hashCode());
                event.consume();
			}
		});
		
		treecell.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				target_drag = treecell.getTreeItem();
				if (event.getGestureSource() != treecell && event.getDragboard().hasString()) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);						
				}
			}
		});

		treecell.setOnDragDone(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
                if (event.getTransferMode() == TransferMode.MOVE) {
	                source_drag = null;
	                target_drag = null;
                }
                event.consume();
			}
		});
		
		treecell.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
                boolean success = false;
                if (source_drag != null && target_drag != null) {
                    target_drag.getChildren().add(source_drag);
                    skos.objPropAssertion(target_drag.getValue(), source_drag.getValue(), 0);
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
                System.out.println("");
                if(treecell.getTreeView() == collection_tree) {
                	System.out.println("collection tree, there was a drop");
                    TreeItem<String> tmp = search(root2, target_drag.getValue());
                    if(tmp != null) {
                    	TreeItem<String> parent = tmp.getParent();
                    	parent.getChildren().remove(tmp);
                    	tmp = deepcopy(target_drag);
                    	parent.getChildren().add(tmp);
                    }
                } else if(treecell.getTreeView() == ordCollection_tree) {
                	System.out.println("ord collection tree, there was a drop");
                    TreeItem<String> tmp = search(root1, target_drag.getValue());
                    if(tmp != null) {
                    	TreeItem<String> parent = tmp.getParent();
                    	parent.getChildren().remove(tmp);
                    	tmp = deepcopy(target_drag);
                    	parent.getChildren().add(tmp);
                    }
                }
			}
		});
	}
	
	/**
	 * This method is used to make a deep copy of a tree
	 * @param  node  The root of the tree
	 */
	TreeItem<String> deepcopy(TreeItem<String> node) {
	    TreeItem<String> copy = new TreeItem<String>(node.getValue());
	    for (TreeItem<String> child : node.getChildren()) {
	        copy.getChildren().add(deepcopy(child));
	    }
	    return copy;
	}
	
	
	/**
	 * This method is used to traverse a TreeTableView in order to find a specific node
	 * @param  root  The root of the tree
	 * @param  toSearch  The node to search
	 */
    private TreeItem<String> search(final TreeItem<String> root, final String toSearch) {
        TreeItem<String> result = null;
        if (root.getValue() == toSearch) {
            result = root;
        } else if (!root.isLeaf()) {
            for (TreeItem<String> child : root.getChildren()) {
            	System.out.println(child.getValue());
                result = search(child, toSearch);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }
}
