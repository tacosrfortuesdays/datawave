package datawave.vectorindex;

import org.apache.hadoop.util.StringUtils;

import java.util.List;
import java.util.Iterator;

public final class Distance {
    private Distance() {}
    public static double l2Distance(List<Double> a, List<Double> b) {
        if (a.size() != b.size()) {
            throw new ArrayIndexOutOfBoundsException(StringUtils.format("Attempting to perform inner product on mismatched vector dimensions, dimA={}, dimB={}", a.size(), b.size()));
        }
        return l2Distance(a.iterator(), b.iterator());
    }

    public static double l2Distance(Iterator<Double> itrA, Iterator<Double> itrB){
        double sum = 0.0;
        while(itrA.hasNext() && itrB.hasNext()){
            double diff = itrA.next()-itrB.next();
            sum += diff*diff;
        }
        return Math.sqrt(sum);
    }

    public static double cosineSimilarityDistance(List<Double> a, List<Double> b) {
        if (a.size() != b.size()) {
            throw new ArrayIndexOutOfBoundsException(StringUtils.format("Attempting to perform cosine similarity on mismatched vector dimensions, dimA={}, dimB={}", a.size(), b.size()));
        }
        Iterator<Double> itrA = a.iterator();
        Iterator<Double> itrB = b.iterator();
        return cosineSimilarityDistance(itrA, itrB);
    }

    public static double cosineSimilarityDistance(Iterator<Double> itrA, Iterator<Double> itrB){
        double sum = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        while(itrA.hasNext() && itrB.hasNext()){
            double tmpA = itrA.next();
            double tmpB = itrB.next();
            sum += tmpA*tmpB;
            normA += tmpA*tmpA;
            normB += tmpB*tmpB;
        }
        if(normA == 0 || normB == 0){
            throw new ArithmeticException("Cannot calculate cosine similarity using a vector of norm zero");
        }
        return 1.0 - sum/(Math.sqrt(normA)*Math.sqrt(normB));
    }

    //Inner product distance assumes vectors are norm 1.
    public static double innerProductDistance(List<Double> a, List<Double> b) {
        if (a.size() != b.size()) {
            throw new ArrayIndexOutOfBoundsException(StringUtils.format("Attempting to perform cosine similarity on mismatched vector dimensions, dimA={}, dimB={}", a.size(), b.size()));
        }
        Iterator<Double> itrA = a.iterator();
        Iterator<Double> itrB = b.iterator();
        return innerProductDistance(itrA, itrB);
    }
    public static double innerProductDistance(Iterator<Double> itrA, Iterator<Double> itrB){
        double sum = 0.0;
        while(itrA.hasNext() && itrB.hasNext()){
            sum += itrA.next()*itrB.next();
        }
        return 1.0 - sum;
    }
}
