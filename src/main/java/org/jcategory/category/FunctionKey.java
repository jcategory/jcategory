package org.jcategory.category;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A pseudo-key is not stored in the local map of a category.
 * Instead, it defines a property value according to a function that receives as its unique argument a category.
 * @author sergioc
 *
 * @param <T> the type of the category.
 * @param <U> the type of the pseudo-property.
 */
public class FunctionKey<T extends Category, U> implements Key {

	private final Function<T, Optional<U>> function;
	/**
	 * @param function function defining the value of this pseudo-key for a given category.
	 */
	private FunctionKey(Function<T, Optional<U>> function) {
		this.function = function;
	}

	public static <T extends Category, U> FunctionKey<T, U> functionKey(Function<T, Optional<U>> function) {
		return new FunctionKey<>(function);
	}

	/**
	 * @param category the queried category.
	 * @return the value of the property according to the pseudo-key function (received in the constructor) in the given category.
	 */
	@Override
	public List<U> getForCategory(Category category) {
		Optional<U> opt = function.apply((T)category);
		if (opt.isPresent()) {
			return asList(opt.get());
		} else {
			return emptyList();
		}
	}
	
	/**
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void setForCategory(Category category, Object value) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @throws UnsupportedOperationException
	 */
	public void removeFromCategory(Category category) {
		throw new UnsupportedOperationException();
	}
	
}
