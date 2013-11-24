package org.jgum.category;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgum.category.CategoryProperty.PropertyIterable;
import org.jgum.strategy.ChainOfResponsibility;
import org.jgum.strategy.StrategyInvocationHandler;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * A hierarchical category associated with named properties.
 * @author sergioc
 *
 */
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Map<Key, Object> properties; //properties associated with this category are backed up in this map.
	private Categorization categorization; //the categorization where this category exists.
	private final List<? extends Category> parents; //default placeholder for the parents of this category. Subclasses may choose to store parents in a different structure.
	private final List<? extends Category> children; //default placeholder for the children of this category. Subclasses may choose to store children in a different structure.
	private List<? extends Category> bottomUpLinearization; //lazily initialized bottom-up linearization
	private final int level; //the (max) level of this category in the category hierarchy.
	
	/**
	 * @param categorization the categorization where this category exists.
	 */
	public Category(Categorization<?> categorization) {
		this(new ArrayList());
		setCategorization(categorization);
	}
	
	/**
	 * @parem parents the parents of this category.
	 */
	public Category(List<? extends Category> parents) {
		this.parents = parents;
		children = new ArrayList<>();
		properties = new HashMap<>();
		level = findLevel(parents);
	}
	
	private int findLevel(List<? extends Category> parents) {
		int maxParentLevel = -1;
		for(Category parent : parents) {
			int parentLevel = parent.getLevel();
			if(maxParentLevel == -1)
				maxParentLevel = parentLevel;
			else if(parentLevel > maxParentLevel)
				maxParentLevel = parentLevel;
		}
		return maxParentLevel + 1;
	}
	
	public int getLevel() {
		return level;
	}
	
	
	private void setCategorization(Categorization categorization) {
		this.categorization = categorization;
		categorization.setRoot(this);
	}

	/**
	 * 
	 * @return the categorization where this category exists.
	 */
	public Categorization<?> getCategorization() {
		if(categorization == null) //implies that this is not the root category
			categorization = getParents().get(0).getCategorization(); //there is at least one parent 
		return categorization;
	}

	/**
	 * @param key the property identifier.
	 * @return a category property.
	 */
	public <T> CategoryProperty<T> getProperty(Key key) {
		return new CategoryProperty<>(this, key);
	}
	
	/**
	 * @param key the property identifier.
	 * @return true if the property is defined in the category. false otherwise. It attempts to find it in ancestor categories if the property is not locally present.
	 */
	public boolean containsProperty(Key key) {
		return getProperty(key).isPresent();
	}
	
	/**
	 * @param key the property identifier.
	 * @return an optional with the property value in the current category (if any). It does not query ancestor categories if the property is not locally present.
	 */
	public <T> Optional<T> getLocalProperty(Key key) {
		return key.getForCategory(this);
	}
	
	/**
	 * @param key the property identifier.
	 * @return an optional with the property value in the current category map (if any).
	 */
	<T> Optional<T> getFromLocalMap(Key key) {
		return Optional.<T>fromNullable((T)properties.get(key));
	}
	
	/**
	 * @param key the property identifier.
	 * @return true if the property exists in the current category. false otherwise. It does not query ancestor categories if the property is not locally present.
	 */
	public boolean containsLocalProperty(Key key) {
		return getLocalProperty(key).isPresent();
	}
	
	/**
	 * @param key the property identifier.
	 */
	public void removeLocalProperty(Key key) {
		key.removeFromCategory(this);
	}
	
	/**
	 * @param key the property identifier.
	 */
	void removeFromLocalMap(Key key) {
		properties.remove(key);
	}
	
	/**
	 * Set a property to a given value.
	 * @param key the property identifier.
	 * @param value the value of the property.
	 */
	public void setProperty(Key key, Object value) {
		key.setForCategory(this, value);
	}
	
	/**
	 * Set a property in the local category map to a given value.
	 * @param key the property identifier.
	 * @param value the value of the property.
	 */
	void putAtLocalMap(Key key, Object value) {
		properties.put(key, value);
	}
	
	
	/**
	 * @param strategyInterface the interface implemented by the desired strategy object. It is also the property identifier under which strategies are associated with categories in this and upper categories.
	 * @return a strategy object implementing the given interface.
	 */
	public <T> T getStrategy(Class<T> strategyInterface) {
		return getStrategy(new Key(strategyInterface), new Class[]{strategyInterface}, ChainOfResponsibility.DEFAULT_DELEGATION_EXCEPTION);
	}
	
	/**
	 * @param strategyInterface the interface implemented by the desired strategy object. It is also the property identifier under which strategies are associated with categories in this and upper categories.
	 * @param exceptionClass instances of this exception class denote that a strategy delegates to the next one in the responsibility chain.
	 * @return a strategy object implementing the given interface.
	 */
	public <T> T getStrategy(Class<T> strategyInterface, Class<? extends RuntimeException> exceptionClass) {
		return getStrategy(new Key(strategyInterface), new Class[]{strategyInterface}, exceptionClass);
	}
	
	/**
	 * @param key the property identifier.
	 * @param strategyInterfaces the interfaces implemented by the strategy object.
	 * @return a strategy object implementing the given interfaces.
	 */
	public <T> T getStrategy(Key key, Class<?>[] strategyInterfaces) {
		return getStrategy(key, strategyInterfaces, ChainOfResponsibility.DEFAULT_DELEGATION_EXCEPTION);
	}
	
	/**
	 * @param key the property identifier.
	 * @param strategyInterfaces the interfaces implemented by the strategy object.
	 * @param exceptionClass instances of this exception class denote that a strategy delegates to the next one in the responsibility chain.
	 * @return a strategy object implementing the given interfaces.
	 */
	public <T> T getStrategy(Key key, Class<?>[] strategyInterfaces, Class<? extends RuntimeException> exceptionClass) {
		return (T)Proxy.newProxyInstance(getClass().getClassLoader(), strategyInterfaces, new StrategyInvocationHandler(this, key, exceptionClass));
	}
	
	
	/**
	 * 
	 * @return an optional with the super category.
	 */
	public <U extends Category> Optional<U> getSuper() {
		List bottomUpLinearization = bottomUpCategories();
		if(bottomUpLinearization.size() == 1) //there are no super categories (according to the default linearization function)
			return Optional.absent();
		else
			return Optional.of((U)bottomUpLinearization.get(1));
	}
	
	/**
	 * 
	 * @param linearizationFunction is a linearization function.
	 * @return a list of categories, according to the given linearization function.
	 */
	public <U extends Category> List<U> linearize(Function<U,List<U>> linearizationFunction) {
		return linearizationFunction.apply((U)this);
	}

	/**
	 * @param key the property identifier.
	 * @param linearizationFunction is a linearization function.
	 * @return a list of properties in the categories obtained with the given linearization function.
	 */
	public <U> List<U> properties(Key key, Function<Category,List<Category>> linearizationFunction) {
		return Lists.newArrayList(new PropertyIterable(linearize(linearizationFunction), key));
	}
	
	/**
	 * 
	 * @return the ancestors of this category.
	 */
	public <T extends Category> List<T> getAncestors() {
		List<T> ancestors = bottomUpCategories();
		ancestors = ancestors.subList(1, ancestors.size());
		return ancestors;
	}
	
	/**
	 * @param key the property identifier.
	 * @return a list of properties in the bottom-up linearization.
	 */
	public <U> List<U> bottomUpProperties(Key key) {
		return Lists.newArrayList(new PropertyIterable(bottomUpCategories(), key));
	}

	/**
	 * @param key the property identifier.
	 * @return a list of properties in the top-down linearization.
	 */
	public <U> List<U> topDownProperties(Key key) {
		return Lists.newArrayList(new PropertyIterable(topDownCategories(), key));
	}
	
	/**
	 * 
	 * @return a linearization using the default bottom-up linearization function.
	 */
	public <U extends Category> List<U> bottomUpCategories() {
		if(bottomUpLinearization == null) {
			bottomUpLinearization = linearize(getCategorization().getBottomUpLinearizationFunction());
		}
		return (List<U>)bottomUpLinearization;
	}

	/**
	 * 
	 * @return a linearization using the default top-down linearization function.
	 */
	public <U extends Category> List<U> topDownCategories() {
		return (List<U>)linearize(getCategorization().getTopDownLinearizationFunction());
	}
	
	/**
	 * 
	 * @return the parents of this category. The ordering in which parents are returned is determined by subclasses.
	 */
	public <U extends Category> List<U> getParents() {
		return (List)parents;
	}

	/**
	 * 
	 * @return the children of this category. The ordering in which children are returned is determined by subclasses.
	 */
	public <U extends Category> List<U> getChildren() {
		return (List)children;
	}

//	protected void onAddChild(Category category) {
//		categorization.notifyCategorizationListeners(category);
//	}
	
	/**
	 * 
	 * @return true if the current category corresponds to the root category. false otherwise.
	 */
	public boolean isRoot() {
		return getParents().isEmpty();
	}
	
	@Override
	public String toString() {
		return properties.toString();
	}

}
