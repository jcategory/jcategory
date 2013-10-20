package org.jgum;

import org.jgum.classmodel.BottomUpTypeTraversalPolicy;
import org.jgum.classmodel.ClassHierarchyRoot;
import org.jgum.classmodel.InterfaceOrder;
import org.jgum.classmodel.Priority;
import org.jgum.classmodel.TopDownTypeTraversalPolicy;
import org.jgum.classmodel.TypeNode;
import org.jgum.graph.CycleDetection;
import org.jgum.graph.SearchStrategy;
import org.jgum.packagemodel.BottomUpPackageTraversalPolicy;
import org.jgum.packagemodel.PackageHierarchyRoot;
import org.jgum.packagemodel.PackageNode;
import org.jgum.packagemodel.TopDownPackageTraversalPolicy;

public class JGum {

	public static final BottomUpTypeTraversalPolicy<TypeNode<?>> DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY = 
			new BottomUpTypeTraversalPolicy<>(SearchStrategy.PRE_ORDER, CycleDetection.IGNORE, Priority.INTERFACES_FIRST, InterfaceOrder.INVERSE);
	
	public static final TopDownTypeTraversalPolicy<TypeNode<?>> DEFAULT_TOP_DOWN_CLASS_TRAVERSAL_POLICY = 
			new TopDownTypeTraversalPolicy<>(SearchStrategy.BREADTH_FIRST, CycleDetection.IGNORE, Priority.INTERFACES_FIRST);
	
	public static final BottomUpPackageTraversalPolicy DEFAULT_BOTTOM_UP_PACKAGE_TRAVERSAL_POLICY = 
			new BottomUpPackageTraversalPolicy(SearchStrategy.PRE_ORDER);
	
	public static final TopDownPackageTraversalPolicy DEFAULT_TOP_DOWN_PACKAGE_TRAVERSAL_POLICY = 
			new TopDownPackageTraversalPolicy(SearchStrategy.PRE_ORDER);
	
	private final PackageHierarchyRoot packageHierarchyRoot;
	private final ClassHierarchyRoot classHierarchyRoot;
	
	private final BottomUpTypeTraversalPolicy<TypeNode<?>> bottomUpTypeTraversalPolicy;
	private final TopDownTypeTraversalPolicy<TypeNode<?>> topDownTypeTraversalPolicy;
	private final BottomUpPackageTraversalPolicy bottomUpPackageTraversalPolicy;
	private final TopDownPackageTraversalPolicy topDownPackageTraversalPolicy;
	
	public JGum() {
		this(DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY, DEFAULT_TOP_DOWN_CLASS_TRAVERSAL_POLICY, DEFAULT_BOTTOM_UP_PACKAGE_TRAVERSAL_POLICY, DEFAULT_TOP_DOWN_PACKAGE_TRAVERSAL_POLICY);
	}

	public JGum(BottomUpTypeTraversalPolicy<TypeNode<?>> bottomUpTypeTraversalPolicy, TopDownTypeTraversalPolicy<TypeNode<?>> topDownTypeTraversalPolicy) {
		this(bottomUpTypeTraversalPolicy, topDownTypeTraversalPolicy, DEFAULT_BOTTOM_UP_PACKAGE_TRAVERSAL_POLICY, DEFAULT_TOP_DOWN_PACKAGE_TRAVERSAL_POLICY);
	}
	
	public JGum(BottomUpPackageTraversalPolicy bottomUpPackageTraversalPolicy, TopDownPackageTraversalPolicy topDownPackageTraversalPolicy) {
		this(DEFAULT_BOTTOM_UP_TYPE_TRAVERSAL_POLICY, DEFAULT_TOP_DOWN_CLASS_TRAVERSAL_POLICY, bottomUpPackageTraversalPolicy, topDownPackageTraversalPolicy);
	}
	
	public JGum(BottomUpTypeTraversalPolicy<TypeNode<?>> bottomUpTypeTraversalPolicy, TopDownTypeTraversalPolicy<TypeNode<?>> topDownTypeTraversalPolicy,
			BottomUpPackageTraversalPolicy bottomUpPackageTraversalPolicy, TopDownPackageTraversalPolicy topDownPackageTraversalPolicy) {
		packageHierarchyRoot = new PackageHierarchyRoot(this);
		classHierarchyRoot = new ClassHierarchyRoot(this);
		this.bottomUpTypeTraversalPolicy = bottomUpTypeTraversalPolicy;
		this.topDownTypeTraversalPolicy = topDownTypeTraversalPolicy;
		this.bottomUpPackageTraversalPolicy = bottomUpPackageTraversalPolicy;
		this.topDownPackageTraversalPolicy = topDownPackageTraversalPolicy;
	}
	
	public PackageNode getPackageHierarchyRoot() {
		return packageHierarchyRoot;
	}

	public ClassHierarchyRoot getClassHierarchyRoot() {
		return classHierarchyRoot;
	}

	public BottomUpTypeTraversalPolicy<TypeNode<?>> getBottomUpTypeTraversalPolicy() {
		return bottomUpTypeTraversalPolicy;
	}

	public TopDownTypeTraversalPolicy<TypeNode<?>> getTopDownTypeTraversalPolicy() {
		return topDownTypeTraversalPolicy;
	}

	public BottomUpPackageTraversalPolicy getBottomUpPackageTraversalPolicy() {
		return bottomUpPackageTraversalPolicy;
	}

	public TopDownPackageTraversalPolicy getTopDownPackageTraversalPolicy() {
		return topDownPackageTraversalPolicy;
	}
	
}
