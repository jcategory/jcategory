package org.jgum.classmodel;

import java.util.Collections;
import java.util.List;

import org.jgum.Path;
import org.jgum.PropertiesNode;

public class AbstractClassPropertiesNode<T> extends PropertiesNode {

	protected Class<T> wrappedClazz;
	private List<InterfacePropertiesNode<? super T>> superInterfaceNodes;

	public AbstractClassPropertiesNode(Class<T> wrappedClazz) {
		this(wrappedClazz, Collections.<InterfacePropertiesNode<? super T>>emptyList());
	}
	
	public AbstractClassPropertiesNode(Class<T> wrappedClazz, List<InterfacePropertiesNode<? super T>> superInterfaceNodes) {
		this.wrappedClazz = wrappedClazz;
		setSuperInterfaceNodes(superInterfaceNodes);
	}
	
	public Class<T> getWrappedClass() {
		return wrappedClazz;
	}
	
	public List<InterfacePropertiesNode<? super T>> getSuperInterfaceNodes() {
		return superInterfaceNodes;
	}
	
	protected void setSuperInterfaceNodes(List<InterfacePropertiesNode<? super T>> superInterfaceNodes) {
		this.superInterfaceNodes = superInterfaceNodes;
	}
	
	public <U extends AbstractClassPropertiesNode<?>> Path<U> path(Direction direction, ClassTraversalPolicy classTraversalPolicy) {
		return new Path(new ConfigurableClassPropertiesIterable(this, direction, classTraversalPolicy));
	}

}
