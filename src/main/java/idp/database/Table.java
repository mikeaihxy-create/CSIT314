package idp.database;

import idp.util.Identifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Table<T extends Identifiable> {

    private final Map<Long, T> rows = new LinkedHashMap<>();
    private long sequence;

    public T save(T row) {
        if (row.getId() <= 0) {
            row.setId(++sequence);
        } else if (row.getId() > sequence) {
            sequence = row.getId();
        }
        rows.put(row.getId(), row);
        return row;
    }

    public T saveWithId(long id, T row) {
        row.setId(id);
        if (id > sequence) {
            sequence = id;
        }
        rows.put(id, row);
        return row;
    }

    public T find(long id) {
        return rows.get(id);
    }

    public boolean contains(long id) {
        return rows.containsKey(id);
    }

    public List<T> all() {
        return new ArrayList<>(rows.values());
    }

    public List<T> findAll(Collection<Long> ids) {
        List<T> result = new ArrayList<>();
        for (Long id : ids) {
            T row = rows.get(id);
            if (row != null) {
                result.add(row);
            }
        }
        return result;
    }

    public boolean remove(long id) {
        return rows.remove(id) != null;
    }

    public void clear() {
        rows.clear();
        sequence = 0;
    }

    public int size() {
        return rows.size();
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public long nextId() {
        return sequence + 1;
    }
}
