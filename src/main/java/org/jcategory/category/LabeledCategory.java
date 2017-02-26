package org.jcategory.category;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * A category uniquely identified by a label in a given categorization.
 * @author sergioc
 *
 * @param <T> the category label type.
 */
public class LabeledCategory<T> extends Category {
	
	public static <U> List<U> labels(List<? extends LabeledCategory<?>> path) {
		return path.stream().map(category -> (U) category.getLabel()).collect(Collectors.toList());
	} 
	
	private final T label; //the identifier of this category.
	
	/**
	 * @param categorization the categorization where this category exists.
	 * @param label a label identifying this category in the categorization.
	 */
	public LabeledCategory(Categorization<?> categorization, T label) {
		super(categorization);
		this.label = label;
	}
	
	/**
	 * @param label a label identifying this category.
	 * @parem parents the parents of this category.
	 */
	public LabeledCategory(T label, List<? extends Category> parents) {
		super(parents);
		this.label = label;
	}
	
	
	/**
	 * 
	 * @return the label of this category.
	 */
	public T getLabel() {
		return label;
	}
	
	/**
	 * 
	 * @param label a category label.
	 * @return true if the category label is equals to the one sent as argument.
	 */
	public boolean hasLabel(Object label) {
		return this.label.equals(label);
	}
	
	/**
	 * 
	 * @param label a category label.
	 * @return the minimum number of levels from this category to an ancestor category with a given label, or -1 if no ancestor has the current label.
	 */
	public int distance(Object label) {
		if(hasLabel(label))
			return 0;
		else {
			int minParentDistance = -1;
			for(Category parent : getParents()) {
				if(parent instanceof LabeledCategory) {
					int parentDistance = ((LabeledCategory) parent).distance(label);
					if( (parentDistance != 1) && (minParentDistance == -1 || parentDistance < minParentDistance) )
						minParentDistance = parentDistance + 1;
				}
			}
			return minParentDistance;
		}
	}
	
	@Override
	public String toString() {
		return "["+labelToString()+"]" + super.toString();
	}
	
	protected String labelToString() {
		return Objects.toString(label);
	}
	
	/**
	 * 
	 * @param linearizationFunction is a linearization function.
	 * @return A list of category labels, according to the given linearization function.
	 */
	public <U extends Category> List<U> linearizeLabels(Function<U,List<U>> linearizationFunction) {
		return LabeledCategory.<U>labels((List)linearize((Function)linearizationFunction));
	}

	/**
	 * 
	 * @return A list of category labels, according to the default bottom-up linearization function.
	 */
	public <U extends Category> List<U> bottomUpLabels() {
		return (List<U>)linearizeLabels(getCategorization().getBottomUpLinearizationFunction());
	}
	
	/**
	 * 
	 * @return An iterable of category labels, according to the default top-down linearization function.
	 */
	public <U extends Category> List<U> topDownLabels() {
		return (List<U>)linearizeLabels(getCategorization().getTopDownLinearizationFunction());
	}

}
