package com.example.husbandrycloud.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.husbandrycloud.Config;
import com.example.husbandrycloud.R;
import com.example.husbandrycloud.util.DatabaseTask;
import com.example.husbandrycloud.util.OSSClientManager;
import com.example.husbandrycloud.util.OssCallback;
import com.example.husbandrycloud.util.SharedPreferencesManager;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UploadAnimalDataFragment extends Fragment {

    private EditText productIdEditText, ageEditText, weightEditText, feedTypeEditText, foodIntakeEditText, excretionEditText, healthStatusEditText, heartbeatEditText, bloodPressureEditText;
    private Uri imageUri;
    private OnDataUploadedListener listener;
    private OnBackPressedListener backPressedListener;

    private static final int PICK_IMAGE_REQUEST = 1;

    public interface OnDataUploadedListener {
        void onDataUploaded(HusbandryData data);
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    public void setOnDataUploadedListener(OnDataUploadedListener listener) {
        this.listener = listener;
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.backPressedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_animal_data, container, false);

        Button backButton = view.findViewById(R.id.back_button);
        ageEditText = view.findViewById(R.id.age_edit_text);
        weightEditText = view.findViewById(R.id.weight_edit_text);
        feedTypeEditText = view.findViewById(R.id.feed_type_edit_text);
        foodIntakeEditText = view.findViewById(R.id.food_intake_edit_text);
        excretionEditText = view.findViewById(R.id.excretion_edit_text);
        healthStatusEditText = view.findViewById(R.id.health_status_edit_text);
        heartbeatEditText = view.findViewById(R.id.heartbeat_edit_text);
        bloodPressureEditText = view.findViewById(R.id.blood_pressure_edit_text);
        productIdEditText = view.findViewById(R.id.index_edit_index);

        Button uploadPhotoButton = view.findViewById(R.id.upload_photo_button);
        Button submitButton = view.findViewById(R.id.submit_button);

        backButton.setOnClickListener(v -> {
            if (backPressedListener != null) {
                backPressedListener.onBackPressed();
            }
        });

        uploadPhotoButton.setOnClickListener(v -> openGallery());
        submitButton.setOnClickListener(v -> submitData());

        return view;
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            //uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            String bucketName = Config.OSS_BUCKET_NAME;

            // 获取原文件名
            String originalFileName = getFileNameFromUri(imageUri);

            // 生成十位随机数
            Date now = new Date();

            // 创建 SimpleDateFormat 实例，格式为 mmddHHmmss
            SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");

            // 格式化当前时间
            String timestamp = sdf.format(now);

            String randomNum = SharedPreferencesManager.getUsername(getActivity()) + "/" + timestamp; // 生成十位随机数

            // 创建新的文件名
            String objectName = randomNum + "_" + originalFileName; // 新的文件名

            String filePath = getPathFromUri(imageUri); // 获取文件路径

            // 调用 OSSClientManager 上传文件
            OSSClientManager.uploadFile(getActivity(), bucketName, objectName, filePath, new OssCallback() {
                @Override
                public void onOssUploadSuccess(String objectName) {
                    // 获取输入数据并验证
                    String age = ageEditText.getText().toString();
                    String weight = weightEditText.getText().toString();
                    String feedType = feedTypeEditText.getText().toString();
                    String foodIntake = foodIntakeEditText.getText().toString();
                    String excretion = excretionEditText.getText().toString();
                    String healthStatus = healthStatusEditText.getText().toString();
                    String heartbeat = heartbeatEditText.getText().toString();
                    String bloodPressure = bloodPressureEditText.getText().toString();
                    String productId = productIdEditText.getText().toString();
                    HusbandryData data = new HusbandryData(productId, age, weight, feedType, foodIntake, excretion, healthStatus, objectName, heartbeat, bloodPressure, SharedPreferencesManager.getUsername(getActivity()));

                    DatabaseTask databaseTask = new DatabaseTask(new DatabaseTask.ResultListener() {
                        @Override
                        public void onqQueryResult(List<HusbandryData> result) {

                        }

                        @Override
                        public void onqInsertResult(List<HusbandryData> result) {

                        }

                        @Override
                        public void onqInsertFailed() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "产品编号重复,上传失败!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    databaseTask.insertData(data);

                    // 清空输入
                    clearInputs();
                }

                @Override
                public void onOssDownloadSuccess(Bitmap bitmap) {

                }
            });
            Toast.makeText(getContext(), "上传中...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "未选择任何图片", Toast.LENGTH_SHORT).show();
        }
    }

    private String getPathFromUri(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        try (Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path != null ? path : uri.getPath(); // 如果仍然为 null，返回 Uri 的 path
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
                cursor.close();
            }
        } else {
            // 对于文件 Uri，直接从 Uri 中获取文件名
            fileName = new File(uri.getPath()).getName();
        }
        return fileName;
    }

    private void submitData() {


        if (imageUri == null) {
            Toast.makeText(getContext(), "未选择任何图片", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImage();


        //Toast.makeText(getContext(), "数据提交成功", Toast.LENGTH_SHORT).show();
    }

    private void clearInputs() {
        productIdEditText.setText("");
        ageEditText.setText("");
        weightEditText.setText("");
        feedTypeEditText.setText("");
        foodIntakeEditText.setText("");
        excretionEditText.setText("");
        healthStatusEditText.setText("");
        heartbeatEditText.setText("");
        bloodPressureEditText.setText("");
    }
}