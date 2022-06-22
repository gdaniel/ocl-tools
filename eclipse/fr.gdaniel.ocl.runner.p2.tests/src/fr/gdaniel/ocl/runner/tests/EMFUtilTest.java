package fr.gdaniel.ocl.runner.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import fr.gdaniel.ocl.runner.EMFUtil;

class EMFUtilTest {
	
	/**
	 * The factory used to create Ecore objects for testing.
	 */
	private EcoreFactory factory = EcoreFactory.eINSTANCE;

	@Test
	public void createResourceSetTest() {
		ResourceSet rSet = EMFUtil.createResourceSet();
		Map<String, Object> extensionMap = rSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
		assertTrue(extensionMap.containsKey("ecore"));
		assertInstanceOf(EcoreResourceFactoryImpl.class, extensionMap.get("ecore"));
		assertTrue(extensionMap.containsKey("*"));
		assertInstanceOf(XMIResourceFactoryImpl.class, extensionMap.get("*"));
	}
	
	@Test
	public void getMetamodelPackagesEmptyResource() {
		ResourceSet rSet = EMFUtil.createResourceSet();
		Resource resource = rSet.createResource(URI.createURI("test"));
		List<EPackage> ePackages = EMFUtil.getMetamodelPackages(resource);
		assertEquals(0, ePackages.size());
	}
	
	@Test
	public void getMetamodelPackagesResourceWithPackages() {
		ResourceSet rSet = EMFUtil.createResourceSet();
		Resource resource = rSet.createResource(URI.createURI("test"));
		EPackage ePackage1 = createEPackage("ePackage1", "ePackage1");
		EPackage ePackage2 = createEPackage("ePackage2", "ePackage2");
		resource.getContents().add(ePackage1);
		resource.getContents().add(ePackage2);
		List<EPackage> ePackages = EMFUtil.getMetamodelPackages(resource);
		assertTrue(ePackages.contains(ePackage1));
		assertTrue(ePackages.contains(ePackage2));
	}
	
	@Test
	public void getMetamodelPackagesResourceWithNestedPackages() {
		ResourceSet rSet = EMFUtil.createResourceSet();
		Resource resource = rSet.createResource(URI.createURI("test"));
		EPackage ePackage1 = createEPackage("ePackage1", "ePackage1");
		EPackage ePackage2 = createEPackage("ePackage2", "ePackage2");
		ePackage1.getESubpackages().add(ePackage2);
		resource.getContents().add(ePackage1);
		List<EPackage> ePackages = EMFUtil.getMetamodelPackages(resource);
		assertTrue(ePackages.contains(ePackage1));
		assertTrue(ePackages.contains(ePackage2));
	}
	
	private EPackage createEPackage(String name, String nsUri) {
		EPackage ePackage = factory.createEPackage();
		ePackage.setName(name);
		ePackage.setNsURI(nsUri);
		return ePackage;
	}

}
