single-interface-plugin
=======================

An experimental plugin (working jointly with the optimization-backend) to allow hard-linking of client and server definitions between fixed interface instances. This optimization is interesting in the case of large pools of component instances of the same type (such as data providers and buffers encapsulation). The gain is both in execution time (direct calls) and memory (vTable removal). Dynamic bindings between different instances of the same types are intended to be supported but the link is frozen between concerned definitions.