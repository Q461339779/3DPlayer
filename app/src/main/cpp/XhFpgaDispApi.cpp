
#include "XhFpgaDispApi.h"
bool bI2cInUse = false;
int i2cFd = -1;
int Fpga3D::OpenDev(){
    while(bI2cInUse){
        usleep(1000);
    }
    bI2cInUse = true;

    i2cFd = open("/dev/fpga-test", O_RDWR);
    if(i2cFd < 0){
        LOGI("FPGA_DEV file open failed!");
        return -1;
    } else{
        LOGI("FPGA_DEV file open success!");
        return i2cFd;
    }
}
int Fpga3D::CloseDev(){
    close(i2cFd);
    bI2cInUse = false;
    i2cFd = -1;
    LOGI("FPGA_DEV file closed");
    return 0;
}
int Fpga3D::PlayCalibreMode(char iCalibreParam){
    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    char cCmd0 [] = {0x50, 0x01, 0x02, iCalibreParam};
    int iRet = write(i2cFd, (void *)cCmd0, 4);
    if(iRet < 0){
        CloseDev();
        LOGI("FPGA_DEV file write calibre mode failed!");
        return -1;
    }

    LOGI("FPGA_DEV file write calibre mode success!: %d byte data have been writen", iRet);

    CloseDev();
    return 0;
}
int Fpga3D::Play3DMode(){
    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    char cCmd0 [] = {0x50, 0x01, 0x01};
    if(write(i2cFd, (void *)cCmd0, 3) < 0){
        LOGE("write 3d play mode fail");
        CloseDev();
        return -1;
    }

    LOGI("write 3d player mode success");
    CloseDev();
    return 0;
}

int Fpga3D::Play2DMode() {
    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    char cCmd0 [] = {0x50, 0x01, 0x00};
    if(write(i2cFd, (void *)cCmd0, 3) < 0){
        LOGE("write 2d play mode fail");
        CloseDev();
        return -1;
    }

    LOGI("write 2d play mode success");
    CloseDev();
    return 0;
}

