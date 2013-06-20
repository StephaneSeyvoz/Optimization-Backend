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

package org.ow2.mind.adl.ast;

import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Loader;
import org.objectweb.fractal.adl.Node;
import org.ow2.mind.adl.annotation.predefined.GarbageUnusedInternals;
import org.ow2.mind.adl.annotation.predefined.Inline;
import org.ow2.mind.adl.annotation.predefined.Static;
import org.ow2.mind.adl.annotation.predefined.StaticBindings;
import org.ow2.mind.annotation.AnnotationHelper;

/**
 * Helper methods for ADL AST nodes.
 */

public class OptimASTHelper extends ASTHelper {

	protected OptimASTHelper(){
		super();
	}

	/**
	 * The name of the decoration used to indicate if a binding is static.
	 * This decoration should only be used by StringTemplate.
	 */
	public static final String STATIC_DECORATION_NAME = "is-static";

	/**
	 * Returns <code>true</code> if the given definition has the {@link Static}
	 * decoration.
	 * 
	 * @param def a definition.
	 * @return <code>true</code> if the given definition has the {@link Static}
	 *         decoration.
	 */
	public static boolean isStatic(final Node node) {
		return AnnotationHelper.getAnnotation(node, Static.class) != null;
	}

	/**
	 * Sets the {@value #STATIC_DECORATION_NAME} decoration to
	 * <code>true</code> on the given definition.
	 * 
	 * @param def a definition.
	 */
	public static void setStaticDecoration(Node node) {
		node.astSetDecoration(STATIC_DECORATION_NAME, Boolean.TRUE);
	}
	
	/**
	 * The name of the decoration used to indicate if a binding's methods 
	 * should be inlined.
	 */
	public static final String INLINE_DECORATION_NAME = "is-inline";

	/**
	 * Returns <code>true</code> if the given definition has the {@link Inline}
	 * decoration.
	 * 
	 * @param def a definition.
	 * @return <code>true</code> if the given definition has the {@link Inline}
	 *         decoration.
	 */
	public static boolean isInline(final Node node) {
		return AnnotationHelper.getAnnotation(node, Inline.class) != null;
	}

	/**
	 * Sets the {@value #INLINE_DECORATION_NAME} decoration to
	 * <code>true</code> on the given definition.
	 * 
	 * @param def a definition.
	 */
	public static void setInlineDecoration(Node node) {
		node.astSetDecoration(INLINE_DECORATION_NAME, Boolean.TRUE);
	}
	
	/**
	 * Get the {@value #INLINE_DECORATION_NAME} decoration to
	 * <code>true</code> from the given definition.
	 * 
	 * @param def a definition.
	 */
	public static Object getInlineDecoration(Node node) {
		return node.astGetDecoration(INLINE_DECORATION_NAME);
	}

	/**
	 * Get the {@value #INLINE_DECORATION_NAME} decoration to
	 * <code>true</code> from the given definition.
	 * 
	 * @param def a definition.
	 */
	public static boolean hasInlineDecoration(Node node) {
		return  (getInlineDecoration(node) != null) ? ((Boolean) getInlineDecoration(node)) : false;
	}
	
	/**
	 * The name of the decoration used to indicate if a component's bindings are static.
	 * This decoration should only be used by StringTemplate.
	 */
	public static final String STATICBINDINGS_DECORATION_NAME = "has-static-bindings";

	/**
	 * Returns <code>true</code> if the given definition has the {@link StaticBindings}
	 * decoration.
	 * 
	 * @param def a definition.
	 * @return <code>true</code> if the given definition has the {@link StaticBindings}
	 *         decoration.
	 */
	public static boolean hasStaticBindings(final Node node) {
		return AnnotationHelper.getAnnotation(node, StaticBindings.class) != null;
	}

	/**
	 * Sets the {@value #STATICBINDINGS_DECORATION_NAME} decoration to
	 * <code>true</code> on the given definition.
	 * 
	 * @param def a definition.
	 */
	public static void setStaticBindingsDecoration(Node node) {
		node.astSetDecoration(STATICBINDINGS_DECORATION_NAME, Boolean.TRUE);
	}

	/**
	 * The name of the decoration used to indicate if a component's bindings are static.
	 * This decoration should only be used by StringTemplate.
	 */
	public static final String PARENTSTATICBINDINGS_DECORATION_NAME = "parent-has-static-bindings";

	/**
	 * Returns <code>true</code> if the given definition parent has the {@link StaticBindings}
	 * decoration.
	 * 
	 * @param def a definition.
	 * @return <code>true</code> if the given definition parent has the {@link StaticBindings}
	 *         decoration.
	 */
	public static boolean hasParentStaticBindings(final Node node) {
		Boolean result = (Boolean) node.astGetDecoration(PARENTSTATICBINDINGS_DECORATION_NAME);
		if (result != null)
			return result.equals(Boolean.TRUE);
		else return false;
	}

	/**
	 * Sets the {@value #PARENTSTATICBINDINGS_DECORATION_NAME} decoration to
	 * <code>true</code> on the given definition.
	 * 
	 * @param def a definition.
	 */
	public static void setParentStaticBindingsDecoration(Node node) {
		node.astSetDecoration(PARENTSTATICBINDINGS_DECORATION_NAME, Boolean.TRUE);
	}

	/**
	 * The name of the decoration used to indicate if a component's bindings are static.
	 * This decoration should only be used by StringTemplate.
	 */
	public static final String GARBAGEUNUSEDINTERNALS_DECORATION_NAME = "garbage-unused-internals";

	/**
	 * Returns <code>true</code> if the given definition has the {@link GarbageUnusedInternals}
	 * decoration.
	 * 
	 * @param def a definition.
	 * @return <code>true</code> if the given definition has the {@link GarbageUnusedInternals}
	 *         decoration.
	 */
	public static boolean areGarbagedUnusedInternals(final Node node) {
		return AnnotationHelper.getAnnotation(node, GarbageUnusedInternals.class) != null;
	}

	/**
	 * Sets the {@value #GARBAGEUNUSEDINTERNALS_DECORATION_NAME} decoration to
	 * <code>true</code> on the given definition.
	 * 
	 * @param def a definition.
	 */
	public static void setGarbageUnusedInternals(Node node) {
		node.astSetDecoration(GARBAGEUNUSEDINTERNALS_DECORATION_NAME, Boolean.TRUE);
	}

	// ---------------------------------------------------------------------------
	// Factory helper methods
	// ---------------------------------------------------------------------------

	public static final String DEFINITION_IS_FACTORIZED = "definition-is-factorized";

	public static void setInstantiatedDefinitionAsFactorized(
			final Definition definition) {
		definition.astSetDecoration(
				DEFINITION_IS_FACTORIZED,
				true);
	}

	public static boolean isDefinitionFactorized(
			final Definition definition, final Loader loaderItf,
			final Map<Object, Object> context) throws ADLException {
		final Boolean isFactorizedDefinitionDecoration = (Boolean) definition
				.astGetDecoration(DEFINITION_IS_FACTORIZED);
		return (isFactorizedDefinitionDecoration == null) ? false : isFactorizedDefinitionDecoration.booleanValue();
	}

}
