package model;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.fuseki.migrate.GraphLoadUtils;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import javafx.beans.property.SimpleStringProperty;

import com.hp.hpl.jena.rdf.model.Statement;

import controller.ProductCategorySettingsController;
import exceptions.fuseki_exceptions.NoDatasetAccessorException;

public class ModelFacade {
	
	/*
	 * 	
	 * Triple is Statement
	 * Object is RDFNode
	 * Predicate is Property
	 * Subject is Resource
	 *
	 * from concept to label
	 *
	 *	skos/core#Concept | rdf-syntax-ns#type | skos/core#Bag
	 *	
	 *	skos/core#BagLabel | un#prefLabel | skos/core#Bag
	 *	
	 *	Bag^^http...XMLSchema#string | un#literalForm | core#BagLabel
	 * 
	 */
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = VARIABLES
	
	
	public static final Logger log = Logger.getLogger(ModelFacade.class);
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = GENERAL METHODS
	
	
	public static Model getAllTriples()
	{
		boolean logging = false;
		
		if(logging) log.info("getAllTriples() START");
		String s;
		
		GraphLoadUtils glu = new GraphLoadUtils();
		Model model;
		
		try {
			model = FusekiModel.getDatasetAccessor().getModel();
			Graph graph = model.getGraph();
			if(logging) log.info("getAllTriples() END");
			return model;
		} catch (NoDatasetAccessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(logging) log.info("getAllTriples() END");
			return null;
		}

	}
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = CONCEPT METHODS
	
	public static Model getConceptsOfConceptScheme(String scheme)
	{
		Model resultModel = ModelFactory.createDefaultModel();
		
		Model allModel = getAllTriples();
		
		StmtIterator stmti = allModel.listStatements();
		
		while(stmti.hasNext())
		{
			Statement stmt = stmti.nextStatement();
			
			//log.info("----------");			
			//log.info("check: "+stmt.getSubject().toString()+", "+stmt.getPredicate().toString()+", "+stmt.getObject().toString());
			
			if(stmt.getObject().toString().contains(scheme)) {
				//log.info(">>> MATCH WITH "+scheme);
				//log.info(">>> Predicate: "+stmt.getPredicate().toString());
				//log.info(">>> Object:    "+stmt.getObject().toString());
				
				if(stmt.getPredicate().toString().contains("inScheme"))
					resultModel.add(stmt);

			} else {
				//log.info("NO MATCH WITH "+scheme);
			}
		}
		
		return resultModel;
	}
	
	public static String[] getPrefLablesOfSkosConcepts()
	{
		return null;
	}
	
	
	
	public static Model getTriplesBySkosConcept(Model model)
	{
		log.info("getTriplesBySkosConcept() START");
		//final Set<Statement> foundTriples = new HashSet<Statement>();
		Model result = ModelFactory.createDefaultModel();
		
		
		StmtIterator stmti = model.listStatements();

		// get all Triples (Statements) which are a skos:concept
		while(stmti.hasNext())
		{
			Statement stmt = stmti.nextStatement();
			
			RDFNode o = stmt.getObject();

			if(o.toString().contains("skos/core#Concept"))
			{
				if(stmt.getPredicate().toString().contains("#type"))
				{
					//objects.add(stmt.getSubject().toString().substring(stmt.getSubject().toString().lastIndexOf("#")+1));
					result.add(stmt);
					log.info("found skos#concept: "+stmt.getSubject().toString());
					
				}
			}

		}
		
		log.info("getTriplesBySkosConcept END");
		return result;
	}
	
	
	
	public static Set<String> getSkosConceptURIs()
	{
		log.info("getSkosConceptURIs() START");
		
		Model m = ModelFactory.createDefaultModel();
		m = getTriplesBySkosConcept(getAllTriples());
		
		StmtIterator stmti = m.listStatements();
		
		Set<String> uris = new HashSet<String>();
		
		while(stmti.hasNext())
		{
			Statement stmt = stmti.nextStatement();
			uris.add(stmt.getSubject().toString());
		}
		
		log.info("getSkosConceptURIs() END");
		return uris;
	}
	
	
	
