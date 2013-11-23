package org.jgum.category;

import com.google.common.base.Optional;

/**
 * A property identifier.
 * @author sergioc
 *
 */
public class Key {

	protected final Object name;
	
	public Key() {
		this(new Object());
	}
	
	/**
	 * 
	 * @param name the name of a category property.
	 */
	public Key(Object name) {
		this.name = name;
	}
	
	/**
	 * @param category the queried category.
	 * @return the value of the property represented by this object in the given category.
	 */
	protected <T> Optional<T> getForCategory(Category category) {
		return category.<T>getFromLocalMap(this);
	}

	/**
	 * @param category the modified category.
	 * @param value the value to set for the property represented by this object in the given category.
	 */
	protected void setForCategory(Category category, Object value) {
		category.putAtLocalMap(this, value);
	}
	
	/**
	 * @param category the modified category.
	 */
	protected void removeFromCategory(Category category) {
		category.removeFromLocalMap(this);
	}
	
	@Override
	public String toString() {
		return name.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Key other = (Key) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
