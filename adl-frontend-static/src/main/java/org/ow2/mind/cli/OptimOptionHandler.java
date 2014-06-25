package org.ow2.mind.cli;

import java.util.Map;

public class OptimOptionHandler implements CommandOptionHandler {

	private static final String OPTIM_CONTEXT_KEY = "optimize";
	
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
