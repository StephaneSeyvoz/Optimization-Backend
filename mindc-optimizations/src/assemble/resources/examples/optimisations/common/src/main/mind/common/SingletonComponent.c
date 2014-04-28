
// -----------------------------------------------------------------------------
// Implementation of the primitive common.SingletonComponent.
// -----------------------------------------------------------------------------


/**
 * We need stdio as we will be using "printf" in this component
 */
#include <stdio.h>

/**
 * Here we simply print a string advertising :
 * <br/> - The current method : 'main'.
 * <br/> - The current interface name : 'serverInterface'.
 * <br/> - The current definition : 'staticbinding.SingletonComponent'
 * <br/>
 *
 * And return a success code.
 */
int METH(serverInterface,main)(int argc, char** argv)
{
	int returnValue=0;
	printf("This is the 'main' method of the 'serverInterface' interface of the 'common.SingletonComponent' definition \n");
	printf("Calling the 'main' method of out 'firstClientInterface' interface\n");
	returnValue += CALL(firstClientInterface,main)(argc,argv);
	if (IS_BOUND(secondClientInterface)) {
		printf("Calling the 'main' method of out 'secondClientInterface' interface\n");
		returnValue += CALL(secondClientInterface,main)(argc,argv);
	} else {
		printf("The 'secondClientInterface' interface is not bound : SKipping\n");
	}
	return returnValue;
}
