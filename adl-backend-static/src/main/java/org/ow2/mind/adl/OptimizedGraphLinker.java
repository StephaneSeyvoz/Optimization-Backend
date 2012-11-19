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

package org.ow2.mind.adl;

import static org.ow2.mind.PathHelper.replaceExtension;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.CompilerError;
import org.objectweb.fractal.adl.error.GenericErrors;
import org.ow2.mind.adl.graph.ComponentGraph;
import org.ow2.mind.compilation.CompilationCommand;
import org.ow2.mind.compilation.CompilerCommand;

public class OptimizedGraphLinker extends BasicGraphLinker {

	@Override
	protected void compileSharedImplementation(final ComponentGraph graph,
			final Collection<CompilationCommand> result,
			final Map<Object, Object> context) throws ADLException {
		final Set<String> sharedImplementations = new HashSet<String>();

		findSharedImplementations(graph, sharedImplementations);

		for (final String sharedImplementation : sharedImplementations) {
			File sharedImpl;
			final URL input = implementationLocatorItf.findSource(
					sharedImplementation, context);
			if (input == null) {
				// try to find shared implementation using output locator
				sharedImpl = outputFileLocatorItf.getCSourceOutputFile(
						sharedImplementation, context);
				if (sharedImpl == null) {
					throw new CompilerError(GenericErrors.INTERNAL_ERROR,
							"Can't find file " + sharedImplementation);
				}
			} else {
				try {
					sharedImpl = new File(input.toURI());
				} catch (final URISyntaxException e) {
					throw new CompilerError(GenericErrors.INTERNAL_ERROR, e,
							"Can't find file " + sharedImplementation);
				}
			}

			final File outFile = outputFileLocatorItf.getCCompiledOutputFile(
					replaceExtension(sharedImplementation, ".mpp.o"), context);
			final File depFile = outputFileLocatorItf.getCCompiledOutputFile(
					replaceExtension(sharedImplementation, ".d"), context);
			final CompilerCommand command = compilationCommandFactory
					.newCompilerCommand(null, null, sharedImpl, false, null, depFile,
							outFile, context);

			result.add(command);
		}
	}


}
