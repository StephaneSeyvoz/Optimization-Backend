
#include <stdio.h>

// int main(int argc, string[] argv)
int METH(entryPoint, main) (int argc, char *argv[]) {

	int r;

	void *serverInstance0;
	void *serverInstance1;

	// reused
	fractal_api_Component serverInstanceCompItf;

	const char *serverInterfaceName = "s";

	void *server0sItf;
	void *server1sItf;

	int i = 0;

	// create a server instance
	r = CALL(f, newFcInstance) (&serverInstance0);
	if (r != FRACTAL_API_OK) {
		printf("ERROR %d In client while creating instance 0\n", r);
		return r;
	}

	// Component controller is the first interface ?
	serverInstanceCompItf = (fractal_api_Component) serverInstance0;

	// Bind to the controller of the server instance
	BIND_MY_INTERFACE(compCtrl, serverInstanceCompItf);
	// Get its service
	r = CALL(compCtrl, getFcInterface)(serverInterfaceName, (void **)& server0sItf);
	if (r != FRACTAL_API_OK) {
		printf("ERROR %d In client while getting server interface 0\n", r);
		return r;
	}

	// Bind the client to the service
	BIND_MY_INTERFACE(s, server0sItf);

	// i + 1 through 's' client interface.
	i = CALL(s, increment)(i);
	// i - 1 through the same interface
	i = CALL(s, decrement)(i);

	// create a server instance
	r = CALL(f, newFcInstance) (&serverInstance1);
	if (r != FRACTAL_API_OK) {
		printf("ERROR %d In client while creating instance 1\n", r);
		return r;
	}

	// BEWARE, INSTANCE0 STILL EXISTS IN MEMORY !

	// Component controller is the first interface ?
	serverInstanceCompItf = (fractal_api_Component) serverInstance1;

	// Bind to the controller of the server instance
	BIND_MY_INTERFACE(compCtrl, serverInstanceCompItf);

	// Get its service
	r = CALL(compCtrl, getFcInterface)(serverInterfaceName, (void **)& server1sItf);
	if (r != FRACTAL_API_OK) {
		printf("ERROR %d In client while getting server interface 1\n", r);
		return r;
	}

	// Bind the client to the service
	BIND_MY_INTERFACE(s, server1sItf);

	// i + 1 through 's' client interface.
	i = CALL(s, increment)(i);
	// i - 1 through the same interface
	i = CALL(s, decrement)(i);


	//////////////////// RESTORE BINDING TO THE FIRST INSTANCE (0)

	// Bind the client to the service
	BIND_MY_INTERFACE(s, server0sItf);

	// i + 1 through 's' client interface.
	i = CALL(s, increment)(i);
	// i - 1 through the same interface
	i = CALL(s, decrement)(i);

	return i;
}
