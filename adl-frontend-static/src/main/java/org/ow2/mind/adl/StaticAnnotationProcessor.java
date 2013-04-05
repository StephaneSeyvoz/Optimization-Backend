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
		Static staticAnno = (Static) annotation;

		boolean isSourceSingleton = false;
		boolean isTargetSingleton = false;

		if ((phase == ADLLoaderPhase.AFTER_CHECKING) && (node instanceof Binding))
		{
			binding = (Binding) node;

			/*
			 * Propagate optimization info to the interfaces (IDL backend)
			 * This code inspiration is MIND SP5 plugin, with a small fix.
			 */
			String fromComponent = binding.getFromComponent();
			String toComponent = binding.getToComponent();

			// We don't want to tag "this.<myItfName>" internal interfaces
			if (fromComponent.equals("this")) {
				if (!OptimASTHelper.isSingleton(definition))
					// logError raises an exception and quits
					if (staticAnno.ifPossible)
						return null;
					else
						errorManagerItf.logError(
								OptimADLErrors.INVALID_STATIC_BINDING_SOURCE_NOT_SINGLETON, definition);
				else {
					//get the client interface involved in the binding
					currentClientItf = OptimASTHelper.getInterface(definition, binding.getFromInterface());
					// we can't optimize collections
					if (TypeInterfaceUtil.isCollection(currentClientItf))
						return null;
					
					isSourceSingleton = true;
				}
			} else {
				//get the client component instance
				currentClientCpt = OptimASTHelper.getComponent(definition, fromComponent);
				//get the client component definition
				currentClientDef = OptimASTHelper.getResolvedComponentDefinition(currentClientCpt, loaderItf, context);
				//get the client interface involved in the binding
				currentClientItf = OptimASTHelper.getInterface(currentClientDef, binding.getFromInterface());

				// we can't optimize collections
				if (TypeInterfaceUtil.isCollection(currentClientItf))
					return null;
				
				// logError raises an exception and quits
				if (!OptimASTHelper.isSingleton(currentClientDef))
					if (staticAnno.ifPossible)
						return null;
					else
						errorManagerItf.logError(
								OptimADLErrors.INVALID_STATIC_BINDING_SOURCE_NOT_SINGLETON, currentClientDef);
				else
					isSourceSingleton = true;
			}

			if (toComponent.equals("this")) {
				// logError raises an exception and quits
				if (!OptimASTHelper.isSingleton(definition))
					if (staticAnno.ifPossible)
						return null;
					else
						errorManagerItf.logError(
								OptimADLErrors.INVALID_STATIC_BINDING_SOURCE_NOT_SINGLETON, definition);
				else {
					//get the client interface involved in the binding
					currentServerItf = OptimASTHelper.getInterface(definition, binding.getToInterface());
					// we can't optimize collections
					if (TypeInterfaceUtil.isCollection(currentServerItf))
						return null;
					
					isTargetSingleton = true;
				}
			} else {
				//get the server component instance
				currentServerCpt = OptimASTHelper.getComponent(definition, toComponent);
				//get the server component definition
				currentServerDef = OptimASTHelper.getResolvedComponentDefinition(currentServerCpt, loaderItf, context);
				//get the server interface involved in the binding
				currentServerItf = OptimASTHelper.getInterface(currentServerDef, binding.getToInterface());

				if (TypeInterfaceUtil.isCollection(currentServerItf))
					return null;
				
				// logError raises an exception and quits
				if (!OptimASTHelper.isSingleton(currentServerDef))
					if (staticAnno.ifPossible)
						return null;
					else
						errorManagerItf.logError(
								OptimADLErrors.INVALID_STATIC_BINDING_DESTINATION_NOT_SINGLETON, currentServerDef);
				else
					isTargetSingleton = true;
			}

			// Propagating the decorations only when everything is certain to be optimized
			if (isSourceSingleton && isTargetSingleton) {
				if (!fromComponent.equals("this"))
					// Decorate the client interface for later optimization
					OptimASTHelper.setStaticDecoration(currentClientItf);

				if (!toComponent.equals("this"))
					// Decorate the server interface for later optimization
					OptimASTHelper.setStaticDecoration(currentServerItf);

				OptimASTHelper.setStaticDecoration(binding);
				
				logger.info("In composite " + definition.getName() + ", binding from " + fromComponent + "." + binding.getFromInterface() + " to " + toComponent + "." + binding.getToInterface() + " @Static optimization check result: OK");
			}
		}

		return definition;

	}

}
