package datawave.vectorindex;

import org.apache.commons.math3.util.Pair;
import org.apache.hadoop.hdfs.server.datanode.ReplicaNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.function.Function;

public class HNSW {
    private static final Function<Vertex, Boolean> noFilter = v -> true;

    private final int mult;
    private final int Mmax;
    private final int efConstruction;
    private final long softMaxElements; //counts on big data is hard, so we assume a soft max is enforced.
    private final int dim;
    private final boolean allowReplaceDeleted;

    private ArrayList<VectorGraph> hGraph;
    private Vertex entryPoint;
    private int maxLevel;
    int entryPointLevel;
    //private static final Function<Pair<List<Double>, List<Double>>, Double> dist = a -> Distance.l2Distance(a.getFirst(), a.getSecond());
    private static final Function<Pair<Iterator<Double>, Iterator<Double>>, Double> dist = a -> Distance.l2Distance(a.getFirst(), a.getSecond());

    public HNSW(int dim) {
        this(dim, 20, 2, 3, 4, 50000000);
    }

    public HNSW(int dim, int mult, int Mmax, int efConstruction, long softMaxElements) {
        this.hGraph = new ArrayList<>();
        //for (int i = 0; i < maxLevel; i++) {
        //    VectorGraph g = new MemoryVectorGraph();
        //    hGraph.add(g);
        //}
        this.mult = mult;
        this.Mmax = Mmax;
        this.efConstruction = efConstruction;
        this.dim = dim;
        this.softMaxElements = softMaxElements;
        this.entryPoint = null;
        this.maxLevel = -1;
        this.allowReplaceDeleted = false;
    }


    public boolean isEmpty() {
        return this.hGraph.isEmpty() || this.hGraph.get(0).isEmpty();
    }

    private void addLevel() {
        VectorGraph g = new MemoryVectorGraph();
        hGraph.add(g);
    }

    private int getRandomLevel(double reverseSize){
        double r = -Math.log(1-Math.random())*reverseSize;
        return (int) r;
    }


    public Vertex addPoint(ArrayList<Double> data) {
        //Check dimension and values of vector input and warn if it's bad.
        //Locking note: hnswlib tracks the current number of elements in memory
        // we will want to prevent creating nodes with same somehow.

        //we will change to datawave style uid eventually, for now just use a random uuid
        String uuid = UUID.randomUUID().toString();
        Vertex v = new Vertex(uuid, data);
        return addPoint(v, false);
    }

    public void addPoint(Vertex newVertex, boolean replaceDeleted){
        if(!this.allowReplaceDeleted && replaceDeleted) {
            throw new RuntimeException("Unable to replace deleted point when replacement has been disabled");
        }
        // lock all operations with element by label
        // std::unique_lock <std::mutex> lock_label(getLabelOpMutex(label));
        // Handle without replacing deleted.
        if (!replaceDeleted) {
            addPoint(newVertex,  -1);
            return;
        }
        //Check if there is a vacant place
        int internalIDReplaced = 0;
        //std::unique_lock <std::mutex> lock_deleted_elements(deleted_elements_lock);
        boolean isVacantPlace = !this.deletedElements.isEmpty();
        // If there is no vacant place, then add or update point
        if (!isVacantPlace){
            // lock_deleted_elements.unlock();
            addPoint(newVertex,  -1);
            return;
        }
        // else add point vacant place
        internalIDReplaced = this.deletedElements.get(0);
        deletedElements.erase(internalIDReplaced);
        // lock_deleted_elements.unlock();

            // we assume that there are no concurrent operations on deleted element
            String labelReplaced = getExternalLabel(internalIDReplaced);
            setExternalLabel(internalIDReplaced, newVertex.uid);

            //std::unique_lock <std::mutex> lock_table(label_lookup_lock);
            labelLookup.erase(label_replaced);
            labelLookup[label] = internalIDReplaced;
            lockTable.unlock();

            unmarkDeletedInternal(internalIDReplaced);
            updatePoint(newVertex, internaIDReplaced, 1.0);
    }

