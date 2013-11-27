package org.jgum.category;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;

/**
 * The key of a category. A category key value depends on the location of the category in a categorization.
 * If it does not explicitly set a key, its value will be looked up in its ancestor categories. 
 * @author sergioc
 *
 * @param <T>
 */
public class CategoryProperty<T> {

	public static <U> FluentIterable<U> properties(Iterable<? extends Category> categories, Object key) {
		return FluentIterable.<U>from(new PropertyIterable<U>(categories, key));
	}
	
	private final Category category;
	private final Object key;
	private final PropertyIterable<T> propertyIterable;
	
	/**
	 * Resolves a given key according to the default bottom-up linearization function.
	 * @param category the category where a key is queried.
	 * @param key the property identifier.
	 */
	public CategoryProperty(Category category, Object key) {
		this(category, key, category.bottomUpCategories());
	}

	/**
	 * Resolves a given key according to a given bottom-up linearization function.
	 * @param category the category where a key is queried.
	 * @param key the property identifier.
	 * @param linearizationFunction the bottom-up linearization function that will be used to find the key value.
	 */
	public CategoryProperty(Category category, Object key, Function<Category, List<Category>> linearizationFunction) {
		this(category, key, linearizationFunction.apply(category));
	}
	
	private CategoryProperty(Category category, Object key, List<? extends Category> linearization) {
		this.category = category;
		this.key = key;
		this.propertyIterable = new PropertyIterable<T>(linearization, key);
	}
	
	public Category getCategory() {
		return category;
	}

	/**
	 * 
	 * @return the property identifier.
	 */
	public Object getKey() {
		return key;
	}
	
	/**
	 * 
	 * @return true if the key is present. false otherwise.
	 */
	public boolean isPresent() {
		return propertyIterable.iterator().hasNext();
	}
	
	/**
	 * 
	 * @return the value of a key in a category. If the key is not set it will throw an exception.
	 */
	public T get() {
		return propertyIterable.iterator().next();
	}
	
	/**
	 * Sets the value of the key in the wrapped category.
	 * @param value the value of the key.
	 */
	public void set(T value) {
		category.setProperty(key, value);
	}
	
	
	
	public static class PropertyIterator<T> extends AbstractIterator<T> {
		
		private final Iterator<? extends Category> propertyNodes;
		private final Object key;
		
		public PropertyIterator(Category category, Object key) {
			this((Iterator)category.bottomUpCategories(), key);
		}
		
		public PropertyIterator(Iterator<? extends Category> propertyNodes, final Object key) {
			this.key = key;
			this.propertyNodes = Iterators.filter(propertyNodes, new Predicate<Category>() {
				@Override
				public boolean apply(Category category) {
					return category.containsLocalProperty(key);
				}
			});
		}
		
		@Override
		protected T computeNext() {
			if(propertyNodes.hasNext()) {
				Category nextPropertiesNode = propertyNodes.next();
				return (T) nextPropertiesNode.getLocalProperty(key).get();
			} else
				return endOfData();
		}
	}

	
	
	public static class PropertyIterable<T> implements Iterable<T> {

		private final Iterable<? extends Category> propertyNodes;
		private final Object key;
		
		public PropertyIterable(Category category, Object key) {
			this((Iterable)category.bottomUpCategories(), key);
		}
		
		public PropertyIterable(Iterable<? extends Category> propertyNodes, Object key) {
			this.propertyNodes = propertyNodes;
			this.key = key;
		}
		
		@Override
		public Iterator<T> iterator() {
			return new PropertyIterator<>(propertyNodes.iterator(), key);
		}

	}
	
}
