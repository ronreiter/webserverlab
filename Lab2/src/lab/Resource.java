package lab;

import java.net.URL;

public class Resource {
    public static final int TYPE_OTHER = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_DOCUMENT = 4;

    public URL url;
    public byte[] body;
    public int type;
}
