package org.jgum.category;

import java.util.List;
import java.util.Objects;

import com.google.common.base.Function;
import com.google.common.collect.Lists;


/**
 * A category uniquely identified by a label in a given categorization.
 * @author sergioc
 *
 * @param <T> the category label type.
 */
public class LabeledCategory<T> extends Category {
	
	public static <U> List<U> labels(List<? extends LabeledCategory<?>> path) {
		return Lists.transform(path, new Function<LabeledCategory<?>, U>() {
			@Override
			public U apply(LabeledCategory<?> category) {
				return (U)category.getLabel();
			}
		});
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
