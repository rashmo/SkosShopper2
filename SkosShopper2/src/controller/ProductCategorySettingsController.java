package controller;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import model.ModelFacade;
import model.ProductFactory;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ProductCategorySettingsController implements Initializable{
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = DEFINITION OF VARIABLES AND OBJECTS
	
	// = = = = = = = = = = FXML vars
	
	
	// FXML Elements of the left Pane
	@FXML	ScrollPane selectionPaneScrollPane;
	@FXML	AnchorPane selectionPaneAnchorPane;
	
	
	// FXML Elements of product categories
	@FXML	Pane productCategoriesPane;
	@FXML	Pane productCategoriesBtn;
	@FXML	TreeView productCategoriesTreeView;
			private static int productCategoriesTreeViewNumElements;
	
	
	// FXML Elements of product propeties and values
	@FXML	Pane productPropertiesPane;
	@FXML	Pane productPropertiesBtn;
	@FXML	ListView<String> productPropertiesListView;
	
	
	// FXML Elements of product and category relations
	@FXML	Pane productRelationsPane;
	@FXML	Pane productRelationsBtn;
	@FXML	ListView productRelationsListView;
	

	// FXML other Elements
	@FXML	Button btnCategoryCreate;
	@FXML	ScrollPane categoryProperties;
	@FXML	AnchorPane categorySettingsContent;
	
	
	
	// = = = = = = = = = = other vars
	
	public static final Logger log = Logger.getLogger(ProductCategorySettingsController.class);
	private boolean initialized;
	private EventHandler<MouseEvent> mouseEventHandler;
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = DE-/CONSTRUCTOR AND INITIALIZATION METHODS
		
	
	// when tab is opened, this function is called
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		System.out.println("initialize()");
		
		// resize and position panes
		setStartSizes();
		//setStartPositions();
		setStartVisibilities();

		//loadCategories();
		loadProperties();
		loadRelations();
		
		
		// set eventHandler to call a method by clicking a treeItem
		productCategoriesTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>(){
			public void changed(ObservableValue<? extends TreeItem> paramObservableValue, TreeItem paramT1, TreeItem selectedItem)
			{
				treeItemOnClick(selectedItem);
			}
		});
	}
	
	
	private void setStartSizes()
	{
		double PaneHeight = 50.0;
		
		productCategoriesPane.setPrefHeight(PaneHeight);
		productCategoriesPane.setMinHeight(PaneHeight);
		productCategoriesPane.setMaxHeight(PaneHeight);
		
		productPropertiesPane.setPrefHeight(PaneHeight);
		productPropertiesPane.setMinHeight(PaneHeight);
		productPropertiesPane.setMaxHeight(PaneHeight);
		
		productRelationsPane.setPrefHeight(PaneHeight);
		productRelationsPane.setMinHeight(PaneHeight);
		productRelationsPane.setMaxHeight(PaneHeight);
	}
	
	
	private void setStartPositions()
	{
		productCategoriesPane.requestLayout();
		
		log.info("setStartPositions()");
		
		productCategoriesPane.setLayoutY(0.0);
		productPropertiesPane.setLayoutY(50);
		productRelationsPane.setLayoutY(100);

	}
	
	
	private void setStartVisibilities()
	{
		log.info("setStartVisibilities()");
		productCategoriesTreeView.setVisible(false);
		productPropertiesListView.setVisible(false);
		productRelationsListView.setVisible(false);
	}
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = SIMPLE GETTER & SETTER
	
	// = = = = = = = = = = getter
	
	// = = = = = = = = = = setter
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = COMPLEX GETTER & SETTER
	
	
	private void setSelectionPaneAnchorPaneHeight(double newHeight, int fromPane)
	{
		int height = 0;
		
		height += newHeight;
		
		switch(fromPane)
		{
		case 1:
			height += productPropertiesPane.getHeight(); height += productRelationsPane.getHeight();
			break;
		case 2:
			height += productCategoriesPane.getHeight(); height += productRelationsPane.getHeight();
			break;
		case 3:
			height += productPropertiesPane.getHeight(); height += productCategoriesPane.getHeight();
		}
		
		selectionPaneAnchorPane.setPrefHeight(height);
		selectionPaneAnchorPane.setMinHeight(height);
		selectionPaneAnchorPane.setMaxHeight(height);
	}
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = EVENT METHODS - CLICK SIDEMENU ITEMS
	
	private void treeItemOnClick(TreeItem ti)
	{
		initCategoryEditor();
		loadCategoryEditor(ti.getValue().toString());
	}
	
	
	private void treeClicked(MouseEvent e){
		System.out.println(e);
	}
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = EVENT METHODS - OPEN SIDEMENUS
	
	
	// when you click on product categories, this method is called
	public void openProductCategories(MouseEvent me)
	{
		double newHeight = calculateHeight(1);

		if(!productCategoriesTreeView.isVisible())
		{
			productCategoriesTreeView.setVisible(true);
		} else {
			newHeight = 40.0;
			productCategoriesTreeView.setVisible(false);
		}
		
		productCategoriesPane.setPrefHeight(newHeight);
		productCategoriesPane.setMinHeight(newHeight);
		productCategoriesPane.setMaxHeight(newHeight);
		
		productPropertiesPane.setLayoutY(newHeight);
		
		productRelationsPane.setLayoutY(newHeight+productPropertiesPane.getHeight());
		
		printYAndHeight();
		setSelectionPaneAnchorPaneHeight(newHeight, 1);
	}
	
	
	// when you click on product properties, this method is called
	public void openProductProperties(MouseEvent me)
	{
		double newHeight = calculateHeight(2);
		
		if(!productPropertiesListView.isVisible())
		{
			productPropertiesListView.setVisible(true);
		} else {
			newHeight = 40.0;
			productPropertiesListView.setVisible(false);
		}
			
		productPropertiesPane.setPrefHeight(newHeight);
		productPropertiesPane.setMinHeight(newHeight);
		productPropertiesPane.setMaxHeight(newHeight);
			
		productRelationsPane.setLayoutY(productCategoriesPane.getHeight()+newHeight);
		
		printYAndHeight();
		setSelectionPaneAnchorPaneHeight(newHeight, 2);
	}
	
	
	// when you click on product relations, this method is called
	public void openProductRelations(MouseEvent me)
	{
		double newHeight = calculateHeight(3);
		
		if(!productRelationsListView.isVisible())
		{
			productRelationsListView.setVisible(true);
		} else {
			newHeight = 40.0;
			productRelationsListView.setVisible(false);
		}
			
		productRelationsPane.setPrefHeight(newHeight);
		productRelationsPane.setMinHeight(newHeight);
		productRelationsPane.setMaxHeight(newHeight);
		
		printYAndHeight();
		
		setSelectionPaneAnchorPaneHeight(newHeight, 3);
	}
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = INITIALIZING EDITORS
	
	private void initCategoryEditor()
	{
		
	}
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = LOADING EDITORS
	
	private void loadCategoryEditor(String uri)
	{
		
	}
	
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = LOADING CONTENT - SIDEMENU


	// loads the name of the categories and puts them into the TreeView of the product categories
	public void loadCategories()
	{
		log.info("loadCategories()");
		
		// initializing variables
		TreeView tv = productCategoriesTreeView;
		
		
		// Adding the Names of the ConceptSchemes as roots of the TreeView
		String[] conceptSchemeNames = ProductFactory.getConceptSchemeAsStringArray();
		
		// will built the Tree only if there is a conceptScheme
		if(conceptSchemeNames.length != 0)
		{
		
			for(int i = 0; i < conceptSchemeNames.length; i++)
			{
				System.out.println("#"+i+" ConceptSchemeLabel: "+conceptSchemeNames[i]);
				
				TreeItem root = new TreeItem();
				root.setValue(conceptSchemeNames[i]);
				root.setExpanded(true);
				tv.setRoot(root);
			}
			
			// Iterate through all the root children and just print if they have narrower
			String[] conArr = ProductFactory.getConceptURIsOfConceptScheme(tv.getRoot().getValue().toString());
			List<String> conList = Arrays.asList(conArr);
			ListIterator<String> conIte = conList.listIterator();
		
			while(conIte.hasNext())
			{
				productCategoriesTreeViewNumElements++;
				
				String uri = conIte.next();
				boolean hasNarrower = ModelFacade.hasNarrower(uri);
				TreeItem ti = new TreeItem();
				ti.setValue(uri);
				
				log.info("> skos:narrower "+uri);
				log.info("> "+hasNarrower);
				
				if(hasNarrower)
				{
					//tv.getRoot().getChildren().add(addChilds(tv.getRoot(), ti, ""));
					ti.setExpanded(true);
					addChilds(ti, "");
				} else {
					ti.setValue(ModelFacade.getLiteralByConcept(ti.getValue().toString()));
				}
				
				tv.getRoot().getChildren().add(ti);
			}
			StmtIterator stmti = ModelFacade.getNarrowerModel("http://rdf.getting-started.net/ontology/Camera").listStatements();
		}
	}
	
	
	// loading the product properties when tab is loaded
	private void loadProperties()
	{
		ListView<String> pp = productPropertiesListView;
		ObservableList<String> items = FXCollections.observableArrayList();
		//items.add(arg0)
		
		pp.setItems(items);
		
		pp.getChildrenUnmodifiable();
	}
	
	
	private void loadRelations()
	{
		
	}
	
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = CALCULATIONS
	
	
	private double calculateHeight(int num)
	{
		switch(num)
		{
		case 1:
			return calculateCategoriesHeight();
		case 2:
			return calculatePropertiesHeight();
		case 3:
			return calculateRelationsHeight();
		}
		return 0.0;
	}
	
	
	private double calculateCategoriesHeight()
	{
		log.info("calculateCategoriesHeight");
		
		double height;
		double childs = (double) ProductCategorySettingsController.productCategoriesTreeViewNumElements;
		
		log.info("Number of TreeView Elements: "+productCategoriesTreeViewNumElements+" + 1 (root)");
		
		//height = ((childs) * 23.0) + 10.0 + 50.0;
		height = ((childs) * 23.0)+ 20.0;
		
		log.info("Calculated height: "+height);
		
		return height;
		//return 400;
	}
	
	
	private double calculatePropertiesHeight()
	{
		return 200.0;
	}
	
	
	private double calculateRelationsHeight()
	{
		return 150.0;
	}
	
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = OTHER METHODS

	
	// adds all narrower individuums of a treeItem to it, or something like that...
	public static void addChilds(TreeItem root, String pre) {
		
		root.setExpanded(true);
		
		productCategoriesTreeViewNumElements++;
		
		String rootURI = root.getValue().toString();
		
		log.info(pre+"> I am "+root);
		
		if(ModelFacade.hasNarrower(rootURI))
		{
			log.info(pre+"> > I have children");
			
			Model model = ModelFacade.getNarrowerModel(rootURI);
			
			StmtIterator childs = model.listStatements();
			ModelFacade.printModel(model, "-----------> ");
			
			while(childs.hasNext())
			{
				Statement childChild = childs.nextStatement();
				TreeItem child = new TreeItem();
				child.setValue(childChild.getObject().toString());
				
				log.info(pre+"> > > One Child is "+child);
				
				root.getChildren().add(child);
				
				log.info(pre+"> > > I added it to me: "+root);
				
				log.info(pre+"> > > I check him");
				
				addChilds(child, pre+"> > > ");
			}
			
			log.info(pre+"> I have no more childs");
			
			ListIterator<TreeItem> it;
			it = root.getChildren().listIterator();
			
			log.info(pre+"> My Childs are now:");
			
			while(it.hasNext())
			{
				TreeItem ti = it.next();
				log.info(pre+"> "+ti);
			}
			
		} else {
			log.info(pre+"> I have no childs");
		}

		root.setValue(ModelFacade.getLiteralByConcept(root.getValue().toString()));

	}
	
	private void printYAndHeight()
	{
		System.out.println("Categories: Y = "+productCategoriesPane.getLayoutY()+", H = "+productCategoriesPane.getHeight());
		System.out.println("Properties: Y = "+productPropertiesPane.getLayoutY()+", H = "+productPropertiesPane.getHeight());
		System.out.println("Relations:  Y = "+productRelationsPane.getLayoutY()+", H = "+productRelationsPane.getHeight()+"\n");
	}
	


}
