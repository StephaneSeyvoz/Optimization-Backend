package org.ow2.mind.idl;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Here we refine the standard IDLHeaderCompiler to use our own StringTemplate.
 * @author Stephane Seyvoz
 *
 */
public class SingleInterfaceIDLHeaderCompiler extends IDLHeaderCompiler {

	/** The name to be used to inject the templateGroupName used by this class. */
	public static final String     TEMPLATE_NAME    = "optim.interfaces.IDL2C";

	/** The default templateGroupName used by this class. */
	public static final String     DEFAULT_TEMPLATE = "st.optim.interfaces.IDL2C";

	@Inject
	protected SingleInterfaceIDLHeaderCompiler(@Named(TEMPLATE_NAME) String templateGroupName) {
		super(templateGroupName);
	}

}
