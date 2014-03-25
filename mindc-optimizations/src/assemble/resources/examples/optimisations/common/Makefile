# If the MIND_ROOT variable is defined, look for 'mindc' and 'mindunit' in this directory
# otherwise, expect that 'mindc' and 'mindunit' are in the PATH.
ifdef MIND_ROOT
  # If MIND_ROOT contains '\', replace them by '/' (for Windows)
  MINDC = $(subst \,/, $(MIND_ROOT)/bin/mindc)
  MINDUNIT = $(subst \,/, $(MIND_ROOT)/bin/mindunit)
else 
  MINDC = mindc
  MINDUNIT = mindunit
endif

# Documentation generator command
MINDOC = mindoc

# Non-MindEd users should invoke this Makefile using "make CONFIGURATION=<configName> all" to run the build,
# where <configName> matches the desired <configName>.properties file.
# Set CONFIGURATION to Default if not set

CONFIGURATION ?= Default
include $(CONFIGURATION).properties

# Strings and comments management - Skip to "all:" for the real build.

#Platform Separator handling
PATH_SEPARATOR =:
ifneq (,$(findstring Windows, $(OS))) # Handle Windows-specific separators (':' otherwise)
    PATH_SEPARATOR =;
endif

sourcePathArg=
ifdef sourcePath
	ifneq ($(sourcePath), "")
		sourcePathArg=--src-path="$(subst :,$(PATH_SEPARATOR),$(subst ::,:,$(sourcePath)))"
	endif
endif

testSourcePathArg=
ifdef testSourcePath
	ifneq ($(sourcePath), "")
		testSourcePathArg="$(subst :,$(PATH_SEPARATOR),$(subst ::,:,$(testSourcePath)))"
	endif
endif

includePathArg=
ifdef includePath
	ifneq ($(includePath), "")
		includePathArg=--inc-path="$(subst :,$(PATH_SEPARATOR),$(subst ::,:,$(includePath)))"
	endif
endif

compilerCommandArg=
ifdef compilerCommand
	ifneq ($(compilerCommand), "")
		compilerCommandArg=--compiler-command="$(compilerCommand)"
	endif
endif

linkerCommandArg=
ifdef linkerCommand
	ifneq ($(linkerCommand), "")
		linkerCommandArg=--linker-command="$(linkerCommand)"
	endif
endif

assemblerCommandArg=
ifdef assemblerCommand
	ifneq ($(assemblerCommand), "")
		assemblerCommandArg=--assembler-command="$(assemblerCommand)"
	endif
endif

asFlagsArg=
ifdef asFlags
	ifneq ($(asFlags), "")
		asFlagsArg=--as-flags="$(asFlags)"
	endif
endif

cppFlagsArg=
ifdef cppFlags
	ifneq ($(cppFlags), "")
		cppFlagsArg=--cpp-flags="$(cppFlags)"
	endif
endif

cFlagsArg=
ifdef cFlags
	ifneq ($(cFlags), "")
		cFlagsArg=--c-flags="$(cFlags)"
	endif
endif

ldFlagsArg=
ifdef ldFlags
	ifneq ($(ldFlags), "")
		ldFlagsArg=--ld-flags="$(ldFlags)"
	endif
endif

extraOptionsArg=
ifdef extraOptions
	ifneq ($(extraOptions), "")
		extraOptionsArg="$(extraOptions)"
	endif
endif

testExtraOptionsArg=
ifdef testExtraOptions
	ifneq ($(testExtraOptions), "")
		testExtraOptionsArg="$(testExtraOptions)"
	endif
endif

#default value
confOutputDirVar=target/$(CONFIGURATION)
ifdef outputDirectory
	ifneq ($(outputDirectory), "")
		confOutputDirVar="$(subst :,$(PATH_SEPARATOR),$(subst ::,:,$(outputDirectory)))/$(CONFIGURATION)"
	endif
endif

# folder utilities
empty=
space= $(empty) $(empty)
# if MIND_ROOT is not defined, 'runtime' folder components such as the Bootstrap or the Main interface
# will not be documented and this can lead to broken links. Within MindEd, MIND_ROOT should always be
# automatically defined. However, for common builds from the terminal, you should define it or at least
# do a dry run of "mindc" once for MIND_ROOT to be defined for the terminal session. 
RUNTIME_PATH=$(subst \,/, $(MIND_ROOT))/runtime

binOutputDir=$(confOutputDirVar)/binaries
docOutputDir=$(confOutputDirVar)/doc
graphOutputDir=$(confOutputDirVar)/graph
testBinOutputDir=$(confOutputDirVar)/test-binaries
testDocOutputDir=$(confOutputDirVar)/test-doc

# The real build command.

all: build
ifdef targetComponent
	if test ! -x $(MINDC); \
        then @echo ' Not found mindc ! Set MIND_ROOT variable or go to 'Mindc location' eclipse preference page'; \
    	else $(MINDC) $(sourcePathArg) $(includePathArg) $(compilerCommandArg) $(linkerCommandArg) $(assemblerCommandArg) $(asFlagsArg) $(cppFlagsArg) $(cFlagsArg) $(ldFlagsArg) $(extraOptionsArg) -o=$(binOutputDir) $(targetComponent):$(binaryName) ;\
    fi
else
	@echo "No application defined - MindEd users: Please configure your project target with the 'Mind Path' -> (right click) 'New application' menu."
endif

graph: build_graph
ifdef targetComponent
	if test ! -x $(MINDC); \
        then @echo ' Not found mindc ! Set MIND_ROOT variable or go to 'Mindc location' eclipse preference page'; \
    	else $(MINDC) --check-adl $(sourcePathArg) $(includePathArg) $(compilerCommandArg) $(linkerCommandArg) $(assemblerCommandArg) $(asFlagsArg) $(cppFlagsArg) $(cFlagsArg) $(ldFlagsArg) $(extraOptionsArg) -o=$(graphOutputDir) 'dot.DumpComponentDot<'$(targetComponent)'>' ;\
    fi
else
	@echo "No application defined - MindEd users: Please configure your project target with the 'Mind Path' -> (right click) 'New application' menu."
endif

doc: build_doc
	$(MINDOC) $(subst $(PATH_SEPARATOR),$(space),$(subst :,$(PATH_SEPARATOR),$(sourcePath))) $(RUNTIME_PATH) -d $(docOutputDir)

# here we reuse the same flags as the common build ones
test: build_test
	$(MINDUNIT) $(sourcePathArg) $(includePathArg) $(compilerCommandArg) $(linkerCommandArg) $(assemblerCommandArg) $(asFlagsArg) $(cppFlagsArg) $(cFlagsArg) $(ldFlagsArg) $(extraOptionsArg) $(testExtraOptionsArg) -o=$(testBinOutputDir) $(testSourcePathArg)

# Output folder handling.

build:
	mkdir -p $(binOutputDir)
	
build_doc:
	mkdir -p $(docOutputDir)
	
build_test:
	mkdir -p $(testBinOutputDir)
	
build_graph:
	mkdir -p $(graphOutputDir)

clean:
	rm -Rf $(binOutputDir); \
	mkdir -p $(binOutputDir)
	
clean_doc:
	rm -Rf $(docOutputDir); \
	mkdir -p $(docOutputDir)
	
clean_test:
	rm -Rf $(testBinOutputDir); \
	mkdir -p $(testBinOutputDir)
	
clean_graph:
	rm -Rf $(graphOutputDir); \
	mkdir -p $(graphOutputDir)

.PHONY: all clean build
