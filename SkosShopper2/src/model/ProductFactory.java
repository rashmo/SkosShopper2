package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import controller.SkosEditorController;
import controller.TriplesShowController.TripleOwn;

public class ProductFactory {
	
	public static final Logger log = Logger.getLogger(ProductFactory.class);
	private static boolean firstLoaded;
	
	
	public static String[] getCreatableProductsAsString()
	{
		// Triple is Statement
		// Object is RDFNode
		// Predicate is Property
		// Subject is Resource
		
		/* from concept to label

		 	skos/core#Concept | rdf-syntax-ns#type | skos/core#Bag
		 	
		 	skos/core#BagLabel | un#prefLabel | skos/core#Bag
		 	
		 	Bag^^http...XMLSchema#string | un#literalForm | core#BagLabel
		 */
		
		Model modelAllTriples = ModelFacade.getAllTriples();
		
		Model modelSkosConcepts = ModelFacade.getTriplesBySkosConcept(modelAllTriples);
		
		Model modelConceptLabels = ModelFacade.getLabelsByConcept(modelAllTriples, modelSkosConcepts);
		
		Model modelLabelLiterals = ModelFacade.getLiteralOfPrefLabel(modelConceptLabels, modelAllTriples);
		
		StmtIterator litIt = modelLabelLiterals.listStatements();
		
		final Set<String> productNames = new HashSet<String>();
		
		while(litIt.hasNext())
		{
			Statement productName = litIt.nextStatement();
			productNames.add(StringUtils.substringBefore(productName.getObject().toString(), "^^"));
		}
		
		String[] result = productNames.toArray(new String[productNames.size()]);
		Arrays.sort(result);
		
		//-------------- TEST
		
		//Model test = ModelFacade.getModelByPredicateAndOther("#type", "#Concept", ModelFacade.getAllTriples(), 2);
		
		//System.out.println("test: "+test.toString());
		
		//-------------------
		
		return result;

	}
	
	
	public static String[] getConceptSchemeAsStringArray()
	{
		log.info("getConceptSchemeAsStringArray() START");
		
		Model modelConceptSchemes = ModelFacade.getModelOfConceptSchemes(ModelFacade.getAllTriples());

		Model modelConceptSchemesLabels = ModelFacade.getLabelsByConceptScheme(ModelFacade.getAllTriples(), modelConceptSchemes);
		ModelFacade.printModel(modelConceptSchemesLabels, "ProductFactory().getConceptSchemeAsStringArray()#1");					// 32
		
		Model modelLabelLiterals = ModelFacade.getLiteralOfPrefLabel(modelConceptSchemesLabels);		// 40
		ModelFacade.printModel(modelLabelLiterals, "ProductFactory().getConceptSchemeAsStringArray()#2");
		
		String[] result = splitLiteral(modelLabelLiterals);
		
		if(result != null && result.length != 0)
			log.info("ConceptScheme[0] Name: "+result[0]);
		else
			log.info("ConceptScheme[] = null");
		
		
		log.info("getConceptSchemeAsStringArray END");
		
		return result;
	}
	
	
	public static String[] getConceptsOfConceptScheme(String scheme)
	{
		Model modelConcepts = ModelFacade.getConceptsOfConceptScheme(scheme);
		ModelFacade.printModel(modelConcepts, "getConceptsOfConceptScheme");
		
		
		Model modelConceptLabels = ModelFacade.getLabelsByConcept(modelConcepts);
		
		Model modelLabelLiterals = ModelFacade.getLiteralOfPrefLabel(modelConceptLabels);
		
		ModelFacade.printModel(modelLabelLiterals, "getConceptsOfConceptScheme() -> modelLabelLiterals");
		
		StmtIterator litIt = modelLabelLiterals.listStatements();
		
		final Set<String> productNames = new HashSet<String>();
		
		while(litIt.hasNext())
		{
			Statement productName = litIt.nextStatement();
			productNames.add(StringUtils.substringBefore(productName.getObject().toString(), "^^"));
		}
		
		String[] result = productNames.toArray(new String[productNames.size()]);
		Arrays.sort(result);
		
		return result;
	}
	
	
	public static String[] getConceptURIsOfConceptScheme(String scheme)
	{
		Model modelConcepts = ModelFacade.getConceptsOfConceptScheme(scheme);
			
		StmtIterator conIte = modelConcepts.listStatements();
		
		final Set<String> labelURIs = new HashSet<String>();
		
		while(conIte.hasNext())
		{
			Statement labelName = conIte.nextStatement();
			labelURIs.add(labelName.getSubject().toString());
		}
		
		String[] result = labelURIs.toArray(new String[labelURIs.size()]);
		Arrays.sort(result);
		
		return result;
	}
	
	
	
	public static String[] splitLiteral(Model model)
	{
		boolean logging = false;
		
		String[] result;
		
		StmtIterator stmti = model.listStatements();
		
		Set<String> resultSet = new HashSet<String>();
		
		while(stmti.hasNext())
		{
			Statement stmt = stmti.nextStatement();
			
			if(logging) log.info("splitLiteral() - splitting :"+stmt.getObject());
			if(logging) log.info("splitLiteral() ... of      :"+stmt.getSubject());
			if(logging) log.info("splitLiteral() ... results :"+StringUtils.substringBefore(stmt.getObject().toString(), "^^"));
			
			
			if(stmt.getObject().toString().contains("^^"));
			resultSet.add(StringUtils.substringBefore(stmt.getObject().toString(), "^^"));
		}
		
		result = resultSet.toArray(new String[resultSet.size()]);
		Arrays.sort(result);
		
		return result;
	}
	
	
	
	public static String[] getCreatableProductsAsURI()
	{
		final Set<String> productNames = ModelFacade.getSkosConceptURIs();
		String[] result = productNames.toArray(new String[productNames.size()]);
		Arrays.sort(result);
		return result;
	}
	
	

}
