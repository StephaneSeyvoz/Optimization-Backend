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
 * Authors: Stephane Seyvoz, Assystem (for Schneider-Electric)
 * Contributors: 
 */

package org.ow2.mind.adl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Node;
import org.objectweb.fractal.adl.interfaces.Interface;
import org.objectweb.fractal.adl.types.TypeInterfaceUtil;
import org.objectweb.fractal.adl.util.FractalADLLogManager;
import org.ow2.mind.adl.annotation.ADLLoaderPhase;
import org.ow2.mind.adl.annotation.AbstractADLLoaderAnnotationProcessor;
import org.ow2.mind.adl.annotation.predefined.Static;
import org.ow2.mind.adl.ast.OptimASTHelper;
import org.ow2.mind.adl.ast.Binding;
import org.ow2.mind.adl.ast.Component;
import org.ow2.mind.annotation.Annotation;

public class StaticAnnotationProcessor extends
AbstractADLLoaderAnnotationProcessor {

	/**
	 * The current binding client component.
	 */
	private Component currentClientCpt = null;

	/**
	 * The current binding server component.
	 */
	private Component currentServerCpt = null;

	/**
	 * The current binding client component definition. 
	 */
	private Definition currentClientDef = null;

	/**
	 * The current binding server component definition.
	 */
	private Definition currentServerDef = null;

	/**
	 * The current server interface involved in the binding.
	 */
	private Interface currentServerItf;

	/**
	 * The current client interface involved in the binding.
	 */
	private Interface currentClientItf;

	private Binding binding;

	/**
	 * Logger.
	 */
	private static Logger logger = FractalADLLogManager.getLogger(Static.VALUE);

	public Definition processAnnotation(Annotation annotation, Node node,
			Definition definition, ADLLoaderPhase phase,
			Map<Object, Object> context) throws ADLException {

		assert annotation instanceof Static;
		assert node instanceof Binding;
		Static staticAnno = (Static) annotation;

		boolean isSourceSingleton = false;
		boolean isTargetSingleton = false;

		// Nowadays the 'ifPossible' option is the default

		if (phase == ADLLoaderPhase.AFTER_CHECKING)
		{
			binding = (Binding) node;

			/*
			 * Propagate optimization info to the interfaces (IDL backend)
			 */
			String fromComponent = binding.getFromComponent();
			String fromInterface = binding.getFromInterface();
			String toComponent = binding.getToComponent();
			String toInterface = binding.getToInterface();

			// We don't want to tag "this.<myItfName>" internal interfaces
			if (fromComponent.equals("this")) {
				if (!OptimASTHelper.isSingleton(definition))
					// logError raises an exception and quits
					if (staticAnno.ifPossible) {
						logger.info("In definition " + definition.getName() + ", could not optimize '" + fromComponent + "." + fromInterface + " -> " + toComponent + "." + toInterface + "', since " + definition.getName() + " (source 'this') isn't a @Singleton");
						return null;
					} else
						errorManagerItf.logError(
								OptimADLErrors.INVALID_STATIC_BINDING_SOURCE_NOT_SINGLETON, definition);
				else {
					//get the client interface involved in the binding
					currentClientItf = OptimASTHelper.getInterface(definition, fromInterface);
					isSourceSingleton = true;
				}
			} else {
				//get the client component instance
				currentClientCpt = OptimASTHelper.getComponent(definition, fromComponent);
				//get the client component definition
				currentClientDef = OptimASTHelper.getResolvedComponentDefinition(currentClientCpt, loaderItf, context);
				//get the client interface involved in the binding
				currentClientItf = OptimASTHelper.getInterface(currentClientDef, fromInterface);

				// logError raises an exception and quits
				if (!OptimASTHelper.isSingleton(currentClientDef))
					if (staticAnno.ifPossible) {
						logger.info("In definition " + definition.getName() + ", could not optimize '" + fromComponent + "." + fromInterface + " -> " + toComponent + "." + toInterface + "', since " + currentClientDef.getName() + " (source) isn't a @Singleton");
						return null;
					} else
						errorManagerItf.logError(
								OptimADLErrors.INVALID_STATIC_BINDING_SOURCE_NOT_SINGLETON, currentClientDef);
				else
					isSourceSingleton = true;
			}

			if (toComponent.equals("this")) {
				// logError raises an exception and quits
				if (!OptimASTHelper.isSingleton(definition))
					if (staticAnno.ifPossible) {
						logger.info("In definition " + definition.getName() + ", could not optimize '" + fromComponent + "." + fromInterface + " -> " + toComponent + "." + toInterface + "', since " + definition.getName() + " (target) isn't a @Singleton");
						return null;
					} else
						errorManagerItf.logError(
								OptimADLErrors.INVALID_STATIC_BINDING_SOURCE_NOT_SINGLETON, definition);
				else {
					//get the client interface involved in the binding
					currentServerItf = OptimASTHelper.getInterface(definition, toInterface);
					isTargetSingleton = true;
				}
			} else {
				//get the server component instance
				currentServerCpt = OptimASTHelper.getComponent(definition, toComponent);
				//get the server component definition
				currentServerDef = OptimASTHelper.getResolvedComponentDefinition(currentServerCpt, loaderItf, context);
				//get the server interface involved in the binding
				currentServerItf = OptimASTHelper.getInterface(currentServerDef, toInterface);

				// logError raises an exception and quits
				if (!OptimASTHelper.isSingleton(currentServerDef))
					if (staticAnno.ifPossible) {
						logger.info("In definition " + definition.getName() + ", could not optimize '" + fromComponent + "." + fromInterface + " -> " + toComponent + "." + toInterface + "', since " + currentServerDef.getName() + " (target) isn't a @Singleton");
						return null;
					} else
						errorManagerItf.logError(
								OptimADLErrors.INVALID_STATIC_BINDING_DESTINATION_NOT_SINGLETON, currentServerDef);
				else
					isTargetSingleton = true;
			}

			// Propagating the decorations only when everything is certain to be optimized
			if (isSourceSingleton && isTargetSingleton) {
				if (!fromComponent.equals("this")) {
					// Decorate the client interface for later optimization
					OptimASTHelper.setStaticDecoration(currentClientItf);
					
					// For IS_BOUND to always return true we need a switch at the definition level
					currentClientDef.astSetDecoration("static-in-parent", Boolean.TRUE);
					
					// optimize further on ?
					if (OptimASTHelper.isInline(binding)) {
						OptimASTHelper.setInlineDecoration(currentClientItf);
						
						// We need for the client to know which target <Definition.name>.inline
						// files to -include (in the OptimizedDefinitionCompiler)
						List<Definition> inlineTargetDefinitions = null;
						Object oldInlineTargetDefinitions = currentServerItf.astGetDecoration("inline-target-defs");
						if (oldInlineTargetDefinitions == null)
							inlineTargetDefinitions = new ArrayList<Definition>();
						else inlineTargetDefinitions = (List<Definition>) oldInlineTargetDefinitions; // TODO add check
							
						inlineTargetDefinitions.add(currentServerDef);
						currentClientDef.astSetDecoration("inline-target-defs", inlineTargetDefinitions);
					}
				}

				if (!toComponent.equals("this")) {
					// Decorate the server interface for later optimization
					OptimASTHelper.setStaticDecoration(currentServerItf);
					
					// optimize further on ?
					if (OptimASTHelper.isInline(binding) && !fromComponent.equals("this")) { 
						
						OptimASTHelper.setInlineDecoration(currentServerItf);
						
						// In the OptimizedDefinitionCompiler we want to know if
						// the current definition (server) will have to generate
						// a <Definition.name>.inline file
						OptimASTHelper.setInlineDecoration(currentServerDef);
					}
				}

				OptimASTHelper.setStaticDecoration(binding);
				
				// optimize further on ?
				if (OptimASTHelper.isInline(binding))
					OptimASTHelper.setInlineDecoration(binding);

				logger.info("In composite " + definition.getName() + ", binding from " + fromComponent + "." + fromInterface + " to " + toComponent + "." + toInterface + " optimization check result: OK");
			}
		}

		return definition;

	}

}
