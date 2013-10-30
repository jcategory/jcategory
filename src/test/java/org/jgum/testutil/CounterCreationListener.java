package org.jgum.testutil;

import org.jgum.category.Category;
import org.jgum.category.CategoryCreationListener;

public class CounterCreationListener implements CategoryCreationListener<Category<?>> {

	private int counter;
	
	@Override
	public void onCategoryCreation(Category<?> category) {
		counter++;
	}
	
	public int getCounter() {
		return counter;
	}

}
