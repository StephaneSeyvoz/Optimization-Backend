/**
 * mindc examples
 *
 * Copyright (C) 2010 STMicroelectronics
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 * Contact: mind@ow2.org
 *
 * Authors: Matthieu Leclercq
 */

/* -----------------------------------------------------------------------------
   Implementation of the entryPoint interface with signature boot.Main.
   -------------------------------------------------------------------------- */

#include <stdio.h>

/* int main(int argc, string[] argv) */
int METH(entryPoint, main) (int argc, char *argv[]) {

  int r;
  
  void *serverInstance0;
  void *serverInstance1;

  /* reused */
  fractal_api_Component serverInstanceCompItf;
  
  const char *serverInterfaceName = "s";
  const char *attributeControllerInterfaceName = "attributeController";
  
  void *acItf;
  void *server0sItf;
  void *server1sItf;

  const char *hello = "hello world !";
  const char *bye = "goodbye world !";

  /* create a server instance */
  r = CALL(f, newFcInstance) (&serverInstance0);
  if (r != FRACTAL_API_OK) {
    printf("ERROR %d In client while creating instance 0\n", r);
    return r;
  }
  
  /* Component controller is the first interface ? */
  serverInstanceCompItf = (fractal_api_Component) serverInstance0;
  
  /* Bind to the controller of the server instance */
  BIND_MY_INTERFACE(compCtrl, serverInstanceCompItf);
  /* Get its service */
  r = CALL(compCtrl, getFcInterface)(serverInterfaceName, (void **)& server0sItf);
  if (r != FRACTAL_API_OK) {
    printf("ERROR %d In client while getting server interface 0\n", r);
    return r;
  }
  
  /* Bind the client to the service */
  BIND_MY_INTERFACE(s, server0sItf);
  
  /* call the 'print' method of the 's' client interface. */
  CALL(s, print)(hello);
  /* call again the same interface to look at invocation count */
  CALL(s, println)(bye);

  /* create a server instance */
  r = CALL(f, newFcInstance) (&serverInstance1);
  if (r != FRACTAL_API_OK) {
    printf("ERROR %d In client while creating instance 1\n", r);
    return r;
  }
  
  /* BEWARE, INSTANCE0 STILL EXISTS IN MEMORY ! */
  
  /* Component controller is the first interface ? */
  serverInstanceCompItf = (fractal_api_Component) serverInstance1;
  
  /* Bind to the controller of the server instance */
  BIND_MY_INTERFACE(compCtrl, serverInstanceCompItf);
  
  /* Get its attributeController */
  r = CALL(compCtrl, getFcInterface)(attributeControllerInterfaceName, (void **)& acItf);
  if (r != FRACTAL_API_OK) {
    printf("ERROR %d In client while getting attribute controller\n", r);
    return r;
  }
  /* Bind the client to the AttributeController */
  BIND_MY_INTERFACE(attrCtrl, acItf);
  CALL(attrCtrl, setFcAttribute)("instance_number", (void*) 1);
  
  /* Get its service */
  r = CALL(compCtrl, getFcInterface)(serverInterfaceName, (void **)& server1sItf);
  if (r != FRACTAL_API_OK) {
    printf("ERROR %d In client while getting server interface 1\n", r);
    return r;
  }
  
  /* Bind the client to the service */
  BIND_MY_INTERFACE(s, server1sItf);
  
  /* call the 'print' method of the 's' client interface. */
  CALL(s, print)(hello);
  /* call again the same interface to look at invocation count */
  CALL(s, println)(bye);
  
  
  /* RESTORE BINDING TO THE FIRST INSTANCE (0) */
  
  /* Bind the client to the service */
  BIND_MY_INTERFACE(s, server0sItf);
  
  /* call the 'print' method of the 's' client interface. */
  CALL(s, print)(hello);
  /* call again the same interface to look at invocation count */
  CALL(s, println)(bye);
  
  return 0;
}
