package com.example.husbandrycloud.util;

import android.graphics.Bitmap;

public interface OssCallback {
    void onOssUploadSuccess(String objectName);
    void onOssDownloadSuccess(Bitmap bitmap);
}
