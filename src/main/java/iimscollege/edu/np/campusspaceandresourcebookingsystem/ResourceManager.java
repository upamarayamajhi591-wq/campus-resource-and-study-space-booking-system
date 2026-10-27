package iimscollege.edu.np.campusspaceandresourcebookingsystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class ResourceManager<T> {

    private final Map<String, T> store = new LinkedHashMap<>();
    private final Function<T, String> idExtractor;

    public ResourceManager(Function<T, String> idExtractor) {
        this.idExtractor = idExtractor;
    }

    public void add(T item) {
        if (item != null) store.put(idExtractor.apply(item), item);
    }

    public T get(String id) {
        return store.get(id);
    }

    public List<T> getAll() {
        return new ArrayList<>(store.values());
    }

    /** Replaces the whole managed set - used to re-sync after load/seed from disk. */
    public void reset(Collection<? extends T> items) {
        store.clear();
        if (items != null) {
            for (T item : items) add(item);
        }
    }

    public int size() {
        return store.size();
    }
}