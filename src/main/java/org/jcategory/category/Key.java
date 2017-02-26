package org.jcategory.category;

import java.util.List;


public interface Key {

	static Key key() {
		return new SingletonKey();
	}

	static Key key(Object object) {
		return new SingletonKey(object);
	}


	/**
	 * @param category the queried category.
	 * @return the value of the property represented by this object in the given category.
	 */
	<T> List<T> getForCategory(Category category);

	/**
	 * @param category the modified category.
	 * @param value the value to set for the property represented by this object in the given category.
	 */
	void setForCategory(Category category, Object value);
	
	/**
	 * @param category the modified category.
	 */
	void removeFromCategory(Category category);

}
