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

import static org.ow2.mind.st.BackendFormatRenderer.toCPath;
import static org.ow2.mind.st.BackendFormatRenderer.toUpperCName;

import java.io.File;
import java.io.IOException;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.CompilerError;
import org.objectweb.fractal.adl.Definition;
import org.ow2.mind.SourceFileWriter;
import org.ow2.mind.adl.ast.ImplementationContainer;
import org.ow2.mind.adl.ast.OptimASTHelper;
import org.ow2.mind.adl.ast.Source;
import org.ow2.mind.io.IOErrors;

public class OptimizedImplementationHeaderSourceGenerator extends
ImplementationHeaderSourceGenerator {

	protected OptimizedImplementationHeaderSourceGenerator() {
		super();
	}

	// ---------------------------------------------------------------------------
	// Implementation of the Visitor interface
	// ---------------------------------------------------------------------------

	@Override
	public void visit(final Definition definition,
			final java.util.Map<Object, Object> context) throws ADLException {

		if (!(definition instanceof ImplementationContainer)) return;
		final Source[] sources = ((ImplementationContainer) definition)
				.getSources();
		if (sources.length <= 1) return;

		final File headerFile = outputFileLocatorItf.getCSourceOutputFile(
				getImplHeaderFileName(definition), context);

		if (regenerate(headerFile, definition, context)) {

			try {
				SourceFileWriter.writeToFile(headerFile, getFileContent(definition));
			} catch (final IOException e) {
				throw new CompilerError(IOErrors.WRITE_ERROR, e,
						headerFile.getAbsolutePath());
			}
		}
	}

	@Override
	protected String getFileContent(final Definition definition) {
		final StringBuilder sb = new StringBuilder();
		sb.append("/* Generated file: ").append(getImplHeaderFileName(definition))
		.append("*/\n");
		sb.append("#ifndef ").append(toUpperCName(definition.getName()))
		.append("_IMPL_H\n");
		sb.append("#define ").append(toUpperCName(definition.getName()))
		.append("_IMPL_H\n");

		final Source[] sources = ((ImplementationContainer) definition)
				.getSources();
		for (int i = 0; i < sources.length; i++) {
			final Source src = sources[i];

			// SSZ
			Boolean raw = (Boolean) src.astGetDecoration("raw");
			raw = ((raw != null) && raw); // null or false => false

			if (!OptimASTHelper.isPreCompiled(src) && !OptimASTHelper.isAssembly(src) && (!raw)) {
				sb.append("#include \"")
				.append(toCPath(getImplHeaderFileName(definition, i)))
				.append("\"\n");
			}
		}

		sb.append("#endif /* ").append(toUpperCName(definition.getName()))
		.append("_IMPL_H */\n");

		return sb.toString();
	}

}
