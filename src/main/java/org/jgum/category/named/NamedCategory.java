package org.jgum.category.named;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgum.category.LabeledCategory;
import org.jgum.traversal.RedundancyCheck;
import org.jgum.traversal.SearchStrategy;
import org.jgum.traversal.StopUntilConditionIterable;
import org.jgum.traversal.TraversalPolicy;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * A node wrapping a package object.
 * @author sergioc
 *
 */
public class NamedCategory extends LabeledCategory<String> {

	private Map<String, NamedCategory> children; //the children categories
	private List<NamedCategory> parents; //the parent category
	private final String simpleName; //the simple id of the category
	
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
	NamedCategory(NamedCategorization namedCategorization) {
		super(namedCategorization, "");
		this.simpleName = "";
		children = new TreeMap<>(); //to preserve insertion order
	}
	
	/**
	 * @param simpleName the simple id of this category.
	 * @param parent the parent category 
	 */
	NamedCategory(String simpleName, NamedCategory parent) {
		super(parent.getName(simpleName), asList(parent));
		this.simpleName = simpleName;
		children = new TreeMap<>(); //to preserve insertion order
	}

	public String getSimpleName() {
		return simpleName;
	}
	
	private String getName(String name) {
		return isRoot() ? name : getLabel() + "." + name;
	}
	
	public NamedCategory getParent() {
		if(getParents().isEmpty())
			return null;
		else
			return (NamedCategory) getParents().get(0);
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
		NamedCategory child = new NamedCategory(simpleName, this);
		children.put(simpleName, child);
		getCategorization().notifyCategorizationListeners(child);
		return child;
	}
	
	public NamedCategorization getCategorization() {
		return (NamedCategorization)super.getCategorization();
	}
	
	public List<NamedCategory> topDownPath(String relativePackageName) {
		Iterable<NamedCategory> bottomUpIterable = getOrCreateCategory(relativePackageName).<NamedCategory>linearize(
				TraversalPolicy.bottomUpTraversalPolicy(SearchStrategy.PRE_ORDER, RedundancyCheck.IGNORE));
		Iterable<NamedCategory> filteredBottomUpIterable = new StopUntilConditionIterable(bottomUpIterable, new Predicate<NamedCategory>() {
			@Override
			public boolean apply(NamedCategory node) {
				return NamedCategory.this.equals(node);
			}
		});
		List<NamedCategory> topDownIterable = Lists.reverse(Lists.newArrayList(filteredBottomUpIterable));
		return topDownIterable;
	}
	
	public List<NamedCategory> topDownPath(Package pakkage) {
		return topDownPath(pakkage.getName());
	}

}
