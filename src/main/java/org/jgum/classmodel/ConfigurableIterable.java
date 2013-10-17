package org.jgum.classmodel;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jgum.path.SearchStrategy;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ConfigurableIterable<T extends AbstractClassNode<?>> implements Iterable<T> {

	private ClassTraversalPolicy classTraversalPolicy;
	private T abstractClassPropertiesNode;

	private List<T> children;
	private Iterable<T> nextNodeIterable;
	
	public ConfigurableIterable(T abstractClassPropertiesNode, Direction direction, ClassTraversalPolicy classTraversalPolicy) {
		this.abstractClassPropertiesNode = abstractClassPropertiesNode;
		this.classTraversalPolicy = classTraversalPolicy;
		
		List<ClassNode<?>> classPropertiesIterator;
		List<InterfaceNode<?>> interfacePropertiesIterator;
		
		if(direction.equals(Direction.BOTTOM_UP)) {
			if(abstractClassPropertiesNode instanceof ClassNode) {
				ClassNode<?> classPropertiesNode = (ClassNode<?>) abstractClassPropertiesNode;
				classPropertiesIterator = (List)asList(classPropertiesNode.getSuperClassNode());				
			} else { //it is an InterfaceNode or a ClassHierarchyRoot
				classPropertiesIterator = Collections.emptyList();
			}
			if(classTraversalPolicy.interfaceOrder.equals(InterfaceOrder.DIRECT)) {
				interfacePropertiesIterator = (List)abstractClassPropertiesNode.getSuperInterfaceNodes();
			} else { //INVERSE
				interfacePropertiesIterator = (List)Lists.reverse(abstractClassPropertiesNode.getSuperInterfaceNodes());
			}
		} else { //TOP_DOWN
			if(abstractClassPropertiesNode instanceof ClassNode) {
				ClassNode<?> classPropertiesNode = (ClassNode<?>) abstractClassPropertiesNode;
				classPropertiesIterator = (List)classPropertiesNode.getKnownSubClassNodes();
				interfacePropertiesIterator = Collections.emptyList();
			} else if(abstractClassPropertiesNode instanceof InterfaceNode) { //it is an InterfaceNode
				InterfaceNode<?> interfacePropertiesNode = (InterfaceNode<?>) abstractClassPropertiesNode;
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
			nextNodeIterable = Iterables.filter(nextNodeIterable, new Predicate<AbstractClassNode<?>>() {
				@Override
				public boolean apply(AbstractClassNode<?> node) {
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

