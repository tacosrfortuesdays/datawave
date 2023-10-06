package datawave.vectorIndex;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;

public class HNSW {
    private static final Function<Vertex, Boolean> noFilter = v -> true;

    private final int M;
    private final int Mmax;
    private final int efConstruction;
    private final int maxLevel;
    private final int dim;

    ArrayList<VectorGraph> hGraph;
    VectorGraph zeroLevel;
    private static final Function<Pair<List<Double>, List<Double>>, Double> dist = a -> Distance.l2Distance(a.getLeft(),a.getRight());

    public HNSW(int dim) {
        this(dim, 20, 2, 3, 4);
    }
    public HNSW(int dim, int M, int Mmax, int efConstruction, int maxLevel) {
        this.zeroLevel = new MemoryVectorGraph();
        this.hGraph = new ArrayList<>(maxLevel);
        for(int i=0; i<maxLevel-1; i++){
            VectorGraph g = new MemoryVectorGraph();
            hGraph.add(g);
        }
        this.M = M;
        this.Mmax = Mmax;
        this.efConstruction = efConstruction;
        this.maxLevel = maxLevel;
        this.dim = dim;
    }

    public boolean isEmpty(){
        return this.zeroLevel.isEmpty();
    }

    public int addVector(ArrayList<Double> data) {
        return this.insert(data);
    }

    public int insert(ArrayList<Double> data) {
        //Check dimension and values of vector input and warn if it's bad.
        return 0;
    }

    public ArrayList<Vertex> searchKnn(ArrayList<Double> data){
        return searchKnn(data, noFilter);
    }

    //We are using the hnswlib as a base which consolidates a lot of the paper code
    public ArrayList<Pair<Vertex,Double>> searchKnn(ArrayList<Double> queryData, Function<Vertex, Boolean> isAllowed){
        //TODO BEFORE COMMIT: WRAP IN A TRY CLAUSE

        PriorityQueue<Pair<Vertex,Double>> result = new PriorityQueue<>();
        //PriorityQueue<Pair<Vertex,Double>> result = new PriorityQueue<>(Comparator.reverseOrder());
        if(hGraph.isEmpty()){
            return new ArrayList<>(result);
        }
        Vertex currObj = enterpoint_vertex;
        //hnswlib has an optimization where for non-base layers they just perform greedy searches.
        if(hnswlibGreedy) {
            double currdist = dist.apply(new Pair<currObj.data(), queryData >);
            for (int level = maxLevel; level > 0; level--) {
                boolean changed = true;
                while (changed) {
                    changed = false;
                    Iterator<Vertex> nbrs = hGraph[level-1].getNeighbors(currObj,level);
                    //metric_hops++;
                    while (nbrs.hasNext()) {
                        //metric_distance_computations+=1;
                        Vertex cand = nbrs.next();
                        double d = dist.apply(new Pair<queryData, cand.data() >);
                        if (d < currdist) {
                            currdist = d;
                            currObj = cand;
                            changed = true;
                        }
                    }
                }
            }
        } else {
            for(int level = maxLevel; level > 0; level--){
                PriorityQueue<Pair<Vertex,Double>> W = searchLayer(queryData, currObj, level, noFilter);
                currObj = W.peek().getLeft();
                W.clear();
            }
        }
        PriorityQueue<Pair<Vertex, Double>> topCandidates = searchLayer(queryData, currObj, 0, isAllowed);
        // Return closest k verticies in desired order
        while (topCandidates.size() > k) {
            topCandidates.remove();
        }
        while (topCandidates.size() > 0){
            result.add(topCandidates.remove();
        }
        return new ArrayList<>(result);
    }

}
