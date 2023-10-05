package datawave.vectorIndex;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Vertex {
    private static final Logger log = Logger.getLogger(Vertex.class);
    private final String uid;
    private final byte[] data;

    Vertex(final String uid, final byte[] data) {
        this.uid = uid;
        this.data = data;
    }

    public String uid() {
        return this.uid;
    }

    public byte[] data() {
        return this.data;
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
