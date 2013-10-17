package org.jgum.path;

import org.jgum.JGum;

public class JGumNode extends PropertiesNode {

	private final JGum context;
	
	public JGumNode(JGum context) {
		this.context = context;
	}

	public JGum getContext() {
		return context;
	}
	
}
