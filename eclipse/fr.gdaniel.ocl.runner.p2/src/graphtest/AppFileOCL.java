package graphtest;

import static graphtest.EMFUtil.createResourceSet;
import static graphtest.EMFUtil.getMetamodelPackages;
import static graphtest.EMFUtil.loadResource;
import static graphtest.EMFUtil.registerMetamodelPackages;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ocl.pivot.utilities.OCL;
import org.eclipse.ocl.pivot.utilities.PivotUtil;
import org.eclipse.ocl.pivot.validation.ComposedEValidator;
import org.eclipse.ocl.xtext.completeocl.CompleteOCLStandaloneSetup;
import org.eclipse.ocl.xtext.completeocl.validation.CompleteOCLEObjectValidator;

public class AppFileOCL {

	public static void main(String[] args) {
		ResourceSet rSet = createResourceSet();
		// Args
		String constraintFileUri = "constraints/constraints.ocl";
		String metamodelFileUri = "model/graph.ecore";
		Resource metamodelResource = loadResource(metamodelFileUri, rSet);
		registerMetamodelPackages(metamodelResource);
		String modelFileUri = "model/graph-example.xmi";
		Resource modelResource = loadResource(modelFileUri, rSet);
		
		
		CompleteOCLStandaloneSetup.doSetup();
		
		OCL ocl = OCL.newInstance(EPackage.Registry.INSTANCE);
		ResourceSet resourceSet = ocl.getResourceSet();
		URI uri = URI.createURI(constraintFileUri);
		Resource asResource = ocl.parse(uri);
		
		for(EPackage ePackage : getMetamodelPackages(metamodelResource)) {
			ComposedEValidator newEValidator = ComposedEValidator.install(ePackage);
			newEValidator.addChild(new CompleteOCLEObjectValidator(ePackage, uri, ocl.getEnvironmentFactory()));
		}
		
		MyDiagnostician diagnostician = new MyDiagnostician();
		Diagnostic diag = diagnostician.validate(modelResource);
		if(diag.getSeverity() != Diagnostic.OK) {
			String formattedDiagnostics = PivotUtil.formatDiagnostics(diag, "\n");
			System.out.println("Validation: " + formattedDiagnostics);
		}
	}
	
}
