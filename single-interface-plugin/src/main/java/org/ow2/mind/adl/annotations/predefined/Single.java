package org.ow2.mind.adl.annotations.predefined;

import org.ow2.mind.adl.annotation.ADLAnnotationTarget;
import org.ow2.mind.adl.annotation.ADLLoaderPhase;
import org.ow2.mind.adl.annotation.ADLLoaderProcessor;
import org.ow2.mind.annotation.Annotation;
import org.ow2.mind.annotation.AnnotationTarget;

/**
 * @author Stephane Seyvoz
 */
@ADLLoaderProcessor(processor = SingleAnnotationProcessor.class, phases = { ADLLoaderPhase.AFTER_CHECKING, ADLLoaderPhase.ON_SUB_COMPONENT })
public class Single implements Annotation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3159560476705029540L;
	private static final AnnotationTarget[] ANNOTATION_TARGETS = { ADLAnnotationTarget.INTERFACE };
	
	public AnnotationTarget[] getAnnotationTargets() {
		return ANNOTATION_TARGETS;
	}
	
	public boolean isInherited() {
		return false;
	}

}
