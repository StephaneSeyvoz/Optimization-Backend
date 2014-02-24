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
import org.ow2.mind.annotation.Annotation;
import org.ow2.mind.annotation.AnnotationTarget;

public class Inline implements Annotation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3970732593177592189L;

	/**
	 * The Static annotation is used to specify that a given binding will be optimized
	 * for inline calls, with function suffixed and re-defined in .inc files.
	 * 
	 * There is no AnnotationProcessor for this class, since it's a refinement of
	 * Static and StaticDefinitionBinding, in which according processors we check
	 * for the presence of Inline or not.
	 */
	private final AnnotationTarget[] ANNOTATION_TARGETS = {ADLAnnotationTarget.BINDING};

	public static final String VALUE = "@Inline";
	
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
