package model;

import java.io.FileNotFoundException;

import org.coode.owlapi.rdf.model.RDFResourceNode;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

public class OntHeader {

	private OWLOntologyManager manager;
	private IRI ontologyIRI;
	private IRI sk;
	private IRI sk_xl;
	private OWLOntology ontology;
	private OWLDataFactory factory;
	private PrefixManager pm;
	private PrefixManager skos;
	private PrefixManager skos_xl;

	public OntHeader() throws OWLOntologyCreationException {
		createIRI();
		ontology = manager.createOntology(ontologyIRI);
		factory = manager.getOWLDataFactory();
		pm = new DefaultPrefixManager(ontologyIRI.toString());
		skos = new DefaultPrefixManager(sk.toString());
		skos_xl = new DefaultPrefixManager(sk_xl.toString());
	}

	public OWLOntologyManager getManager() {
		return manager;
	}

	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}

	public IRI getOntologyIRI() {
		return ontologyIRI;
	}

	public void setOntologyIRI(IRI ontologyIRI) {
		this.ontologyIRI = ontologyIRI;
	}

	public IRI getSk() {
		return sk;
	}

	public void setSk(IRI sk) {
		this.sk = sk;
	}

	public IRI getSk_xl() {
		return sk_xl;
	}

	public void setSk_xl(IRI sk_xl) {
		this.sk_xl = sk_xl;
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

	public OWLDataFactory getFactory() {
		return factory;
	}

	public void setFactory(OWLDataFactory factory) {
		this.factory = factory;
	}

	public PrefixManager getPm() {
		return pm;
	}

	public void setPm(PrefixManager pm) {
		this.pm = pm;
	}

	public PrefixManager getSkos() {
		return skos;
	}

	public void setSkos(PrefixManager skos) {
		this.skos = skos;
	}

	public PrefixManager getSkos_xl() {
		return skos_xl;
	}

	public void setSkos_xl(PrefixManager skos_xl) {
		this.skos_xl = skos_xl;
	}

	public void createIRI() {
		manager = OWLManager.createOWLOntologyManager();
		setOntologyIRI(IRI.create("http://example.com/skos/example/"));
		sk = IRI.create("http://www.w3.org/2004/02/skos/core#");
		sk_xl = IRI.create("http://www.w3.org/2008/05/skos-xl#");

	}
}
