package com.xhsj.a3dlocalvideo.suface;


import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Size;
import android.view.TextureView;

import java.util.Iterator;
import java.util.List;

public class NlCamera {//NL的所有操作必须在主线程内??
    public Camera camera = null;
    public Size sizeCap = new Size(-1, -1);
    public Size sizePrev = new Size(-1, -1);

    private boolean bPause = false;

    public int Open(int iCameraId) {
        camera = Camera.open(iCameraId);
        if (camera == null) {
            return -1;
        }

        final Camera.Parameters cameraParam = camera.getParameters();
        List<Camera.Size> sizePrevSizelist = cameraParam.getSupportedPreviewSizes();
        if (sizePrevSizelist.size() > 0) {
            Iterator<Camera.Size> itor = sizePrevSizelist.iterator();
            while (itor.hasNext()) {
                Camera.Size cur = itor.next();
                if (sizePrev.getWidth() <= 0 || sizePrev.getHeight() <= 0) {
                    sizePrev = new Size(cur.width, cur.height);
                } else if (sizePrev.getWidth() * sizePrev.getHeight() < cur.width * cur.height) {
                    sizePrev = new Size(cur.width, cur.height);
                }
            }
            cameraParam.setPreviewSize(sizePrev.getWidth(), sizePrev.getHeight());
        } else {
            camera.release();
            camera = null;
            return -1;
        }

        List<Camera.Size> sizeCapSizelist = cameraParam.getSupportedPictureSizes();
        if (sizeCapSizelist.size() > 0) {
            Iterator<Camera.Size> itor = sizeCapSizelist.iterator();
            while (itor.hasNext()) {
                Camera.Size cur = itor.next();
                if (sizeCap.getWidth() <= 0 || sizeCap.getHeight() <= 0) {
                    sizeCap = new Size(cur.width, cur.height);
                } else if (sizeCap.getWidth() * sizeCap.getHeight() < cur.width * cur.height) {
                    sizeCap = new Size(cur.width, cur.height);
                }
            }
            cameraParam.setPictureSize(sizeCap.getWidth(), sizeCap.getHeight());
        } else {
            camera.release();
            camera = null;
            return -1;
        }

        cameraParam.setPreviewFormat(ImageFormat.NV21);
        camera.setParameters(cameraParam);
        return 0;
    };

    public int SetPreviewTexture(TextureView textureView){
        if(bPause)
            return -1;
        if(textureView == null)
            return -1;

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                if(camera != null){
                    try{
                        camera.setPreviewTexture(surfaceTexture);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    camera.startPreview();
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if(success){
                                camera.cancelAutoFocus();
                            }
                        }
                    });
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
                if(camera != null){
                    try{
                        camera.setPreviewTexture(surfaceTexture);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                Destroy();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                if(camera != null){
                    try{
                        camera.setPreviewTexture(surfaceTexture);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        return 0;
    }

    public void Destroy(){
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    public void Pause(){
        bPause = true;
        if (camera != null) {
            camera.stopPreview();
        }
    }

    public void Resume(){
        if ((camera != null) && bPause) {
            camera.startPreview();
        }
        bPause = false;
    }
}
