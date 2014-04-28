EXAMPLES = garbage-composite static static-bindings static-definition-binding

all: common_all $(EXAMPLES:%=%.all)
clean: common_clean $(EXAMPLES:%=%.clean)

%.all:
	$(MAKE) -C $(@:%.all=%) compile CONFIGURATION=Default
	$(MAKE) -C $(@:%.all=%) compile CONFIGURATION=Ext

%.clean:
	$(MAKE) -C $(@:%.clean=%) clean CONFIGURATION=Default
	$(MAKE) -C $(@:%.clean=%) clean CONFIGURATION=Ext

common_all:
	$(MAKE) -C common compile CONFIGURATION=BaseApplication
	$(MAKE) -C common compile CONFIGURATION=CompositeApplication
	$(MAKE) -C common compile CONFIGURATION=MultiInstanceApplication

	
common_clean:
	$(MAKE) -C common clean CONFIGURATION=BaseApplication
	$(MAKE) -C common clean CONFIGURATION=CompositeApplication
	$(MAKE) -C common clean CONFIGURATION=MultiInstanceApplication
