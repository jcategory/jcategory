package org.jgum.testutil;

import org.jgum.category.Category;
import org.jgum.category.CategorizationListener;

public class CounterCreationListener implements CategorizationListener<Category> {

	private int counter;
	
	@Override
	public void onCategorization(Category category) {
		counter++;
	}
	
	public int getCounter() {
		return counter;
	}

}
