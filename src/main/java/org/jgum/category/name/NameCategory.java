package org.jgum.category.name;

import static java.util.Arrays.asList;
import static org.jgum.category.PropertyIterable.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgum.category.Category;
import org.jgum.category.SearchStrategy;
import org.jgum.category.StopUntilConditionIterable;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

/**
 * A node wrapping a package object.
 * @author sergioc
 *
 */
public class NameCategory extends Category<String> {

	private Map<String, NameCategory> children; //the children nodes
	private NameCategory parent; //the parent node
	private String packageName; //the full name of the package, lazily initialized
	private final NameHierarchy nameHierarchy;
	
	static List<String> asPackageFragmentsList(String packageName) {
		List<String> packageFragmentsList;
		if(packageName.isEmpty())
			packageFragmentsList = new ArrayList<>();
		else {
			String[] packageFragments = packageName.split("\\.");
			packageFragmentsList = asList(packageFragments);
		}
		return packageFragmentsList;
	}
	
	/**
	 * Creates a root NameCategory
	 */
	protected NameCategory(NameHierarchy nameHierarchy) {
		this(nameHierarchy, "", null);
	}
	
	/**
	 * 
	 * @param packageFragment the name of this node package fragment.
	 * @param parent the parent node 
	 */
	protected NameCategory(NameHierarchy nameHierarchy, String packageFragment, NameCategory parent) {
		super(packageFragment);
		this.nameHierarchy = nameHierarchy;
		this.parent = parent;
		children = new TreeMap<>(); //to preserve insertion order
	}

	public List<NameCategory> getChildren() {
		return new ArrayList<>(children.values());
	}
	
	public NameCategory getParent() {
		return parent;
	}
	
	public Object get(String relativePackageName, Object key) {
		NameCategory nameCategory = getNode(relativePackageName);
		if(nameCategory == null)
			return null;
		else
			return nameCategory.get(key);
	}
	
	public NameCategory getNode(String relativePackageName) {
		NameCategory node = this;
		List<String> packageFragmentsList = asPackageFragmentsList(relativePackageName);
		for(String packageFragment : packageFragmentsList) {
			node = node.getChild(packageFragment);
			if(node == null)
				break;
		}
		return node;
	}
	
	protected NameCategory getOrCreateNode(String relativePackageName) {
		NameCategory node = this;
		List<String> packageFragmentsList = asPackageFragmentsList(relativePackageName);
		for(String packageFragment : packageFragmentsList) {
			node = node.getOrCreateChild(packageFragment);
		}
		return node;
	}
	
	private NameCategory getChild(String subpackageName) {
		return children.get(subpackageName);
	}

	private NameCategory getOrCreateChild(String subpackageName) {
		NameCategory child = children.get(subpackageName);
		if(child == null) {
			child = addChild(subpackageName);
		}
		return child;
	}
	
	private NameCategory addChild(String subpackageName) {
		NameCategory child = new NameCategory(nameHierarchy, subpackageName, this);
		children.put(subpackageName, child);
		nameHierarchy.notifyCreationListeners(child);
		return child;
	}
	
	/**
	 * 
	 * @return if the current Category corresponds to the root package.
	 */
	public boolean isRoot() {
		return parent == null;
	}
	
	public NameCategoryRoot getRoot() {
		NameCategory root = this;
		while(!root.isRoot())
			root = root.getParent();
		return (NameCategoryRoot)root;
	}
	
	/**
	 * 
	 * @return the package name of this node
	 */
	public String getPackageName() {
		if(packageName == null) {
			StringBuilder sb = new StringBuilder();
			if(!isRoot() && !parent.isRoot()) {
				sb.append(parent.getPackageName());
				sb.append(".");
			}
			sb.append(getValue());
			packageName = sb.toString();
		}
		return packageName;
	}
	
	@Override
	public FluentIterable<NameCategory> bottomUpLinearization() {
		return linearization(nameHierarchy.getBottomUpNameTraversalPolicy());
	}

	public <U> FluentIterable<U> bottomUpLinearizationProperties(Object key) {
		return properties(bottomUpLinearization(), key);
	}
	
	@Override
	public FluentIterable<NameCategory> topDownLinearization() {
		return linearization(nameHierarchy.getTopDownNameTraversalPolicy());
	}
	
	public FluentIterable<NameCategory> topDownPath(String relativePackageName) {
		Iterable<NameCategory> bottomUpIterable = getOrCreateNode(relativePackageName).linearization(new BottomUpNameTraversalPolicy(SearchStrategy.PRE_ORDER));
		Iterable<NameCategory> filteredBottomUpIterable = new StopUntilConditionIterable(bottomUpIterable, new Predicate<NameCategory>() {
			@Override
			public boolean apply(NameCategory node) {
				return NameCategory.this.equals(node);
			}
		});
		Iterable<NameCategory> topDownIterable = Lists.reverse(Lists.newArrayList(filteredBottomUpIterable));
		return FluentIterable.from(topDownIterable);
	}
	
	public <U> FluentIterable<U> topDownPathProperties(String relativePackageName, Object key) {
		return properties(topDownPath(relativePackageName), key);
	}
	
	@Override
	public String toString() {
		return getPackageName() + super.toString();
	}

}
