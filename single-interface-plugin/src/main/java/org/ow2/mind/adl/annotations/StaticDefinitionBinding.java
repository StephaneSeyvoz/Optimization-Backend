package org.ow2.mind.adl.annotations;

import org.ow2.mind.adl.annotation.ADLAnnotationTarget;
import org.ow2.mind.annotation.Annotation;
import org.ow2.mind.annotation.AnnotationElement;
import org.ow2.mind.annotation.AnnotationTarget;

public class StaticDefinitionBinding implements Annotation {

	private static final long serialVersionUID = 5860025635724467653L;
	
	private static final AnnotationTarget[] ANNOTATION_TARGETS = { ADLAnnotationTarget.DEFINITION };
	
	@AnnotationElement
	public String fromItf;
	@AnnotationElement
	public String toItf;
	
	@AnnotationElement(hasDefaultValue=true)
	public Boolean inline = false;
	
	public AnnotationTarget[] getAnnotationTargets() {
		return ANNOTATION_TARGETS;
	}

	public boolean isInherited() {
		return false;
	}


}
