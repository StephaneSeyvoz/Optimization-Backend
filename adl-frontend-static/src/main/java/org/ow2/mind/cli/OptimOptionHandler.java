/**
 * Copyright (C) 2014 Schneider-Electric
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
 * Authors: Stephane Seyvoz, Assystem (for Schneider-Electric)
 * Contributors: 
 */

package org.ow2.mind.cli;

import java.util.Map;

public class OptimOptionHandler implements CommandOptionHandler {

	public static final String OPTIM_CONTEXT_KEY = "optimize";
	
	public void processCommandOption(CmdOption cmdOption, CommandLine cmdLine,
			Map<Object, Object> context) throws InvalidCommandLineException {
		
		// Only keep track of the flag being enabled
		context.put(OPTIM_CONTEXT_KEY, true);
	}

	public static boolean isOptimizationBackendEnabled(Map<Object, Object> context) {
		Boolean result = (Boolean) context.get(OPTIM_CONTEXT_KEY);
		if (result == null)
			return false;
		else
			return result.booleanValue();
	}
	
}
