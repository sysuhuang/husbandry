package com.example.husbandrycloud.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.CannedAccessControlList;
import com.alibaba.sdk.android.oss.model.CreateBucketRequest;
import com.alibaba.sdk.android.oss.model.CreateBucketResult;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteObjectResult;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.ClientException;
import com.example.husbandrycloud.Config;

import java.io.IOException;
import java.io.InputStream;

public class OSSClientManager {
    private static OSSClient ossClient;

    // 私有构造函数，防止外部实例化
    private OSSClientManager() {
    }

    public static OSSClient getInstance(Context context) {
        if (ossClient == null) {
            synchronized (OSSClientManager.class) {
                if (ossClient == null) {
                    // 创建 OSSClient 实例
                    OSSCredentialProvider credentialProvider =
                            new OSSPlainTextAKSKCredentialProvider(Config.OSS_ACCESS_KEY_ID, Config.OSS_ACCESS_KEY_SECRET);
                    String endpoint = Config.OSS_ENDPOINT;
                    ossClient = new OSSClient(context.getApplicationContext(), endpoint, credentialProvider);
                }
            }
        }
        return ossClient;
    }


    // 上传文件的方法
    public static void uploadFile(Context context, String bucketName, String objectName, String filePath, OssCallback callback) {
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucketName, objectName, filePath);

        long threadId2 = Thread.currentThread().getId();
        Log.d("ThreadInfo", "class uploadFile ID: " + threadId2);
        // 设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                //Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });



        // 异步上传
        OSSAsyncTask task = getInstance(context).asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                callback.onOssUploadSuccess(objectName);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                Toast.makeText(context.getApplicationContext(), "照片上传失败，请重新上传", Toast.LENGTH_SHORT).show();
                if (clientException != null) {
                    clientException.printStackTrace();
                }
                if (serviceException != null) {
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

    }


    public static void downloadFileFromOSS(Context context, String bucketName, String objectPath, OssCallback callback) {
        // 构造下载文件请求
        GetObjectRequest get = new GetObjectRequest(bucketName, objectPath);

        // 设置下载进度回调
        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                Log.d("OSS", "getobj_progress: " + currentSize + "  total_size: " + totalSize);
            }
        });

        // 发起异步请求
        OSSAsyncTask task = getInstance(context).asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                // 直接将输入流转换为 Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                callback.onOssDownloadSuccess(bitmap);
                try {
                    inputStream.close(); // 确保关闭输入流
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException, ServiceException serviceException) {
                // 请求异常
                if (clientException != null) {
                    // 本地异常如网络异常等
                    clientException.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    public static void deleteFileFromOSS(Context context, String bucketName, String objectPath) {
        DeleteObjectRequest delete = new DeleteObjectRequest(bucketName, objectPath);

        OSSAsyncTask deleteTask = getInstance(context).asyncDeleteObject(delete, new OSSCompletedCallback<DeleteObjectRequest, DeleteObjectResult>() {
            @Override
            public void onSuccess(DeleteObjectRequest request, DeleteObjectResult result) {
                Log.d("asyncDeleteObject", "success!");
            }

            @Override
            public void onFailure(DeleteObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常。
                if (clientExcepion != null) {
                    // 客户端异常，例如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务端异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }


}