	public static Model getLabelsByConcept(Model modelSkosConcepts)
	{
		return getLabelsByConcept(getAllTriples(), modelSkosConcepts);
	}
	
	
	
	public static Model getLabelsByConcept(Model modelAllTriples, Model modelSkosConcepts)
	{
		boolean logging = false;
		
		if(logging) log.info("getLabelsByConcept() START");
		
		Model result = ModelFactory.createDefaultModel();
		
		StmtIterator allIt = modelAllTriples.listStatements();
		
		while(allIt.hasNext())
		{
			StmtIterator concIt = modelSkosConcepts.listStatements();
			
			Statement allItem = allIt.next();
			
			while(concIt.hasNext())
			{
				Statement concItem = concIt.next();
				
				if(allItem.getSubject().equals(concItem.getSubject()) && allItem.getPredicate().toString().contains("#prefLabel"))
				{
					result.add(allItem);
					if(logging) log.info("found skos-xl#prefLabel: "+allItem.getObject().toString());
				}
			}
		}
		
		if(logging) log.info("getLabelsByConcept() END");
		return result;
	}

	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = CONCEPT SCHEME METHODS
	
	
	
	public static Model getModelOfConceptSchemes(Model model)
	{
		log.info("getModelOfConceptSchemes() START");
		//final Set<Statement> foundTriples = new HashSet<Statement>();
		Model resultModel = ModelFactory.createDefaultModel();
		
		
		StmtIterator stmti = model.listStatements();

		// get all Triples (Statements) which are a skos:concept
		while(stmti.hasNext())
		{
			Statement stmt = stmti.nextStatement();
			
			RDFNode o = stmt.getObject();

			if(o.toString().contains("skos/core#ConceptScheme"))
			{
				if(stmt.getPredicate().toString().contains("#type"))
				{
					//objects.add(stmt.getSubject().toString().substring(stmt.getSubject().toString().lastIndexOf("#")+1));
					resultModel.add(stmt);
					log.info("getModelOfConceptSchemes() found skos#concept: "+stmt.getSubject().toString());
					
				}
			}

		}
		
		printModel(resultModel, "ModelFacade.getModelOfConceptSchemes()");
		
		log.info("getModelOfConceptSchemes() END");
		return resultModel;
	}
	
	
	
	public static Model getLabelsByConceptScheme(Model modelAllTriples, Model modelSkosConceptSchemes)
	{
		log.info("getLabelsByConcept() START");
		
		Model result = ModelFactory.createDefaultModel();
		
		StmtIterator allIt = modelAllTriples.listStatements();
		
		while(allIt.hasNext())
		{
			StmtIterator concIt = modelSkosConceptSchemes.listStatements();
			
			Statement allItem = allIt.next();
			
			while(concIt.hasNext())
			{
				Statement concItem = concIt.next();
				
				if(allItem.getSubject().equals(concItem.getSubject()) && allItem.getPredicate().toString().contains("#prefLabel"))
				{
					result.add(allItem);
					log.info("found skos-xl#prefLabel: "+allItem.getObject().toString());
				}
			}
		}
		
		log.info("getLabelsByConcept() END");
		return result;
	}
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = LABEL METHODS
	
	
	
