package datawave.vectorIndex;

import java.util.ArrayList;
import java.util.Iterator;

public class HNSW {

    private final int M;
    private final int Mmax;
    private final int efConstruction;
private final int mL;

    VectorGraph graph;

    public HNSW(int dim) {
        this(dim, 20, 2, 3, 4);
    }
    public HNSW(int dim, int M, int Mmax, int efConstruction, int mL) {
        this.graph = new MemoryVectorGraph();
        this.M = M;
        this.Mmax = Mmax;
        this.efConstruction = efConstruction;
        this.mL = mL;
        this.dim =
    }

    public double innerProductNorm(ArrayList<Double> A, ArrayList<Double> B){
        if(A.size() != this.dim || B.size() != this.dim){
            throw new ArrayIndexOutOfBoundsException("Cannot perform inner product on unexpected vector dimensions, dimA=%d, dimB=%d", A.size(), B.size());
        }
        Iterator<Double> itrA = A.iterator();
        Iterator<Double> itrB = B.iterator();

        double sum = 0.0;
        while(itrA.hasNext() && itrB.hasNext()){
            double tmpA = itrA.next();
            double tmpB = itrB.next();
            sum += (tmpA-tmpB)*(tmpA-tmpB);
        }
        return Math.sqrt(sum);
    }
    public double cosineSimilarity(ArrayList<Double> A, ArrayList<Double> B){
        if(A.size() != this.dim || B.size() != this.dim){
            throw new ArrayIndexOutOfBoundsException("Cannot calculate cosine similarity on unexpected vector dimensions, dimA=%d, dimB=%d", A.size(), B.size());
        }
        Iterator<Double> itrA = A.iterator();
        Iterator<Double> itrB = B.iterator();

        double sum = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        while(itrA.hasNext() && itrB.hasNext()){
            double tmpA = itrA.next();
            double tmpB = itrB.next();
            sum += itrA.next()*itrB.next();
            normA += tmpA*tmpA;
            normB += tmpB*tmpB;
        }
        if(normA == 0 || normB == 0){
            throw new ArithmeticException("Cannot calculate cosine similarity using a vector of norm zero");
        }
        return sum/(Math.sqrt(normA)*Math.sqrt(normB));
    }

    public int addVector(ArrayList<Double> vec) {
        return this.insert(vec);
    }

    public int insert(ArrayList<Double> q) {
        //Check dimension and values of vector input and warn if it's bad.
        return 0;
    }

    public ArrayList<Vertex> knnSearch(Vertex )

}
