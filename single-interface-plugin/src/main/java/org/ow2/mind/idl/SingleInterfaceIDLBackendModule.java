package org.ow2.mind.idl;

import org.ow2.mind.inject.AbstractMindModule;

import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

public class SingleInterfaceIDLBackendModule extends AbstractMindModule {

	/*
	 * SSZ: We use a SingleInterfaceGuiceAnnotation to differentiate THIS MultiBinder from the standard MultiBinder.
	 * Otherwise, the Google Guice MultiBinder API (at least in version 2.0) gives you the MultiBinder already populated in
	 * the Standard IDL Backend Module.
	 * 
	 * Not using this means queueing our Compiler in the delegation chain, which works like a stack, meaning our
	 * generator is called first, then the files would be overwritter by the standard IDL backend. 
	 * 
	 * We discriminate the code generators injection by using our custom @SingleInterfaceGuiceAnnotation
	 * on the injection field, recognized by Google Guice as it implements @BindingAnnotation.
	 * 
	 * For more information, see "public static Multibinder<T> newSetBinder (Binder binder, Class<T> type, Annotation annotation)" at:
	 * http://google-guice.googlecode.com/svn/trunk/latest-javadoc/com/google/inject/multibindings/Multibinder.html
	 * and
	 * http://code.google.com/p/google-guice/wiki/BindingAnnotations
	 * 
	 * The same way was used in the ADL Static Backend.
	 */

	protected void configureIDLVisitor() {
		// We override the Dispatcher to use our SingleInterfaceGuiceAnnotation-annotated set of bindings
		// and visitors, not the original ones
		bind(IDLVisitor.class).toChainStartingWith(IncludeCompiler.class)
		.endingWith(SingleInterfaceIDLVisitorDispatcher.class);

		// new set of bindings to replace the original one
		final Multibinder<IDLVisitor> setBinder = Multibinder.newSetBinder(
				binder(), IDLVisitor.class, SingleInterfaceGuiceAnnotation.class);

		// Here we use our own IDLHeaderCompiler to change the StringTemplate that will be used
		setBinder.addBinding().to(SingleInterfaceIDLHeaderCompiler.class);
		setBinder.addBinding().to(BinaryIDLWriter.class);
	}

	protected void configureIDLHeaderCompilerGenerator() {
		bind(String.class).annotatedWith(
				Names.named(SingleInterfaceIDLHeaderCompiler.TEMPLATE_NAME)).toInstance(
						SingleInterfaceIDLHeaderCompiler.DEFAULT_TEMPLATE);
	}

}
