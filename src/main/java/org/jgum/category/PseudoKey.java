package org.jgum.category;

import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * A pseudo-key is not stored in the local map of a category.
 * Instead, it defines a property value according to a function that receives as its unique argument a category.
 * @author sergioc
 *
 * @param <T> the type of the category.
 * @param <U> the type of the pseudo-property.
 */
public class PseudoKey<T extends Category, U> extends Key {

	/**
	 * @param function function defining the value of this pseudo-key for a given category.
	 */
	public PseudoKey(Function<T, Optional<U>> function) {
		super(function);
	}

	/**
	 * @param category the queried category.
	 * @return the value of the property according to the pseudo-key function (received in the constructor) in the given category.
	 */
	@Override
	protected Optional<U> getForCategory(Category category) {
		Function<T, Optional<U>> function = (Function<T, Optional<U>>) id;
		return function.apply((T)category);
	}
	
	/**
	 * @throws UnsupportedOperationException
	 */
	@Override
	protected void setForCategory(Category category, Object value) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @throws UnsupportedOperationException
	 */
	protected void removeFromCategory(Category category) {
		throw new UnsupportedOperationException();
	}
	
}
