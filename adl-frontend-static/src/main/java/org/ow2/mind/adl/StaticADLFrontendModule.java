package org.ow2.mind.adl;

import org.ow2.mind.adl.annotation.AnnotationProcessorTemplateInstantiator;
import org.ow2.mind.adl.factory.ParametricFactoryTemplateInstantiator;
import org.ow2.mind.adl.factory.SelectiveFactoryTemplateInstantiator;
import org.ow2.mind.adl.generic.CachingTemplateInstantiator;
import org.ow2.mind.adl.generic.InputResourceTemplateInstantiator;
import org.ow2.mind.adl.generic.TemplateInstantiator;
import org.ow2.mind.adl.generic.TemplateInstantiatorImpl;
import org.ow2.mind.adl.parameter.ParametricTemplateInstantiator;
import org.ow2.mind.inject.AbstractMindModule;

public class StaticADLFrontendModule extends AbstractMindModule {

	protected void configureTemplateInstantiator() {
	    bind(TemplateInstantiator.class)
	        .toChainStartingWith(CachingTemplateInstantiator.class)
	        .followedBy(AnnotationProcessorTemplateInstantiator.class)
	        .followedBy(ParametricFactoryTemplateInstantiator.class)
	        .followedBy(ParametricTemplateInstantiator.class)
	        // The change compared to the standard ADLFrontendModule configuration
	        // This is used for selective factory code generation in the component's membranes
	        .followedBy(SelectiveFactoryTemplateInstantiator.class)
	        //
	        .followedBy(InputResourceTemplateInstantiator.class)
	        .endingWith(TemplateInstantiatorImpl.class);
	  }
	
}
