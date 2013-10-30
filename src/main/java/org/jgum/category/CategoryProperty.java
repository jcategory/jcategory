package org.jgum.category;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;

/**
 * The property of a category. A category property value depends on the location of the category in a hierarchy.
 * If it does not explicitly set a property, its value will be looked up in its ancestor categories. 
 * @author sergioc
 *
 * @param <T>
 */
public class CategoryProperty<T> {

	public static <U> FluentIterable<U> properties(Iterable<? extends Category<?>> categories, Object key) {
		return FluentIterable.<U>from(new PropertyIterable<U>(categories, key));
	}
	
	private final Category<?> category;
	private final Object key;
	private final PropertyIterable<T> propertyIterable;
	
	public CategoryProperty(Category<?> category, Object key) {
		this.category = category;
		this.key = key;
		propertyIterable = new PropertyIterable<T>(category.bottomUpLinearization(), key);
	}

	public Category<?> getCategory() {
		return category;
	}

	public Object getKey() {
		return key;
	}
	
	/**
	 * 
	 * @return an optional wrapping the value of a property in a category.
	 */
	public Optional<T> get() {
		try {
			return Optional.of(propertyIterable.iterator().next());
		} catch(NoSuchElementException e) {
			return Optional.absent();
		}
	}
	
	/**
	 * 
	 * @return the value of the property in a category. If the value is not set, it will throw an exception.
	 */
	public T getOrThrow() {
		return propertyIterable.iterator().next();
	}
	
	/**
	 * Sets the value of the property in the wrapped category.
	 * @param value the value of the property.
	 */
	public void set(T value) {
		category.putProperty(key, value);
	}
	
	
	
	public static class PropertyIterator<T> extends AbstractIterator<T> {
		
		private final Iterator<? extends Category<?>> propertyNodes;
		private final Object key;
		
		public PropertyIterator(Category<?> category, final Object key) {
			this((Iterator)category.bottomUpLinearization(), key);
		}
		
		public PropertyIterator(Iterator<? extends Category<?>> propertyNodes, final Object key) {
			this.key = key;
			this.propertyNodes = Iterators.filter(propertyNodes, new Predicate<Category<?>>() {
				@Override
				public boolean apply(Category category) {
					return category.containsProperty(key);
				}
			});
		}
		
		@Override
		protected T computeNext() {
			if(propertyNodes.hasNext()) {
				Category nextPropertiesNode = propertyNodes.next();
				return (T) nextPropertiesNode.getProperty(key);
			} else
				return endOfData();
		}

	}

	
	
	public static class PropertyIterable<T> implements Iterable<T> {

		private final Iterable<? extends Category<?>> propertyNodes;
		private final Object key;
		
		public PropertyIterable(Category<?> category, final Object key) {
			this((Iterable)category.bottomUpLinearization(), key);
		}
		
		public PropertyIterable(Iterable<? extends Category<?>> propertyNodes, Object key) {
			this.propertyNodes = propertyNodes;
			this.key = key;
		}
		
		@Override
		public Iterator<T> iterator() {
			return new PropertyIterator<>(propertyNodes.iterator(), key);
		}

	}
	
}
