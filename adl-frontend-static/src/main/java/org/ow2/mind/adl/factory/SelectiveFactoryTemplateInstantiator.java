package org.ow2.mind.adl.factory;

import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.ow2.mind.adl.ADLErrors;
import org.ow2.mind.adl.annotation.predefined.Singleton;
import org.ow2.mind.adl.ast.Component;
import org.ow2.mind.adl.ast.ComponentContainer;
import org.ow2.mind.adl.ast.DefinitionReference;
import org.ow2.mind.adl.ast.OptimASTHelper;
import org.ow2.mind.annotation.AnnotationHelper;

public class SelectiveFactoryTemplateInstantiator extends
FactoryTemplateInstantiator {

	/**
	 * We enhance the check to decorate the instantiated definition and all its recursive
	 * sub-component definitions. This allows us to finely select which definitions need
	 * their membrane to be compiled with or without factory code.
	 */
	protected void checkReferencedInstantiatedDef(
			final Definition instantiatedDef, final DefinitionReference defRef,
			final Map<Object, Object> context) throws ADLException {
		
		// Only added part
		OptimASTHelper.setInstantiatedDefinitionAsFactorized(instantiatedDef);
		//
		
		if (instantiatedDef instanceof ComponentContainer) {
			for (final Component subComp : ((ComponentContainer) instantiatedDef)
					.getComponents()) {
				if (subComp.getDefinitionReference() == null) continue;
				final Definition subCompDef = definitionReferenceResolverItf.resolve(
						subComp.getDefinitionReference(), instantiatedDef, context);

				if (AnnotationHelper.getAnnotation(subCompDef, Singleton.class) != null) {
					// definition is a singleton, cannot make a factory of that kind of
					// definition.
					errorManagerItf.logError(
							ADLErrors.INVALID_FACTORY_OF_REFERENCED_SINGLETON, defRef,
							subCompDef.getName());
				}

				checkReferencedInstantiatedDef(subCompDef, defRef, context);
			}
		}
	}



}
