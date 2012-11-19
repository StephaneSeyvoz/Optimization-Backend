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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.ow2.mind.adl.graph.ComponentGraph;
import org.ow2.mind.compilation.CompilationCommand;

public class OptimizedGraphCompiler extends BasicGraphCompiler {
	
	// ---------------------------------------------------------------------------
	// Implementation of the Visitor interface
	// ---------------------------------------------------------------------------

	@Override
	public Collection<CompilationCommand> visit(final ComponentGraph graph,
			final Map<Object, Object> context) throws ADLException {
		final List<CompilationCommand> result = new ArrayList<CompilationCommand>();

		final List<Definition> definitionList = new ArrayList<Definition>();
		final Map<String, Collection<ComponentGraph>> instanceMap = new HashMap<String, Collection<ComponentGraph>>();

		// SSZ : BEGIN MODIFICATION$
		// We need to save who is the toplevel component for optimized macro instance name construction
		context.put("topLevelGraph", graph);
		// SSZ : END MODIFICATION

		// visit graph to build instanceMap and compile definitions
		visitGraph(graph, instanceMap, definitionList, result, context);

		if (instanceCompilerItf != null) {
			final Definition topLevelDef = graph.getDefinition();
			for (final Definition def : definitionList) {
				result.addAll(instanceCompilerItf.visit(new InstancesDescriptor(
						topLevelDef, def, instanceMap.get(def.getName())), context));
			}
		}

		// SSZ : BEGIN MODIFICATION : Optimization context hack clean-up (else it makes templates crash)
		context.remove("topLevelGraph");
		// SSZ : END MODIFICATION

		return result;
	}

	// ---------------------------------------------------------------------------
	// Utility methods
	// ---------------------------------------------------------------------------

	@Override
	protected void visitGraph(final ComponentGraph graph,
			final Map<String, Collection<ComponentGraph>> instanceMap,
			final List<Definition> definitionList,
			final List<CompilationCommand> result, final Map<Object, Object> context)
					throws ADLException {

		Collection<ComponentGraph> instances = instanceMap.get(graph
				.getDefinition().getName());
		if (instances == null) {

			// SSZ : BEGIN MODIFICATION
			// Here we want to know the Definition <-> Instance mapping
			// You could actually get the definition of an instance but not
			// the reverse : this is what this code is about.
			// Let's put it in the context map for convenience
			context.put("currentInstanceGraph", graph);
			// SSZ : END MODIFICATION

			// new definition, compile it.
			result
			.addAll(definitionCompilerItf.visit(graph.getDefinition(), context));

			instances = new ArrayList<ComponentGraph>();
			instances.add(graph);
			instanceMap.put(graph.getDefinition().getName(), instances);
			definitionList.add(graph.getDefinition());
		} else {
			// definition already compiled, simply add instance.
			instances.add(graph);
		}

		for (final ComponentGraph subComp : graph.getSubComponents()) {
			// TODO handle shared components
			visitGraph(subComp, instanceMap, definitionList, result, context);
		}

		// SSZ : BEGIN MODIFICATION : Optimization context hack clean-up (else it makes Factories build crash, because they would use the last
		// "currentInstanceGraph" as a current info for their different definition compilation)
		context.remove("currentInstanceGraph");
		// SSZ : END MODIFICATION
	}
}