    Vertex addPoint(Vertex newVertex, int inputLevel) {
        //Check if the element with the same label/uid already exists.
        //If so, update it
        //std::unique_lock <std::mutex> lock_table(label_lookup_lock);
        Vertex search = hGraph.get(0).getVertex(newVertex.uid());
        if(search != null) {
            if (this.allowReplaceDeleted) {
                if (search.isMarkedDeleted()) {
                    throw new RuntimeException("Can't use add point to update elements if the replacement of deleted elements is enabled");
                }
            }
            //lock_table.unlock();
            if (search.isMarkedDeleted()){
                search.setDeleted(false);
            }
            updatePoint(newVertex, search, 1.0);
            return search;
        }
        if (hGraph.get(0).numVerticies() >= this.softMaxElements){
            throw new RuntimeException("The number of elements exceeds the specified limit");
        }
        // Calculate the level for the added point. based on input;
        //  std::unique_lock <std::mutex> lock_el(link_list_locks_[cur_c]);
        int curLevel = getRandomLevel(this.mult);
        if (inputLevel > 0) {
            curLevel = inputLevel;
        }

        // element_levels_[cur_c] = curlevel;
        //std::unique_lock <std::mutex> templock(global);
        int maxLevelcopy = maxLevel;
        //if (curLevel <= maxLevel) {
        //    templock.unlock();
        //}
        Vertex currObj = entryPoint;
        Vertex enterpointCopy = new Vertex(currObj.uid, currObj.data());

        //Allocate memory calls
        //memset(data_level0_memory_ + cur_c * size_data_per_element_ + offsetLevel0_, 0, size_data_per_element_);
        // Initialisation of the data and label
        //memcpy(getExternalLabeLp(cur_c), &label, sizeof(labeltype));
        //memcpy(getDataByInternalId(cur_c), data_point, data_size_);

        //if (curlevel != 0) {
         //   linkLists_[cur_c] = (char *) malloc(size_links_per_element_ * curlevel + 1);
         //   if (linkLists_[cur_c] == nullptr)
         //       throw std::runtime_error("Not enough memory: addPoint failed to allocate linklist");
         //   memset(linkLists_[cur_c], 0, size_links_per_element_ * curlevel + 1);
       // }

        if (entryPoint != null) {
            if (curLevel < maxLevelcopy) {
                Pair<Iterator<Double>, Iterator<Double>> param = new Pair<>(Arrays.stream(newVertex.data()).iterator(), Arrays.stream(currObj.data()).iterator());
                Double curDist = dist.apply(param);
                for (int level = maxLevelcopy; level > curLevel; level--) {
                    boolean changed = true;
                    while (changed) {
                        changed = false;
                        //std::unique_lock <std::mutex> lock(link_list_locks_[currObj]);

                        Iterator<Vertex> nbrs = hGraph.get(level).getNeighbors(currObj, level);
                        while(nbrs.hasNext()){
                            Vertex cand = nbrs.next();
                            param = new Pair<>(Arrays.stream(newVertex.data()).iterator(), Arrays.stream(cand.data()).iterator());
                            Double d = dist.apply(param);
                            if (d<curDist){
                                curDist = d;
                                currObj = cand;
                                changed = true;
                            }
                        }
                    }
                }
            }

            boolean epDeleted = enterpointCopy.isMarkedDeleted();
            for (int level = Math.min(curLevel, maxLevelcopy); level >= 0; level--) {
                //Note: Java priority queues are min queues, not max queues like C++.
                PriorityQueue<Pair<Vertex,Double>> topCandidates = searchBaseLayer(currObj, newVertex, level);
                if (epDeleted) {
                    Pair<Iterator<Double>,Iterator<Double>> param = new Pair<>(Arrays.stream(newVertex.data()).iterator(), Arrays.stream(enterpointCopy.data()).iterator());
                    topCandidates.add(new Pair<>(enterpointCopy, -dist.apply(param)));
                    if (topCandidates.size() > efConstruction){
                       topCandidates.remove();
                    }
                }
                currObj = mutuallyConnectNewElement(newVertex, topCandidates, level, false);
            }
        } else {
            // For a new point, no edges are made, but we track the new entrypoint
            this.entryPoint = newVertex;
            this.maxLevel = curLevel;
        }

        if (curLevel > maxLevelcopy) {
            // Should add a lock here for adding new levels.
            while(hGraph.size() <= curLevel) {
                this.addLevel();
            }
            this.entryPoint = newVertex;
            this.maxLevel = curLevel;
        }
        return newVertex;
    }

    public PriorityQueue<Pair<Vertex, Double>> searchKnn(Double[] queryData, int k)  {
        std::priority_queue<std::pair<dist_t, labeltype >> result;
        if (cur_element_count == 0) return result;

        tableint currObj = enterpoint_node_;
        dist_t curdist = fstdistfunc_(query_data, getDataByInternalId(enterpoint_node_), dist_func_param_);

        for (int level = maxlevel_; level > 0; level--) {
            bool changed = true;
            while (changed) {
                changed = false;
                unsigned int *data;

                data = (unsigned int *) get_linklist(currObj, level);
                int size = getListCount(data);
                metric_hops++;
                metric_distance_computations+=size;

                tableint *datal = (tableint *) (data + 1);
                for (int i = 0; i < size; i++) {
                    tableint cand = datal[i];
                    if (cand < 0 || cand > max_elements_)
                        throw std::runtime_error("cand error");
                    dist_t d = fstdistfunc_(query_data, getDataByInternalId(cand), dist_func_param_);

                    if (d < curdist) {
                        curdist = d;
                        currObj = cand;
                        changed = true;
                    }
                }
            }
        }

        std::priority_queue<std::pair<dist_t, tableint>, std::vector<std::pair<dist_t, tableint>>, CompareByFirst> top_candidates;
        if (num_deleted_) {
            top_candidates = searchBaseLayerST<true, true>(
                    currObj, query_data, std::max(ef_, k), isIdAllowed);
        } else {
            top_candidates = searchBaseLayerST<false, true>(
                    currObj, query_data, std::max(ef_, k), isIdAllowed);
        }

        while (top_candidates.size() > k) {
            top_candidates.pop();
        }
        while (top_candidates.size() > 0) {
            std::pair<dist_t, tableint> rez = top_candidates.top();
            result.push(std::pair<dist_t, labeltype>(rez.first, getExternalLabel(rez.second)));
            top_candidates.pop();
        }
        return result;
    }


}