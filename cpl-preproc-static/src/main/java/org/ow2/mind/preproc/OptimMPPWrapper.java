/**
 * Copyright (C) 2009 STMicroelectronics
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
 * Authors: Matthieu Leclercq
 * Contributors: Matthieu ANNE
 */

package org.ow2.mind.preproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognitionException;
import org.objectweb.fractal.adl.ADLErrors;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.CompilerError;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.error.GenericErrors;
import org.objectweb.fractal.adl.util.FractalADLLogManager;
import org.ow2.mind.error.ErrorManager;
import org.ow2.mind.plugin.PluginManager;
import org.ow2.mind.preproc.BasicMPPWrapper.BasicMPPCommand;
import org.ow2.mind.preproc.parser.AbstractCPLParser;
import org.ow2.mind.preproc.parser.OptimAbstractCPLParser;

import com.google.inject.Inject;

public class OptimMPPWrapper extends BasicMPPWrapper implements MPPWrapper {

	protected static Logger logger = FractalADLLogManager.getLogger("io");

	@Inject
	protected ErrorManager  errorManagerItf;

	@Inject
	protected PluginManager pluginManagerItf;

	// ---------------------------------------------------------------------------
	// Implementation of the MPPWrapper interface
	// ---------------------------------------------------------------------------

	public MPPCommand newMPPCommand(final Definition definition,
			final Map<Object, Object> context) {
		return new OptimMPPCommand(definition, context);
	}

	protected class OptimMPPCommand extends BasicMPPCommand implements MPPCommand {

		private List<File>                  inputFiles;
		private List<File>                  outputFiles;

		OptimMPPCommand(final Definition definition,
				final Map<Object, Object> context) {

			super(definition, context);

			this.cplChecker = new OptimCPLChecker(errorManagerItf, definition, context);
		}

		public boolean exec() throws ADLException, InterruptedException {
			final Lexer lex;
			try {
				lex = ExtensionHelper.getLexer(pluginManagerItf, inputFile.getPath(),
						context);
			} catch (final IOException e) {
				throw new ADLException(ADLErrors.IO_ERROR, e, inputFile.getPath());
			}

			final CommonTokenStream tokens = new CommonTokenStream(lex);

			final AbstractCPLParser basicMpp = ExtensionHelper.getParser(pluginManagerItf,
					tokens, context);

			// should never happen
			if (!(basicMpp instanceof OptimAbstractCPLParser)) {
				return super.exec();
			}
			
			final OptimAbstractCPLParser mpp = (OptimAbstractCPLParser) ExtensionHelper.getParser(pluginManagerItf,
					tokens, context);

			mpp.setCplChecker(cplChecker);
			mpp.setErrorManager(errorManagerItf);

			PrintStream outPS = null;
			PrintStream headerOutPS = null;
			try {
				try {
					outputFile.getParentFile().mkdirs();
					outPS = new PrintStream(new FileOutputStream(outputFile));
				} catch (final FileNotFoundException e) {
					throw new CompilerError(GenericErrors.INTERNAL_ERROR, e, "IO error");
				}
				mpp.setOutputStream(outPS);

				if (headerOutputFile != null) {
					try {
						headerOutputFile.getParentFile().mkdirs();
						headerOutPS = new PrintStream(
								new FileOutputStream(headerOutputFile));
					} catch (final FileNotFoundException e) {
						throw new CompilerError(GenericErrors.INTERNAL_ERROR, e, "IO error");
					}
					mpp.setHeaderOutputStream(headerOutPS);
				}

				mpp.setSingletonMode(singletonMode);

				if (logger.isLoggable(Level.INFO)) logger.info(getDescription());

				if (logger.isLoggable(Level.FINE))
					logger.fine("MPP: inputFile=" + inputFile.getPath() + " outputFile="
							+ outputFile.getPath() + " singletonMode=" + singletonMode);

				final int nbErrors = errorManagerItf.getErrors().size();
				try {
					mpp.preprocess();
				} catch (final RecognitionException e) {
					errorManagerItf.logError(MPPErrors.PARSE_ERROR, e,
							inputFile.getPath(), "MPP parse error.");
					return false;
				}

				return errorManagerItf.getErrors().size() == nbErrors;

			} finally {
				if (outPS != null) outPS.close();
				if (headerOutPS != null) headerOutPS.close();
			}
		}

		public String getDescription() {
			return "MPP: " + outputFile.getPath();
		}
	}
}
