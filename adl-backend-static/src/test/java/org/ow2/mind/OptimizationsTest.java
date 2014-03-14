/**
 * Copyright (C) 2012 Schneider-Electric
 *
 * This file is part of "Mind Compiler" is free software: you can redistribute 
 * it and/or modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: mind@ow2.org, sseyvoz@assystem.com
 *
 * Authors: Stéphane Seyvoz, Assystem (for Schneider-Electric)
 * Contributors: 
 */

package org.ow2.mind;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.util.FractalADLLogManager;
import org.ow2.mind.adl.annotation.predefined.Run;
import org.ow2.mind.annotation.AnnotationHelper;
import org.ow2.mind.unit.UnitTestDataProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class OptimizationsTest extends AbstractOptimizationTest {

	protected static Logger logger = FractalADLLogManager.getLogger("optimizations-test");

	@Override
	protected void initPath() {
		initSourcePath(getDepsDir("fractal/api/Component.itf").getAbsolutePath(),
				"common", SRC_ROOT);
	}

	/**
	 * This method builds a set of tests based on all possible combinations of optimizations at the
	 * root level of applications. All combinations are applied to every @Run-annotated components,
	 * including legacy tests.
	 * 
	 * Data providing inspired from the following tutorial/blog (in french) :
	 * http://blog.paumard.org/2011/09/11/testng-et-ses-dataprovider/
	 */
	@DataProvider(name = "complex-optimizations-test")
	protected Object[][] complexDataProvider() throws Exception {

		final Object[][] ADLs = UnitTestDataProvider.listADLs(SRC_ROOT);

		ArrayList<Object[]> tmpList = new ArrayList<Object[]>();

		// Force the use of "setup" because it's usually automatically by TestNG AFTER the DataProvider
		// creation !
		setup();

		List<String> flags = new ArrayList<String>();

		// GarbageUnusedInternals <=> GUI, StaticBindings <=> SB, r = recursive
		String annotationCombinations[] = { "", "GUI", "GUIr", "S", "SB", "SBGUI", "SBGUIr", "SBr", "SBrGUI", "SBrGUIr"} ;

		for (int i = 0; i < ADLs.length; i++) {
			for (String annoCombo : annotationCombinations) {

				// ADLs[i][0] = rootDir, ADLs[i][1] = adlName (Standard row of the classic tests)
				String rootDir = (String) ADLs[i][0];
				String adlName = (String) ADLs[i][1];

				initSourcePath(getDepsDir("fractal/api/Component.itf").getAbsolutePath(),
						"common", rootDir);

				final Definition d = runner.load(adlName);
				final Run runAnno = AnnotationHelper.getAnnotation(d, Run.class);

				// Only add the test case to the list if it's @Run-annotated
				// (useless test case otherwise : gain some execution time !)
				if (runAnno != null)
					tmpList.add(new Object[] { new TestCase(rootDir, adlName, annoCombo, flags) });
			}
		}

		return tmpList.toArray(new Object[tmpList.size()][1]);
	}


	/**
	 * @param testCase The current generated TestCase to run.
	 * The TestCase is a row from the DataProvider, and consists of a path and an ADL name issued from a combination
	 * of optimisations (with the help of templates/generics).
	 * @throws Exception
	 */
	@Test(dataProvider = "complex-optimizations-test", groups = {"optimizations"})
	public void optimizationsComplexDataTest(TestCase testCase)
			throws Exception {
		initSourcePath(getDepsDir("fractal/api/Component.itf").getAbsolutePath(),
				"common", testCase.rootDir);

		// Note : "true" forces to clean and rebuild completely
		initContext(true);

		List<String> cFlags = testCase.flags;
		String adlName = testCase.adlName;

		final Definition d = runner.load(adlName);
		final Run runAnno = AnnotationHelper.getAnnotation(d, Run.class);
		if (runAnno != null) {

			if ((cFlags != null) && (!cFlags.isEmpty()))
				runner.addCFlags(cFlags);

			final String adl;
			adl = (runAnno.addBootstrap)
					? "GenericApplication" + testCase.optimCombo + "<" + adlName + ">"
							: adlName;

			if (logger.isLoggable(Level.FINE)) {

				String cFlagsString = ", without flag.";
				if ((cFlags != null) && (!cFlags.isEmpty()))
					cFlagsString = ", with flag : " + cFlags + ".";

				logger.log(Level.FINE, "Testing Combination " + adl + cFlagsString);
			}

			File exeFile = runner.compile(adl, runAnno.executableName);
			final int r = runner.run(exeFile, (String[]) null);

			assertEquals(r, 0, "Unexpected return value");

		} else {
			if (logger.isLoggable(Level.FINE))
				logger.log(Level.FINE, "Skipped test on ADL " + adlName + " : no @Run annotation was found.");
		}

	}


	protected class TestCase {

		public String rootDir;
		public String adlName;
		public String optimCombo;
		public List<String> flags ;

		public TestCase(String rootDir,
				String adlName,
				String optimCombo,
				List<String> flags) {
			this.rootDir 		= rootDir;
			this.adlName 		= adlName;
			this.optimCombo 	= optimCombo ;
			this.flags 			= flags ;
		}

		public String toString(){
			String flagsStr;
			String optimStr;

			if ((flags != null) && (!flags.isEmpty()))
				flagsStr = ", flags=" + flags;
			else flagsStr = ", no flag";

			if ((optimCombo != null) && (!optimCombo.equals("")))
				optimStr = ", optimCombo=" + optimCombo;
			else optimStr = ", no optim";

			return "[TestCase] " + rootDir + "/" + adlName + optimStr + flagsStr;
		}

	}

	/**
	 * The most basic test.
	 */
	@Test(groups = {"optimizations"})
	public void basicHelloworldTest()
			throws Exception {
		initSourcePath(getDepsDir("fractal/api/Component.itf").getAbsolutePath(),
				"common", "optimizations");

		initContext(true);
		String adlName = "helloworld.Helloworld";

		List<String> flags = new ArrayList<String>();

		final Definition d = runner.load(adlName);
		final Run runAnno = AnnotationHelper.getAnnotation(d, Run.class);
		if (runAnno != null) {
			runner.addCFlags(flags);

			final String adl;
			adl = (runAnno.addBootstrap)
					? "GenericApplication" + "SBrGUIr" + "<" + adlName + ">"
							: adlName;

			File exeFile = runner.compile(adl, runAnno.executableName);
			final int r = runner.run(exeFile, (String[]) null);

			assertEquals(r, 0, "Unexpected return value");

		} else {
			if (logger.isLoggable(Level.FINE))
				logger.log(Level.FINE, "Skipped test on ADL " + adlName + " : no @Run annotation was found.");
		}

	}

	/**
	 * Alternate @StaticBindings optimization/no optimization/optimization.
	 * No internal data garbaging (empty structs remain and no optimization propagation done).
	 */
	@Test(groups = {"optimizations"})
	public void partialAlternateOptimizationsTest()
			throws Exception {
		initSourcePath(getDepsDir("fractal/api/Component.itf").getAbsolutePath(),
				"common", "optimizations");

		initContext(true);
		String adlName = "partial.HelloworldApplicationSB_HWnoSB_sHWSB";

		List<String> flags = new ArrayList<String>();

		runner.addCFlags(flags);

		File exeFile = runner.compile(adlName, null);
		final int r = runner.run(exeFile, (String[]) null);

		assertEquals(r, 0, "Unexpected return value");
	}

	/**
	 * Same as partialAlternateOptimizationsTest() but with internal data garbaging (recursively on the whole architecture).
	 */
	@Test(groups = {"optimizations"})
	public void partialAlternateOptimizationsTestWithGUIr()
			throws Exception {
		initSourcePath(getDepsDir("fractal/api/Component.itf").getAbsolutePath(),
				"common", "optimizations");

		initContext(true);
		String adlName = "partial.HelloworldApplicationSBGUIr_HWnoSB_sHWSB";

		List<String> flags = new ArrayList<String>();

		runner.addCFlags(flags);

		File exeFile = runner.compile(adlName, null);
		final int r = runner.run(exeFile, (String[]) null);

		assertEquals(r, 0, "Unexpected return value");
	}

	/**
	 * Testing @Single with @StaticDefinitionsBindingsList and @StaticDefinitionsBinding
	 */
	@Test(groups = {"optimizations"})
	public void staticDefinitionBindingsTestWithSelfCall()
			throws Exception {
		initSourcePath(getDepsDir("fractal/api/Component.itf").getAbsolutePath(),
				"common", "optimizations");

		initContext(true);
		String adlName = "GenericApplication<" + "definitionbinding.factory.selfcall.Helloworld" + ">";

		File exeFile = runner.compile(adlName, null);
		final int r = runner.run(exeFile, (String[]) null);

		assertEquals(r, 0, "Unexpected return value");
	}

	/**
	 * Testing @SingleStaticCallPointer
	 */
	@Test(groups = {"optimizations"})
	public void staticFreezeCallPointerTestWithSelfCall()
			throws Exception {
		initSourcePath(getDepsDir("fractal/api/Component.itf").getAbsolutePath(),
				"common", "optimizations");

		initContext(true);
		String adlName = "GenericApplication<" + "singlestaticcallptr.factory.selfcall.Helloworld" + ">";

		File exeFile = runner.compile(adlName, null);
		final int r = runner.run(exeFile, (String[]) null);

		assertEquals(r, 0, "Unexpected return value");
	}

	/**
	 * Test if @Static @Inline works well.
	 */
	//@Test(groups = {"optimizations"})
	public void basicInlineTest()
			throws Exception {
		initSourcePath(getDepsDir("fractal/api/Component.itf").getAbsolutePath(),
				"common", "optimizations");

		initContext(true);
		String adlName = "GenericApplication" + "<" + "inline.Increment" + ">";

		File exeFile = runner.compile(adlName, null);
		final int r = runner.run(exeFile, (String[]) null);

		assertEquals(r, 0, "Unexpected return value");

	}
	
	/**
	 * Test if @StaticDefinitionBindingList({@StaticDefinitionBinding(fromItf=..., toItf=..., inline=true)}) works well.
	 */
	//@Test(groups = {"optimizations"})
	public void inlineDefinitionBindingTest()
			throws Exception {
		initSourcePath(getDepsDir("fractal/api/Component.itf").getAbsolutePath(),
				"common", "optimizations");

		initContext(true);
		String adlName = "GenericApplication" + "<" + "inline.definitionbinding.IncrementDefinitionBinding" + ">";

		File exeFile = runner.compile(adlName, null);
		final int r = runner.run(exeFile, (String[]) null);

		assertEquals(r, 0, "Unexpected return value");

	}
}

