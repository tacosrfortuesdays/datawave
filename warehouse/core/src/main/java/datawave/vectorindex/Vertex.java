package datawave.vectorindex;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Vertex {
    private static final Logger log = Logger.getLogger(Vertex.class);
    protected final String uid;
    protected Double[] data;
    protected boolean markedDeleted;

    Vertex(final String uid, final Double[] data) {
        this.uid = uid;
        this.data = data;
    }

    public String uid() {
        return this.uid;
    }

    public Double[] data() {
        return this.data;
    }

    public void setData(final Double[] data){
        this.data = data;
    }

    public boolean isMarkedDeleted(){
        return markedDeleted;
    }
    public void setDeleted(boolean deleted){
        this.markedDeleted = deleted;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Vertex other = (Vertex) obj;
        return StringUtils.equals(this.uid(), other.uid());
    }

    @Override
    public int hashCode() {
        return this.uid().hashCode();
    }

}
