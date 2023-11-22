package datawave.vectorindex;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;

public class VertexDistanceTupleComparator implements Comparator<Pair<Vertex,Double>> {
    @Override
    public int compare(Pair<Vertex,Double> o1, Pair<Vertex,Double> o2) {
        return o1.getValue().compareTo(o2.getValue());
    }

}
