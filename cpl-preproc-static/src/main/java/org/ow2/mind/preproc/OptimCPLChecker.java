/**
 * Copyright (C) 2010 France Telecom
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
 * Contact: mind@ow2.org
 *
 * Authors: Matthieu ANNE
 * Contributors: Stephane SEYVOZ (Assystem)
 */

package org.ow2.mind.preproc;

import java.util.Map;

import org.antlr.runtime.Token;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.interfaces.Interface;
import org.objectweb.fractal.adl.types.TypeInterface;
import org.ow2.mind.adl.ast.ASTHelper;
import org.ow2.mind.adl.idl.InterfaceDefinitionDecorationHelper;
import org.ow2.mind.error.ErrorManager;
import org.ow2.mind.idl.ast.ArrayOf;
import org.ow2.mind.idl.ast.ConstantDefinition;
import org.ow2.mind.idl.ast.EnumDefinition;
import org.ow2.mind.idl.ast.EnumReference;
import org.ow2.mind.idl.ast.IDLASTHelper;
import org.ow2.mind.idl.ast.InterfaceDefinition;
import org.ow2.mind.idl.ast.Method;
import org.ow2.mind.idl.ast.PointerOf;
import org.ow2.mind.idl.ast.PrimitiveType;
import org.ow2.mind.idl.ast.StructDefinition;
import org.ow2.mind.idl.ast.StructReference;
import org.ow2.mind.idl.ast.Type;
import org.ow2.mind.idl.ast.TypeDefReference;
import org.ow2.mind.idl.ast.TypeDefinition;
import org.ow2.mind.idl.ast.UnionDefinition;
import org.ow2.mind.idl.ast.UnionReference;

public class OptimCPLChecker extends CPLChecker {

	protected String inlineParams = null;
	final protected String staticInlinePrefix = "static inline";

	public OptimCPLChecker(final ErrorManager errorManager,
			final Definition definition, final Map<Object, Object> context) {

		super(errorManager, definition, context);
	}

	/**
	 * @param itfName
	 * @param itfIdx
	 * @param methName
	 * @param sourceFile
	 * @param body
	 * @throws ADLException
	 */
	//@SuppressWarnings("unchecked")
	public String computeInlinePrefix(final Token itfName,
			final String itfIdx, final Token methName, final String sourceFile) throws ADLException {

		if (definition == null) {
			// Add this condition so that the testNG will not throw exceptions
			// (stand-alone node)
			return null;
		}

		// all previous checks from serverMethDef have already been done
		final Interface itf = ASTHelper.getInterface(definition, itfName.getText());

		if (itfIdx != null) {
			logger.severe("Cannot inline collection methods ! - Skip");
			return null;
		}

		// ----------------------------------------
		// Inline checking and method body storage

		// Then we need to remember the method body for its inline variation
		// declaration in the .inc file

		// already loaded and checked by serverMethDef
		final InterfaceDefinition itfDef = InterfaceDefinitionDecorationHelper
				.getResolvedInterfaceDefinition((TypeInterface) itf, null, null);
		final Method currMethod = IDLASTHelper.getMethod(itfDef,
				methName.getText());
		if (currMethod == null) {
			errorManager.logError(MPPErrors.UNKNOWN_METHOD,
					locator(methName, sourceFile), itfName.getText(),
					methName.getText());
		}

		// Care: this method is a prototype
		String typeString = typeToString(currMethod.getType());

		final String methodSymbol = "__component_" + definition.getName().replace(".", "_") + "_"
          + itfName.getText() + "_" + methName.getText() + "_inline";
		
		String methodSymbolAndParams = staticInlinePrefix + " " + typeString + " " + methodSymbol + " (" + (inlineParams != null ? inlineParams : "") + ")";

		// cleanup
		inlineParams = null;

		return methodSymbolAndParams;
	}

	public void setInlineParams(StringBuilder inlineParams) {
		this.inlineParams = inlineParams.toString();
	}

	public String getInlineParams() {
		return inlineParams;
	}

	protected String typeToString(Type type) {
		if (type instanceof EnumDefinition) {
			return ((EnumDefinition) type).getName();
		} else if (type instanceof EnumReference) {
			return ((EnumReference) type).getName();
		} else if (type instanceof StructDefinition) {
			return ((StructDefinition) type).getName();
		} else if (type instanceof StructReference) {
			return ((StructReference) type).getName();
		} else if (type instanceof UnionDefinition) {
			return ((UnionDefinition) type).getName();
		} else if (type instanceof UnionReference) {
			return ((UnionReference) type).getName();
		} else if (type instanceof TypeDefinition) {
			return ((TypeDefinition) type).getName();
		} else if (type instanceof TypeDefReference) {
			return ((TypeDefReference) type).getName();
		} else if (type instanceof ConstantDefinition) {
			return ((ConstantDefinition) type).getName();
		} else if (type instanceof PrimitiveType) {
			return ((PrimitiveType) type).getName();
		} else if (type instanceof ArrayOf) {
			// TODO:see IDL2C.stc arrayOfVarName for cleaner handling
			return typeToString(((ArrayOf) type).getType()) + " * "; 
		} else if (type instanceof PointerOf) {
			return typeToString(((ArrayOf) type).getType()) + " * ";
		} else return ""; // TODO: check even if this should never happen, or raise an error
	}

}