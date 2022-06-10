#include <jni.h>
#include <string>

#include <android/log.h>
#include <unistd.h>
#include "opencvinclude/opencv2/core/types.hpp"
#include "opencvinclude/opencv2/opencv.hpp"
#include "XhFpgaDispApi.h"

#define LOG_TAG "Fpga3D"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
static cv::CascadeClassifier faceCascadeClassifier = cv::CascadeClassifier();
int iReadingCoords = 0;
std::vector<cv::Point2f> objPosiVect1, objPosiVect2;
std::vector<cv::Point3d> objPosiVect;
int iLastFaceSizeWidth = -1;
int iLastFaceSizeHeight = -1;
cv::Mat matCam1,matCam2,matDist1,matDist2,matRttC,matTtlC;
cv::Size prevSize1,prevSize2;

clock_t clockLast;

cv::Point myFindTemplate2(cv::Mat matFindArea, cv::Mat matTemplate) {
    cv::Mat mResult1(matFindArea.rows - matTemplate.rows + 1,
                     matFindArea.cols - matTemplate.cols + 1, CV_32FC1);
    cv::matchTemplate(matFindArea, matTemplate, mResult1, cv::TM_SQDIFF);

    cv::Point ptMin = cv::Point(0, 0);
    cv::Point ptMax = cv::Point(0, 0);
    double dMax = 0, dmin = 0;
    minMaxLoc(mResult1, &dmin, &dMax, &ptMin, &ptMax);
    mResult1.release();
    cv::Point ptRet = cv::Point(ptMin.x + matTemplate.cols / 2, ptMin.y + matTemplate.rows / 2);
    return ptRet;
}

