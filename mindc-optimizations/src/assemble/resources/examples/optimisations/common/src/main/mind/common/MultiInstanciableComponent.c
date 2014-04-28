
// -----------------------------------------------------------------------------
// Implementation of the primitive common.MultiInstanciableComponent.
// -----------------------------------------------------------------------------

/**
 * We need stdio as we will be using "printf" in this component
 */
#include <stdio.h>

/**
 * Here we simply print a string advertising :
 * <br/> - The current method : 'main'.
 * <br/> - The current interface name : 'serverInterface'.
 * <br/> - The current definition : 'staticbinding.MultiInstanciableComponent'
 * <br/>And return a success code.
 */
int METH(serverInterface,main)(int argc, char** argv)
{
	printf("This is the 'main' method of the 'serverInterface' interface of the 'common.MultiInstanciableComponent' definition \n");
	return 0;
}
