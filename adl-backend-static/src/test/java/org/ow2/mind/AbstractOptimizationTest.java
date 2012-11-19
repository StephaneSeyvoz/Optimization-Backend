package org.ow2.mind;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.ow2.mind.io.BasicOutputFileLocator;
import org.testng.annotations.BeforeMethod;

public abstract class AbstractOptimizationTest extends AbstractFunctionalTest {

	//protected CompilerRunner runner;

	protected static final String SRC_ROOT = "optimizations";
	protected static File         buildDir = new File(
			"target/build/optimizations-test");

	protected void initContext(final boolean force) throws ADLException {
		// delete previous temporary directory.
		if (runner.context != null) {
			final File tempDir = (File) runner.context
					.get(BasicOutputFileLocator.TEMPORARY_OUTPUT_DIR_CONTEXT_KEY);
			if (tempDir != null) {
				deleteDir(tempDir);
			}
		}

		runner.initContext();
		initPath();

		if (!buildDir.exists()) {
			buildDir.mkdirs();
		}
		runner.context.put(BasicOutputFileLocator.OUTPUT_DIR_CONTEXT_KEY, buildDir);
		ForceRegenContextHelper.setForceRegen(runner.context, force);
		ForceRegenContextHelper.setKeepTemp(runner.context, false);
		ForceRegenContextHelper.setNoBinaryAST(runner.context, false);
	}

	protected abstract void initPath();

	protected void cleanBuildDir() {
		if (buildDir.exists()) deleteDir(buildDir);
	}

	protected void deleteDir(final File f) {
		if (f.isDirectory()) {
			for (final File subFile : f.listFiles())
				deleteDir(subFile);
		}
		// f.delete();
		assertTrue(f.delete(), "Can't delete \"" + f + "\".");
	}
}
