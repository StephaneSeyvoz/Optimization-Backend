EXAMPLES = $(filter-out common, $(patsubst %/,%, $(sort $(dir $(wildcard */)))))

all: common_all $(EXAMPLES:%=%.all)
clean: common_clean $(EXAMPLES:%=%.clean)

%.all:
	$(MAKE) -C $(@:%.all=%) all CONFIGURATION=Default
	$(MAKE) -C $(@:%.all=%) all CONFIGURATION=Ext

%.clean:
	$(MAKE) -C $(@:%.clean=%) clean CONFIGURATION=Default
	$(MAKE) -C $(@:%.clean=%) clean CONFIGURATION=Ext

common_all:
	$(MAKE) -C common all CONFIGURATION=BaseApplication
	$(MAKE) -C common all CONFIGURATION=CompositeApplication
	$(MAKE) -C common all CONFIGURATION=MultiInstanceApplication

	
common_clean:
	$(MAKE) -C common clean CONFIGURATION=BaseApplication
	$(MAKE) -C common clean CONFIGURATION=CompositeApplication
	$(MAKE) -C common clean CONFIGURATION=MultiInstanceApplication
