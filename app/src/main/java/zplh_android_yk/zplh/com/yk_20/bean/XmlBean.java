package zplh_android_yk.zplh.com.yk_20.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lichun on 2017/6/6.
 * Description:
 */

public class XmlBean {
    public Hierarchy hierarchy;

    public static class Hierarchy{
        @SerializedName("rotation")
        public String rotation;
        public Node node;
    }

    public static class Node{
        @SerializedName("index")
        public String index;
        @SerializedName("class")
        public String _class;
        @SerializedName("package")
        public String _package;
        @SerializedName("content-desc")
        public String content_desc;
        @SerializedName("checkable")
        public boolean checkable;
        @SerializedName("clickable")
        public boolean clickable;
        @SerializedName("enabled")
        public boolean enabled;
        @SerializedName("focusable")
        public boolean focusable;
        @SerializedName("focused")
        public boolean focused;
        @SerializedName("scrollable")
        public boolean scrollable;
        @SerializedName("long-clickable")
        public boolean long_clickable;
        @SerializedName("password")
        public boolean password;
        @SerializedName("selected")
        public boolean selected;
        @SerializedName("bounds")
        public String bounds;

        @SerializedName("text")
        public String text;//可能有可能没有

        @SerializedName("checked")
        public boolean checked;//可能有可能没有

        @SerializedName("resource-id")
        public String resource_id;   //可能有，可能没有

        @SerializedName("node")
        public Node node;    //可能有，可能没有

        @SerializedName("node_list")
        public List<Node> node_list;    //可能有，可能没有
    }
}
