package com.baiduofflinemanagedemo;

import android.support.annotation.NonNull;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;

/**
 * Created by ZL on 2018/4/23.<BR/>
 * 城市类
 */

public class BaiduOfflineMapModule implements Comparable<BaiduOfflineMapModule> {
    /**
     * 下载信息
     */
    private volatile MKOLUpdateElement mDownInfo;
    /**
     * 城市基本信息<BR/>
     * cityType城市类型:0:全国；1：省份；2：城市,如果是省份，可以通过childCities得到子城市列表
     */
    private volatile MKOLSearchRecord mCityInfo;//这里仅代表单个城市，不代表省份

    /**
     * 获取当前城市下载进度
     *
     * @return
     */
    public int getProgress() {
        if (mDownInfo != null) {
            return mDownInfo.ratio;
        }
        return 0;
    }

    /**
     * 获取当前城市下载状态<P/>
     * DOWNLOADING
     * 正在下载<BR/>
     * eOLDSFormatError
     * 数据错误，需重新下载<BR/>
     * eOLDSInstalling
     * 离线包正在导入中<BR/>
     * eOLDSIOError
     * 读写异常<BR/>
     * eOLDSMd5Error
     * 校验失败<BR/>
     * eOLDSNetError
     * 网络异常<BR/>
     * eOLDSWifiError
     * wifi网络异常<BR/>
     * FINISHED
     * 完成<BR/>
     * SUSPENDED
     * 已暂停<BR/>
     * UNDEFINED
     * 未定义<BR/>
     * WAITING
     * 等待下载<BR/>
     *
     * @return
     */
    public int getStatus() {
        if (mDownInfo != null) {
            return mDownInfo.status;
        }
        return MKOLUpdateElement.UNDEFINED;
    }

    public void setStatus(int status) {
        if (mDownInfo == null) {
            mDownInfo = new MKOLUpdateElement();
        }
        mDownInfo.status = status;
    }

    /**
     * 城市是否有更新
     *
     * @return
     */
    public boolean isHaveUpdate() {
        if (mDownInfo != null) {
            return mDownInfo.update;
        }
        return false;
    }

    /**
     * 获取当前城市的大小
     *
     * @return
     */
    public long getSize() {
        if (mCityInfo != null) {
            return mCityInfo.dataSize;
        }
        return 0;
    }

    /**
     * 设置当前城市名称
     *
     * @param name
     */
    public void setCityName(String name) {
        if (mCityInfo == null) {
            mCityInfo = new MKOLSearchRecord();
        }
        mCityInfo.cityName = name;
    }

    public String getCityName() {
        if (mCityInfo != null) {
            return mCityInfo.cityName;
        }
        return null;
    }

    public void setCityID(int ID) {
        if (mCityInfo == null) {
            mCityInfo = new MKOLSearchRecord();
        }
        mCityInfo.cityID = ID;
    }

    public int getCityID() {
        if (mCityInfo != null) {
            return mCityInfo.cityID;
        }
        return 0;
    }

    public MKOLSearchRecord getCityInfo() {
        return mCityInfo;
    }

    public void setCityInfo(MKOLSearchRecord mRecord) {
        this.mCityInfo = mRecord;
    }

    public MKOLUpdateElement getDownInfo() {
        return mDownInfo;
    }

    public void setDownInfo(MKOLUpdateElement mElement) {
        this.mDownInfo = mElement;
    }

    @Override
    public int compareTo(@NonNull BaiduOfflineMapModule o) {
        if (o.getStatus() == 111) {
            return 1;
        } else if (this.getStatus() == 111) {
            return -1;
        } else if (o.getStatus() == 222 && (this.getStatus() == 4 || this.getStatus() == 10)) {
            return 1;
        } else {
            if ((this.getStatus() != 4 && this.getStatus() != 10) && (o.getStatus() == 4 || o.getStatus() == 10)) {
                return -1;
            } else if ((this.getStatus() == 4 || this.getStatus() == 10) && (o.getStatus() == 4 || o.getStatus() == 10)) {
                return 0;
            } else if ((this.getStatus() == 4 || this.getStatus() == 10) && (o.getStatus() != 4 && o.getStatus() != 10)) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
