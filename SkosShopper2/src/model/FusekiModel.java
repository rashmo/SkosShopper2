package model;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpRequestFactory;
import org.apache.http.impl.io.HttpRequestWriter;
import org.apache.jena.*;
import org.apache.jena.atlas.lib.FileOps;
import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.migrate.GraphLoadUtils;
import org.apache.jena.fuseki.server.DatasetRef;
import org.apache.jena.fuseki.server.FusekiConfig;
import org.apache.jena.fuseki.server.SPARQLServer;
import org.apache.jena.fuseki.server.ServerConfig;
import org.apache.jena.fuseki.servlets.HttpAction;
import org.apache.jena.fuseki.servlets.HttpServletResponseTracker;
import org.apache.jena.web.DatasetGraphAccessor;
import org.apache.log4j.PropertyConfigurator;



















import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.util.Context;

import exceptions.fuseki_exceptions.NoDatasetAccessorException;
import exceptions.fuseki_exceptions.NoDatasetGraphException;
import exceptions.fuseki_exceptions.NoPagesDirException;
import exceptions.fuseki_exceptions.NoServerConfigException;

public class FusekiModel {
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = DEFINITION OF VARIABLES AND OBJECTS
	
		
	private static DatasetAccessor datasetAccessor;			// DatasetAccessor - used to access RDF-files
	private static ServerConfig serverConfig;					// Server configuration file
	private static DatasetGraph datasetGraph;					// Default dataset graph
	private static SPARQLServer sparqlServer;

	
	private static String dataset;
	private static String pageDirPath;
	private static String logPropertyPath;
	
	
	private static boolean serverStatus;
	private static boolean configured;
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = DE-/CONSTRUCTOR AND INITIALIZATION METHODS
	
	// constructor
	public FusekiModel()
	{
		FusekiModel.serverStatus = false;
		FusekiModel.configured = false;
		
		
		if(!FusekiModel.configured) {
			try {
				FusekiModel.configureServer();
			} catch (NoPagesDirException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	// making the init configurations
	private static void configureServer() throws NoPagesDirException
	{
		FusekiModel.dataset = "/ds";
		FusekiModel.pageDirPath = "./fuseki/pages";
		FusekiModel.logPropertyPath = "./fuseki/log4j.properties";
		
		PropertyConfigurator.configureAndWatch(FusekiModel.logPropertyPath);
		
		FusekiModel.datasetGraph = DatasetGraphFactory.createMemFixed();
		FusekiModel.datasetAccessor = DatasetAccessorFactory.create(FusekiModel.datasetGraph);
		FusekiModel.serverConfig = FusekiConfig.configure("./fuseki/config.ttl");
		FusekiModel.serverConfig.pages = FusekiModel.pageDirPath;

		
		if(! FileOps.exists(FusekiModel.serverConfig.pages))
			throw new NoPagesDirException("No pages directory path set.");
		
		if(FusekiModel.getDatasetGraph() == null)
			try {
				throw new NoDatasetGraphException("No default DatasetGraph created.");
			} catch (NoDatasetGraphException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		if(FusekiModel.getServerConf() == null)
			try {
				throw new NoServerConfigException("No server configuration file.");
			} catch (NoServerConfigException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		FusekiModel.sparqlServer = new SPARQLServer(FusekiModel.serverConfig);
		FusekiModel.configured = true;
	}
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = SIMPLE GETTER & SETTER
	
	
	// = = = = = = = = = = getter
	
	// return datasetAccessor
	public static DatasetAccessor getDatasetAccessor() throws NoDatasetAccessorException {
		if(FusekiModel.datasetAccessor == null)
			throw new NoDatasetAccessorException("No DatasetAccessor created.");
		return FusekiModel.datasetAccessor;
	}
	
	public static ServerConfig getServerConf() { return FusekiModel.serverConfig;	}
	public static DatasetGraph getDatasetGraph() { return FusekiModel.datasetGraph; }
	
	public static boolean getServerStatus() { return serverStatus; }
	
	
	// = = = = = = = = = = setter
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = START AND STOP METHODS
	
	// EXXTERN - starts the sparql (fuseki) server
	public static void startStopFuseki() throws NoDatasetGraphException, NoServerConfigException
	{
		if(!FusekiModel.configured) {
			try {
				FusekiModel.configureServer();
			} catch (NoPagesDirException e) {
				e.printStackTrace();
			}
		}
		
		boolean checkActive = FusekiModel.checkActiveServer();
		
		
		
		
		if(checkActive) {
			FusekiModel.stopServer();
		} else if(!checkActive) {
			FusekiModel.startServer();
		}
		
	}

	
	// INTERN - starts the sparql (fuseki) server
	private static void startServer() throws NoDatasetGraphException, NoServerConfigException {
		System.out.println("Start Sever...");
		
		FusekiModel.sparqlServer.start();
		
		FusekiModel.serverStatus = true;
		System.out.println("Server started!");
		
	}
	
	
	// INTERN - stops the sparql (fuseki) server
	private static void stopServer() {
		System.out.println("Stop Sever...");
		
		FusekiModel.sparqlServer.stop();
		FusekiModel.serverStatus = false;
		
		System.out.println("Server stopped!");
	}
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = CHECKING METHODS
	
	public static boolean checkActiveServer()
	{
		if(!FusekiModel.configured) {
			try {
				FusekiModel.configureServer();
			} catch (NoPagesDirException e) {
				e.printStackTrace();
			}
		}
		
		boolean check;
		check = false;
		
		
		String serviceEndpoint = "http://localhost:3030";
		String queryString = "ASK {}";
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.sparqlService(serviceEndpoint, query);
		
		try {
	        ResultSet rs = qe.execSelect();
	        if ( rs.hasNext() ) {
	            // show the result, more can be done here
	            System.out.println(">>> "+ResultSetFormatter.asText(rs));
	        }
	    } 
	    catch(Exception e) { 
	        System.out.println(">>>-- "+e.getMessage());
	        if(e.getMessage().contains("Endpoint returned"))
	        	check = true;
	        
	        if(e.getMessage().contains("Unexpected error"))
	        	check = false;
	    }
	    finally {
	        qe.close();
	    }
	    System.out.println("\nall done.");
		
		System.out.println("---"+FusekiModel.sparqlServer.getServer());
		System.out.println("---"+FusekiModel.datasetAccessor);
		System.out.println("---"+"Active Server: "+check);
		
		return check;
	}
	public static String sendSparQLQuery(String Query){
		Query query = QueryFactory.create(Query);
		QueryExecution qe = QueryExecutionFactory.create ( Query, ModelFacade.getAllTriples());
		ResultSet rs = qe.execSelect();
		
		//ResultSetFormatter.out(System.out, query, m);
		String output;
		//ResultSetFormatter.out(System.out, rs, query);
		output = ResultSetFormatter.asText(rs);

		return output;
	}
	
	
	
	// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = COMPLEX GETTER & SETTER
	

}

