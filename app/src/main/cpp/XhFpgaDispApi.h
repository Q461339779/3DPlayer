#include <android/log.h>
#define LOG_TAG "Fpga3D"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#include <fcntl.h>
#include <vector>
#include <jni.h>
#include <unistd.h>
#include <stdio.h>
#include <fstream>
#include <jni.h>
#include "ac_types-master/ac_fixed.h"

typedef ac_fixed<16, 12, true> FdSpaceCoords;//unit mm //32, 12
typedef ac_fixed<16, 4, true> FdTanTheta;//mobile & tablet only,//32,4
typedef ac_fixed<16, 3, false> FdPixelSize; //< 8mm //32，3
typedef ac_fixed<4, 2, false> FdOffsetValue; //4, 2
typedef ac_fixed<10, 5, false> FdViewPosi;//10, 5//< CalibreViewNo=18, 2^^5
#define FPGA_DEV "/dev/fpga-test"

class Fpga3D {
public:
    static int PlayCalibreMode(char iCalibreParam);
    static int Play3DMode();
    static int Play2DMode();
    static int PlayDebugMode(short iStartOffset);
    static int PlayFlickMode();
    static int setDispPxSize(double dSize);//mm

    static int RerversChanels(bool bRvs);
    static int setLtPxPosiCam(double dDistH, double dDisV);

    static int burnProgram(char *pData, short iDataLength);
    static uint getPrgVer();
    static uint getOptCheckSum();
    static int burnOptData(char* pData, short iDataLength);

    static int setEyePosi(double dxl, double dyl, double dzl,
            double dxr, double dyr, double dzr);
private:
    static int OpenDev();
    static int CloseDev();
};