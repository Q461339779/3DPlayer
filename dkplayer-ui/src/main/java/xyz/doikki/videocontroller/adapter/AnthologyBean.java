package xyz.doikki.videocontroller.adapter;

public  class AnthologyBean implements Vistable {

    private String name;
    private String path;
    private boolean ischecked = false;

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int type(TypeFactory factory) {
        return factory.type(this);
    }
}