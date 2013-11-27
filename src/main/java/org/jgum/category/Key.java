package org.jgum.category;

import com.google.common.base.Optional;

/**
 * A property identifier.
 * @author sergioc
 *
 */
public class Key {

	protected final Object id;
	
	public Key() {
		this(new Object());
	}
	
	/**
	 * 
	 * @param id the id of a category property.
	 */
	public Key(Object id) {
		this.id = id;
	}
	
	/**
	 * @param category the queried category.
	 * @return the value of the property represented by this object in the given category.
	 */
	protected <T> Optional<T> getForCategory(Category category) {
		return category.<T>getFromLocalMap(id);
	}

	/**
	 * @param category the modified category.
	 * @param value the value to set for the property represented by this object in the given category.
	 */
	protected void setForCategory(Category category, Object value) {
		category.putAtLocalMap(id, value);
	}
	
	/**
	 * @param category the modified category.
	 */
	protected void removeFromCategory(Category category) {
		category.removeFromLocalMap(id);
	}
	
	@Override
	public String toString() {
		return id.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
