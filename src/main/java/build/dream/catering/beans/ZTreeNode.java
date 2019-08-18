package build.dream.catering.beans;

public class ZTreeNode {
    private String id;
    private String name;
    private String pId;

    public ZTreeNode() {

    }

    public ZTreeNode(String id, String name, String pId) {
        this.id = id;
        this.name = name;
        this.pId = pId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }
}
