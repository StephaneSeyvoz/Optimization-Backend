/**
 * Copyright (C) 2010 STMicroelectronics
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
 * Contributors: 
 */

package org.ow2.mind.preproc.parser;

import java.io.PrintStream;

import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.ow2.mind.preproc.OptimCPLChecker;

public abstract class OptimAbstractCPLParser extends AbstractCPLParser {

	protected OptimCPLChecker   cplChecker  = null;
	protected PrintStream 		inlineOut 	= null;

	public OptimAbstractCPLParser(final TokenStream input,
			final RecognizerSharedState state) {
		super(input, state);
	}

	public OptimAbstractCPLParser(final TokenStream input) {
		super(input);
	}

	public void setCplChecker(final OptimCPLChecker cplChecker) {
		this.cplChecker = cplChecker;
	}

	public void setInlineOutputStream(PrintStream out) {
		this.inlineOut = out;
	}

}
