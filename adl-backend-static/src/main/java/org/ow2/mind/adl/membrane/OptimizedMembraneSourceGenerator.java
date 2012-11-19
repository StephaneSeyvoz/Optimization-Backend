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
 * Contact: mind@ow2.org
 *
 * Authors: Stephane Seyvoz, Assystem (for Schneider-Electric)
 * Contributors: 
 */

package org.ow2.mind.adl.membrane;

import static org.ow2.mind.adl.CompilationDecorationHelper.addAdditionalCompilationUnit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.CompilerError;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.interfaces.Interface;
import org.objectweb.fractal.adl.interfaces.InterfaceContainer;
import org.objectweb.fractal.adl.types.TypeInterface;
import org.ow2.mind.SourceFileWriter;
import org.ow2.mind.adl.CompilationDecorationHelper.AdditionalCompilationUnitDecoration;
import org.ow2.mind.adl.ImplementationHeaderSourceGenerator;
import org.ow2.mind.adl.ast.ImplementationContainer;
import org.ow2.mind.adl.ast.OptimASTHelper;
import org.ow2.mind.adl.ast.Source;
import org.ow2.mind.adl.graph.ComponentGraph;
import org.ow2.mind.adl.idl.InterfaceDefinitionDecorationHelper;
import org.ow2.mind.idl.ast.InterfaceDefinition;
import org.ow2.mind.io.IOErrors;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class OptimizedMembraneSourceGenerator extends MembraneSourceGenerator {

	/** The name to be used to inject the templateGroupName used by this class. */
	public static final String    TEMPLATE_NAME    = "optim.membrane.implementation";

	/** The default templateGroupName used by this class. */
	public static final String    DEFAULT_TEMPLATE = "st.optim.membrane.MembraneImplementation";

	protected static final String FILE_SUFFIX      = "_ctrl_impl";
	protected static final String FILE_EXT         = ".c";

	@Inject
	protected OptimizedMembraneSourceGenerator(
			@Named(TEMPLATE_NAME) final String templateGroupName) {
		super(templateGroupName);
	}

	// ---------------------------------------------------------------------------
	// Implementation of the Visitor interface
	// ---------------------------------------------------------------------------

	@Override
	public void visit(final Definition definition,
			final Map<Object, Object> context) throws ADLException {

		final String outputFileName = getCtrlImplFileName(definition);
		final File outputFile = outputFileLocatorItf.getCSourceOutputFile(
				outputFileName, context);

		if (regenerate(outputFile, definition, context)) {

			final StringTemplate st = getInstanceOf("ComponentDefinition");

			st.setAttribute("definition", definition);

			// SSZ : BEGIN MODIFICATION

			//ComponentGraph topLevelGraph = (ComponentGraph) context.get("topLevelGraph");
			ComponentGraph instanceGraph = (ComponentGraph) context.get("currentInstanceGraph");

			// /!\ 	HERE WE CONSIDER AS A PRE-CONDITION THAT WE WILL HAVE
			// 		ONLY ONE AND ONLY ONE INSTANCE PER DEFINITION 			/!\
			st.setAttribute("instance", instanceGraph);

			//topLevelGraph = null;
			//instanceGraph = null;

			// SSZ : END MODIFICATION

			if (definition instanceof InterfaceContainer) {
				final Interface[] itfs = ((InterfaceContainer) definition)
						.getInterfaces();
				final Map<String, InterfaceDefinition> itfDefs = new LinkedHashMap<String, InterfaceDefinition>(
						itfs.length);
				for (final Interface itf : itfs) {
					if (itf instanceof TypeInterface) {
						final TypeInterface tItf = (TypeInterface) itf;
						itfDefs
						.put(tItf.getSignature(),
								InterfaceDefinitionDecorationHelper
								.getResolvedInterfaceDefinition(tItf, idlLoaderItf,
										context));
					}
				}
				st.setAttribute("interfaceDefinitions", itfDefs);
			} else {
				st.setAttribute("interfaceDefinitions", Collections.emptyMap());
			}

			try {
				SourceFileWriter.writeToFile(outputFile, st.toString());
			} catch (final IOException e) {
				throw new CompilerError(IOErrors.WRITE_ERROR, e,
						outputFile.getAbsolutePath());
			}
		}

		Collection<File> dependencies = null;
		if (definition instanceof ImplementationContainer) {
			final Source sources[] = ((ImplementationContainer) definition)
					.getSources();
			if (sources.length == 1) {
				dependencies = new ArrayList<File>();
				dependencies.add(outputFileLocatorItf.getCCompiledTemporaryOutputFile(
						ImplementationHeaderSourceGenerator
						.getImplHeaderFileName(definition), context));
			} else if (sources.length > 1) {
				dependencies = new ArrayList<File>();
				for (int i = 0; i < sources.length; i++) {
					final Source src = sources[i];

					// SSZ
					Boolean raw = (Boolean) src.astGetDecoration("raw");
					raw = ((raw != null) && raw); // null or false => false

					if (!OptimASTHelper.isPreCompiled(src) && !OptimASTHelper.isAssembly(src) && (!raw)) {
						dependencies.add(outputFileLocatorItf
								.getCCompiledTemporaryOutputFile(
										ImplementationHeaderSourceGenerator.getImplHeaderFileName(
												definition, i), context));
					}
				}
			}
		}

		addAdditionalCompilationUnit(definition,
				new AdditionalCompilationUnitDecoration(outputFileName, true,
						dependencies));
	}

}