int getEyesPoint(std::vector<cv::Point2f> *pEyePosiVect, cv::Mat *pInputFrame,
                 cv::CascadeClassifier *pFaceCascadeClassifier,
                 int *pLastFaceSizeWidth, int *pLastFaceSizeHeight) {
    cv::Mat gray = cv::Mat();
    cvtColor(*pInputFrame, gray, cv::COLOR_RGB2GRAY);
    cv::equalizeHist(gray, gray);
    std::vector<cv::Rect> faces;
    LOGI("%s","CameraActivity_sendDataL1q0");
    //LOGI("%#v,%s",*pLastFaceSizeWidth,"pLastFaceSizeWidth");
    if ((*pLastFaceSizeWidth) < 0)
    {
        pFaceCascadeClassifier->detectMultiScale(gray, faces, 1.1, 2,
                                                 cv::CASCADE_FIND_BIGGEST_OBJECT,
                                                 cv::Size((int) (0.12 * gray.cols),
                                                          (int) (0.25 * gray.rows)),
                                                 cv::Size((int) (0.5 * gray.cols),
                                                          (int) (0.8 * gray.rows)));
        LOGI("%s","CameraActivity_sendDataL1q20");
    }
    else
    {
        pFaceCascadeClassifier->detectMultiScale(gray, faces, 1.1, 2,
                                                 cv::CASCADE_FIND_BIGGEST_OBJECT,
                                                 cv::Size((int) ((*pLastFaceSizeWidth) * 0.8),
                                                          (int) ((*pLastFaceSizeHeight) * 0.8)),
                                                 cv::Size((int) ((*pLastFaceSizeWidth) * 1.2),
                                                          (int) ((*pLastFaceSizeHeight) * 1.2)));
        LOGI("%s","CameraActivity_sendDataL1q21");
    }


    (*pLastFaceSizeWidth) = -1.0;
    (*pLastFaceSizeHeight) = -1.0;
    LOGI("%s","CameraActivity_sendDataL1q1");
    for (int i = 0; i < faces.size(); i++) {
        LOGI("%s","CameraActivity_sendDataL1q2");
        cv::Rect r = faces[i];
        (*pLastFaceSizeWidth) = r.width;
        (*pLastFaceSizeHeight) = r.height;

        cv::Rect eyeArea_right = cv::Rect((int) (r.x + r.width / 8.0),
                                          (int) (r.y + r.height * 0.17),
                                          (int) (r.width / 2 - r.width / 4),
                                          (int) (r.height * 0.23));
        cv::Rect eyeArea_left = cv::Rect((int) (r.x + r.width / 8.0 + r.width / 2.0),
                                         (int) (r.y + r.height * 0.17),
                                         (int) (r.width / 2 - r.width / 4),
                                         (int) (r.height * 0.23));

        cv::Mat matLEGray = cv::Mat(gray, eyeArea_left);
        cv::Mat matREGray = cv::Mat(gray, eyeArea_right);

        cv::equalizeHist(matLEGray, matLEGray);
        cv::equalizeHist(matREGray, matREGray);

        cv::Mat matPupilTmplate = cv::Mat((int) (0.06 * r.height), (int) (0.09 * r.width), CV_8UC1,
                                          cv::Scalar(0));

        //cv::rectangle(*pInputFrame, eyeArea_left.br(), eyeArea_left.tl(), cv::Scalar(0, 0, 255, 255), 10, cv::LINE_8, 0);
        //cv::rectangle(*pInputFrame, eyeArea_right.br(), eyeArea_right.tl(), cv::Scalar(0, 0, 255, 255), 10, cv::LINE_8, 0);

        cv::Point centerL = myFindTemplate2(matLEGray, matPupilTmplate);
        cv::Point centerR = myFindTemplate2(matREGray, matPupilTmplate);

        pEyePosiVect->push_back(
                cv::Point2f(eyeArea_left.x + centerL.x, eyeArea_left.y + centerL.y));
        pEyePosiVect->push_back(
                cv::Point2f(eyeArea_right.x + centerR.x, eyeArea_right.y + centerR.y));

        cv::Mat matLERgb = cv::Mat((*pInputFrame), eyeArea_left);
        cv::Mat matRERgb = cv::Mat((*pInputFrame), eyeArea_right);

        cv::rectangle(matLERgb, cv::Point(centerL.x - r.width * 0.045, centerL.y - r.width * 0.03),
                      cv::Point(centerL.x + r.width * 0.045, centerL.y + r.width * 0.03),
                      cv::Scalar(0, 0, 255, 255), -1, cv::LINE_8, 0);
        cv::rectangle(matRERgb, cv::Point(centerR.x - r.width * 0.045, centerR.y - r.width * 0.03),
                      cv::Point(centerR.x + r.width * 0.045, centerR.y + r.width * 0.03),
                      cv::Scalar(0, 0, 255, 255), -1, cv::LINE_8, 0);

        matLERgb.release();
        matRERgb.release();

        matLEGray.release();
        matREGray.release();
        matPupilTmplate.release();
        break;

    }
    LOGI("%s","CameraActivity_sendDataL1q3");
    gray.release();

    return 0;
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_xhsj_a3dlocalvideo_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_xhsj_a3dlocalvideo_CameraActivity_sendData(JNIEnv *env, jclass type, jbyteArray bytes_,
                                                    jboolean dataFlag, jint width, jint height ,jintArray array) {


    LOGI("%d,%s\n",bytes_,"CameraActivity_sendDatatest2");
    if (dataFlag){

        LOGI("%s","CameraActivity_sendData");
    } else{
        LOGI("%s","CameraActivity_sendData111111");
    }

    jbyte *pByteImg = env->GetByteArrayElements(bytes_, JNI_FALSE);
    //jint *pIntImgL = env->GetIntArrayElements(dataCanvas, JNI_FALSE);
    cv::Mat matNV21(height + height / 2, width, CV_8UC1, pByteImg);
    //cv::Mat matCanvas(jiCanvasV, jiCanvasH, CV_8UC4, pIntImg);
    cv::Mat matBGRA(height, width, CV_8UC4);
    cv::cvtColor(matNV21, matBGRA, cv::COLOR_YUV2BGRA_NV21);

    //jint *pIntImg = env->GetIntArrayElements(array, JNI_FALSE);
    //cv::Mat matCanvas(width, height, CV_8UC4, pIntImg);

    while (iReadingCoords ) {//原子操作，更新眼球数据////////////
        usleep(30);
    }
    iReadingCoords = 1;
    if (dataFlag) {
        LOGI("%s","CameraActivity_sendDataL");
        objPosiVect1.clear();
        LOGI("%s","CameraActivity_sendDataL1q");
        getEyesPoint(&objPosiVect1, &matBGRA, &faceCascadeClassifier, &iLastFaceSizeWidth,
                     &iLastFaceSizeHeight);
        //if(objPosiVect1.size())
            //cv::resize(matBGRA, matCanvas, cv::Size(matCanvas.cols, matCanvas.rows));
        LOGI("%d,%s\n",objPosiVect1.size(),"test2");
    } else {
        LOGI("%s","CameraActivity_sendDataR");
        objPosiVect2.clear();
        getEyesPoint(&objPosiVect2, &matBGRA, &faceCascadeClassifier, &iLastFaceSizeWidth,
                     &iLastFaceSizeHeight);

        //if(objPosiVect2.size())
           //cv::resize(matBGRA, matCanvas, cv::Size(matCanvas.cols, matCanvas.rows));


        if (objPosiVect1.size()>1 && (objPosiVect1.size() == objPosiVect2.size())){

            objPosiVect.clear();
            std::vector<cv::Point2f> objPosiVect1Undistort;

            cv::undistortPoints(objPosiVect1, objPosiVect1Undistort, matCam1, matDist1, cv::noArray());
            LOGI("%d,%s\n",objPosiVect2.size(),"test2");
            for(int i = 0; i < objPosiVect1.size() / 2; i++) {

                cv::Point3d eyePosi3D1 = cv::Point3d();
                eyePosi3D1.z = 62.0 / abs(objPosiVect1Undistort[2 * i].x - objPosiVect1Undistort[2 * i + 1].x);
                eyePosi3D1.x = -objPosiVect1Undistort[2 * i].x * eyePosi3D1.z;
                eyePosi3D1.y = -objPosiVect1Undistort[2 * i].y * eyePosi3D1.z;

                cv::Point3d eyePosi3D2 = cv::Point3d();
                eyePosi3D2.z = 62.0 / abs(objPosiVect1Undistort[2 * i].x - objPosiVect1Undistort[2 * i + 1].x);
                eyePosi3D2.x = -objPosiVect1Undistort[2 * i + 1].x * eyePosi3D2.z;
                eyePosi3D2.y = -objPosiVect1Undistort[2 * i + 1].y * eyePosi3D2.z;

                objPosiVect.push_back(eyePosi3D1);
                objPosiVect.push_back(eyePosi3D2);

                LOGD("1st eye Posi = (%f, %f, %f), 2nd eye posi = (%f, %f, %f)", eyePosi3D1.x, eyePosi3D1.y, eyePosi3D1.z, eyePosi3D2.x, eyePosi3D2.y, eyePosi3D2.z);
            }
//
            if (objPosiVect.size()>1){
                LOGE("%s\n","test201");
                Fpga3D::setEyePosi(objPosiVect[0].x, objPosiVect[0].y, objPosiVect[0].z,
                                   objPosiVect[1].x, objPosiVect[1].y, objPosiVect[1].z);
            }
        }


    }

    iReadingCoords = 0;
    //LOGE("%s\n","test2011");
    env->ReleaseByteArrayElements(bytes_, pByteImg, 0);
    //LOGE("%s\n","test201111");
//    env->ReleaseIntArrayElements(array,pIntImg, 0);
    //LOGE("%s\n","test2012");
    clock_t clockCur = clock();
    double dFrameTimeMs = ((double)(clockCur - clockLast))/CLOCKS_PER_SEC * 1000;
    clockLast = clockCur;

    LOGE("test dual feame time %f ms",dFrameTimeMs);


    return 0;



//    extern "C" JNIEXPORT jint JNICALL
//    Java_com_ivisual3d_calibre3ddisp_MainActivity_setCascadeClassifier(
//            JNIEnv *env, jobject obj, jstring jstrFaceFilePath) {
//        const char* face_cascade_file_name = env->GetStringUTFChars(jstrFaceFilePath, NULL);
//        if(faceCascadeClassifier.load(face_cascade_file_name))
//            return 0;
//        else
//            return -1;
//    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_xhsj_a3dlocalvideo_CameraActivity_setCascadeClassifier(JNIEnv *env, jclass type,
                                                                jstring jstrFaceFilePath_) {

    const char* face_cascade_file_name = env->GetStringUTFChars(jstrFaceFilePath_, NULL);
    if(faceCascadeClassifier.load(face_cascade_file_name))
        return 0;
    else
        return -1;


}


int dMatLoad(cv::Mat *pMat, char *pData, int *piOffset){
    int iRows = *((int *)(pData + (*piOffset)));
    (*piOffset) += sizeof(int)/sizeof(char);
    int iCols = *((int *)(pData + (*piOffset)));
    (*piOffset) += sizeof(int)/sizeof(char);

    *pMat = cv::Mat(iRows, iCols, CV_64FC1);
    for(int i = 0; i < pMat->rows; i++){
        for(int j = 0; j < pMat->cols; j++){
            double dValue = *((double *)(pData + (*piOffset)));
            (*piOffset) += sizeof(double)/sizeof(char);
            pMat->at<double>(i, j) = dValue;
        }
    }
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_xhsj_a3dlocalvideo_CameraActivity_loadCalibrateParam(JNIEnv *env, jobject instance,
                                                              jstring strStereoParamFile_) {
    //jbyte *pData = env->GetByteArrayElements(jba, JNI_FALSE);
    jbyte *pData = NULL;
    std::ifstream fs(env->GetStringUTFChars(strStereoParamFile_, NULL), std::ios::in | std::ios::binary);
    if(fs.is_open()){
        fs.seekg(0, std::ios::end);
        int iDataLen = fs.tellg();
        fs.seekg(0, std::ios::beg);
        pData = new jbyte[iDataLen];
        fs.read((char *)pData, iDataLen);
        fs.close();
    }

    if(pData == NULL)
        return -1;

    int iOffset = 0;

    dMatLoad(&matCam1, (char *)pData, &iOffset);
    dMatLoad(&matCam2, (char *)pData, &iOffset);
    dMatLoad(&matDist1, (char *)pData, &iOffset);
    dMatLoad(&matDist2, (char *)pData, &iOffset);
    dMatLoad(&matRttC, (char *)pData, &iOffset);
    dMatLoad(&matTtlC, (char *)pData, &iOffset);



    prevSize1.width = *((int *)(pData + iOffset));
    pData += sizeof(int)/sizeof(char);

    prevSize1.height = *((int *)(pData + iOffset));
    pData += sizeof(int)/sizeof(char);

    prevSize2.width = *((int *)(pData + iOffset));
    pData += sizeof(int)/sizeof(char);

    prevSize2.height = *((int *)(pData + iOffset));
    pData += sizeof(int)/sizeof(char);

    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_xhsj_a3dlocalvideo_CameraActivity_init3D(JNIEnv *env, jobject instance) {
    if(Fpga3D::Play3DMode() < 0)
        LOGE("XHLOG: set Play 3d Mode failed!");
    if(Fpga3D::setDispPxSize(0.1254))
        LOGE("XHLOG: set px size failed!");
    if(Fpga3D::setLtPxPosiCam(-109.65, -11.8))
    {
        LOGE("XHLOG: set Lt Px Posi failed!");
    }

    // 1
    system("/system/bin/i2ctool -w 0x50 0x0b 0x00 0x00 0x02 0x04 0x59 0x57 0xff 0x61");
    //2
    system("/system/bin/i2ctool -w 0x50 0x0b 0x00 0x00 0x02 0x08 0x00 0x00 0x52 0x3c");
    // 3
    system("/system/bin/i2ctool -w 0x50 0x0b 0x00 0x00 0x02 0x0c 0x57 0x59 0x3c 0x52");
    //4
    system("/system/bin/i2ctool -w 0x50 0x0b 0x00 0x00 0x02 0x10 0x00 0x00 0x61 0xff");
    LOGI("%s","system option test success");

    return 0;

}


extern "C"
JNIEXPORT jint JNICALL
Java_com_xhsj_a3dlocalvideo_CameraActivity_onInit(JNIEnv *env, jobject instance) {

    if(Fpga3D::Play2DMode() < 0)
        LOGE("XHLOG: set Play 2d Mode failed!");
    return 0;

}

