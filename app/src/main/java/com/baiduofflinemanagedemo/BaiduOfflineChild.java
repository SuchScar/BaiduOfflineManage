package com.baiduofflinemanagedemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.common.BaiduMapSDKException;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;

public class BaiduOfflineChild implements OnClickListener, OnLongClickListener {
    private Context mContext;

    private TextView mOffLineCityName;// 离线包名称

    private TextView mOffLineCitySize;// 离线包大小

    private ImageView mDownloadImage;// 下载相关Image

    private TextView mDownloadProgress;

    private TextView mGroupText;//分组命名

    private RelativeLayout mRelContent;//主内容框架

    private MKOfflineMap mOffline;
    private BaiduOfflineMapModule mMapItem;// 离线下载城市

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int completeCode = (Integer) msg.obj;
            switch (msg.what) {
                case MKOLUpdateElement.DOWNLOADING://下载中
                    displyaLoadingStatus();
                    break;
                case MKOLUpdateElement.FINISHED://下载完成
                    displaySuccessStatus();
                    break;
                case MKOLUpdateElement.SUSPENDED://下载暂停
                    displayPauseStatus(completeCode);
                    break;
                case MKOLUpdateElement.UNDEFINED://未定义（初始？）
                    displayDefault();
                    break;
                case MKOLUpdateElement.WAITING://等待中
                    displayWaitingStatus();
                    break;
                case MKOLUpdateElement.eOLDSFormatError:
                case MKOLUpdateElement.eOLDSIOError:
                case MKOLUpdateElement.eOLDSMd5Error:
                case MKOLUpdateElement.eOLDSNetError:
                case MKOLUpdateElement.eOLDSWifiError:
                    displayExceptionStatus();
                    break;
                case MKOLUpdateElement.eOLDSInstalling:
                    displaySuccessStatus();
                    break;
            }
        }

    };


    public BaiduOfflineChild(Context context, MKOfflineMap mkOfflineMap) {
        mContext = context;
        initView();
        mOffline = mkOfflineMap;
    }

    public String getCityName() {
        if (mMapItem != null) {
            return mMapItem.getCityName();
        }
        return null;
    }

    public View getOffLineChildView() {
        return mOffLineChildView;
    }

    private View mOffLineChildView;

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mOffLineChildView = inflater.inflate(R.layout.offlinemap_child, null);
        mOffLineCityName = mOffLineChildView.findViewById(R.id.name);
        mOffLineCitySize = mOffLineChildView
                .findViewById(R.id.name_size);
        mDownloadImage = mOffLineChildView
                .findViewById(R.id.download_status_image);
        mDownloadProgress = mOffLineChildView
                .findViewById(R.id.download_progress_status);
        mGroupText = mOffLineChildView
                .findViewById(R.id.group_text);
        mRelContent = mOffLineChildView
                .findViewById(R.id.rel_content);

        mOffLineChildView.setOnClickListener(this);
        mOffLineChildView.setOnLongClickListener(this);

    }

    public void setOffLineCity(BaiduOfflineMapModule mapCity) {
        if (mapCity != null) {
            mMapItem = mapCity;
            if (mapCity.getSize() != 0) {
                mGroupText.setVisibility(View.GONE);
                mRelContent.setVisibility(View.VISIBLE);
                mOffLineCityName.setText(mapCity.getCityName());
                double size = ((int) (mapCity.getSize() / 1024.0 / 1024.0 * 100)) / 100.0;
                mOffLineCitySize.setText(String.valueOf(size) + " M");

                notifyViewDisplay(mMapItem.getStatus(), mMapItem.getProgress());
            } else {
                mGroupText.setVisibility(View.VISIBLE);
                mRelContent.setVisibility(View.GONE);
                if (mapCity.getStatus() == 111) {
                    mGroupText.setText("正在下载");
                } else if (mapCity.getStatus() == 222) {
                    mGroupText.setText("已经下载");
                }
            }
        }
    }

    /**
     * 更新显示状态 在被点击和下载进度发生改变时会被调用
     *
     * @param status
     * @param completeCode
     */
    private void notifyViewDisplay(int status, int completeCode) {
        Message msg = new Message();
        msg.what = status;
        msg.obj = completeCode;
        handler.sendMessage(msg);

    }

    /**
     * 最原始的状态，未下载，显示下载按钮
     */
    private void displayDefault() {
        mDownloadProgress.setVisibility(View.INVISIBLE);
        mDownloadImage.setVisibility(View.VISIBLE);
        mDownloadImage.setImageResource(R.drawable.offlinearrow_download);
    }

    /**
     * 显示有更新
     */
    private void displayHasNewVersion() {
        mDownloadProgress.setVisibility(View.VISIBLE);
        mDownloadImage.setVisibility(View.VISIBLE);
        mDownloadImage.setImageResource(R.drawable.offlinearrow_download);
        mDownloadProgress.setText("已下载-有更新");
    }

    /**
     * 等待中
     *
     */
    private void displayWaitingStatus() {
        mDownloadProgress.setVisibility(View.VISIBLE);
        mDownloadImage.setVisibility(View.VISIBLE);
        mDownloadImage.setImageResource(R.drawable.offlinearrow_start);
        mDownloadProgress.setText("等待中");
    }

    /**
     * 下载出现异常
     *
     * @param
     */
    private void displayExceptionStatus() {
        mDownloadProgress.setVisibility(View.VISIBLE);
        mDownloadImage.setVisibility(View.VISIBLE);
        mDownloadImage.setImageResource(R.drawable.offlinearrow_start);
        mDownloadProgress.setText("下载出现异常");
    }

    /**
     * 暂停
     *
     * @param completeCode
     */
    private void displayPauseStatus(int completeCode) {
        if (mMapItem != null) {
            completeCode = mMapItem.getProgress();
        }
        mDownloadProgress.setVisibility(View.VISIBLE);
        mDownloadImage.setVisibility(View.VISIBLE);
        mDownloadImage.setImageResource(R.drawable.offlinearrow_start);
        mDownloadProgress.setText("暂停中:" + completeCode + "%");

    }

    /**
     * 下载成功
     */
    private void displaySuccessStatus() {
        if (mMapItem != null && mMapItem.isHaveUpdate()) {//这里检查更新
            displayHasNewVersion();
        } else {
            mDownloadProgress.setVisibility(View.GONE);
            mDownloadImage.setVisibility(View.VISIBLE);
            mDownloadImage.setImageResource(R.drawable.offlinearrow_done);
        }
    }

    /**
     * 下载中进度显示
     */
    private void displyaLoadingStatus() {
        if (mMapItem == null) {
            return;
        }
        mDownloadProgress.setVisibility(View.VISIBLE);
        mDownloadProgress.setText(mMapItem.getProgress() + "%");
        mDownloadImage.setVisibility(View.VISIBLE);
        mDownloadImage.setImageResource(R.drawable.offlinearrow_stop);
    }

    private synchronized void pauseDownload(int cityID) {
        mOffline.pause(cityID);
    }

    /**
     * 启动下载任务
     */
    private synchronized boolean startDownload() {
        try {
            return mOffline.start(mMapItem.getCityID());
        } catch (BaiduMapSDKException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void onClick(View view) {
        // 避免频繁点击事件，避免不断从夫开始下载和暂停下载
        mOffLineChildView.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mOffLineChildView.setEnabled(true);
            }
        }, 100);// 这个时间段刚刚好

        int completeCode, status;
        if (mMapItem != null) {
            status = mMapItem.getStatus();
            completeCode = mMapItem.getProgress();
            switch (status) {
                case MKOLUpdateElement.eOLDSInstalling:
                case MKOLUpdateElement.WAITING:
                    break;
                case MKOLUpdateElement.FINISHED:
                    if (mMapItem.isHaveUpdate()) {//有更新
                        if (startDownload()) {
                            displayWaitingStatus();
                            mMapItem.setStatus(MKOLUpdateElement.WAITING);
                            ((BaiduOfflineMapActivity) mContext).initCitData();
                        } else {
                            displayExceptionStatus();
                        }
                    }
                    break;
                case MKOLUpdateElement.DOWNLOADING:
                    // 在下载中的时候点击，表示要暂停
                    pauseDownload(mMapItem.getCityID());
                    displayPauseStatus(completeCode);
                    mMapItem.setStatus(MKOLUpdateElement.SUSPENDED);//手动设置状态暂停
                    ((BaiduOfflineMapActivity) mContext).initCitData();
                    break;
                case MKOLUpdateElement.eOLDSFormatError:
                case MKOLUpdateElement.eOLDSIOError:
                case MKOLUpdateElement.eOLDSMd5Error:
                case MKOLUpdateElement.eOLDSNetError:
                case MKOLUpdateElement.eOLDSWifiError:
                case MKOLUpdateElement.SUSPENDED:
                case MKOLUpdateElement.UNDEFINED:
                default:
                    if (startDownload()) {
                        displayWaitingStatus();
                        mMapItem.setStatus(MKOLUpdateElement.WAITING);
                        ((BaiduOfflineMapActivity) mContext).initCitData();
                    } else {
                        displayExceptionStatus();
                    }
                    // 在暂停中点击，表示要开始下载
                    // 在默认状态点击，表示开始下载
                    // 在等待中点击，表示要开始下载
                    // 要开始下载状态改为等待中，再回调中会自己修改
                    break;
            }

        }

    }

    /**
     * 长按弹出提示框 删除（取消）下载
     * 加入synchronized 避免在dialog还没有关闭的时候再次，请求弹出的bug
     */
    public synchronized void showDeleteDialog(final String name, final int cityID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(name);
        builder.setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mOffline == null) {
                    return;
                }
                switch (which) {
                    case 0:
                        mOffline.remove(cityID);
                        break;

                    default:
                        break;
                }
            }
        });
        builder.setNegativeButton("取消", null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    public boolean onLongClick(View arg0) {
        if (mMapItem.getStatus() == MKOLUpdateElement.FINISHED) {
            showDeleteDialog(mMapItem.getCityName(), mMapItem.getCityID());
        }
        return false;
    }

}
