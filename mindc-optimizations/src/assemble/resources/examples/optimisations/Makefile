SUBDIRS = $(filter-out common, $(patsubst %/,%, $(sort $(dir $(wildcard */)))))

.PHONY: $(MAKECMDGOALS) $(SUBDIRS) subdir common

# To handle the case of $(MAKECMDGOALS) is empty
subdir: $(SUBDIRS)

$(SUBDIRS): common
	$(MAKE) -C $@ CONFIGURATION=Default
	$(MAKE) -C $@ CONFIGURATION=Ext

# To handle the case of $(MAKECMDGOALS) is not empty
$(MAKECMDGOALS): common
	@for dir in $(SUBDIRS); do \
        $(MAKE) -C $$dir $(MAKECMDGOALS) CONFIGURATION=Default || exit $$? ;\
        $(MAKE) -C $$dir $(MAKECMDGOALS) CONFIGURATION=Ext || exit $$? ;\
    done

common:
	$(MAKE) -C common $(MAKECMDGOALS) CONFIGURATION=BaseApplication
	$(MAKE) -C common $(MAKECMDGOALS) CONFIGURATION=CompositeApplication
	$(MAKE) -C common $(MAKECMDGOALS) CONFIGURATION=MultiInstanceApplication
