package org.jcategory.testutil;

import org.jcategory.category.Category;
import org.jcategory.category.CategorizationListener;

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