int Fpga3D::PlayDebugMode(short iStartOffset){
    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    char cCmd0 [] = {0x50, 0x0b, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x30};
    if(write(i2cFd, (void *)cCmd0, 10) < 0){
        CloseDev();
        return -1;
    }

    unsigned char cCmd1 [] = {0x50, 0x0b, 0x00, 0x00, 0x01, 0xff, 0x00, 0x00, 0x00, 0xff};
    if(write(i2cFd, (void *)cCmd1, 10) < 0){
        CloseDev();
        return -1;
    }

    char cCmd2 [] = {0x50, 0x0b, 0x00, 0x00, 0x01, 0x64, 0x00, 0x00, (char)(iStartOffset >> 8), (char)(iStartOffset & 0x00ff)};
    if(write(i2cFd, (void *)cCmd2, 10) < 0){
        CloseDev();
        return -1;
    }

    CloseDev();
    return 0;
}
int Fpga3D::PlayFlickMode() {
    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    char cCmd0 [] = {0x50, 0x01, 0x02, 0x07};
    if(write(i2cFd, (void *)cCmd0, 4) < 0) {
        CloseDev();
        return -1;
    }

    CloseDev();
    return 0;
}
uint Fpga3D::getOptCheckSum() {
    uint uiCheckSum = 0;
    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    char cCmd0 [] = {0x50, 0x11};
    if(write(i2cFd, (void *)cCmd0, 2) < 0) {
        CloseDev();
        return -1;
    }

    if(read(i2cFd, (void *)(&uiCheckSum), 4) < 0){
        CloseDev();
        return -1;
    }

    CloseDev();
    return 0;
}
uint Fpga3D::getPrgVer() {
    uint uiVer = 0;
    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    char cCmd0 [] = {0x50, 0x00};
    if(write(i2cFd, (void *)cCmd0, 2) < 0) {
        CloseDev();
        return -1;
    }

    if(read(i2cFd, (void *)(&uiVer), 4) < 0){
        CloseDev();
        return -1;
    }

    CloseDev();
    return 0;
}
int Fpga3D::RerversChanels(bool bRvs){
    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    char cCmdTure [] = {0x50, 0x14, 0x01};
    char cCmdFalse [] = {0x50, 0x14, 0x00};
    if(bRvs){
        if(write(i2cFd, (void *)cCmdTure, 3) < 0){
            CloseDev();
            return -1;
        }
    }else{
        if(write(i2cFd, (void *)cCmdFalse, 3) < 0){
            CloseDev();
            return -1;
        }
    }

    CloseDev();
    return 0;
}
int Fpga3D::setLtPxPosiCam(double dDistH, double dDistV){
    FdSpaceCoords fdDistH = (FdSpaceCoords)dDistH;
    FdSpaceCoords fdDistV = (FdSpaceCoords)dDistV;
    short sDistH = (short)fdDistH.slc<16>(0);
    short sDistV = (short)fdDistV.slc<16>(0);
    char cDistV0 = (char)((sDistV >> 8) & 0xff);//0xff
    char cDistV1 = (char)(sDistV & 0xff);//0x60
    char cDistH0 = (char)((sDistH >> 8) & 0xff);//0xf8
    char cDistH1 = (char)(sDistH & 0xff);//0x7b


    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    char cCmd0 [] = {0x50, 0x0b, 0x00, 0x00, 0x01, 0x54, cDistV0, cDistV1, cDistH0, cDistH1};
    LOGD("set Cam Posi size: %02X, %02X, %02X, %02X, %02X, %02X, %02X, %02X, %02X, %02X", cCmd0[0], cCmd0[1], cCmd0[2], cCmd0[3], cCmd0[4], cCmd0[5], cCmd0[6], cCmd0[7], cCmd0[8], cCmd0[9] );

    if(write(i2cFd, (void *)cCmd0, 10) < 0){
        CloseDev();
        return -1;
    }

    /*char rdata[11];
    rdata[0] = 0x50;
    rdata[1] = 0x0a;
    rdata[2] = 0x00;
    rdata[3] = 0x00;
    rdata[4] = 0x01;
    rdata[5] = 0x54;
    if(write(i2cFd, (void *)rdata, 6) < 0){
        CloseDev();
        LOGE("set cam posi read write failed");
        return -1;
    }if(read(i2cFd, (void *)rdata, 4) != 4){
        CloseDev();
        LOGE("cam posi data read failed");
        return -1;
    }
    LOGD("cam posi read data: d[0] = %02x, d[1] = %02x, d[2] = %02x, d[3] = %02x",
         rdata[0], rdata[1], rdata[2], rdata[3]);
    */
    CloseDev();
    return 0;
}
int Fpga3D::setDispPxSize(double dSize){
    FdPixelSize fdDist = (FdPixelSize)dSize;
    short sDist = fdDist.slc<16>(0);
    char cDist0 = (char)((sDist >> 8) & 0xff);//0xf8
    char cDist1 = sDist & 0xff;//0x7b

    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    //char cCmd0 [] = {0x50, 0x0b, 0x00, 0x00, 0x01, 0x50, 0x00, 0x00, 0x04, 0x03};
    char cCmd0 [] = {0x50, 0x0b, 0x00, 0x00, 0x01, 0x50, 0x00, 0x00, cDist0, cDist1};
    LOGD("set Disp Px size: %02X, %02X, %02X, %02X, %02X, %02X, %02X, %02X, %02X, %02X", cCmd0[0], cCmd0[1], cCmd0[2], cCmd0[3], cCmd0[4], cCmd0[5], cCmd0[6], cCmd0[7], cCmd0[8], cCmd0[9] );
    if(write(i2cFd, (void *)cCmd0, 10) < 0){
        CloseDev();
        return -1;
    }


    //CloseDev();
    //usleep(100);
    //i2cFd = OpenDev();
    //if(i2cFd < 0)
        //return -1;

    //char rdata[11];
    //rdata[0] = 0x50;
    //rdata[1] = 0x0a;
    //rdata[2] = 0x00;
    //rdata[3] = 0x00;
    //rdata[4] = 0x01;
    //rdata[5] = 0x50;
    //if(write(i2cFd, (void *)rdata, 6) < 0){
        //CloseDev();
        //LOGE("set pixel size read write failed");
        //return -1;
    //}if(read(i2cFd, (void *)rdata, 4) != 4){
        //CloseDev();
        //LOGE("pixel posi read failed");
        //return -1;
    //}
    //LOGD("pixel pitch read data: d[0] = %02x, d[1] = %02x, d[2] = %02x, d[3] = %02x",
         //rdata[0], rdata[1], rdata[2], rdata[3]);

    CloseDev();
    return 0;
}
int Fpga3D::burnProgram(char *pData, short iDataLength) {
    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    unsigned char wData[262];   //6 + 256
    unsigned char rData[256];
    int iOffset = 0;
    while(iOffset < iDataLength){
        int iWDataLen = iDataLength - iOffset > 256 ? 256 : iDataLength - iOffset;
        wData[0] = 0x50;
        wData[1] = 0x08;
        wData[2] = iOffset & 0xff;
        wData[3] = (iOffset >> 8) & 0xff;
        wData[4] = (iOffset >> 16) & 0xff;
        wData[5] = (iWDataLen - 1) & 0xff; //write len: 255->256
        memcpy(wData + 6, pData + iOffset, iWDataLen);
        if(write(i2cFd, wData, 6 + iWDataLen + 6)){
            LOGE("burn program failed!");
            CloseDev();
            return -1;
        }

        memset(rData, 0, 256);
        unsigned char cmd[] = {0x50, 0x08, wData[2], wData[3], wData[4], wData[5]};
        if(read(i2cFd, cmd, 6) != iWDataLen){
            LOGE("check Opt data failed!");
            CloseDev();
            return -1;
        }

        for(int i = 0; i < iWDataLen; i++){
            if(wData[i + 6] != rData[i]){
                LOGE("readback mismatch: expect: addr: %04x -> %02x, got %02x\n", iOffset + i, wData[6 + i], rData[i]);
                CloseDev();
                return -1;
            }
        }

        LOGI("\r %2.2f %% complete.", iOffset * 100.0f / iDataLength);
        iOffset += iWDataLen;
    }
    CloseDev();
    return 0;
}

