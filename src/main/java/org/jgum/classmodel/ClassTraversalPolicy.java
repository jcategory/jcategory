package org.jgum.classmodel;

import org.jgum.CycleDetection;
import org.jgum.SearchStrategy;

public class ClassTraversalPolicy {
	
	public final Priority priority;
	public final InterfaceOrder interfaceOrder;
	public final SearchStrategy searchStrategy;
	public final CycleDetection cycleDetection;
	
	public ClassTraversalPolicy(Priority priority, InterfaceOrder interfaceOrder, SearchStrategy searchStrategy, CycleDetection cycleDetection) {
		this.priority = priority;
		this.interfaceOrder = interfaceOrder;
		this.searchStrategy = searchStrategy;
		this.cycleDetection = cycleDetection;
	}

}
