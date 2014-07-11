package org.ow2.mind.adl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Node;
import org.objectweb.fractal.adl.interfaces.Interface;
import org.objectweb.fractal.adl.interfaces.InterfaceContainer;
import org.objectweb.fractal.adl.util.FractalADLLogManager;
import org.ow2.mind.adl.annotation.ADLLoaderPhase;
import org.ow2.mind.adl.annotation.AbstractADLLoaderAnnotationProcessor;
import org.ow2.mind.adl.annotation.predefined.StaticDefinitionBinding;
import org.ow2.mind.adl.annotation.predefined.StaticDefinitionsBindingsList;
import org.ow2.mind.adl.ast.MindInterface;
import org.ow2.mind.adl.ast.OptimASTHelper;
import org.ow2.mind.annotation.Annotation;
import org.ow2.mind.cli.OptimOptionHandler;

public class StaticDefinitionsBindingsListAnnotationProcessor extends
AbstractADLLoaderAnnotationProcessor {

	protected static Logger        sdblapLogger        = FractalADLLogManager
			.getLogger("sdblap");

	@SuppressWarnings("unchecked")
	public Definition processAnnotation(Annotation annotation, Node node,
			Definition definition, ADLLoaderPhase phase,
			Map<Object, Object> context) throws ADLException {

		assert annotation instanceof StaticDefinitionsBindingsList;

		if (!OptimOptionHandler.isOptimizationBackendEnabled(context))
			logger.warning("@StaticBindingsList found in '" + definition.getName() + "' definition, but --optimize is missing ! Add it to enable optimizations.");
		
		StaticDefinitionsBindingsList anno = (StaticDefinitionsBindingsList) annotation;
		StaticDefinitionBinding[] arrayOfDefinitionsBinding = anno.value;

		// Here we need a need to find the according definition which SHOULD be used in the composite
		// and decorate its according interface with the destination interface name, so as to
		// build the call the correct way
		Definition sourceDef = null;
		Definition targetDef = null;
		for (StaticDefinitionBinding currDefBdg : arrayOfDefinitionsBinding) {
			int idx = currDefBdg.fromItf.lastIndexOf(".");
			String sourceDefName = currDefBdg.fromItf.substring(0, idx);
			String sourceItfInstanceName = currDefBdg.fromItf.substring(idx + 1, currDefBdg.fromItf.length());

			idx = currDefBdg.toItf.lastIndexOf(".");
			String targetDefName = currDefBdg.toItf.substring(0, idx);
			String targetItfInstanceName = currDefBdg.toItf.substring(idx + 1, currDefBdg.toItf.length());

			try {
				sourceDef = loaderItf.load(sourceDefName, context);
			} catch (ADLException e) {
				sdblapLogger.log(Level.WARNING, sourceDefName + " definition not found, @StaticDefinitionsBinding(" + currDefBdg.fromItf + ", " + currDefBdg.toItf + ") skipped !");
				continue;
			}

			try {
				targetDef = loaderItf.load(targetDefName, context);
			} catch (ADLException e) {
				sdblapLogger.log(Level.WARNING, targetDefName + " definition not found, @StaticDefinitionsBinding(" + currDefBdg.fromItf + ", " + currDefBdg.toItf + ") skipped !");
				continue;
			}

			// both sourceDef and targetDef should be InterfaceContainer-s
			assert sourceDef instanceof InterfaceContainer;
			assert targetDef instanceof InterfaceContainer;

			Interface[] sourceDefItfs = ((InterfaceContainer)sourceDef).getInterfaces();

			// CHECK IF THE DESTINATION INTERFACE REALLY EXISTS
			Boolean foundTargetItfInstance = false;
			Interface[] targetDefItfs = ((InterfaceContainer)targetDef).getInterfaces();
			for (Interface currItf : targetDefItfs) {
				assert currItf instanceof MindInterface;
				if (((MindInterface) currItf).getRole() == MindInterface.SERVER_ROLE && currItf.getName().equals(targetItfInstanceName)) {
					foundTargetItfInstance = true;

					// as in StaticAnnotationProcessor (slightly adapted)
					if (currDefBdg.inline) {
						// Decorate the server interface for later optimization
						OptimASTHelper.setStaticDecoration(currItf);
						// In the OptimizedDefinitionCompiler we want to know if
						// the current definition (server) will have to generate
						// a <Definition.name>.inline file
						OptimASTHelper.setInlineDecoration(targetDef);
					}

					break;
				}
			}

			// if the destination exists then we decorate to propagate the symbol for the CALL
			if (foundTargetItfInstance)
				for (Interface currItf : sourceDefItfs) {
					assert currItf instanceof MindInterface;
					if (((MindInterface) currItf).getRole() == MindInterface.CLIENT_ROLE && currItf.getName().equals(sourceItfInstanceName)) {
						// found the interface, do the job
						currItf.astSetDecoration("static-definition-binding-target-itf", "__component_" + targetDefName.replace(".", "_") + "_" + targetItfInstanceName);
						
						// as in StaticAnnotationProcessor (slightly adapted)
						if (currDefBdg.inline) {
							OptimASTHelper.setInlineDecoration(currItf);
							
							// We need for the client to know which target <Definition.name>.inline
							// files to -include (in the OptimizedDefinitionCompiler)
							List<Definition> inlineTargetDefinitions = null;
							Object oldInlineTargetDefinitions = currItf.astGetDecoration("inline-target-defs");
							if (oldInlineTargetDefinitions == null)
								inlineTargetDefinitions = new ArrayList<Definition>();
							else inlineTargetDefinitions = (List<Definition>) oldInlineTargetDefinitions; // TODO add check

							inlineTargetDefinitions.add(targetDef);
							sourceDef.astSetDecoration("inline-target-defs", inlineTargetDefinitions);
						}

						break;
					}
				}
			else
				sdblapLogger.log(Level.WARNING, targetItfInstanceName + " interface not found, @StaticDefinitionsBinding(" + currDefBdg.fromItf + ", " + currDefBdg.toItf + ") skipped !");

		}

		return null;
	}

}
