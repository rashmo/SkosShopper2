package model;

import java.util.ArrayList;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class ServerImporter{


	public static Model model;
	public static DatasetAccessor ds;
	public static OntDocumentManager mgr;
	public static OntModelSpec spec;
	public static  String serviceURI = "";
	public static Map<String, String> uriMap;
	public static ArrayList<String> graphList;
	public static String graphURI;
	
	public ServerImporter() {
		model = ModelFactory.createDefaultModel();
		mgr = new OntDocumentManager();
		spec = new OntModelSpec( OntModelSpec.OWL_DL_MEM);
		spec.setDocumentManager(mgr);
	}
	
	public static boolean importNamedGraph(String grphURI) {
		try {
			graphURI = grphURI;
			model = ds.getModel(grphURI);
			uriMap = model.getNsPrefixMap();
			return true;
		} catch(Exception e) {
		}
		return false;
	}
	
	public void importDefaultGraph() {
		try {
			model = ds.getModel();
		} catch(Exception e) {
		}
	}

	public void setServiceURI(String uri) {
		serviceURI = uri;
		ds = DatasetAccessorFactory.createHTTP(serviceURI);
	}
	
	// Use this method when you want to write back and update a model to server
	public static boolean updateModelOfServer() {
		try {
			model = ModelFacadeTEST.ontModel.getBaseModel();
			model.write(System.out);
			ds.add(graphURI, model);
			return true;
		} catch(Exception e) {
		}
		return false;
	}
	
	// Use this method when you want to write back and replace a model to server
	public static boolean replaceModelOfServer() {
		try {
			model = ModelFacadeTEST.ontModel.getBaseModel();
			ds.putModel(model);
			return true;
		} catch(Exception e) {
		}
		return false;
	}
	
	// Use this method to enter an alternative url location of a file
	public void addAltEntry(String destination, String alternativePath) {
		mgr.addAltEntry(destination, alternativePath);
	}
	
	public boolean queryServerGraphs() {
		try {
			// Tricky query because of different server
			String servURI = serviceURI;
			Query graphQuery = null;
			graphList = new ArrayList<String>();
			if(servURI.toLowerCase().contains("data")) {
				servURI = servURI.replaceAll("data", "sparql");
				graphQuery = QueryFactory.create("SELECT DISTINCT ?g WHERE { GRAPH ?g { } }");
			} else {
				graphQuery = QueryFactory.create("SELECT DISTINCT ?g WHERE { GRAPH ?g { ?x ?y ?z } }");
			}
			QueryEngineHTTP qeHttp = QueryExecutionFactory.createServiceRequest(servURI, graphQuery);
			ResultSet results = qeHttp.execSelect();
			
			while (results.hasNext()) {
				Resource individual = results.next().getResource("?g");
				if (individual.isURIResource()) {
					graphList.add(individual.toString());
					System.out.println(individual.toString());
				}
			}
			return true;
		} catch(Exception e) {
		}
		return false;
	}
}
