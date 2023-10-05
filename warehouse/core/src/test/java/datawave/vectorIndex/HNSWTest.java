package datawave.vectorIndex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class HNSWTest {
        private static final Logger log = Logger.getLogger(datawave.vectorIndex.HNSWTest.class);
        private HNSW index;
        private final ArrayList<Double> VecA = new ArrayList<>(Arrays.asList(0.25, 0.91, 0.32, 0.5, 0.5));
        private final ArrayList<Double> VecB = new ArrayList<>(Arrays.asList(1.0, 0.22, 0.32, 0.5, 0.5));
        private final ArrayList<Double> VecC = new ArrayList<>(Arrays.asList(0.25, 0.91, 0.32, 0.5, 100.5));
        private final ArrayList<Double> VecD = new ArrayList<>(Arrays.asList(0.24, 91.22, 0.32, 0.5, 0.5));
        private final ArrayList<Double> VecE = new ArrayList<>(Arrays.asList(0.24, 0.90, 0.31, 0.5, 0.51));
        private final ArrayList<Double> VecF = new ArrayList<>(Arrays.asList(12.22, 199.2, 31.0, 5.44, 5.66));

        private int uuidA;
        private int uuidB;
        private int uuidC;
        private int uuidD;
        private int uuidE;
        private int uuidF;


        @Before
        public void setup() {
            this.index = new MemoryHNSW();

            this.uuidA = this.index.addVector(VecA);
            this.uuidB = this.index.addVector(VecB);
            this.uuidC = this.index.addVector(VecC);
            this.uuidD = this.index.addVector(VecD);
            this.uuidE = this.index.addVector(VecE);
            this.uuidF = this.index.addVector(VecF);
        }

        @Test
        public void checkInit(){
            assertEquals(6, this.index.size());
        }

        @Test
        public void checkAddVertex(){
            final ArrayList<Double> VecG = new ArrayList<>(Arrays.asList(1.22, 1.2, 31.0, 5.44, 5.66));
            this.index.addVector(VecG);
            assertEquals(7, this.index.size());
        }

        @Test
        public void checkRecall(){
            //Test if we put in a vector, we get its exact match and it's nearest neighbor at the top
            final ArrayList<Vertex> results = this.index.knnSearch(VecA);
            assertEquals(uuidA, results.get(0));
            assertEquals(uuidE, results.get(1));
        }
        @Test
        public void checkKNN(){
            final ArrayList<Double> VecH = new ArrayList<>(Arrays.asList(0.241, 0.901, 0.31, 0.5, 0.51));
            this.index.addVector(VecH);
            final ArrayList<Vertex> results = this.index.knnSearch(VecA);
            assertEquals(uuidE, results.get(0));
            assertEquals(uuidA, results.get(1));
        }
}
