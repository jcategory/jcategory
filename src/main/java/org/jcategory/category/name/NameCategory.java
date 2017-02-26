package org.jcategory.category.name;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.jcategory.category.LabeledCategory;
import org.jcategory.traversal.RedundancyCheck;
import org.jcategory.traversal.SearchStrategy;
import org.jcategory.traversal.StopUntilConditionIterable;
import org.jcategory.traversal.TraversalPolicy;

import com.google.common.collect.Lists;

/**
 * A node wrapping a package object.
 * @author sergioc
 *
 */
public class NameCategory extends LabeledCategory<String> {

	private Map<String, NameCategory> children; //the children categories
	private List<NameCategory> parents; //the parent category
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
	NameCategory(NameCategorization namedCategorization) {
		super(namedCategorization, "");
		this.simpleName = "";
		children = new TreeMap<>(); //to preserve insertion order
	}
	
	/**
	 * @param simpleName the simple id of this category.
	 * @param parent the parent category 
	 */
	NameCategory(String simpleName, NameCategory parent) {
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
	
	public NameCategory getParent() {
		if(getParents().isEmpty())
			return null;
		else
			return (NameCategory) getParents().get(0);
	}

	@Override
	public List<NameCategory> getChildren() {
		return new ArrayList<>(children.values());
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
		NameCategory child = new NameCategory(simpleName, this);
		children.put(simpleName, child);
		getCategorization().notifyCategorizationListeners(child);
		return child;
	}
	
	public NameCategorization getCategorization() {
		return (NameCategorization)super.getCategorization();
	}
	
	public List<NameCategory> topDownPath(String relativePackageName) {
		Iterable<NameCategory> bottomUpIterable = getOrCreateCategory(relativePackageName).<NameCategory>linearize(
				TraversalPolicy.bottomUpTraversalPolicy(SearchStrategy.PRE_ORDER, RedundancyCheck.IGNORE));
		Iterable<NameCategory> filteredBottomUpIterable = new StopUntilConditionIterable(bottomUpIterable, new Predicate<NameCategory>() {
			@Override
			public boolean test(NameCategory node) {
				return NameCategory.this.equals(node);
			}
		});
		List<NameCategory> topDownIterable = Lists.reverse(Lists.newArrayList(filteredBottomUpIterable));
		return topDownIterable;
	}
	
	public List<NameCategory> topDownPath(Package pakkage) {
		return topDownPath(pakkage.getName());
	}

}
