package fr.gdaniel.ocl;

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

import graphtest.EMFUtil;
import graphtest.MyDiagnostician;

public class OCLRunner {
	
	public OCLRunner() {
		
	}
	
	public Diagnostic checkConstraints(File metamodelFile, File modelFile, File constraintFile) {
		ResourceSet rSet = EMFUtil.createResourceSet();
		Resource metamodelResource = EMFUtil.loadResource(metamodelFile, rSet);
		EMFUtil.registerMetamodelPackages(metamodelResource);
		Resource modelResource = EMFUtil.loadResource(modelFile, rSet);
		CompleteOCLStandaloneSetup.doSetup();
		
		OCL ocl = OCL.newInstance(EPackage.Registry.INSTANCE);
		URI constraintUri = URI.createFileURI(constraintFile.getAbsolutePath());
		Resource asResource = ocl.parse(constraintUri);
		
		for(EPackage ePackage : EMFUtil.getMetamodelPackages(metamodelResource)) {
			ComposedEValidator newEValidator = ComposedEValidator.install(ePackage);
			newEValidator.addChild(new CompleteOCLEObjectValidator(ePackage, constraintUri));
		}
		
		MyDiagnostician diagnostician = new MyDiagnostician();
		Diagnostic diagnostics = diagnostician.validate(modelResource);
		return diagnostics;
	}

}
