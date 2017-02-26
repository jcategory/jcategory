package org.jcategory.category;


import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SingletonKey implements Key {


    protected final Object id;

    protected SingletonKey() {
        this(new Object());
    }

    /**
     *
     * @param id the id of a category property.
     */
    protected SingletonKey(Object id) {
        this.id = id;
    }


    /**
     * @param category the queried category.
     * @return the value of the property represented by this object in the given category.
     */
    public <T> List<T> getForCategory(Category category) {
        Optional<T> propertyOpt = category.<T>getFromLocalMap(this);
        if (propertyOpt.isPresent()) {
            return asList(propertyOpt.get());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * @param category the modified category.
     * @param value the value to set for the property represented by this object in the given category.
     */
    public void setForCategory(Category category, Object value) {
        category.putAtLocalMap(this, value);
    }

    /**
     * @param category the modified category.
     */
    public void removeFromCategory(Category category) {
        category.removeFromLocalMap(this);
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
        SingletonKey other = (SingletonKey) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
