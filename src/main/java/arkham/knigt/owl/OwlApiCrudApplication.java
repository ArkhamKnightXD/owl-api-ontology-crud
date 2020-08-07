package arkham.knigt.owl;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.io.File;
import java.util.Set;

@SpringBootApplication
public class OwlApiCrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(OwlApiCrudApplication.class, args);
	}


	@Bean
	public CommandLineRunner run() {
		return args -> {

			OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();

			final IRI ontologyIRI = IRI.create("http://www.semanticweb.org/luis_/ontologies/2020/6/untitled-ontology-2");
			// Cargo la ontologia que esta en el archivo
			File ontologyFile = new File("diccionario.owl");

			IRI ontologySaveIRI = IRI.create(ontologyFile);

			OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();

			OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);


			//Aqui puedo agregar clases nuevas que la api las llama axiomas
			OWLClass classA = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Persona"));
			OWLClass classB = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Animal"));

			OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(classA, classB);
			// add the axiom to the ontology the axioms are the classes.
			AddAxiom addAxiom = new AddAxiom(ontology, axiom);

			// We now use the manager to apply the change
			//ontologyManager.applyChange(addAxiom);

			// remove the axiom from the ontology
			//RemoveAxiom removeAxiom = new RemoveAxiom(ontology, axiom);
			//ontologyManager.applyChange(removeAxiom);


			System.out.println(ontology.getClassesInSignature());

			// Add individuals
			// We want to state that matthew has a father who is peter.
			OWLIndividual alex = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#Alex"));
			OWLIndividual pedro = dataFactory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#Pedro"));

			// Aqui defino el object property que servira para relacionar las clases
			OWLObjectProperty hasFather = dataFactory.getOWLObjectProperty(IRI.create(ontologyIRI + "#hasFather"));

			// Aqui indico la relacion que hay entre ambas clases por ejemplo alex --> hasFather --> pedro
			OWLObjectPropertyAssertionAxiom assertion = dataFactory.getOWLObjectPropertyAssertionAxiom(hasFather, alex, pedro);
			// Finally, add the axiom to our ontology and save
			AddAxiom addAxiomChange = new AddAxiom(ontology, assertion);
			ontologyManager.applyChange(addAxiomChange);

			// matthew is an instance of Person
			OWLClass fatherClass = dataFactory.getOWLClass(IRI.create(ontologyIRI + "#Persona"));
			OWLClassAssertionAxiom axiom1 = dataFactory.getOWLClassAssertionAxiom(fatherClass, alex);

			// Add this axiom to our ontology - with a convenience method
			ontologyManager.addAxiom(ontology, axiom1);


			//Add datatyproperties


			// save in RDF/XML
			ontologyManager.saveOntology(ontology, ontologySaveIRI);

			//Show all classes
			/*for (OWLClass classes : ontology.getClassesInSignature()) {
				// use the class for whatever purpose

				System.out.println(classes);
			}*/




			//Get and configure hermit reasoner
			/*OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
			ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();

			OWLReasonerConfiguration configuration = new SimpleConfiguration(progressMonitor);

			// create the reasoner instance, classify and compute inference
			OWLReasoner reasoner = reasonerFactory.createReasoner(ontology,configuration);

			reasoner.precomputeInferences(InferenceType.values());

			DefaultPrefixManager prefixManager = new DefaultPrefixManager(null,null, "http://www.semanticweb.org/luis_/ontologies/2020/6/untitled-ontology-2#");


			//Get all individuals
			OWLDataFactory factory = ontologyManager.getOWLDataFactory();

			OWLClass classes = factory.getOWLClass(IRI.create(prefixManager.getDefaultPrefix(), "letters"));

			NodeSet<OWLNamedIndividual> individualNodeSet = reasoner.getInstances(classes, false);

			Set<OWLNamedIndividual> individualSet = individualNodeSet.getFlattened();

			for (OWLNamedIndividual individual: individualSet) {

				System.out.println(prefixManager.getShortForm(individual));

			}*/

		};
	}
}
