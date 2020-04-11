package sudokudbj;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Pivot Table
 */
public class PivotUtil {

    /**
     * flat2pivot
     * @param <S> grouping
     * @param <T> the type of keys maintained by this map
     * @param <U> fact
     * @param <V> flat element
     */
    public static <S, T, U, V extends Pivotable<S, T, U, V>> List<V> flat2pivot(List<V> table) {

        // grouping
        Map<S, List<V>> groupMap = table.stream().collect(Collectors.groupingBy(v -> v.groupBy()));

        // pivot
        List<V> resList = new ArrayList<V>();
        for (List<V> list : groupMap.values()) {
            V pivotable = list.get(0);

            Map<T, U> eleMap = pivotable.getMap();
            for (V p : list) {
                eleMap.put(p.getColumn(), p.getValue());
            }

            resList.add(pivotable);
        }

        return resList;
    }

    /**
     * pivot2flat
     */
    public static <S, T, U, V extends Pivotable<S, T, U, V>> List<V> pivot2flat(List<V> table) {

        List<V> resList = new ArrayList<V>();
        for (V p : table) {
            for (Entry<T, U> entry : p.getMap().entrySet()) {
                V pp = p.copy();
                pp.setColumn(entry.getKey());
                pp.setValue(entry.getValue());

                resList.add(pp);
            }
        }

        return resList;
    }

    /**
     * Pivot Table Interface
     */
    public static interface Pivotable<S, T, U, V extends Pivotable<S, T, U, V>> {

        /**
         * grouping(分组)
         */
        public S groupBy();

        /**
         * Column(列)
         */
        public T getColumn();

        /**
         * Value(数据)
         */
        public U getValue();

        /**
         * Column(列)
         */
        public void setColumn(T t);

        /**
         * Value(数据)
         */
        public void setValue(U u);

        /**
         * Column(列), Value(数据)
         */
        public Map<T, U> getMap();

        /**
         * copy
         */
        public V copy();
    }
}
