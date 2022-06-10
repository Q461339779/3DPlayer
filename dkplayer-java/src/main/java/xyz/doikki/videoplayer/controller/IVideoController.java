package xyz.doikki.videoplayer.controller;

public interface IVideoController {

    /**
     * 开始控制视图自动隐藏倒计时
     */
    void startFadeOut();

    /**
     * 取消控制视图自动隐藏倒计时
     */
    void stopFadeOut();

    /**
     * 控制视图是否处于显示状态
     */
    boolean isShowing();

    /**
     * 设置锁定状态
     * @param locked 是否锁定
     */
    void setLocked(boolean locked);

    /**
     * 是否处于锁定状态
     */
    boolean isLocked();

    /**
     * 是否开启蒙版状态
     */
    boolean isMasked();

    /**
     * 开始刷新进度
     */
    void startProgress();

    /**
     * 停止刷新进度
     */
    void stopProgress();

    /**
     * 显示控制视图
     */
    void hide();

    /**
     * 隐藏控制视图
     */
    void show();

    /**
     * 是否需要适配刘海
     */
    boolean hasCutout();

    /**
     * 获取刘海的高度
     */
    int getCutoutHeight();


    /**
     * 是否设置状态
     * @param masked 是否设置
     */
    void setMask(boolean masked,String flag);





}
