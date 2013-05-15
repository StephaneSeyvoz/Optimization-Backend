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
 * 
 * Based on an idea suggested by Julien Tous, answering time-critical issues
 * on an hard real-time internal product development.
 */

package org.ow2.mind.adl.annotation.predefined;

import org.ow2.mind.adl.annotation.ADLAnnotationTarget;
import org.ow2.mind.adl.annotation.ADLLoaderPhase;
import org.ow2.mind.adl.annotation.ADLLoaderProcessor;
import org.ow2.mind.annotation.Annotation;
import org.ow2.mind.annotation.AnnotationElement;
import org.ow2.mind.annotation.AnnotationTarget;
import org.ow2.mind.adl.StaticAnnotationProcessor;

/**
 * The SingleStaticCallPointer annotation is used to specify that CALL_PTR in the
 * concerned component will be replaced by a static function call.
 * However counter-intuitive this can be, since we don't use a pointer anymore,
 * there are foundations to this annotation.
 * 
 * This annotation was made for performances to allow non-blocking multi-threaded cases
 * where we need reentrancy and to be able to call different instances of the same definition.
 * 
 * The problem:
 * Let's have a Client component (one definition, one instance) and Server components (S1, S2...).
 * Let's have two threads going through the same Client but wanting each to call S1 or S2 (passed
 * as argument of a server interface).
 * With a standard CALL, or even with "@StaticDefinitionBinding" (hard-written function call to Server
 * definition code, but keeping the "this" to select the instance of destination (S1, S2...), we would
 * need to use the BIND_MY_INTERFACE macro in order to define the "this" argument of the call.
 * This BIND_MY_INTERFACE then gives the interface pointer a value before the CALL. But this
 * interface pointer is stored in a global variable, the struct defining our component instance internal
 * data and type. Our two threads (or more) calling BIND_MY_INTERFACE and then CALL may lead to
 * concurrency issues: if Thread1 does BIND Client to S1 interface, and then Thread2 does BIND to S2
 * interface, and then Thread1 CALLs, Thread1 will target the S2 interface and our execution is broken.
 * 
 * CALL_PTR usually leverages such an issue since it does not need a BIND before its execution and
 * all needed information (both target function, and target instance interface) is contained by the
 * pointer, which is stored in the execution stack. Each thread then contains its own variable and
 * we have full non-blocking concurrency, and no mutex is needed.
 * 
 * However, CALL_PTR both concerns the function and instance, in pseudo-algo let's say:
 * this.type->itf->meth(this, args...)
 * which has a cost in execution on very low-end processors since we must compute two indirections
 * everytime.
 * 
 * Solution:
 * In our case we will "freeze" the function call of the CALL_PTR to a unique destination
 * function/definition but still use the "this" argument, so as to remove the indirections (for
 * performance), and still be able to target a specific instance of Server (S1/S2...), while
 * storing the target pointer on the stack, allowing each thread to use its specific context, which
 * is not possible with BIND/CALL without a mutex. We then reach optimal performance with
 * multi-threaded non-blocking parallellism.
 * 
 * Benefit:
 * Such a component can be both used in the optimized and non-optimized way since the CALL_PTR
 * logic will be good in each case.
 * 
 * Limitation:
 * The CALL_PTR macro can be used only for ONE target definition since we cannot refine it for different
 * cases (we don't know on which criteria to discriminate and generate different CALL_PTR variations
 * for the same client component).
 *  
 */
@ADLLoaderProcessor(processor = StaticAnnotationProcessor.class, phases = {ADLLoaderPhase.AFTER_CHECKING})
public class SingleStaticCallPointer implements Annotation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1018300207099478502L;
	
	private final AnnotationTarget[] ANNOTATION_TARGETS = {ADLAnnotationTarget.BINDING};

	public final String VALUE = "@SingleStaticCallPointer";
	
	/**
	 * We need to configure the one and only static destination of CALL_PTR for this
	 * annotation.
	 */
	@AnnotationElement
	public String toItf;
	
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
