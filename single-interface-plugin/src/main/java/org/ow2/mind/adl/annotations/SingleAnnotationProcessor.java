package org.ow2.mind.adl.annotations;

import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Node;
import org.objectweb.fractal.adl.interfaces.Interface;
import org.objectweb.fractal.adl.types.TypeInterface;
import org.ow2.mind.adl.annotation.ADLLoaderPhase;
import org.ow2.mind.adl.annotation.AbstractADLLoaderAnnotationProcessor;
import org.ow2.mind.adl.ast.ASTHelper;
import org.ow2.mind.adl.ast.Binding;
import org.ow2.mind.adl.ast.BindingContainer;
import org.ow2.mind.adl.ast.Component;
import org.ow2.mind.adl.ast.MindInterface;
import org.ow2.mind.annotation.Annotation;
import org.ow2.mind.idl.IDLLoader;
import org.ow2.mind.idl.ast.IDL;

import com.google.inject.Inject;

public class SingleAnnotationProcessor extends AbstractADLLoaderAnnotationProcessor {

	@Inject
	protected IDLLoader idlLoaderItf;
	
	public Definition processAnnotation(Annotation annotation, Node node,
			Definition definition, ADLLoaderPhase phase,
			Map<Object, Object> context) throws ADLException {


		if (phase == ADLLoaderPhase.AFTER_CHECKING) {
			assert node instanceof MindInterface;

			MindInterface annotatedItf = (MindInterface) node;

			if (annotatedItf.getRole() != TypeInterface.SERVER_ROLE)
				return null; // TODO: raise a warning or an error ?

			// Get the interface type from its signature
			IDL targetIDL = idlLoaderItf.load(annotatedItf.getSignature(), context);
			// We will use this decoration in our st.optim.interfaces.IDL2C StringTemplate
			targetIDL.astSetDecoration("single-itf", true);
			
			// We also need to tag the instance as binding initialization (INIT_INSTANCE macros in .adl.h)
			// deals with interface instance and not types (we wish to bind s1, s2, s3... of same type s)
			annotatedItf.astSetDecoration("single-itf", true);
			
			return null;
		} else if (phase == ADLLoaderPhase.ON_SUB_COMPONENT) {
			/*
			 * This phase is used to get a level higher (host composite) in the architecture
			 * in order to list bindings leading to the current component instance, and for all of 
			 * the possible Single-annotated interfaces, decorate the according binding source interfaces
			 * with "target-is-single-itf" in order for the OptimizedMacro StringTemplate to know if
			 * we can change the function call to a static one (but keeping the context pointer).
			 */
			assert definition instanceof BindingContainer;
			Binding[] innerBindings = ((BindingContainer) definition).getBindings();
			for (Binding currBinding : innerBindings) {
				Component targetComp = ASTHelper.getComponent(definition, currBinding.getToComponent());
				// Is the binding concerning our component ?
				if (targetComp == node) {
					// get the server component definition
					Definition currentServerDef = ASTHelper.getResolvedComponentDefinition(targetComp, loaderItf, context);
					// get the according interface in its context
					Interface targetItf = ASTHelper.getInterface(currentServerDef, currBinding.getToInterface());
					assert targetItf instanceof MindInterface;
					IDL targetIDL = idlLoaderItf.load(((MindInterface) targetItf).getSignature(), context);
					// Is the interface concerned by @Single ?
					Boolean isSingleItf = (Boolean) targetIDL.astGetDecoration("single-itf");
					if (isSingleItf) {
						// Then we will decorate the IDL source of the binding
						Component sourceComp = ASTHelper.getComponent(definition, currBinding.getFromComponent());
						Definition currentClientDef = ASTHelper.getResolvedComponentDefinition(sourceComp, loaderItf, context);
						Interface sourceItf = ASTHelper.getInterface(currentClientDef, currBinding.getFromInterface());
						assert sourceItf instanceof MindInterface;
//						IDL sourceIDL = idlLoaderItf.load(((MindInterface) sourceItf).getSignature(), context);
//						sourceIDL.astSetDecoration("target-is-single-itf", "true");
						sourceItf.astSetDecoration("target-is-single-itf", true);
					}
				}
					
			}
			return null;
		} else
			return null;
	}

}
