package controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.RadioMenuItemBuilder;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.JOptionPane;

import model.FusekiModel;
import model.ServerImporter;
import model.ModelFacadeTEST;
import model.ModelFacadeTEST.ModelState;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.riot.RiotException;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntDocumentManager;

import exceptions.fuseki_exceptions.NoDatasetAccessorException;

public class OverviewController implements Initializable{
	
	
	/* JAVAFX COMPONENTS */
	private RadioMenuItem btn_server_import, btn_file_import, btn_web_import;
	@FXML private ComboBox<String> cb_save_graph;
	@FXML private MenuButton btn_source;
	@FXML private TextArea ta_log_field;
	@FXML private TextField tf_dest_url, tf_curr_loaded_graph, tf_alt_url;
	@FXML private Button btn_add_entry, btn_save_graph;
	@FXML private TableView<String> tv_graph_uri;
	@FXML private TableView<AltEntriesManager> tv_alt_entries;
	@FXML private TableColumn<String, String> col_graph_uri;
	@FXML private TableColumn<AltEntriesManager, String> col_dest_url;
	@FXML private TableColumn<AltEntriesManager, String> col_alt_url;
	private ObservableList<AltEntriesManager> altEntryList = FXCollections.observableArrayList();
	private ObservableList<String> graphURIs = FXCollections.observableArrayList();
	private final ObservableList<String> saveModelTo = FXCollections.observableArrayList();
	@FXML	private Button startStopFuseki;
	@FXML Button btnHome;
	@FXML	private Label fusekiStatus;
	@FXML Label lblIndividuals;
	@FXML Label lblObjektProperties;
	@FXML Label lblDataProperties;
	@FXML Label lblClasses;
	@FXML Label OverviewlblState;
	@FXML WebView webView;
	@FXML TextField txtFieldURL;
	@FXML TextField OverviewtxtField;
	@FXML Button OverviewbtnLoadFromStorage;
	File localFile= null;
	public static boolean modelLoaded = false;
	ToggleGroup group = new ToggleGroup();
	
	WebEngine webEngine;
	WebHistory webHistory;

	
	public static final Logger log = Logger.getLogger(SkosEditorController.class);
	private ArrayList<Entry> browserHistory = new ArrayList<WebHistory.Entry>();
	public static OntDocumentManager mgr;

	public void initialize(URL fxmlPath, ResourceBundle resources) {
		// Initialize overview components
		col_alt_url.setCellValueFactory(cellData -> cellData.getValue().altURLProperty());
		col_dest_url.setCellValueFactory(cellData -> cellData.getValue().destURLProperty());
		tv_alt_entries.setItems(altEntryList);
		tv_graph_uri.setItems(graphURIs);
		ta_log_field.setEditable(false);
		tf_curr_loaded_graph.setStyle("-fx-text-inner-color: green;");
		tf_curr_loaded_graph.setEditable(false);
		saveModelTo.addAll("Add/Update Model from Server", "Replace Model from Server", "Save Model to File", "Discard Model");
		cb_save_graph.setItems(saveModelTo);
		
		// Helpful for copy pasterino url at beginning
		ta_log_field.setText("For copy pasterino:\nFuseki:\nhttp://localhost:3030/ds/data\nor sesame server:\nhttp://localhost:8080/openrdf-sesame/repositories/test");
		
		
		setGraphTable();
		setMenuButtons();
		

		webEngine = webView.getEngine();
		webHistory = webEngine.getHistory();
		webHistory.setMaxSize(3);
		
//		webHistory.getEntries().addListener(new ListChangeListener<WebHistory.Entry>(){
//			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Entry> c) {
//				c.next();
//				for (Entry e : c.getAddedSubList()) {
//					browserHistory.add(e);
//					url =e.getUrl();
//					txtFieldURL.setText(url);
//					System.out.println(e.getUrl());
//				}
//			}
//		});
	}
	
	
	@FXML private void backButtonOnAction(ActionEvent event ){
//		url = browserHistory.get(browserHistory.size()-2).getUrl();
//		System.out.println("URRRRL"+url);
//		for (Entry e : browserHistory) {
//			System.out.println("LOLOL"+e.getUrl());
//		}
//		System.out.println();
//		webEngine.load(url);
//		txtFieldURL.setText(url);
//		
	}

