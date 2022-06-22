package fr.gdaniel.ocl.runner;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * Utility methods to manipulate EMF resources and models.
 */
public class EMFUtil {

	/**
	 * The logger for this class.
	 */
	private static final Logger log = Logger.getLogger(EMFUtil.class);

	/**
	 * Creates a {@link ResourceSet} with a resource factory registry initialized
	 * for generic and {@code ecore} files.
	 * <p>
	 * {@code ecore} resources are created with the
	 * {@link EcoreResourceFactoryImpl}. Generic resources (i.e. resources with an
	 * extension different from {@code .ecore}) are created with the
	 * {@link XMIResourceFactoryImpl}. This assumes that every non-ecore resource is
	 * an XMI file, which may not be true in some cases (e.g. when the model is
	 * defined with an Xtext DSL).
	 * 
	 * @return the {@link ResourceSet}
	 */
	public static ResourceSet createResourceSet() {
		ResourceSet rSet = new ResourceSetImpl();
		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		return rSet;
	}

	/**
	 * Returns the list of {@link EPackage} instances from the provided
	 * {@code resource}.
	 * <p>
	 * This method iterates the content of the provided resource and returns all the
	 * EObjects that are instances of Ecore's {@link EPackage}.
	 * 
	 * @param resource the {@link Resource} to retrieve the {@link EPackage}
	 *                 instances of
	 * @return the list of {@link EPackage}s
	 * 
	 * @see #registerMetamodelPackages(Resource) to register all the
	 *      {@link EPackage}s from a given {@link Resource}
	 */
	public static List<EPackage> getMetamodelPackages(Resource resource) {
		Iterable<EObject> it = () -> resource.getAllContents();
		return StreamSupport.stream(it.spliterator(), false)
				.filter(e -> EcorePackage.eINSTANCE.getEPackage().isInstance(e)).map(e -> (EPackage) e).toList();
	}

	/**
	 * Registers all the {@link EPackage}s of the provided {@code resource} in the
	 * global registry.
	 * <p>
	 * This method iterates the content of the provided resource and register every
	 * {@link EPackage} it contains. Instances with
	 * {@code EPackage#getNsURI() == null} are omitted since NsURI is required to
	 * add a package to the registry.
	 * 
	 * @param resource the {@link Resource} to register the {@link EPackage}s from
	 */
	public static void registerMetamodelPackages(Resource resource) {
		getMetamodelPackages(resource).forEach(ePackage -> {
			if (ePackage.getNsURI() != null) {
				EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
			} else {
				log.warn(MessageFormat.format("Cannot register ePackage {0}: nsURI not defined", ePackage.getName()));
			}
		});
	}

}
