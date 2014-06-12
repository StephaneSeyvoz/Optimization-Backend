package org.ow2.mind.adl.annotation.predefined;

import org.ow2.mind.adl.StaticDefinitionsBindingsListAnnotationProcessor;
import org.ow2.mind.adl.annotation.ADLAnnotationTarget;
import org.ow2.mind.adl.annotation.ADLLoaderPhase;
import org.ow2.mind.adl.annotation.ADLLoaderProcessor;
import org.ow2.mind.annotation.Annotation;
import org.ow2.mind.annotation.AnnotationElement;
import org.ow2.mind.annotation.AnnotationTarget;

/**
 * @author Stephane Seyvoz
 */
@ADLLoaderProcessor(processor = StaticDefinitionsBindingsListAnnotationProcessor.class, phases = { ADLLoaderPhase.AFTER_CHECKING })
public class StaticDefinitionsBindingsList implements Annotation {
	
	private static final long serialVersionUID = -2795000100099282184L;

	private static final AnnotationTarget[] ANNOTATION_TARGETS = { ADLAnnotationTarget.DEFINITION };

	/**
	 * TODO: Fix this comment: OUTDATED LOGIC 
	 * 
	 * We want to use a list of bindings, for example:
	 * @StaticDefinitionsBindings( { {TypeA.itf1, TypeB.itf2 }, { TypeC.itf3, TypeD.itf4 } , ...} })
	 * Where TypeA.itf1 will always call the methods of TypeB.itf2 and never any other one when it's bound.
	 * As a matter of fact, every call will be generated as a direct function call, allowing multiple instances
	 * of the destination type, as we do not remove the "this" argument in this case, but the function call is frozen:
	 * this is a hard-defined link between definitions.
	 */
	@AnnotationElement
	public StaticDefinitionBinding[] value;
	
	public AnnotationTarget[] getAnnotationTargets() {
		return ANNOTATION_TARGETS;
	}

	public boolean isInherited() {
		return false;
	}

}
