package graphtest;

import static graphtest.EMFUtil.loadResource;
import static graphtest.EMFUtil.registerMetamodelPackages;
import static graphtest.EMFUtil.createResourceSet;

import java.util.stream.StreamSupport;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ocl.pivot.ExpressionInOCL;
import org.eclipse.ocl.pivot.utilities.OCL;
import org.eclipse.ocl.pivot.utilities.ParserException;
import org.eclipse.ocl.xtext.essentialocl.EssentialOCLStandaloneSetup;

public class AppInlineOCL {

	public static void main(String[] args) {
		
		ResourceSet rSet = createResourceSet();
		// Args
		String eClassifierUri = "//Node";
		String constraint = "self.name <> null and self.name.size() > 0";
		String metamodelFileUri = "model/graph.ecore";
		Resource metamodelResource = loadResource(metamodelFileUri, rSet);
		registerMetamodelPackages(metamodelResource);
		String modelFileUri = "model/graph-example.xmi";
		Resource modelResource = loadResource(modelFileUri, rSet);
		
		Object metamodelObject = metamodelResource.getEObject(eClassifierUri);
		if(metamodelObject == null) {
			System.err.println("Cannot find the eClassifier " + eClassifierUri);
			System.exit(-1);
		} else if(!(metamodelObject instanceof EClassifier)) {
			System.err.println("Identifier " + eClassifierUri + " does not correspond to an EClassifier (found " + metamodelObject.getClass() + ")");
			System.exit(-1);
		}
		EClassifier eClassifier = (EClassifier) metamodelObject;
		
		EssentialOCLStandaloneSetup.doSetup();
		
		OCL ocl = OCL.newInstance(OCL.CLASS_PATH);
		ExpressionInOCL invariant;
		try {
			invariant = ocl.createInvariant(eClassifier, constraint);
		} catch(ParserException e) {
			throw new RuntimeException(e);
		}
		
		Iterable<EObject> i = () -> modelResource.getAllContents();
		StreamSupport.stream(i.spliterator(), false)
			.filter(e -> eClassifier.isInstance(e))
			.forEach(e -> System.out.println(e.toString() + ": " + ocl.check(e, invariant)));
	}
	
	
	
}
