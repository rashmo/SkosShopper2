package controller;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import model.ProductFactory;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

public class ProductsCreateController implements Initializable {
	
	@FXML
	ComboBox productsAddable;
	

	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	public void productToAddSelect(ActionEvent e)
	{
		System.out.println(e);
	}
	
	
	public void createAddableProductList(Event e)
	{
		System.out.println("make creatable product list");
		
		String[] productListMade = ProductFactory.getCreatableProductsAsString();
		
		
		ObservableList<String> productListCurr = productsAddable.getItems();
		String[] productListCurr2 = (String[]) productListCurr.toArray(new String[productListCurr.size()]);
		
		if(!Arrays.equals(productListCurr2, productListMade))
			productListCurr.setAll(productListMade);
		
	}


}
