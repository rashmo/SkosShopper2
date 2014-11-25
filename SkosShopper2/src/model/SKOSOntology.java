package model;

import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class SKOSOntology {
	/* SKOS Classes */
	private OntClass collection;
	private OntClass concept;
	private OntClass concept_scheme;
	private OntClass label;
	private OntClass ordered_collection;
	private OntClass list;

	/* OBJECT PROPERTIES */
	// SKOS-XL Label Properties
	private ObjectProperty alt_label_skosxl;
	private ObjectProperty pref_label_skosxl;
	private ObjectProperty hidden_label_skosxl;
	private ObjectProperty label_realtion_skosxl;

	// SKOS Relation Properties
	private ObjectProperty semantic_relation;
	private ObjectProperty broader_transitive;
	private ObjectProperty broader;
	private ObjectProperty broad_match;
	private ObjectProperty narrower_transitive;
	private ObjectProperty narrower;
	private ObjectProperty narrow_match;
	private ObjectProperty related;
	private ObjectProperty related_match;
	private ObjectProperty mapping_realtion;
	private ObjectProperty close_match;
	private ObjectProperty exact_match;

	// SKOS Collection Properties
	private ObjectProperty member;
	private ObjectProperty member_list;

	// SKOS Concept Scheme Properties
	private ObjectProperty has_topconcept;
	private ObjectProperty in_scheme;
	private ObjectProperty top_conceptof;

	/* DATA PROPERTIES */
	// SKOS-XL Data Properties
	private DatatypeProperty literal_form;

	// Notation
	private DatatypeProperty notation;

	/* ANNOTATION PROPERTIES */
	private AnnotationProperty note;
	private AnnotationProperty change_note;
	private AnnotationProperty definition;
	private AnnotationProperty editorial_note;
	private AnnotationProperty example;
	private AnnotationProperty history_note;
	private AnnotationProperty scope_note;

	/* ONTOLOGY HEADER */
	private OntModel model;
	private Ontology ont;
	private String baseURI;
	private OntModelSpec spec;
	private Map<String, String> prefixMap;

	// basic prefixes
	private String sk = "http://www.w3.org/2004/02/skos/core";
	private String skxl = "http://www.w3.org/2008/05/skos-xl";
	private String skos = "http://www.w3.org/2004/02/skos/core#";
	private String skos_xl = "http://www.w3.org/2008/05/skos-xl#";
	private String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private String owl = "http://www.w3.org/2002/07/owl#";
	private String xsd = "http://www.w3.org/2001/XMLSchema#";
	private Resource r;

	/* REASONER */

	// public ArrayList<String> con;
	public ObservableList<String> con;

	/**
	 * This method iterates through a skos ontology model in order to find all concept individuals
	 * @param  concept  A list which will contain all concept individuals found
	 */
	public void getConcepts(ObservableList<String> concepts) {
		ExtendedIterator<Individual> individuals = model.listIndividuals();
		while (individuals.hasNext()) {
			Individual i = individuals.next();
			if(this.concept.getURI().toString().equals(i.getRDFType().toString())) {
				concepts.add(i.getLocalName());
			}
		}
	}

	/**
	 * This method iterates through a skos ontology model in order to find all concept scheme individuals
	 * @param  conceptSchemes  A list which will contain all concept scheme individuals found
	 */
	public void getConceptSchemes(ObservableList<String> conceptSchemes) {
		ExtendedIterator<Individual> individuals = model.listIndividuals();
		while (individuals.hasNext()) {
			Individual i = individuals.next();
			if(this.concept_scheme.getURI().equals(i.getRDFType().toString())) {
				conceptSchemes.add(i.getLocalName());
			}
		}
	}

	/**
	 * This method iterates through a skos ontology model in order to find all collection individuals
	 * @param  col  A list which will contain all collection individuals found
	 */
	public void getCollections(ObservableList<TreeItem<String>> col) {
		ExtendedIterator<Individual> individuals = model.listIndividuals();
		while (individuals.hasNext()) {
			Individual i = individuals.next();
			if(this.collection.getURI().equals(i.getRDFType().toString())) {
				col.add(new TreeItem<String>(i.getLocalName()));
			}
		}
	}

	/**
	 * This method iterates through a skos ontology model in order to find all ordered collection individuals
	 * @param  ord_col  A list which will contain all ordered collection individuals found
	 */
	public void getOrderedCollections(ObservableList<TreeItem<String>> ord_col) {
		ExtendedIterator<Individual> individuals = model.listIndividuals();
		while (individuals.hasNext()) {
			Individual i = individuals.next();
			if(this.ordered_collection.getURI().equals(i.getRDFType().toString())) {
				ord_col.add(new TreeItem<String>(i.getLocalName()));
			}
		}
	}

	/**
	 * This method iterates through a skos ontology model in order to find all label individuals
	 * @param  labels  A list which will contain all label individual found
	 */
	public void getLabels(ObservableList<String> labels) {
		ExtendedIterator<Individual> individuals = model.listIndividuals();
		while (individuals.hasNext()) {
			Individual i = individuals.next();
			if(this.label.getURI().equals(i.getRDFType().toString())) {
				labels.add(i.getLocalName());
			}
		}
	}

	/**
	 * This method is used to delete single individual
	 * @param  individual  name of the individual that should be deleted
	 */
	public void deleteIndividual(String individual) {
		/** NOT IMPLEMENTED YET **/
	}
	
	/**
	 * This method is used to create a skos ontology from scratch
	 * @param  ont_uri  Base URI of the new skos ontology
	 * @param  ont_spec  Integer to define the Ontology model specification
	 */
	public void createNewOntology(String ont_uri) {
		// set the base uri
		baseURI = ont_uri + "#";
		// Add base uri to ontology-header
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		ont = model.createOntology(baseURI);

		// Add skos-xl Import to ontology-header
		ont.addImport(model.createResource(skxl));

		// Add skos and skos-xl prefixes to ontology-header
		model.setNsPrefix("skos", skos);
		model.setNsPrefix("skos-xl", skos_xl);
		model.setNsPrefix("rdf", rdf);
		model.setNsPrefix("owl", owl);

		// Add prefixes to prefix-map
		prefixMap = model.getNsPrefixMap();
		
		// Add base uri to ontology-header
		ont = model.createOntology(baseURI);
		r = model.createResource(owl + "NamedIndividual");

		createAll();
	}

	/**
	 * This method is used to load a skos ontology from physical storage system
	 * @param  path_name  naem of the file path
	 */
	public void getSKOSOntologyFromLocal(String path_name) {
		Model m = FileManager.get().loadModel(path_name);
		model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, m);

		baseURI = model.getNsPrefixURI("");
		
		// Add skos-xl Import to ontology-header
		ont = model.createOntology(baseURI);
		ont.addImport(model.createResource(skxl));
		model.setNsPrefix("skos", skos);
		// check if Online File contains prefix for skos, skos-xl, owl and rdf
		model.setNsPrefix("skos", skos);
		model.setNsPrefix("skos-xl", skos_xl);
		model.setNsPrefix("rdf", rdf);
		model.setNsPrefix("owl", owl);
		
		// Add prefixes to prefix-map
		prefixMap = model.getNsPrefixMap();
		
		createAll();
	}

	/**
	 * This method is used to load a skos ontology from http
	 * @param  uri  URI of the skos file
	 */
	public void getSKOSOntologyFromServer(String uri) {
		// Set base uri
		baseURI = uri;
		
		// read Online SKOS into model
		model = ModelFactory.createOntologyModel();
		model.read(baseURI);

		// Add skos-xl Import to ontology-header
		ont = model.createOntology(baseURI);
		ont.addImport(model.createResource(skxl));

		// check if Online File contains prefix for skos, skos-xl, owl and rdf
		model.setNsPrefix("skos", skos);
		model.setNsPrefix("skos-xl", skos_xl);
		model.setNsPrefix("rdf", rdf);
		model.setNsPrefix("owl", owl);

		// Add prefixes to prefix-map
		prefixMap = model.getNsPrefixMap();
		createAll();
	}
	
	/**
	 * This method is used to retrieve a skos ontology from fuseki-server
	 * A new skos ontology will be created
	 */
	public void getSKOSFromFuseki() {
		String serviceURI = "http://localhost:3030/ds/data";
		DatasetAccessor ds = DatasetAccessorFactory.createHTTP(serviceURI);
		Model m = ModelFactory.createDefaultModel();
		m = ds.getModel();
		
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, m);
		baseURI = model.getNsPrefixURI("");
		
		// Add skos-xl Import to ontology-header
		ont = model.createOntology(baseURI);
		ont.addImport(model.createResource(skxl));
		
		// check if Online File contains prefix for skos, skos-xl, owl and rdf
		model.setNsPrefix("skos", skos);
		model.setNsPrefix("skos-xl", skos_xl);
		model.setNsPrefix("rdf", rdf);
		model.setNsPrefix("owl", owl);
		
		// Add prefixes to prefix-map
		prefixMap = model.getNsPrefixMap();
		
		System.out.println(baseURI);
		createAll();
	}

	/**
	 * This method is used to save an ontology
	 */
	public void saveOntologyLocal() {
		/** NOT IMPLEMENTED YET **/
	}
	
	/**
	 * This method get ontology model
	 * @return  model  model
	 */
	public OntModel getModel() {
		return model;
	}
	
	/**
	 * This method sets a ontology model
	 * @param  model  Model that will be set
	 */
	public void setModel(OntModel model) {
		this.model = model;
	}
	
	public String getType(String individual) {
		String rdftype = model.getIndividual(individual).getRDFType().toString();
		if(rdftype != null)
			return rdftype;
		return null;
	}
	
	public boolean typeConcept(String type) {
		String r = model.getIndividual(type).getRDFType().toString();
		return (r.equals(skos + "Concept")) ? true : false;
	}
	
	public boolean typeConceptScheme(String type) {
		return (type == skos + "ConceptScheme") ? true : false;
	}
	
	public boolean typeCollection(String type) {
		return (type == skos + "Collection") ? true : false;
	}
	
	public boolean typeordCollection(String type) {
		return (type == skos + "OrderedCollection") ? true : false;
	}
	
	public boolean typeLabel(String type) {
		return (type == skos_xl + "Label") ? true : false;
	}
	
	/**
	 * This returns the base uri of a SKOS-Ontology
	 * @return  base uri
	 */
	public String getBaseURI() {
		System.out.println(baseURI);
		return baseURI;
	}

	/**
	 * This method creates all super-classes
	 */
	private void createSKOSClasses() {
		concept = model.createClass(skos + "Concept");
		concept_scheme = model.createClass(skos + "ConceptScheme");
		collection = model.createClass(skos + "Collection");
		ordered_collection = model.createClass(skos + "OrderedCollection");
		label = model.createClass(skos_xl + "Label");
		list = model.createClass(rdf + "List");
	}

	/**
	 * This method creates all super-objectproperties
	 */
	private void createSKOSObjectProperties() {
		// SKOS-XL Label Properties
		alt_label_skosxl = model.createObjectProperty(skos_xl + "altLabel");
		pref_label_skosxl = model.createObjectProperty(skos_xl + "prefLabel");
		hidden_label_skosxl = model.createObjectProperty(skos_xl
				+ "hiddenLabel");
		label_realtion_skosxl = model.createObjectProperty(skos_xl
				+ "labelRelation");

		// SKOS Relation Properties
		semantic_relation = model.createObjectProperty(skos
				+ "semanticRelation");
		broader_transitive = model.createObjectProperty(skos
				+ "broaderTransitive");
		broader = model.createObjectProperty(skos + "broader");
		broad_match = model.createObjectProperty(skos + "broadMatch");
		narrower_transitive = model.createObjectProperty(skos
				+ "narrowerTransitive");
		narrower = model.createObjectProperty(skos + "narrower");
		narrow_match = model.createObjectProperty(skos + "narrowMatch");
		related = model.createObjectProperty(skos + "related");
		related_match = model.createObjectProperty(skos + "relatedMatch");
		mapping_realtion = model.createObjectProperty(skos + "mappingRelation");
		close_match = model.createObjectProperty(skos + "closeMatch");
		exact_match = model.createObjectProperty(skos + "exactMatch");

		// SKOS Collection Properties
		member = model.createObjectProperty(skos + "member");
		member_list = model.createObjectProperty(skos + "memberList");

		// SKOS Concept Scheme Properties
		has_topconcept = model.createObjectProperty(skos + "hasTopConcept");
		in_scheme = model.createObjectProperty(skos + "inScheme");
		top_conceptof = model.createObjectProperty(skos + "topConceptOf");
	}

	/**
	 * This method creates all super-dataproperties
	 */
	private void createSKOSDataProperties() {
		// SKOS-XL Data Properties
		literal_form = model.createDatatypeProperty(skos_xl + "literalForm");

		// Notation
		notation = model.createDatatypeProperty(skos + "notation");
	}

	/**
	 * This method creates all super-annotationproperties
	 */
	private void createSKOSAnnotationProperties() {
		/* SKOS ANNOTATION PROPERTIES */
		note = model.createAnnotationProperty(skos + "note");
		change_note = model.createAnnotationProperty(skos + "changeNote");
		definition = model.createAnnotationProperty(skos + "definition");
		editorial_note = model
				.createAnnotationProperty(skos + "editorial_note");
		example = model.createAnnotationProperty(skos + "example");
		history_note = model.createAnnotationProperty(skos + "historyNote");
		scope_note = model.createAnnotationProperty(skos + "scopeNote");
	}

	/**
	 * This helping-method is needed to create all super-classes, super-properties, super-dataproperties
	 * and super-annotationproperties
	 */
	private void createAll() {
		createSKOSClasses();
		createSKOSObjectProperties();
		createSKOSDataProperties();
		createSKOSAnnotationProperties();
	}

	/**
	 * This method checks whether an individual is available in an skos-ontology
	 * @param  individual  name of the individual
	 * @return  true or false
	 */
	public boolean contains(String individual) {
		return (model.getIndividual(baseURI + individual) != null) ? true
				: false;
	}

	/**
	 * This method creates an instance of the super-class skos:Concept
	 * @param  conceptName  concept name of the instance of skos:Concept
	 */
	public void createConcept(String conceptName) {
		concept.createIndividual(baseURI + conceptName);
		//setType(conceptName);
	}

	/**
	 * This method creates an instance of the super-class skos:Collection
	 * @param  collectionName  collection name of the instance of skos:Collection
	 */
	public void createCollection(String collectionName) {
		collection.createIndividual(baseURI + collectionName);
		//setType(collectionName);
	}

	/**
	 * This method creates an instance of the super-class skos:ConceptScheme
	 * @param  conceptSchemeName  concept scheme name of the instance of skos:ConceptScheme
	 */
	public void createConceptScheme(String conceptSchemeName) {
		concept_scheme.createIndividual(baseURI + conceptSchemeName);
		//setType(conceptSchemeName);
	}

	/**
	 * This method creates an instance of the super-class skos:OrderedCollection
	 * @param  orderedCollection  ordered collection name of the instance of skos:OrderedCollection
	 */
	public void createOrderedCollection(String orderedCollection) {
		ordered_collection.createIndividual(baseURI + orderedCollection);
		//setType(orderedCollection);
	}

	/**
	 * This method creates an instance of the super-class skosxl:Label
	 * @param  label  label name of the instance of skosxl:Label
	 */
	public void createLabel(String label) {
		// Create an Individiual (instance of skos-xl:Label) and assert to
		// Ontology
		this.label.createIndividual(baseURI + label);
		//setType(label);
	}
	
	/**
	 * This method creates an instance of the super-class skos:List
	 * @param  label  label name of the instance of skosxl:Label
	 */
	public void createList(String list) {
		// Create an Individiual (instance of skos:List) and assert to Ontology
		this.list.createIndividual(baseURI + list);
		//setType(list);
	}
	
	/**
	 * This method sets the RDF-Type of any individual to owl:NamedIndividual
	 * @param  label  label name of the instance of skosxl:Label
	 */
	public void setType(String indiv) {
		model.getIndividual(indiv).setRDFType(r);
	}
	
	public void objPropAssertion(String a, String b, int choice) {
		switch (choice) {
		case 0:
			// A CAN BE COLLECTION / ORD COLLECTION
			// B CAN BE COLLECTION / ORD COLLECTION / CONCEPT
			model.getIndividual(baseURI + a).addProperty(this.member,
					model.getIndividual(baseURI + b));
			break;
		case 1:
			// A CAN BE COLLECTION / ORD COLLECTION
			// B CAN BE
			model.getIndividual(baseURI + a).addProperty(member_list, 
					model.getIndividual(baseURI + b));
			break;
		case 2:
			// EVERYTHING CAN HAVE A LABEL
			// CHECK FOR ALL CLASSES
			if(!contains(b))
				createLabel(b);
			model.getIndividual(baseURI + a).addProperty(
					pref_label_skosxl, model.getIndividual(baseURI + b));
			break;
		case 3:
			// EVERYTHING CAN HAVE A LABEL
			// CHECK FOR ALL CLASSES
			if(!contains(b))
				createLabel(b);
			model.getIndividual(baseURI + a).addProperty(alt_label_skosxl,
					model.getIndividual(baseURI + b));
			break;
		case 4:
			// EVERYTHING CAN HAVE A LABEL
			// CHECK FOR ALL CLASSES
			if(!contains(b))
				createLabel(b);
			model.getIndividual(baseURI + a).addProperty(hidden_label_skosxl,
					model.getIndividual(baseURI + b));
			break;
		case 5:
			// ONLY CONCEPT SCHEMES CAN HAVE CONCEPT AS TOP CONCEPT
			// CHECK ONLY FOR CONCEPT SCHEME
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(
					has_topconcept, model.getIndividual(baseURI + b));
			break;
		case 6:
			// ONLY CONCEPTS CAN BE TOP CONCEPT OF CONCEPT SCHEMES
			// CHECK ONLY FOR CONCEPTS
			if(!contains(b))
				createConceptScheme(b);
			model.getIndividual(baseURI + a).addProperty(top_conceptof,
					model.getIndividual(baseURI + b));
			break;
		case 7:
			// DO NOT NEED CHECK
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(broader,
					model.getIndividual(baseURI + b));
			break;
		case 8:
			// DO NOT NEED CHECK
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(narrower,
					model.getIndividual(baseURI + b));
			break;
		case 9:
			// DO NOT NEED CHECK
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(broad_match,
					model.getIndividual(baseURI + b));
			break;
		case 10:
			// DO NOT NEED CHECK
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(narrow_match,
					model.getIndividual(baseURI + b));
			break;
		case 11:
			// DO NOT NEED CHECK
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(related,
					model.getIndividual(baseURI + b));
			break;
		case 12:
			// DO NOT NEED CHECK
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(exact_match,
					model.getIndividual(baseURI + b));
			break;
		case 13:
			// DO NOT NEED CHECK
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(close_match,
					model.getIndividual(baseURI + b));
			break;
		case 14:
			// DO NOT NEED CHECK
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(related_match,
					model.getIndividual(baseURI + b));
			break;
		case 15:
			// CAN ONLY BE LABELS
			// DO NOT NEED CHECK
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(
					label_realtion_skosxl, model.getIndividual(baseURI + b));
			break;
		case 16:
			// B CAN ONLY BE A CONCEPT SCHEME
			// EVERYTHING CAN BE IN SCHEME WITH CONCEPT SCHEMES
			// CHECK ONLY FOR CONCEPT SCHEMES
			if(!contains(b))
				createConceptScheme(b);
			model.getIndividual(baseURI + a).addProperty(in_scheme,
					model.getIndividual(baseURI + b));
			break;
		case 17:
			// NOT USED TO MAKE ASSERTIONS
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(broader_transitive,
					model.getIndividual(baseURI + b));
			break;
		case 18:
			// NOT USED DIRECTLY
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(
					narrower_transitive, model.getIndividual(baseURI + b));
			break;
		case 19:
			// SHOULD NOT BE USED DIRECTLY
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(semantic_relation,
					model.getIndividual(baseURI + b));
			break;
		case 20:
			// DO NOT NEED CHECK
			if(!contains(a))
				createConcept(a);
			if(!contains(b))
				createConcept(b);
			model.getIndividual(baseURI + a).addProperty(mapping_realtion,
					model.getIndividual(baseURI + b));
			break;
		default:
			break;
		}
	}

	// skos:memberList
	public void hasMemberList(String individual1, String individual2) {
		// not implemented yet
	}

	/**
	 * Creates a literal for a label individual. Either with a language tag and without
	 * datatype, or with a datatype and no language tag. 
	 *
	 * @param  label  the label which will be linked to a literal
	 * @param  literal  the literal for the label
	 * @param  lang  language tag of the literal
	 * @param  choice  Needed to decide which datatype the literal should have
	 */
	public void hasLiteralForm(String label, String literal, String lang,
			int choice) {
		Literal litt = null;

		if (!contains(label)) {
			this.label.createIndividual(baseURI + label);
		}

		if (choice < 0) {
			Literal lit = ResourceFactory.createLangLiteral(literal, lang);
			model.getIndividual(baseURI + label).addLiteral(literal_form, lit);
		} else {

			switch (choice) {
			case 0:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDanyURI);
				break;
			case 1:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDbase64Binary);
				break;
			case 2:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDboolean);
				break;
			case 3:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDbyte);
				break;
			case 4:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDdate);
				break;
			case 5:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDdateTime);
				break;
			case 6:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDdecimal);
				break;
			case 7:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDdouble);
				break;
			case 8:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDduration);
				break;
			case 9:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDENTITY);
				break;
			case 10:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDfloat);
				break;
			case 11:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDgDay);
				break;
			case 12:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDgMonth);
				break;
			case 13:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDgMonthDay);
				break;
			case 14:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDgYear);
				break;
			case 15:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDgYearMonth);
				break;
			case 16:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDhexBinary);
				break;
			case 17:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDID);
				break;
			case 18:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDIDREF);
				break;
			case 19:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDint);
				break;
			case 20:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDinteger);
				break;
			case 21:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDlanguage);
				break;
			case 22:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDlong);
				break;
			case 23:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDName);
				break;
			case 24:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDNCName);
				break;
			case 25:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDnegativeInteger);
				break;
			case 26:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDNMTOKEN);
				break;
			case 27:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDnonNegativeInteger);
				break;
			case 28:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDnonPositiveInteger);
				break;
			case 29:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDnormalizedString);
				break;
			case 30:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDNOTATION);
				break;
			case 31:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDpositiveInteger);
				break;
			case 32:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDQName);
				break;
			case 33:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDshort);
				break;
			case 34:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDstring);
				break;
			case 35:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDtime);
				break;
			case 36:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDtoken);
				break;
			case 37:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDunsignedByte);
				break;
			case 38:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDunsignedInt);
				break;
			case 39:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDunsignedLong);
				break;
			case 40:
				litt = ResourceFactory.createTypedLiteral(literal,
						XSDDatatype.XSDunsignedShort);
				break;
			default:
				break;
			}
			model.getIndividual(baseURI + label).addLiteral(literal_form, litt);
		}
	}

	/**
	 * Creates an annotation for an individual. 
	 *
	 * @param  individual  The individual which will have an annotation linked to
	 * @param  annotation  Annotation for the individual
	 * @param  annotation_form  The type of annotation (e.g. Note, Change Note etc.)
	 * @param  lang  language tag for the annotation
	 * @param  choice  Needed to decide which datatype the annotation should have
	 */
	public void hasNote(String individual, String annotation,
			int annotation_form, String lang, int choice) {
		Literal anno2 = null;

		if (!contains(individual)) {
			this.label.createIndividual(baseURI + individual);
		}

		if (choice < 0) {
			Literal anno1 = ResourceFactory.createLangLiteral(annotation, lang);

			switch (annotation_form) {
			case 0:
				model.getIndividual(baseURI + individual).addLiteral(note, anno1);
				break;
			case 1:
				model.getIndividual(baseURI + individual).addLiteral(change_note,
						anno1);
				break;
			case 2:
				model.getIndividual(baseURI + individual).addLiteral(definition,
						anno1);
				break;
			case 3:
				model.getIndividual(baseURI + individual).addLiteral(editorial_note,
						anno1);
				break;
			case 4:
				model.getIndividual(baseURI + individual).addLiteral(example, anno1);
				break;
			case 5:
				model.getIndividual(baseURI + individual).addLiteral(history_note,
						anno1);
				break;
			case 6:
				model.getIndividual(baseURI + individual).addLiteral(scope_note,
						anno1);
				break;
			default:
				break;
			}

		} else {
			switch (choice) {
			case 0:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDanyURI);
				break;
			case 1:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDbase64Binary);
				break;
			case 2:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDboolean);
				break;
			case 3:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDbyte);
				break;
			case 4:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDdate);
				break;
			case 5:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDdateTime);
				break;
			case 6:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDdecimal);
				break;
			case 7:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDdouble);
				break;
			case 8:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDduration);
				break;
			case 9:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDENTITY);
				break;
			case 10:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDfloat);
				break;
			case 11:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDgDay);
				break;
			case 12:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDgMonth);
				break;
			case 13:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDgMonthDay);
				break;
			case 14:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDgYear);
				break;
			case 15:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDgYearMonth);
				break;
			case 16:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDhexBinary);
				break;
			case 17:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDID);
				break;
			case 18:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDIDREF);
				break;
			case 19:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDint);
				break;
			case 20:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDinteger);
				break;
			case 21:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDlanguage);
				break;
			case 22:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDlong);
				break;
			case 23:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDName);
				break;
			case 24:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDNCName);
				break;
			case 25:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDnegativeInteger);
				break;
			case 26:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDNMTOKEN);
				break;
			case 27:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDnonNegativeInteger);
				break;
			case 28:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDnonPositiveInteger);
				break;
			case 29:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDnormalizedString);
				break;
			case 30:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDNOTATION);
				break;
			case 31:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDpositiveInteger);
				break;
			case 32:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDQName);
				break;
			case 33:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDshort);
				break;
			case 34:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDstring);
				break;
			case 35:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDtime);
				break;
			case 36:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDtoken);
				break;
			case 37:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDunsignedByte);
				break;
			case 38:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDunsignedInt);
				break;
			case 39:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDunsignedLong);
				break;
			case 40:
				anno2 = ResourceFactory.createTypedLiteral(annotation,
						XSDDatatype.XSDunsignedShort);
				break;
			default:
				break;
			}
			switch (annotation_form) {
			case 0:
				model.getIndividual(baseURI + individual).addLiteral(note, anno2);
				break;
			case 1:
				model.getIndividual(baseURI + individual).addLiteral(change_note,
						anno2);
				break;
			case 2:
				model.getIndividual(baseURI + individual).addLiteral(definition,
						anno2);
				break;
			case 3:
				model.getIndividual(baseURI + individual).addLiteral(editorial_note,
						anno2);
				break;
			case 4:
				model.getIndividual(baseURI + individual).addLiteral(example, anno2);
				break;
			case 5:
				model.getIndividual(baseURI + individual).addLiteral(history_note,
						anno2);
				break;
			case 6:
				model.getIndividual(baseURI + individual).addLiteral(scope_note,
						anno2);
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * Method to query the rdf:type of an individual
	 *
	 * @param  uri  The individual which will have an annotation linked to
	 * @param  indiv  Annotation for the individual
	 */
	public Boolean query(String uri, String indiv) {
		String sparql = "prefix base: <" + uri + "> "
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "prefix skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "prefix skos-xl: <http://www.w3.org/2008/05/skos-xl#>"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>" + "SELECT *"
				+ "WHERE {base:" + indiv + " skos:member ?z}";
		try {
			Query query = QueryFactory.create(sparql);
			QueryExecution qe = QueryExecutionFactory.create(query, model);
			ResultSet res = qe.execSelect();
			QuerySolution result = res.next();
			Resource individual = result.getResource("?z");
			System.out.println(individual);
			qe.close();
			if(individual.toString().equals("http://www.w3.org/2004/02/skos/core#Concept"))
				return true;
			if(individual.toString().equals("http://www.w3.org/2004/02/skos/core#Collection"))
				return true;
			if(individual.toString().equals("http://www.w3.org/2004/02/skos/core#OrderedCollection"))
				return true;
		} catch (QueryExceptionHTTP e) {
			System.out.println(e);
		}
		return false;
	}
	
	/** SPARQL QUERIES **/
	
	public void getAllMember(String uri, String indiv) {
		String sparql = "prefix base: <" + uri + "> "
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "prefix skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "prefix skos-xl: <http://www.w3.org/2008/05/skos-xl#>"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>" + "SELECT *"
				+ "WHERE {base:" + indiv + " skos:member ?z}";
		try {
			Query query = QueryFactory.create(sparql);
			QueryExecution qe = QueryExecutionFactory.create(query, model);
			ResultSet res = qe.execSelect();
			QuerySolution result = res.next();
			Resource individual = result.getResource("?z");
			System.out.println(individual);
			qe.close();
		} catch (QueryExceptionHTTP e) {
			System.out.println(e);
		}
	}
	
	public void removeIndiv(String uri, String indiv) {
	      String remove= 	"prefix base: <" + uri + ">\n"+
	  			"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
	  			"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
	  			"prefix skos: <http://www.w3.org/2004/02/skos/core#>\n" +
	  			"prefix skos-xl: <http://www.w3.org/2008/05/skos-xl#>\n" +
	  			"prefix owl: <http://www.w3.org/2002/07/owl#>\n" +
	  			"prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
	  			"delete where{ base:" + indiv + " ?y ?z }\n";
	  			//"WHERE { base:DigitalCamera rdf:type skos:Concept }";
	      UpdateAction.parseExecute( remove, model );
	      model.write(System.out);
	}
	
	public OutputStream printIndiv(String uri, String indiv) {
		OutputStream out = new ByteArrayOutputStream();
		String sparql = "prefix base: <" + uri + "> "
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "prefix skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "prefix skos-xl: <http://www.w3.org/2008/05/skos-xl#>"
				+"prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>" + "SELECT *"
				+ "WHERE {base:" + indiv + " ?y ?z}";
		try {
			Query query = QueryFactory.create(sparql);
			QueryExecution qe = QueryExecutionFactory.create(query, model);
			ResultSet res = qe.execSelect();
		    ResultSetFormatter.out(out, res, query);
		    qe.close();
		    return out;
		} catch (QueryExceptionHTTP e) {
			System.out.println(e);
		}
		return null;
	}
	
	public String getType(String uri, String indiv) {
		String sparql = "prefix base: <" + uri + "> "
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "prefix skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "prefix skos-xl: <http://www.w3.org/2008/05/skos-xl#>"
				+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>" + "SELECT *"
				+ "WHERE {base:" + indiv + " ?y ?z}";
		return sparql;
	}
	
	public Boolean getObjectType(String s) {
		if(s.equals(skos + "note") || s.equals(skos + "changeNote") || s.equals(skos + "definition") || 
				s.equals(skos + "editorial_note") || s.equals(skos + "example") || s.equals(skos + "historyNote") ||
				s.equals(skos + "scopeNote") || s.equals(skos_xl + "literalForm")) {
			return true;
		}
		return false;
	}
	
	public void queryIndiv(String uri, String indiv, ObservableList<TempSparql> list) {
		String sparql = "prefix base: <" + uri + ">\n"
				+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n"
				+ "prefix skos-xl: <http://www.w3.org/2008/05/skos-xl#>\n"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "SELECT *\n" + "WHERE {base:" + indiv + " ?y ?z}";
		try {
			Query query = QueryFactory.create(sparql);
			QueryExecution qe = QueryExecutionFactory.create(query, model);
			ResultSet res = QueryExecutionFactory.create(query, model)
					.execSelect();
			while (res.hasNext()) {
				QuerySolution result = res.next();
				Resource y = result.getResource("?y");
				if (getObjectType(y.getURI())) {
					System.out.println(y.getLocalName() + "  has  " + result.getLiteral("?z").getLexicalForm());
					list.add(new TempSparql(y.getLocalName(), result.getLiteral("?z").getLexicalForm()));
				} else {
					System.out.println(y.getLocalName() + "  has  " + result.getResource("?z").getLocalName());
					list.add(new TempSparql(y.getLocalName(), result.getResource("?z").getLocalName()));
				}
			}
			qe.close();
		} catch (QueryExceptionHTTP e) {
			System.out.println(e);
		}
	}
}
