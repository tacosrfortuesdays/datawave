package datawave.vectorIndex;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;

public class HNSW {
    private static final Function<Vertex, Boolean> noFilter = v -> true;

    private final int M;
    private final int Mmax;
    private final int efConstruction;
    private final int maxLevel;
    private final int dim;

    ArrayList<VectorGraph> hGraph;
    private static final Function<Pair<List<Double>, List<Double>>, Double> dist = a -> Distance.l2Distance(a.getLeft(), a.getRight());

    public HNSW(int dim) {
        this(dim, 20, 2, 3, 4);
    }

    public HNSW(int dim, int M, int Mmax, int efConstruction, int maxLevel) {
        this.hGraph = new ArrayList<>(maxLevel);
        for (int i = 0; i < maxLevel; i++) {
            VectorGraph g = new MemoryVectorGraph();
            hGraph.add(g);
        }
        this.M = M;
        this.Mmax = Mmax;
        this.efConstruction = efConstruction;
        this.maxLevel = maxLevel;
        this.dim = dim;
    }

    public boolean isEmpty() {
        return this.zeroLevel.isEmpty();
    }

    public int addVector(ArrayList<Double> data) {
        return this.insert(data);
    }

    public int insert(ArrayList<Double> data) {
        //Check dimension and values of vector input and warn if it's bad.
        return 0;
    }

    public ArrayList<Vertex> searchKnn(ArrayList<Double> data) {
        return searchKnn(data, noFilter);
    }

    //We are using the hnswlib as a base which consolidates a lot of the paper code
    public ArrayList<Pair<Vertex, Double>> searchKnn(ArrayList<Double> queryData, Function<Vertex, Boolean> isAllowed) {
        //TODO BEFORE COMMIT: WRAP IN A TRY CLAUSE

        PriorityQueue<Pair<Vertex, Double>> result = new PriorityQueue<>();
        if (hGraph.isEmpty()) {
            return new ArrayList<>(result);
        }
        Vertex currObj = enterpoint_vertex;
        //hnswlib has an optimization where for non-base layers they just perform greedy searches.
        if (hnswlibGreedy) {
            double currdist = dist.apply(new Pair<currObj.data(), queryData >);
            for (int level = maxLevel; level > 0; level--) {
                boolean changed = true;
                while (changed) {
                    changed = false;
                    Iterator<Vertex> nbrs = hGraph.get(level).getNeighbors(currObj, level);
                    while (nbrs.hasNext()) {
                        Vertex cand = nbrs.next();
                        double d = dist.apply(new Pair<>(queryData, cand.data()));
                        if (d < currdist) {
                            currdist = d;
                            currObj = cand;
                            changed = true;
                        }
                    }
                }
            }
        } else {
            for (int level = maxLevel; level > 0; level--) {
                PriorityQueue<Pair<Vertex, Double>> W = searchLayer(queryData, currObj, level, noFilter);
                currObj = W.peek().getFirst();
                W.clear();
            }
        }
        PriorityQueue<Pair<Vertex, Double>> topCandidates = searchLayer(queryData, currObj, 0, isAllowed);
        // Return closest k verticies in desired order
        while (topCandidates.size() > k) {
            topCandidates.remove();
        }
        while (topCandidates.size() > 0) {
            result.add(topCandidates.remove());
        }
        return new ArrayList<>(result);
    }

    //hnswlib always uses single entry points, the paper calls for multiple entry points
    public PriorityQueue<Pair<Vertex, Double>> searchLayer(ArrayList<Double> queryData, ArrayList<Vertex> entryPoints, int layer, Function<Vertex, Boolean> isAllowed) {
        Set<String> visited = new HashSet<>();
        PriorityQueue<Pair<Vertex, Double>> candidateSet = new PriorityQueue<>();  // C set from paper
        PriorityQueue<Pair<Vertex, Double>> topCandidates = new PriorityQueue<>(Comparator.reverseOrder()); // W set from paper
        Double bound = Double.MAX_VALUE;

        for (Vertex currObj : entryPoints) {
            if (isAllowed.apply(currObj)) {
                bound = dist.apply(new Pair<currObj.data(), queryData >);
                Pair<Vertex, Double> pair = new Pair<>(currObj, bound);
                topCandidates.add(pair);
                candidateSet.add(pair);
            } else {
                candidateSet.add(new Pair<>(currObj, bound)); //TODO BEFORE MERGE: should check or test this and then remove comment
            }
            visited.add(currObj.uid());
        }

        while (!candidateSet.isEmpty()) {
            Pair<Vertex, Double> currentVertexPair = candidateSet.remove();
            Vertex currObj = currentVertexPair.getFirst();
            if (currentVertexPair.getSecond() > bound && isAllowed.apply(currObj)) {
                break;
            }
            Iterator<Vertex> nbrs = hGraph.get(layer).getNeighbors(currObj, layer);
            while (nbrs.hasNext()) {
                Vertex candidate = nbrs.next();
                if (visited.contains(candidate.uid())) {
                    continue;
                }
                visited.add(candidate.uid());
                Double candidateDistance = dist.apply(candidate.data(), queryData);
                if (topCandidates.size() < ef || candidateDistance < bound) {
                    candidateSet.add(new Pair<>(candidate, candidateDistance));
                    if (isAllowed.apply(candidate)) {
                        topCandidates.add(new Pair<>(candidate, candidateDistance));
                        if (topCandidates.size() > ef) {
                            topCandidates.remove();
                        }
                    }
                    if (!topCandidates.isEmpty()) {
                        bound = topCandidates.peek().getSecond();
                    }
                }

            }

            return topCandidates;
        }
    }

}