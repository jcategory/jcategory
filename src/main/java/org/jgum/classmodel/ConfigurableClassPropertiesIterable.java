package org.jgum.classmodel;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jgum.SearchStrategy;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ConfigurableClassPropertiesIterable<T extends AbstractClassPropertiesNode<?>> implements Iterable<T> {

	private ClassTraversalPolicy classTraversalPolicy;
	private T abstractClassPropertiesNode;

	private List<T> children;
	private Iterable<T> nextNodeIterable;
	
	public ConfigurableClassPropertiesIterable(T abstractClassPropertiesNode, Direction direction, ClassTraversalPolicy classTraversalPolicy) {
		this.abstractClassPropertiesNode = abstractClassPropertiesNode;
		this.classTraversalPolicy = classTraversalPolicy;
		
		List<ClassPropertiesNode<?>> classPropertiesIterator;
		List<InterfacePropertiesNode<?>> interfacePropertiesIterator;
		
		if(direction.equals(Direction.BOTTOM_UP)) {
			if(abstractClassPropertiesNode instanceof ClassPropertiesNode) {
				ClassPropertiesNode<?> classPropertiesNode = (ClassPropertiesNode<?>) abstractClassPropertiesNode;
				classPropertiesIterator = (List)asList(classPropertiesNode.getSuperClassNode());				
			} else { //it is an InterfacePropertiesNode or a ClassHierarchyRoot
				classPropertiesIterator = Collections.emptyList();
			}
			if(classTraversalPolicy.interfaceOrder.equals(InterfaceOrder.DECLARATION_ORDER)) {
				interfacePropertiesIterator = (List)abstractClassPropertiesNode.getSuperInterfaceNodes();
			} else { //INVERSE_DECLARATION_ORDER
				interfacePropertiesIterator = (List)Lists.reverse(abstractClassPropertiesNode.getSuperInterfaceNodes());
			}
		} else { //TOP_DOWN
			if(abstractClassPropertiesNode instanceof ClassPropertiesNode) {
				ClassPropertiesNode<?> classPropertiesNode = (ClassPropertiesNode<?>) abstractClassPropertiesNode;
				classPropertiesIterator = (List)classPropertiesNode.getKnownSubClassNodes();
				interfacePropertiesIterator = Collections.emptyList();
			} else if(abstractClassPropertiesNode instanceof InterfacePropertiesNode) { //it is an InterfacePropertiesNode
				InterfacePropertiesNode<?> interfacePropertiesNode = (InterfacePropertiesNode<?>) abstractClassPropertiesNode;
				classPropertiesIterator = (List)interfacePropertiesNode.getKnownImplementorNodes();
				interfacePropertiesIterator = (List)interfacePropertiesNode.getKnownSubInterfaceNodes();
			} else { //it is a ClassHierarchyRoot
				ClassHierarchyRoot hierarchyRoot = (ClassHierarchyRoot) abstractClassPropertiesNode;
				classPropertiesIterator = (List)asList(hierarchyRoot.getObjectClassNode());
				interfacePropertiesIterator = (List)hierarchyRoot.getRootInterfaceNodes();
			}
		}
		
		if(classTraversalPolicy.priority.equals(Priority.CLASSES_FIRST)) {
			children = (List)classPropertiesIterator;
			children.addAll((List)interfacePropertiesIterator);
		} else { //INTERFACES_FIRST
			children = (List)interfacePropertiesIterator;
			children.addAll((List)classPropertiesIterator);
		}
		
		ClassHierarchyTraverser traverser = new ClassHierarchyTraverser(direction, classTraversalPolicy);
		if(classTraversalPolicy.searchStrategy.equals(SearchStrategy.PRE_ORDER)) {
			nextNodeIterable = traverser.preOrderTraversal(abstractClassPropertiesNode);
		} else if(classTraversalPolicy.searchStrategy.equals(SearchStrategy.POST_ORDER)) {
			nextNodeIterable = traverser.postOrderTraversal(abstractClassPropertiesNode);
		} else { //BREADTH_FIRST
			nextNodeIterable = traverser.breadthFirstTraversal(abstractClassPropertiesNode);
		}
		if(abstractClassPropertiesNode instanceof ClassHierarchyRoot) {
			nextNodeIterable = Iterables.filter(nextNodeIterable, new Predicate<AbstractClassPropertiesNode<?>>() {
				@Override
				public boolean apply(AbstractClassPropertiesNode<?> node) {
					return !(node instanceof ClassHierarchyRoot);
				}
			});
		}
	}

	public Iterable<T> getChildren() {
		return children;
	}

	@Override
	public Iterator<T> iterator() {
		return nextNodeIterable.iterator();
	}

}

