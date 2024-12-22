package com.example.husbandrycloud.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.husbandrycloud.Config;
import com.example.husbandrycloud.R;
import com.example.husbandrycloud.util.OSSClientManager;
import com.example.husbandrycloud.util.OssCallback;


public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        HusbandryData husbandryData = (HusbandryData) getIntent().getSerializableExtra("husbandry_data");

        TextView indexTextView = findViewById(R.id.index_text_view);
        TextView ageTextView = findViewById(R.id.age_text_view);
        TextView weightTextView = findViewById(R.id.weight_text_view);
        TextView feedTypeTextView = findViewById(R.id.feed_type_text_view);
        TextView foodIntakeTextView = findViewById(R.id.food_intake_text_view);
        TextView excretionTextView = findViewById(R.id.excretion_text_view);
        TextView healthStatusTextView = findViewById(R.id.health_status_text_view);
        TextView heartbeatTextView = findViewById(R.id.heartbeat_text_view);
        TextView bloodPressureTextView = findViewById(R.id.blood_pressure_text_view);

        ImageView imageView = findViewById(R.id.image_view); // 引用 ImageView

        if (husbandryData != null) {
            indexTextView.setText("产品编号: " + husbandryData.getIndex());
            ageTextView.setText("年龄: " + husbandryData.getAge() + " 个月");
            weightTextView.setText("体重: " + husbandryData.getWeight() + " 千克");
            feedTypeTextView.setText("饲料类型: " + husbandryData.getFeedType());
            foodIntakeTextView.setText("进食量: " + husbandryData.getFoodIntake() + " 千克");
            excretionTextView.setText("排泄量: " + husbandryData.getExcretionRate() + " 千克");
            healthStatusTextView.setText("健康状态: " + husbandryData.getHealthStatus());
            if (Integer.parseInt(husbandryData.getHeartbeat()) > 0) {
                heartbeatTextView.setText("心跳: " + husbandryData.getHeartbeat() + " 次/分钟");
            }
            if (husbandryData.getBloodPressure() != null) {
                bloodPressureTextView.setText("血压: " + husbandryData.getBloodPressure() + " mmHg");
            }

            OSSClientManager.downloadFileFromOSS(this, Config.OSS_BUCKET_NAME, husbandryData.getUri(), new OssCallback() {
                @Override
                public void onOssUploadSuccess(String objectName) {

                }

                @Override
                public void onOssDownloadSuccess(Bitmap bitmap) {
                    Log.i("huang", "receive picture!");
                    Log.e("huang", bitmap.getHeight() + " " + bitmap.getWidth() + " " + "!!!!" + bitmap.getByteCount());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(getApplicationContext())
                                    .load(bitmap) // 或者用 URL
                                    .into(imageView);
                        }
                    });

                }
            });
        }
    }
}