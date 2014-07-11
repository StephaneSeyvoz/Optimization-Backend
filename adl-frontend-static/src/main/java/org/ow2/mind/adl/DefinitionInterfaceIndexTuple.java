package org.ow2.mind.adl;

import java.io.Serializable;

import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.interfaces.Interface;

class DefinitionInterfaceIndexTuple implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7564568010419749906L;
	
	public Definition definition;
	public Interface interfaceInstance;
	public Integer index;
	
	public DefinitionInterfaceIndexTuple(Definition definition, Interface interfaceInstance, Integer index) {
		this.definition = definition;
		this.interfaceInstance = interfaceInstance;
		this.index = index;
	}
	
	@Override
	public boolean equals(Object object){
		if (!(object instanceof DefinitionInterfaceIndexTuple))
			return false;
		
		DefinitionInterfaceIndexTuple testedPair = (DefinitionInterfaceIndexTuple) object;
		if (testedPair.definition == this.definition && testedPair.interfaceInstance == this.interfaceInstance && testedPair.index.equals(this.index))
			return true;

		return false;
	}
	
}