	@FXML private void btnHomeOnAction(ActionEvent event){
//		webEngine.load(initURL+port);
//		txtFieldURL.setText(initURL+port);
	}
	
	@FXML private void OverviewBtnLoadDataFromStorageOnAction(ActionEvent event){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		localFile = fileChooser.showOpenDialog(null);
	}
	
	@FXML public void adding_entry(ActionEvent event) {
		ta_log_field.clear();
		try {
			new URL(tf_dest_url.getText());
			altEntryList.add(new AltEntriesManager(tf_dest_url.getText(), tf_alt_url.getText()));
			mgr.addAltEntry(tf_dest_url.getText(), tf_alt_url.getText());
		} catch(HttpException | MalformedURLException| RiotException e) {
			ta_log_field.appendText(e.getMessage());
		}
	}
	
	@FXML public void saving_graph(ActionEvent event) {
		if(modelLoaded) {
			ta_log_field.clear();
			// send model back to server (add/update)
			if(cb_save_graph.getValue().equals(saveModelTo.get(0))) {
				// check if server is reachable
				if(checkServerConnection()) {
					ta_log_field.appendText("1. Trying to reach \"" + txtFieldURL.getText() + "\"\t... OK\n");
					// try to add/update model from server
					if(ServerImporter.updateModelOfServer()) {
						ta_log_field.appendText("2. Trying to add/update model: \"" + tf_curr_loaded_graph.getText() + "\"\t... OK\n");
						ta_log_field.appendText("3. Transaction successful");
						// add/update was ok
						modelLoaded = false;
					} else {
					    JOptionPane.showMessageDialog(null, "Unable to add/update model of server\nYou should store your model locally or retry",
					            null, JOptionPane.WARNING_MESSAGE);
					}
				} else {
					ta_log_field.appendText("1. Trying to reach \"" + txtFieldURL.getText() + "\"\t... FAILED\n");
				}
			// send model back to server (replace)
			} else if(cb_save_graph.getValue().equals(saveModelTo.get(1))) {
				// check if server is reachable
				if(checkServerConnection()) {
				    JOptionPane.showMessageDialog(null, "This will replace the model which is stored in server\nCannot be undone!",
				            null, JOptionPane.WARNING_MESSAGE);
					ta_log_field.appendText("1. Trying to reach \"" + txtFieldURL.getText() + "\"\t... OK\n");
					// try to add/update model from server
					if(ServerImporter.replaceModelOfServer()) {
						ta_log_field.appendText("2. Trying to replace model: \"" + tf_curr_loaded_graph.getText() + "\"\t... OK\n");
						ta_log_field.appendText("3. Transaction successful");
						// add/update was ok
						modelLoaded = false;
					} else {
					    JOptionPane.showMessageDialog(null, "Unable to replace model of server\nYou should store your model locally or retry",
					            null, JOptionPane.WARNING_MESSAGE);
					}
				} else {
					ta_log_field.appendText("1. Trying to reach \"" + txtFieldURL.getText() + "\"\t... FAILED\n");
				}
			//save model to file
			} else if(cb_save_graph.getValue().equals(saveModelTo.get(2))) {
				System.out.println("saving to file");
			// Discard model
			} else if(cb_save_graph.getValue().equals(saveModelTo.get(3))) {
				System.out.println("Model is getting deleted");
			    int result = JOptionPane.showConfirmDialog(null, "Changes will be lost, cannot be undone!",
			            "Warning: Discard Model", JOptionPane.OK_CANCEL_OPTION);
			}
		} else {
			//Warn the user that there is no model
		    JOptionPane.showMessageDialog(null, "No model available to save",
		            null, JOptionPane.WARNING_MESSAGE);
		}
	}
	
