package org.jgum.category.named;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgum.category.LabeledCategory;
import org.jgum.traversal.DuplicatesDetection;
import org.jgum.traversal.SearchStrategy;
import org.jgum.traversal.StopUntilConditionIterable;
import org.jgum.traversal.TraversalPolicy;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

/**
 * A node wrapping a package object.
 * @author sergioc
 *
 */
public class NamedCategory extends LabeledCategory<String> {

	private Map<String, NamedCategory> children; //the children nodes
	private NamedCategory parent; //the parent node
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
	 * Creates a root NamedCategory
	 */
	protected NamedCategory(NamedCategorization namedCategorization) {
		this(namedCategorization, "", null);
	}
	
	/**
	 * @parem namedCategorization the hierarchy where this category exists.
	 * @param simpleName the simple name of this category.
	 * @param parent the parent category 
	 */
	protected NamedCategory(NamedCategorization namedCategorization, String simpleName, NamedCategory parent) {
		super(namedCategorization, parent != null ? parent.getName(simpleName) : simpleName);
		this.simpleName = simpleName;
		this.parent = parent;
		children = new TreeMap<>(); //to preserve insertion order
	}

	public String getSimpleName() {
		return simpleName;
	}
	
	private String getName(String name) {
		return isRoot() ? name : getLabel() + "." + name;
	}
	
	public NamedCategory getParent() {
		return parent;
	}
	
	@Override
	public List<NamedCategory> getParents() {
		if(parent == null)
			return Collections.emptyList();
		else
			return asList(parent);
	}

	@Override
	public List<NamedCategory> getChildren() {
		return new ArrayList<>(children.values());
	}
	
	public NamedCategory getCategory(String relativeCategoryName) {
		NamedCategory node = this;
		List<String> packageFragmentsList = asPackageFragmentsList(relativeCategoryName);
		for(String packageFragment : packageFragmentsList) {
			node = node.getChild(packageFragment);
			if(node == null)
				break;
		}
		return node;
	}
	
	public NamedCategory getCategory(Package pakkage) {
		return getCategory(pakkage.getName());
	}
	
	public NamedCategory getOrCreateCategory(String relativeName) {
		NamedCategory node = this;
		List<String> packageFragmentsList = asPackageFragmentsList(relativeName);
		for(String packageFragment : packageFragmentsList) {
			node = node.getOrCreateChild(packageFragment);
		}
		return node;
	}
	
	public NamedCategory getOrCreateCategory(Package pakkage) {
		return getOrCreateCategory(pakkage.getName());
	}
	
	private NamedCategory getChild(String simpleName) {
		return children.get(simpleName);
	}

	private NamedCategory getOrCreateChild(String simpleName) {
		NamedCategory child = children.get(simpleName);
		if(child == null) {
			child = addChild(simpleName);
		}
		return child;
	}
	
	private NamedCategory addChild(String simpleName) {
		NamedCategory child = new NamedCategory(getCategorization(), simpleName, this);
		children.put(simpleName, child);
		getCategorization().notifyCreationListeners(child);
		return child;
	}
	
	public NamedCategorization getCategorization() {
		return (NamedCategorization)super.getCategorization();
	}
	
	/**
	 * 
	 * @return if the current category corresponds to the root category.
	 */
	public boolean isRoot() {
		return parent == null;
	}
	
	public NamedCategory getRoot() {
		NamedCategory root = this;
		while(!root.isRoot())
			root = root.getParent();
		return root;
	}
	
	
	public FluentIterable<NamedCategory> topDownPath(String relativePackageName) {
		Iterable<NamedCategory> bottomUpIterable = getOrCreateCategory(relativePackageName).<NamedCategory>linearize(
				TraversalPolicy.bottomUpTraversalPolicy(SearchStrategy.PRE_ORDER, DuplicatesDetection.IGNORE));
		Iterable<NamedCategory> filteredBottomUpIterable = new StopUntilConditionIterable(bottomUpIterable, new Predicate<NamedCategory>() {
			@Override
			public boolean apply(NamedCategory node) {
				return NamedCategory.this.equals(node);
			}
		});
		Iterable<NamedCategory> topDownIterable = Lists.reverse(Lists.newArrayList(filteredBottomUpIterable));
		return FluentIterable.from(topDownIterable);
	}
	
	public FluentIterable<NamedCategory> topDownPath(Package pakkage) {
		return topDownPath(pakkage.getName());
	}

}