	public static Model getLiteralOfPrefLabel(Model modelConceptLabels)
	{
		return getLiteralOfPrefLabel(getAllTriples(), modelConceptLabels);
	}
	
	
	public static Model getLiteralOfPrefLabel(Model modelAllTriples, Model modelConceptLabels)
	{
		boolean logging = false;
		
		if(logging) log.info("getLiteralOfPrefLabel() START");
		if(logging) log.info("getLiteralOfPrefLabel() Print Model to check:");
		//printModel(modelConceptLabels, "ModelFacade.getLiteralOfPrefLabel() --- ");
		
		Model result = ModelFactory.createDefaultModel();
		
		StmtIterator allIt = modelAllTriples.listStatements();
		
		while(allIt.hasNext())
		{
			Statement allItem = allIt.next();
			
			StmtIterator prefIt = modelConceptLabels.listStatements();
			
			while(prefIt.hasNext())
			{
				Statement prefItem = prefIt.next();
				
				//log.info(">>> "+allItem.getSubject().toString()+" ?> "+prefItem.getObject().toString());
				
				if(allItem.getSubject().toString().equals(prefItem.getObject().toString()))
				{
					//log.info("-------> MATCH");
					//log.info("-------> Predicate: "+allItem.getPredicate().toString());
					
					if(allItem.getPredicate().toString().contains("#literalForm"))
					{
						//log.info("--------------> MATCH");
						result.add(allItem);
					}
				}
			}
		}
		
		if(logging) log.info("getLiteralOfPrefLabel() END");
		return result;
	}
		
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = OTHER METHODS
	
	
	
	public static String getLiteralByConcept(String uri)
	{
		boolean logging = false;
		
		if(logging) log.info("getLiteralByConcept() START");
		
		String name = "ERROR";
		Model model = ModelFactory.createDefaultModel();
		Model all = getAllTriples();
		
		StmtIterator stmti = all.listStatements();
		
		while(stmti.hasNext())
		{
			Statement stmt = stmti.nextStatement();
			
			if(stmt.getObject().toString().contains("#Concept") && stmt.getSubject().toString().contains(uri))
			{
				if(logging) log.info(">>>> "+stmt);
				model.add(stmt);
				break;
			}
		}
		
		model = getLabelsByConcept(model);
		model = getLiteralOfPrefLabel(model);
		
		if(logging) printModel(model, "FOUND LITERALS: ");
		
		String[] temp = ProductFactory.splitLiteral(model);
		
		if(temp.length > 0) {
			name = ProductFactory.splitLiteral(model)[0];
		}
		
		if(logging) log.info("getLiteralByConcept() END");
		
		return name;
	}
	
	
	public static boolean hasNarrower(String uri)
	{
		boolean logging = false;
		
		if(logging) log.info("=====> "+uri);
		
		Model model = getNarrowerModel(uri);
		boolean result = false;
		
		if(model.size() > 0)
			return true;
		else
			return false;
		
	}
	
	
	public static Model getNarrowerModel(String uri)
	{
		boolean logging = true;
		Model resultModel = ModelFactory.createDefaultModel();
		
		StmtIterator stmti = ModelFacade.getAllTriples().listStatements();
		
		if(logging) log.info("getNarrowerModel() - Looking for skos:narrower Match with "+uri);
		
		while(stmti.hasNext())
		{
			Statement stmt = stmti.nextStatement();
			
			//if(logging) log.info("getNarrowerModel() - Predicate:"+stmt.getPredicate().toString());
			
			if(stmt.getPredicate().toString().endsWith("#narrower"))
			{
				if(logging) log.info("getNarrowerModel() -------> contains #narrower");
				
				if(logging) log.info("getNarrowerModel() -------> Subject: "+stmt.getSubject().toString());
				
				if(stmt.getSubject().toString().equals(uri))
				{
					if(logging) log.info("getNarrowerModel() --------------> MATCH WITH "+uri);
					if(logging) log.info("getNarrowerModel() -------------------> FOUND "+stmt.getObject().toString());
					
					resultModel.add(stmt);
				}
			}
		}
		
		//if(logging) printModel(resultModel, "ModelFacade.getNarrowerModel()");
		
		return resultModel;
	}
	

	
	public static void printModel(Model model, String identifier)
	{
		StmtIterator stmti = model.listStatements();
		
		log.info(identifier+" PRINT MODEL >>>");
		int i = 0;
		
		while(stmti.hasNext())
		{
			Statement stmt = stmti.nextStatement();
			
			log.info("#"+i+"\t\t"+stmt.getSubject());
			log.info("#"+i+"\t\t"+stmt.getPredicate());
			log.info("#"+i+"\t\t"+stmt.getObject());
			log.info("---");
			
			i++;
		}
		
		log.info(identifier+" PRINT MODEL <<<");
	}
	
	


}
