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


	private static Model model;
	private static DatasetAccessor ds;
	private static OntDocumentManager mgr;
	private static OntModelSpec spec;
	public static  String serviceURI = "";
	private static String graphURI;
	public static Map<String, String> uriMap;
	public static ArrayList<String> graphList;
	
	public ServerImporter() {
		model = ModelFactory.createDefaultModel();
		mgr = new OntDocumentManager();
		spec = new OntModelSpec( OntModelSpec.OWL_DL_MEM );
		spec.setDocumentManager(mgr);
	}
	
	public static boolean importNamedGraph(String grphURI) {
		try {
			model = ds.getModel(grphURI);
			uriMap = model.getNsPrefixMap();
			return true;
		} catch(Exception e) {
		}
		return false;
	}
	
	public void importDefaultGraph() {
		model = ds.getModel();
	}

	public void setServiceURI(String uri) {
		serviceURI = uri;
		ds = DatasetAccessorFactory.createHTTP(serviceURI);
	}
	
	public static void setGraphURI(String grphURI) {
		graphURI = grphURI;
	}
	
	public static OntModelSpec getOntModelSpec() {
		return spec;
	}
	
	public static OntDocumentManager getOntDocMgr() {
		return mgr;
	}
	
	public static Model getModel() {
		return model;
	}

	// Use this method when you want to write back and update a model to server
	public void updateModelOfServer() {
		model = ModelFacadeTEST.ontModel;
		ds.add(model);
	}
	
	// Use this method when you want to write back and replace a model to server
	public void replaceModelOfServer() {
		model = ModelFacadeTEST.ontModel;
		ds.putModel(model);
	}
	
	// Use this method to enter an alternative url location of a file
	public void addAltEntry(String destination, String alternativePath) {
		mgr.addAltEntry(destination, alternativePath);
	}
	
	public boolean queryServerGraphs() {
		try {
			// Tricky query because of different server
			String servURI = serviceURI;
			graphList = new ArrayList<String>();
			if(servURI.toLowerCase().contains("data")) {
				servURI = servURI.replaceAll("data", "sparql");
			}
			Query graphQuery = QueryFactory.create("SELECT DISTINCT ?g WHERE { GRAPH ?g { } }");
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