	@FXML private void OverviewbtnReloadDatasetOnAction(ActionEvent event) throws Exception {
		graphURIs.clear();
		if(!modelLoaded) {
			ta_log_field.clear();
			if(btn_server_import.isSelected()) {
				try {
					// check if URL is not malformed
					new URL(txtFieldURL.getText());
					ServerImporter imp = new ServerImporter();
					imp.setServiceURI(txtFieldURL.getText());
					
					// check if server is reachable
					if(checkServerConnection()) {
						ta_log_field.appendText("1. Trying to reach \"" + txtFieldURL.getText() + "\"\t... OK\n");
						// Query server for graphs
						if(imp.queryServerGraphs()) {
							ta_log_field.appendText("2. Querrying Server Data to to retrieve named graphs\t... OK\n");
							ta_log_field.appendText("3. Listing all founded named graphs\n");
							
							// add models to list of graphs
							for (int i = 0; i < ServerImporter.graphList.size(); i++) {
								if(!graphURIs.contains(ServerImporter.graphList.get(i))) {
									graphURIs.add(ServerImporter.graphList.get(i));
								}						
							}
							// Transaction ok, load server page to web engine
							ta_log_field.appendText("4. Transaction done");
							URL url = new URL(ServerImporter.serviceURI);
							webEngine.load("http://" + url.getHost() + ":" + url.getPort());
							
						} else {
							ta_log_field.appendText("2. Querrying Server Data to to retrieve named graphs\t... FAILED\n");
						}
					} else {
						ta_log_field.appendText("1. Trying to reach \"" + txtFieldURL.getText() + "\"\t... FAILED\n");
					}
				} catch(Exception e) {
					// not implemented yet
				}
			}
			if(btn_file_import.isSelected()) {
				
			}
			if(btn_web_import.isSelected()) {
				
			}
		} else {
		    JOptionPane.showMessageDialog(null, "There is already a model in process!",
		            null, JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public void setGraphTable() {
		col_graph_uri.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String,String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<String, String> param) {
				return new ReadOnlyObjectWrapper<String>(param.getValue());
			}
		});
		
		col_graph_uri.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
			public TableCell<String, String> call(TableColumn<String, String> param) {
				final TableCell<String, String> tablecell = new TableCell<String, String>() {
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (!empty && item != null) {
							setText(item);
						} else {
							setText(null);
							setGraphic(null);
						}
					}
				};
				
				// Listener for table of graphs, double clicking a cell will import a model from server
				tablecell.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					if (event.getButton().equals(MouseButton.PRIMARY)) {
						if(event.getClickCount() == 2 && tablecell.getText() != null){
							if(!modelLoaded) {
								ta_log_field.clear();
								if(ServerImporter.importNamedGraph(tablecell.getText())) {
									ta_log_field.appendText("1. Trying to load named graph: \"" + tablecell.getText() + "\"\t... OK");
									tf_curr_loaded_graph.setText(tablecell.getText());
									try {
										ModelFacadeTEST.loadModelFromServer(tablecell.getText());
									} catch (NoDatasetAccessorException e) {
										// TODO Auto-generated catch block
									}
									modelLoaded = true;
								} else {
									ta_log_field.appendText("1. Trying to load named graph: \"" + tablecell.getText() + "\"\t... FAILED");
								}
							} else {
								warnModelLoaded();
							}
						}
					}
				}
			});
				return tablecell;
			}
		});
	}
	
	public void warnModelLoaded() {
	    JOptionPane.showMessageDialog(null, "Another Model is currently in process! Please save the model or update the Model from Server",
	            null, JOptionPane.WARNING_MESSAGE);
	}
	
	public boolean checkServerConnection() {
		try (Socket s = new Socket("localhost", 3030)) {
			System.out.println("IS ONLINE");
			return true;
		} catch (IOException ex) {
		}
		return false;
	}
	
	public void setMenuButtons() {
		group = new ToggleGroup();
		btn_server_import = new RadioMenuItem("Server Import");
		btn_file_import = new RadioMenuItem("File Import");
		btn_web_import = new RadioMenuItem("Web Import");
		btn_server_import.setToggleGroup(group);
		btn_file_import.setToggleGroup(group);
		btn_web_import.setToggleGroup(group);
		btn_source.getItems().add(btn_server_import);
		btn_source.getItems().add(btn_file_import);
		btn_source.getItems().add(btn_web_import);
	}
}
