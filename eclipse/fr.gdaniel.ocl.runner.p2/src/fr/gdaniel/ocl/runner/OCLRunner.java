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

/**
 * A facade to run OCL queries and constraints over models.
 * <p>
 * This class aims to provide various ways to load metamodels, models, and
 * constraints and validate them.
 * <p>
 * Each {@code validate} call is state-less: the resources used to load the
 * models/constraints (e.g. {@link ResourceSet}) as well as the OCL instance
 * required to validate them are discarded after the execution to avoid
 * side-effects between calls.
 */
public class OCLRunner {

	/**
	 * Validates the provided model with the given constraints.
	 * <p>
	 * This method loads the metamodel, model, and constraints provided in the
	 * corresponding files, and setup an evaluation environment for them. The model
	 * has to be conform to the provided metamodel.
	 * 
	 * @param metamodelFile  the file containing the metamodel the constraints are
	 *                       defined on
	 * @param modelFile      the file containing the model to validate
	 * @param constraintFile the file containing the constraints to evaluate against
	 *                       the provided model
	 * @return the evaluation {@link Diagnostic}
	 */
	public Diagnostic validate(File metamodelFile, File modelFile, File constraintFile) {
		ResourceSet rSet = EMFUtil.createResourceSet();
		Resource metamodelResource = rSet.getResource(URI.createFileURI(metamodelFile.getAbsolutePath()), true);
		EMFUtil.registerMetamodelPackages(metamodelResource);
		Resource modelResource = rSet.getResource(URI.createFileURI(modelFile.getAbsolutePath()), true);
		CompleteOCLStandaloneSetup.doSetup();

		OCL ocl = OCL.newInstance(EPackage.Registry.INSTANCE);
		URI constraintUri = URI.createFileURI(constraintFile.getAbsolutePath());
		Resource asResource = ocl.parse(constraintUri);

		for (EPackage ePackage : EMFUtil.getMetamodelPackages(metamodelResource)) {
			ComposedEValidator newEValidator = ComposedEValidator.install(ePackage);
			newEValidator.addChild(new CompleteOCLEObjectValidator(ePackage, constraintUri));
		}

		ResourceDiagnostician diagnostician = new ResourceDiagnostician();
		Diagnostic diagnostics = diagnostician.validate(modelResource);
		return diagnostics;
	}

}
