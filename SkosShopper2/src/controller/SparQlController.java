package controller;


import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import model.FusekiModel;
import model.ModelFacadeTEST;


public class SparQlController implements Initializable{
	@FXML TextArea txtAreaQuery;	
	@FXML TextArea txtAreaResult;
	@FXML Button SparQlSend;
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	@FXML private void buttonSendQuery(MouseEvent Event) {
		// TODO Auto-generated method stub
	
		if(txtAreaQuery.getText()!= null){
			txtAreaResult.setText(FusekiModel.sendSparQLQuery(txtAreaQuery.getText()));
		//	ModelFacadeTEST.getAktModel().write(System.out,"RDF/XML");
//		System.out.println("ONTMODEL #######################");
//		ModelFacadeTEST.getOntModel().write(System.out,"RDF/XML");
//		System.out.println("MODEL #######################");
//		ModelFacadeTEST.getAktModel().write(System.out,"RDF/XML");
		}
	
	}
}
