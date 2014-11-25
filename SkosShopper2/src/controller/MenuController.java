package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import model.FusekiModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

import exceptions.fuseki_exceptions.NoDatasetAccessorException;
import exceptions.fuseki_exceptions.NoDatasetGraphException;
import exceptions.fuseki_exceptions.NoServerConfigException;

public class MenuController implements Initializable{
	private ResourceBundle bundleEN = ResourceBundle.getBundle("localization.bundle", Locale.forLanguageTag("en"));
	private ResourceBundle bundleDE = ResourceBundle.getBundle("localization.bundle", Locale.forLanguageTag("de"));
	
	@FXML Menu menuFile;
	@FXML MenuItem menuItemClose;
	@FXML MenuItem menuItemDelete;

	@FXML Menu menuLanguage;
	@FXML MenuItem menuItemEN;
	@FXML MenuItem menuItemGER;

	@FXML Menu menuHelp;
	@FXML MenuItem menuItemAbout;

	@FXML MenuItem settingsItem;
	@FXML MenuItem importOnthologyFileItem;
	@FXML MenuItem onStartItem;
	
	
	
	public void initialize(URL fxmlPath, ResourceBundle resources) {
		assert importOnthologyFileItem != null : "fx:id=\"importOnthologyFileItem\" was not injected: check your FXML file";
	}
	
	
	public void importOnthologyFile(ActionEvent e) throws NoDatasetGraphException, NoServerConfigException
	{
		
		System.out.println("Import Onthology File");
	    FileChooser chooser = new FileChooser();
	    chooser.setTitle("Open File");
	    File file = chooser.showOpenDialog(Main.scene.getWindow());
	    
	    System.out.println(file.getAbsolutePath());
	    
	    Model model = FileManager.get().loadModel(file.getAbsolutePath());
	    
	    try {
			FusekiModel.getDatasetAccessor().add(model);
		} catch (NoDatasetAccessorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void importOnthologyFile(String path)
	{
	    Model model = FileManager.get().loadModel(path);
	    
	    try {
			FusekiModel.getDatasetAccessor().add(model);
		} catch (NoDatasetAccessorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	public void showSettingsOnStart(ActionEvent e) throws NoDatasetGraphException, NoServerConfigException
	{
		System.out.println("show settings window");
		
		try {
			Parent popup = FXMLLoader.load(getClass().getResource("/view/SettingsStartup.fxml"));
			Scene popupScene = new Scene(popup);
			Stage popupStage = new Stage();
			popupStage.setScene(popupScene);
			popupStage.show();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
        //Fill stage with content
		
	}
	
	@FXML
	private void setEnglish(){
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"), bundleEN);
			Main.scene.setRoot(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void setGerman(){
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"), bundleDE);
			Main.scene.setRoot(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
