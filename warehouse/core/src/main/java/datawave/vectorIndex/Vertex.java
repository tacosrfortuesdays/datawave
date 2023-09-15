package datawave.vectorIndex;

import org.apache.commons.lang.StringUtils;

public class Vertex {
    private final String uid;
    private final byte[] data;

    Vertex(final String uid, final byte[] data){
        this.uid = uid;
        this.data = data;
    }

    public String uid(){
        return this.uid;
    }
    public byte[] data(){
        return this.data;
    }
}