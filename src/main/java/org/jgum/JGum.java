package org.jgum;

import org.jgum.classmodel.ClassHierarchyRoot;
import org.jgum.classmodel.ClassTraversalPolicy;
import org.jgum.classmodel.InterfaceOrder;
import org.jgum.classmodel.Priority;
import org.jgum.packagemodel.PackageHierarchyRoot;
import org.jgum.packagemodel.PackageNode;
import org.jgum.path.CycleDetection;
import org.jgum.path.SearchStrategy;

public class JGum {

	public static final ClassTraversalPolicy DEFAULT_CLASS_TRAVERSAL_BOTTOM_UP_POLICY = 
			new ClassTraversalPolicy(Priority.INTERFACES_FIRST, InterfaceOrder.INVERSE, SearchStrategy.PRE_ORDER, CycleDetection.IGNORE);
	
	public static final ClassTraversalPolicy DEFAULT_CLASS_TRAVERSAL_TOP_DOWN_POLICY = 
			new ClassTraversalPolicy(Priority.INTERFACES_FIRST, InterfaceOrder.DIRECT, SearchStrategy.BREADTH_FIRST, CycleDetection.IGNORE);
	
	public static final SearchStrategy DEFAULT_SUB_PACKAGES_TRAVERSAL_STRATEGY = SearchStrategy.BREADTH_FIRST;
	
	private final PackageHierarchyRoot packageHierarchyRoot;
	private final ClassHierarchyRoot classHierarchyRoot;
	private final ClassTraversalPolicy classTraversalBottomUpPolicy;
	private final ClassTraversalPolicy classTraversalTopDownPolicy;
	private final SearchStrategy subPackagesTraversalStrategy;
	
	public JGum() {
		this(DEFAULT_CLASS_TRAVERSAL_BOTTOM_UP_POLICY, DEFAULT_CLASS_TRAVERSAL_TOP_DOWN_POLICY, DEFAULT_SUB_PACKAGES_TRAVERSAL_STRATEGY);
	}

	public JGum(ClassTraversalPolicy classTraversalBottomUpPolicy, ClassTraversalPolicy classTraversalTopDownPolicy, SearchStrategy subPackagesTraversalStrategy) {
		packageHierarchyRoot = new PackageHierarchyRoot(this);
		classHierarchyRoot = new ClassHierarchyRoot(this);
		this.classTraversalBottomUpPolicy = classTraversalBottomUpPolicy;
		this.classTraversalTopDownPolicy = classTraversalTopDownPolicy;
		this.subPackagesTraversalStrategy = subPackagesTraversalStrategy;
	}
	
	public PackageNode getPackageHierarchyRoot() {
		return packageHierarchyRoot;
	}

	public ClassHierarchyRoot getClassHierarchyRoot() {
		return classHierarchyRoot;
	}

	public ClassTraversalPolicy getClassTraversalBottomUpPolicy() {
		return classTraversalBottomUpPolicy;
	}

	public ClassTraversalPolicy getClassTraversalTopDownPolicy() {
		return classTraversalTopDownPolicy;
	}

	public SearchStrategy getSubPackagesTraversalStrategy() {
		return subPackagesTraversalStrategy;
	}
	
}
