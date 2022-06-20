package fr.gdaniel.ocl.runner.p2.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.gdaniel.ocl.OCLRunner;

class OCLRunnerTest {

	private OCLRunner runner;

	private static File METAMODEL_FILE;

	private static File MODEL_FILE;

	private static File CONSTRAINTS_FILE;

	@BeforeAll
	public static void setUpBeforeClass() {
		METAMODEL_FILE = new File("resources/model/graph.ecore");
		MODEL_FILE = new File("resources/model/graph-example.xmi");
		CONSTRAINTS_FILE = new File("resources/constraints/constraints.ocl");
	}

	@BeforeEach
	public void setUp() {
		this.runner = new OCLRunner();
	}

	@Test
	public void checkConstraintsNullMetamodelFile() {
		assertThrows(NullPointerException.class, () -> runner.checkConstraints(null, MODEL_FILE, CONSTRAINTS_FILE));
	}

	@Test
	public void checkConstraintsNullModelFile() {
		assertThrows(NullPointerException.class, () -> runner.checkConstraints(METAMODEL_FILE, null, CONSTRAINTS_FILE));
	}

	@Test
	public void checkConstraintsNullConstraintsFile() {
		assertThrows(NullPointerException.class, () -> runner.checkConstraints(METAMODEL_FILE, METAMODEL_FILE, null));
	}

	@Test
	public void checkConstraintsValidFileViolatedConstraints() {
		Diagnostic diagnostic = runner.checkConstraints(METAMODEL_FILE, MODEL_FILE, CONSTRAINTS_FILE);
		assertEquals(Diagnostic.WARNING, diagnostic.getSeverity());
	}

	@Test
	public void checkConstraintsNonXmiModelViolatedConstraints() {
		File graphFile = new File("resources/model/graph-example.graph");
		Diagnostic diagnostic = runner.checkConstraints(METAMODEL_FILE, graphFile, CONSTRAINTS_FILE);
		assertEquals(Diagnostic.WARNING, diagnostic.getSeverity());
		assertDiagnosticChildrenRefersToGraph(diagnostic);
	}

	/**
	 * Checks that the provided {@link Diagnostic} contains 2 children that refer to
	 * the Graph metamodel.
	 * <p>
	 * This method makes sure that the provided {@link Diagnostic} contains 2
	 * children with {@link Diagnostic#getData()} pointing to {@code Node}
	 * instances. This is a minimal check to discard diagnostics generated from
	 * invalid expressions or parsing errors, but it does not check in depth the
	 * content of the diagnostics.
	 * 
	 * @param diagnostic the {@link Diagnostic} to check
	 */
	private void assertDiagnosticChildrenRefersToGraph(Diagnostic diagnostic) {
		assertEquals(2, diagnostic.getChildren().size());
		Diagnostic diagnostic1 = diagnostic.getChildren().get(0);
		assertEquals(1, diagnostic1.getData().size());
		assertEquals(Diagnostic.WARNING, diagnostic1.getSeverity());
		EObject data1 = (EObject) diagnostic1.getData().get(0);
		assertEquals("Node", data1.eClass().getName());
		Diagnostic diagnostic2 = diagnostic.getChildren().get(1);
		assertEquals(1, diagnostic2.getData().size());
		assertEquals(Diagnostic.WARNING, diagnostic2.getSeverity());
		EObject data2 = (EObject) diagnostic2.getData().get(0);
		assertEquals("Node", data2.eClass().getName());
	}

}