int Fpga3D::burnOptData(char* pData, short iDataLength){
    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    LOGD("%f of length received!", (float)iDataLength);

    unsigned char wData[262];   //6 + 256
    unsigned char rData[256];
    int iOffset = 0;
    while(iOffset < iDataLength){
        int iWDataLen = iDataLength - iOffset > 256 ? 256 : iDataLength - iOffset;
        wData[0] = 0x50;
        wData[1] = 0x06;
        wData[2] = iOffset & 0xff;
        wData[3] = (iOffset >> 8) & 0xff;
        wData[4] = (iOffset >> 16) & 0xff;
        wData[5] = (iWDataLen - 1) & 0xff; //write len: 255->256
        memcpy(wData + 6, pData + iOffset, iWDataLen);
        //LOGD("wData[5] = %f", (float)wData[5]);
        if(write(i2cFd, wData, iWDataLen + 6) < 0){
            LOGE("burn Opt data failed!");
            CloseDev();
            return -1;
        }

        //memset(rData, 0, 256);
        //unsigned char cmd[] = {0x50, 0x05, wData[2], wData[3], wData[4], wData[5]};
        //if(write(i2cFd, cmd, 6) < 0){
            //LOGE("write check Opt data cmd failed!");
            //close(i2cFd);
            //return -1;
        //}
        //int iRecvPosi = 0;
        //while(iRecvPosi < iWDataLen){
            //usleep(1000);
            //int iRecvLen = iWDataLen - iRecvPosi;
            //iRecvLen = read(i2cFd, rData + iRecvPosi, iRecvLen);
            //if(iRecvLen < 0){
                //LOGE("check Opt data failed!");
                //close(i2cFd);
                //return -1;
            //}else{
                //iRecvPosi += iRecvLen;
            //}
        //}

        //for(int i = 0; i < iWDataLen; i++){
            //if(wData[i + 6] != rData[i]){
                //LOGE("readback mismatch: expect: addr: %04x -> %02x, got %02x\n", iOffset + i, wData[6 + i], rData[i]);
                //close(i2cFd);
                //return -1;
            //}
        //}
        iOffset += iWDataLen;
        LOGI("%2.2f %% complete.", iOffset * 100.0f / iDataLength);
    }

    char cCmd0 [] = {0x50, 0x01, 0x00};
    if(write(i2cFd, (void *)cCmd0, 3) < 0){
        CloseDev();
        return -1;
    }

    CloseDev();
    return 0;
}

