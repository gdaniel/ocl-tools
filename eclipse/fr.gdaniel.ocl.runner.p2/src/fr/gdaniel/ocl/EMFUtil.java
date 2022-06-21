package fr.gdaniel.ocl;

import java.io.File;
import java.util.List;
import java.util.stream.StreamSupport;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class EMFUtil {
	
	public static ResourceSet createResourceSet() {
		ResourceSet rSet = new ResourceSetImpl();
		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		return rSet;
	}
	
	public static Resource loadResource(String location, ResourceSet rSet) {
		return rSet.getResource(URI.createURI(location), true);
	}
	
	public static Resource loadResource(File file, ResourceSet rSet) {
		return rSet.getResource(URI.createFileURI(file.getAbsolutePath()), true);
	}
	
	public static List<EPackage> getMetamodelPackages(Resource resource) {
		Iterable<EObject> it = () -> resource.getAllContents();
		return StreamSupport.stream(it.spliterator(), false)
			.filter(e -> EcorePackage.eINSTANCE.getEPackage().isInstance(e))
			.map(e -> (EPackage) e)
			.toList();
	}
	
	public static void registerMetamodelPackages(Resource resource) {
		getMetamodelPackages(resource).forEach(ePackage -> EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage));
	}

}
