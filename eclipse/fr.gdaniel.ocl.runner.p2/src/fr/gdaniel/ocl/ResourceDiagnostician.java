package fr.gdaniel.ocl;

import java.util.Map;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.ocl.pivot.internal.labels.LabelSubstitutionLabelProvider;

public class ResourceDiagnostician extends Diagnostician {

	@Override
	public Map<Object, Object> createDefaultContext() {
		Map<Object, Object> context = super.createDefaultContext();
		context.put(EValidator.SubstitutionLabelProvider.class, new LabelSubstitutionLabelProvider());
		return context;
	}

	public BasicDiagnostic createDefaultDiagnostic(Resource resource) {
		return new BasicDiagnostic(EObjectValidator.DIAGNOSTIC_SOURCE, 0,
				"Diagnosis",
				new Object[] { resource });
	}
	
	public Diagnostic validate(Resource resource) {
		BasicDiagnostic diagnostics = createDefaultDiagnostic(resource);
		Map<Object, Object> context = createDefaultContext();
		for(EObject eObject : resource.getContents()) {
			validate(eObject, diagnostics, context);
		}
		return diagnostics;
	}

}
