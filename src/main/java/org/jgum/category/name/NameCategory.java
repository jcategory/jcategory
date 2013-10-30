package org.jgum.category.name;

import static java.util.Arrays.asList;

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
	private final String simpleName; //the full name of the package, lazily initialized
	
	static List<String> asPackageFragmentsList(String simpleName) {
		List<String> packageFragmentsList;
		if(simpleName.isEmpty())
			packageFragmentsList = new ArrayList<>();
		else {
			String[] packageFragments = simpleName.split("\\.");
			packageFragmentsList = asList(packageFragments);
		}
		return packageFragmentsList;
	}
	
	/**
	 * Creates a root NameCategory
	 */
	protected NameCategory(NameHierarchy nameHierarchy) {
		this("", nameHierarchy, null);
	}
	
	/**
	 * 
	 * @param packageFragment the name of this node package fragment.
	 * @param parent the parent node 
	 */
	protected NameCategory(String simpleName, NameHierarchy nameHierarchy, NameCategory parent) {
		super(parent != null ? parent.getName(simpleName) : simpleName, nameHierarchy);
		this.simpleName = simpleName;
		this.parent = parent;
		children = new TreeMap<>(); //to preserve insertion order
	}

	public String getSimpleName() {
		return simpleName;
	}
	
	private String getName(String name) {
		return isRoot() ? name : getId() + "." + name;
	}
	
	public List<NameCategory> getChildren() {
		return new ArrayList<>(children.values());
	}
	
	public NameCategory getParent() {
		return parent;
	}
	
	public Object get(String relativeCategoryName, Object key) {
		NameCategory nameCategory = getCategory(relativeCategoryName);
		if(nameCategory == null)
			return null;
		else
			return nameCategory.getProperty(key);
	}
	
	public NameCategory getCategory(String relativeCategoryName) {
		NameCategory node = this;
		List<String> packageFragmentsList = asPackageFragmentsList(relativeCategoryName);
		for(String packageFragment : packageFragmentsList) {
			node = node.getChild(packageFragment);
			if(node == null)
				break;
		}
		return node;
	}
	
	public NameCategory getCategory(Package pakkage) {
		return getCategory(pakkage.getName());
	}
	
	public NameCategory getOrCreateCategory(String relativeName) {
		NameCategory node = this;
		List<String> packageFragmentsList = asPackageFragmentsList(relativeName);
		for(String packageFragment : packageFragmentsList) {
			node = node.getOrCreateChild(packageFragment);
		}
		return node;
	}
	
	public NameCategory getOrCreateCategory(Package pakkage) {
		return getOrCreateCategory(pakkage.getName());
	}
	
	private NameCategory getChild(String simpleName) {
		return children.get(simpleName);
	}

	private NameCategory getOrCreateChild(String simpleName) {
		NameCategory child = children.get(simpleName);
		if(child == null) {
			child = addChild(simpleName);
		}
		return child;
	}
	
	private NameCategory addChild(String simpleName) {
		NameCategory child = new NameCategory(simpleName, getCategoryHierarchy(), this);
		children.put(simpleName, child);
		getCategoryHierarchy().notifyCreationListeners(child);
		return child;
	}
	
	public NameHierarchy getCategoryHierarchy() {
		return (NameHierarchy)super.getCategoryHierarchy();
	}
	
	/**
	 * 
	 * @return if the current category corresponds to the root category.
	 */
	public boolean isRoot() {
		return parent == null;
	}
	
	public NameCategory getRoot() {
		NameCategory root = this;
		while(!root.isRoot())
			root = root.getParent();
		return root;
	}
	
	
	public FluentIterable<NameCategory> topDownPath(String relativePackageName) {
		Iterable<NameCategory> bottomUpIterable = getOrCreateCategory(relativePackageName).linearize(new BottomUpNameTraversalPolicy(SearchStrategy.PRE_ORDER));
		Iterable<NameCategory> filteredBottomUpIterable = new StopUntilConditionIterable(bottomUpIterable, new Predicate<NameCategory>() {
			@Override
			public boolean apply(NameCategory node) {
				return NameCategory.this.equals(node);
			}
		});
		Iterable<NameCategory> topDownIterable = Lists.reverse(Lists.newArrayList(filteredBottomUpIterable));
		return FluentIterable.from(topDownIterable);
	}
	
	public FluentIterable<NameCategory> topDownPath(Package pakkage) {
		return topDownPath(pakkage.getName());
	}

}
