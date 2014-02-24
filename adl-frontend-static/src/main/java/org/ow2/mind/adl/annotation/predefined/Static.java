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

package org.ow2.mind.adl.annotation.predefined;

import org.ow2.mind.adl.annotation.ADLAnnotationTarget;
import org.ow2.mind.adl.annotation.ADLLoaderPhase;
import org.ow2.mind.adl.annotation.ADLLoaderProcessor;
import org.ow2.mind.annotation.Annotation;
import org.ow2.mind.annotation.AnnotationElement;
import org.ow2.mind.annotation.AnnotationTarget;
import org.ow2.mind.adl.StaticAnnotationProcessor;

@ADLLoaderProcessor(processor = StaticAnnotationProcessor.class, phases = {ADLLoaderPhase.AFTER_CHECKING})
public class Static implements Annotation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4235614542560118182L;

	/**
	 * The Static annotation is used to specify that a given binding will be optimized
	 * and it's CALLs in the CPL are replaced by direct function calls to the server (no more "this" context
	 * pointer and function pointer dereferencing).
	 */
	private final AnnotationTarget[] ANNOTATION_TARGETS = {ADLAnnotationTarget.BINDING};

	public static final String VALUE = "@Static";
	
	/**
	 * More fault-tolerant configuration argument to allow skipping optimizing when not possible,
	 * instead of raising a blocking error.
	 */
	@AnnotationElement(hasDefaultValue=true)
	public boolean ifPossible = true;
	
	public AnnotationTarget[] getAnnotationTargets() {
		return ANNOTATION_TARGETS;
	}

	public boolean isInherited() {
		return true;
	}
	
	public String toString()
	{
		return VALUE;
	}

}
