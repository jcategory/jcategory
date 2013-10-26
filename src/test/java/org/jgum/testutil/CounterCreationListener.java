package org.jgum.testutil;

import org.jgum.graph.Node;
import org.jgum.graph.NodeCreationListener;

public class CounterCreationListener implements NodeCreationListener<Node<?>> {

	private int counter;
	
	@Override
	public void onNodeCreation(Node node) {
		counter++;
	}
	
	public int getCounter() {
		return counter;
	}

}
