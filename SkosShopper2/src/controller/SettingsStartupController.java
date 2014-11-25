package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class SettingsStartupController  implements Initializable{
	
	@FXML
	private Label startfilePath;

	public void initialize(URL fxmlPath, ResourceBundle resources) {
		//startfilePath = (Label) resources.getObject("startfilePath");
		//startfilePath.setText("../../fuseki/Data/books.ttl");
		
	}
	
	

}