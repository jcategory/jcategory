package org.jcategory.category;


import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public class MultiKey implements Key {

    private final List<Key> keys;

    public MultiKey(List<Key> keys) {
        this.keys = keys;
    }

    public static MultiKey keys(Key ...keys) {
        return new MultiKey(asList(keys));
    }

    @Override
    public <T> List<T> getForCategory(Category category) {
        List<T> result = new ArrayList<>();
        keys.stream().forEach(key -> result.addAll(key.getForCategory(category)));
        return result;
    }

    @Override
    public void setForCategory(Category category, Object value) {
        keys.stream().forEach(key -> key.setForCategory(category, value));
    }

    @Override
    public void removeFromCategory(Category category) {
        keys.stream().forEach(key -> key.removeFromCategory(category));
    }
}
