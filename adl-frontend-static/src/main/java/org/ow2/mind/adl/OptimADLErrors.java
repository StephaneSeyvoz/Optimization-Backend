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

package org.ow2.mind.adl;

import static org.objectweb.fractal.adl.error.ErrorTemplateValidator.validErrorTemplate;

import org.objectweb.fractal.adl.error.ErrorTemplate;

/**
 * Enumeration of Errors that can be raised by the ADL-front-end.
 */
public enum OptimADLErrors implements ErrorTemplate {

  // ---------------------------------------------------------------------------
  // All standard Errors : @see org.ow2.mind.adl.ADLErrors
  // ---------------------------------------------------------------------------

	/*
	 * These errors are specifically related to our optimizations and annotations
	 */

  // ---------------------------------------------------------------------------
  // Bindings errors (120-149)
  // ---------------------------------------------------------------------------

  /** */
  INVALID_STATICBINDINGS_NOT_A_COMPOSITE(
      123,
      "Invalid @StaticBindings annotation, definition is not a composite"),      
          
  /** */
  INVALID_STATIC_BINDING_SOURCE_NOT_SINGLETON(
      124,
      "Invalid @Static annotation, source of the binding must be a @Singleton"),     
      
  /** */
  INVALID_STATIC_BINDING_DESTINATION_NOT_SINGLETON(
      125,
      "Invalid @Static annotation, destination of the binding must be a @Singleton"),
  
  /** */
  INVALID_STATICBINDINGS_NOT_SINGLETON(
      126,
      "Invalid @StaticBindings annotation, definition must be a @Singleton"),
      
  /** */
  INVALID_STATICBINDINGS_SOURCE_NOT_SINGLETON(
      127,
      "Host component @StaticBindings annotation cannot be applied here, the binding source component must be @Singleton"),
      
  /** */
  INVALID_STATICBINDINGS_DESTINATION_NOT_SINGLETON(
      128,
      "Host component @StaticBindings annotation cannot be applied here, the binding destination component must be @Singleton"),
  
  /** */
  INVALID_STATICBINDINGS_HOSTING_BINDINGCONTROLLER(
      129,
      "@controller.BindingController incompatible with host composite @StaticBindings"),
      
  // ---------------------------------------------------------------------------
  // Membrane errors (150-159)
  // ---------------------------------------------------------------------------

  /** */
  INVALID_GARBAGEUNUSEDINTERNALS_NOT_SINGLETON(
	  153,
	  "Invalid @GarbageUnusedInternals annotation, definition must be a @Singleton");

  /** The groupId of ErrorTemplates defined in this enumeration. */
  public static final String GROUP_ID = "MADL";

  private int                id;
  private String             format;

  private OptimADLErrors(final int id, final String format, final Object... args) {
    this.id = id;
    this.format = format;

    assert validErrorTemplate(this, args);
  }

  public int getErrorId() {
    return id;
  }

  public String getGroupId() {
    return GROUP_ID;
  }

  public String getFormatedMessage(final Object... args) {
    return String.format(format, args);
  }

  public String getFormat() {
    return format;
  }

}
