package xyz.doikki.videocontroller.adapter;

public  class SpeedBean implements Vistable {

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getNum() {
        return num;
    }

    public void setNum(float num) {
        this.num = num;
    }

    float num;

    private boolean ischecked = false;

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }



    @Override
    public int type(TypeFactory factory) {
        return factory.type(this);
    }
}