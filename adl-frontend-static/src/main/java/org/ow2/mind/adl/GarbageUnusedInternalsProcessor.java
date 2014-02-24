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

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Node;
import org.ow2.mind.adl.annotation.ADLLoaderPhase;
import org.ow2.mind.adl.annotation.AbstractADLLoaderAnnotationProcessor;
import org.ow2.mind.adl.annotation.predefined.GarbageUnusedInternals;
import org.ow2.mind.adl.ast.Component;
import org.ow2.mind.adl.ast.ComponentContainer;
import org.ow2.mind.adl.ast.OptimASTHelper;
import org.ow2.mind.annotation.Annotation;
import org.ow2.mind.annotation.AnnotationHelper;

public class GarbageUnusedInternalsProcessor extends
AbstractADLLoaderAnnotationProcessor {

	public Definition processAnnotation(Annotation annotation, Node node,
			Definition definition, ADLLoaderPhase phase,
			Map<Object, Object> context) throws ADLException {

		// TODO : check if the source/destination of bindings are Singleton
		// TODO : check if the current component should be a Singleton ?
		if ((phase == ADLLoaderPhase.AFTER_CHECKING) || (phase == ADLLoaderPhase.AFTER_TEMPLATE_INSTANTIATE)) {
			OptimASTHelper.setGarbageUnusedInternals((Definition) node);

			if (!OptimASTHelper.isSingleton(definition)){
				errorManagerItf.logError(
						OptimADLErrors.INVALID_GARBAGEUNUSEDINTERNALS_NOT_SINGLETON, definition);
				//logger.warning("Invalid @GarbageUnusedInternals annotation, definition " + definition + " must be a @Singleton");
				return null;
			}

			if (node instanceof Definition && node instanceof ComponentContainer && ((GarbageUnusedInternals) annotation).recursive)
				recursiveDecorationPropagation((Definition) node, context, ((GarbageUnusedInternals) annotation).recursive);
		}

		return null;
	}

	private void recursiveDecorationPropagation(Definition definition, Map<Object, Object> context, boolean recursive) throws ADLException{
		Definition subCompDef;

		// For all sub components of our composite
		for (final Component subComp : ((ComponentContainer) definition).getComponents()) {

			// We want to work on the definition, not instance, of the current component.
			// Otherwise we wouldn't know the primitive/composite nature and possible sub-components information
			subCompDef = OptimASTHelper.getResolvedComponentDefinition(subComp, loaderItf, context);

			// Both annotate (for BasicInternalDataOptimizer) and decorate the AST (for StringTemplates)
			if (!OptimASTHelper.areGarbagedUnusedInternals(subCompDef)){
				AnnotationHelper.addAnnotation(subCompDef, new GarbageUnusedInternals());
				OptimASTHelper.setGarbageUnusedInternals(subCompDef);
				// Here we also need to decorate the inner comp (and not only definition)
				// Because when a composite has a sub-component that can be "Internal-Data-Garbaged" it must not make
				// reference to this one, and the type of data accessible from StringTemplate in that case is Component 
				OptimASTHelper.setGarbageUnusedInternals(subComp);
			}

			// Now take care of other sub-components in the architecture tree
			if (subCompDef instanceof ComponentContainer)
				recursiveDecorationPropagation(subCompDef, context, recursive);
		}
	}

}
