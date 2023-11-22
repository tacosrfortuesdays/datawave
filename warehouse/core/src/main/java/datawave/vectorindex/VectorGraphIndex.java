package datawave.vectorindex;

import java.util.List;

import com.google.common.annotations.VisibleForTesting;

public interface VectorGraphIndex<T> {
    @VisibleForTesting
    void resetIndex(String graphUID);

    public void initializeIndex(String graphUID);

    public void addNodes(List<T> nodes);

    public void removeNode(List<T> nodes);

    public List<T> knnQuery(T node);

}
