package fr.gdaniel.ocl.runner;

import java.io.File;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ocl.pivot.utilities.OCL;
import org.eclipse.ocl.pivot.validation.ComposedEValidator;
import org.eclipse.ocl.xtext.completeocl.CompleteOCLStandaloneSetup;
import org.eclipse.ocl.xtext.completeocl.validation.CompleteOCLEObjectValidator;

public class OCLRunner {
	
	public OCLRunner() {
		
	}
	
	public Diagnostic validate(File metamodelFile, File modelFile, File constraintFile) {
		ResourceSet rSet = EMFUtil.createResourceSet();
		Resource metamodelResource = rSet.getResource(URI.createFileURI(metamodelFile.getAbsolutePath()), true);
		EMFUtil.registerMetamodelPackages(metamodelResource);
		Resource modelResource = rSet.getResource(URI.createFileURI(modelFile.getAbsolutePath()), true);
		CompleteOCLStandaloneSetup.doSetup();
		
		OCL ocl = OCL.newInstance(EPackage.Registry.INSTANCE);
		URI constraintUri = URI.createFileURI(constraintFile.getAbsolutePath());
		Resource asResource = ocl.parse(constraintUri);
		
		for(EPackage ePackage : EMFUtil.getMetamodelPackages(metamodelResource)) {
			ComposedEValidator newEValidator = ComposedEValidator.install(ePackage);
			newEValidator.addChild(new CompleteOCLEObjectValidator(ePackage, constraintUri));
		}
		
		ResourceDiagnostician diagnostician = new ResourceDiagnostician();
		Diagnostic diagnostics = diagnostician.validate(modelResource);
		return diagnostics;
	}

}
