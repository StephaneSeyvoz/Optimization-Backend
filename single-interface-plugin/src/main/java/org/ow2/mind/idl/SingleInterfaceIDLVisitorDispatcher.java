package org.ow2.mind.idl;

import java.util.Map;
import java.util.Set;

import org.ow2.mind.AbstractVoidVisitorDispatcher;
import org.ow2.mind.VoidVisitor;
import org.ow2.mind.idl.ast.IDL;

import com.google.inject.Inject;

/**
 * Override the dispatcher to allow branching IDL production through our classes
 * with the @SingleInterfaceGuiceAnnotation annotation as a filter.
 * @author Stephane Seyvoz
 *
 */
public class SingleInterfaceIDLVisitorDispatcher extends AbstractVoidVisitorDispatcher<IDL> implements IDLVisitor {

	@Inject
	@SingleInterfaceGuiceAnnotation
	protected Set<IDLVisitor> visitors;

	@Override
	protected Iterable<? extends VoidVisitor<IDL>> getVisitorsItf(
			final Map<Object, Object> context) {
		return visitors;
	}
}
