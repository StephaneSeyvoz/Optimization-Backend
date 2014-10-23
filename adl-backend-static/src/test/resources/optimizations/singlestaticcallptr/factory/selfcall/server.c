#include <stdio.h>

void METH(s, print)(string msg) {
  /* retrieve the value of the "header" attribute defined in "helloworld.Server"
     ADL. */
  string h = ATTR(header);

  /* retrieve the value of the "instance_number" attribute */
  int c = ATTR(instance_number);

  if (ATTR(instance_number) == 0)
	CALL(s, println)(msg);
  
  /* Use printf to print message. */
  printf("%s(instance_number=%d) %s", h, c, msg);
}

void METH(s, println)(string msg) {
  if (ATTR(instance_number) == 1)
	CALL(s, print)(msg);
  printf("%s(instance_number=%d) %s\n", ATTR(header), ATTR(instance_number), msg);
}

singlestaticcallptr_factory_selfcall_Service METH(getServicePtrItf, getServicePtr)(void){
	return (singlestaticcallptr_factory_selfcall_Service) GET_MY_INTERFACE(s);
}
