package xyz.doikki.videocontroller.eventbus;

public class MessageEventPostion {

    String flag;

    int postion;

    int prePosition;//之前选中位置

    float speed ;

    String mResolutionUrl;

    public String getmResolutionUrl() {
        return mResolutionUrl;
    }

    public void setmResolutionUrl(String mResolutionUrl) {
        this.mResolutionUrl = mResolutionUrl;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }


    public int getPrePosition() {
        return prePosition;
    }

    public void setPrePosition(int prePosition) {
        this.prePosition = prePosition;
    }



    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
