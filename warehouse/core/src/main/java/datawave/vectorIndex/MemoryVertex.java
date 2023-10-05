package datawave.vectorIndex;

import org.apache.commons.lang.StringUtils;

public class MemoryVertex extends Vertex {
    MemoryVertex(final String uid, final byte[] data) {
        super(uid, data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        MemoryVertex other = (MemoryVertex) obj;
        return StringUtils.equals(this.uid(), other.uid());
    }

    @Override
    public int hashCode() {
        return this.uid().hashCode();
    }

}
