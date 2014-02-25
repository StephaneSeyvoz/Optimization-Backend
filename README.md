Optimization-Backend
====================

An experimental optimization backend plugin for the Mindc compiler. The optimizations are expressed by the mean of architectural annotations, and code generation uses new StringTemplates such as optimized macros for calls, and various component structures metadata will not be generated at compile-time, leading to a smaller footprint in text and data.


################################################################################

To generate the User and Developer Guides, perform the following command in the
mind-compiler directory :

$ mvn -N docbkx:generate-html docbkx:generate-pdf

The PDF and HTML documents can then be found in the <module-dir>/target/site
directory.

When editing docbooks, it is recommended to "touch" top-level docbook files 
before executing the maven-docbkx-plugin. Indeed, docbook sources are split in
various XML files which is not correctly handle by the plugin (if the top-level
file has not been updated, the plugin will not re-compile the doc). So when 
editing the documentation the command should be :

$ touch src/docbkx/*.xml; mvn  -N docbkx:generate-html docbkx:generate-pdf