int Fpga3D::setEyePosi(double dxl, double dyl, double dzl,
                       double dxr, double dyr, double dzr) {
    FdSpaceCoords fdParamX1 = (FdSpaceCoords)(dxl);
    FdSpaceCoords fdParamY1 = (FdSpaceCoords)(dyl);
    FdSpaceCoords fdParamZ1 = (FdSpaceCoords)(dzl);
    FdSpaceCoords fdParamX2 = (FdSpaceCoords)(dxr);
    FdSpaceCoords fdParamY2 = (FdSpaceCoords)(dyr);
    FdSpaceCoords fdParamZ2 = (FdSpaceCoords)(dzr);

    short sParamX1 = (short)(fdParamX1.slc<16>(0));
    short sParamY1 = (short)(fdParamY1.slc<16>(0));
    short sParamZ1 = (short)(fdParamZ1.slc<16>(0));

    short sParamX2 = (short)(fdParamX2.slc<16>(0));
    short sParamY2 = (short)(fdParamY2.slc<16>(0));
    short sParamZ2 = (short)(fdParamZ2.slc<16>(0));

    i2cFd = OpenDev();
    if(i2cFd < 0)
        return -1;

    char wdata[15];
    wdata[0] = 0x50;
    wdata[1] = 0x02;
    wdata[2] = 0x00;
    wdata[3] = (char)(sParamX1 & 0xff);//0x10;
    wdata[4] = (char)((sParamX1 >> 8) & 0xff);//0xfe;
    wdata[5] = (char)(sParamY1 & 0xff);//0xc4;
    wdata[6] = (char)((sParamY1 >> 8) & 0xff);//0xfb;
    wdata[7] = (char)(sParamZ1 & 0xff);//0x00;
    wdata[8] = (char)((sParamZ1 >> 8) & 0xff);//0x19;//

    wdata[9] = (char)(sParamX2 & 0xff);//0xf0;
    wdata[10] = (char)((sParamX2 >> 8) & 0xff);//0x01;
    wdata[11] = (char)(sParamY2 & 0xff);//0xc4;
    wdata[12] = (char)((sParamY2 >> 8) & 0xff);//0xfb;
    wdata[13] = (char)(sParamZ2 & 0xff);//0x00;
    wdata[14] = (char)((sParamZ2 >> 8) & 0xff);//0x19;

    LOGD("set eye posi cmd: d[0] = %02x, d[1] = %02x, d[2] = %02x", wdata[0], wdata[1], wdata[2]);
    LOGD("set left eye posi: d[3] = %02x, d[4] = %02x, d[5] = %02x, d[6] = %02x, d[7] = %02x, d[8] = %02x", wdata[3], wdata[4], wdata[5], wdata[6], wdata[7], wdata[8]);
    LOGD("set right eye posi: d[9] = %02x, d[10] = %02x, d[11] = %02x, d[12] = %02x, d[13] = %02x, d[14] = %02x", wdata[9], wdata[10], wdata[11], wdata[12], wdata[13], wdata[14]);

    if(write(i2cFd, (void *)wdata, 15) < 0){
        CloseDev();
        LOGE("set eye posi write failed");
        return -1;
    }

    /*char rdata[12];
    rdata[0] = 0x50;
    rdata[1] = 0x03;
    rdata[2] = 0x00;
    if(write(i2cFd, (void *)rdata, 3) < 0){
        CloseDev();
        LOGE("set eye posi read write failed");
        return -1;
    }if(read(i2cFd, (void *)rdata, 12) != 12){
        CloseDev();
        LOGE("eye posi read failed");
        return -1;
    }
    LOGD("read eye posi data: d[0] = %02x, d[1] = %02x, d[2] = %02x, d[3] = %02x, d[4] = %02x, d[5] = %02x, d[6] = %02x, d[7] = %02x, d[8] = %02x, d[9] = %02x, d[10] = %02x, d[11] = %02x",
         rdata[0], rdata[1], rdata[2], rdata[3], rdata[4], rdata[5], rdata[6], rdata[7], rdata[8], rdata[9], rdata[10], rdata[11]);
*/
    CloseDev();
    LOGD("set eye posi write success");

    return 0;
};

