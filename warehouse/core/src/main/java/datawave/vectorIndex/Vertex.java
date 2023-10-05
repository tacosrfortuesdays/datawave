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
        log.warn(String.format("CHECKING OBJ IS THIS"));
        if (this == obj) {
            return true;
        }
        log.warn(String.format("CHECKING OBJ AND CLASS"));
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Vertex other = (Vertex) obj;
        log.warn(String.format("ID STRINGS ARE %s AND %s", this.uid(), other.uid()));
        return StringUtils.equals(this.uid(), other.uid());
    }

    @Override
    public int hashCode() {
        return this.uid().hashCode();
    }

}
