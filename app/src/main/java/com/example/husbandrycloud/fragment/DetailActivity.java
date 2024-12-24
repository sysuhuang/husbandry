package com.example.husbandrycloud.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.husbandrycloud.Config;
import com.example.husbandrycloud.R;
import com.example.husbandrycloud.util.DatabaseTask;
import com.example.husbandrycloud.util.OSSClientManager;
import com.example.husbandrycloud.util.OssCallback;
import com.example.husbandrycloud.util.SharedPreferencesManager;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // 创建并设置 TextView 作为标题
        TextView toolbarTitle = new TextView(this);
        toolbarTitle.setText("商品细节");
        toolbarTitle.setTextColor(getResources().getColor(android.R.color.white)); // 设置文本颜色
        toolbarTitle.setTextSize(20); // 设置文本大小

        // 设置 TextView 居中显示
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER; // 居中
        toolbarTitle.setLayoutParams(layoutParams);

        // 将 TextView 添加到 Toolbar
        toolbar.addView(toolbarTitle);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

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
        TextView enterpriseSuggestionTextView = findViewById(R.id.enterprise_suggestion_text_iew);

        ImageView imageView = findViewById(R.id.image_view); // 引用 ImageView

        if (husbandryData != null) {
            indexTextView.setText("产品编号: " + husbandryData.getIndex());
            ageTextView.setText("年龄: " + husbandryData.getAge() + " 个月");
            weightTextView.setText("体重: " + husbandryData.getWeight() + " 千克");
            feedTypeTextView.setText("饲料类型: " + husbandryData.getFeedType());
            foodIntakeTextView.setText("进食量: " + husbandryData.getFoodIntake() + " 千克");
            excretionTextView.setText("排泄量: " + husbandryData.getExcretionRate() + " 千克");
            healthStatusTextView.setText("健康状态: " + husbandryData.getHealthStatus());
            heartbeatTextView.setText("心跳: " + husbandryData.getHeartbeat() + " 次/分钟");
            bloodPressureTextView.setText("血压: " + husbandryData.getBloodPressure() + " mmHg");

            // 如果当前用户是 user 且 enterpriseAdvice 不为空
            if ("merchant".equals(SharedPreferencesManager.getUserRole(this)) && !husbandryData.getEnterpriseAdvice().isEmpty()) {
                enterpriseSuggestionTextView.setText("企业建议: "+husbandryData.getEnterpriseAdvice());
                enterpriseSuggestionTextView.setVisibility(View.VISIBLE);
            }

            // 如果当前用户是 enterprise
            if ("enterprise".equals(SharedPreferencesManager.getUserRole(this))) {
                enterpriseSuggestionTextView.setVisibility(View.GONE);
                Button inputSuggestionButton = new Button(this);
                inputSuggestionButton.setText("输入企业建议");
                inputSuggestionButton.setOnClickListener(v -> showInputSuggestionDialog(husbandryData));
                ((ViewGroup) bloodPressureTextView.getParent()).addView(inputSuggestionButton); // 将按钮添加到布局中
            }

            OSSClientManager.downloadFileFromOSS(this, Config.OSS_BUCKET_NAME, husbandryData.getUri(), new OssCallback() {
                @Override
                public void onOssUploadSuccess(String objectName) {
                    // 上传成功的处理逻辑
                }

                @Override
                public void onOssDownloadSuccess(Bitmap bitmap) {
                    Log.i("huang", "receive picture!");
                    runOnUiThread(() -> Glide.with(getApplicationContext())
                            .load(bitmap) // 或者用 URL
                            .into(imageView));
                }
            });
        }
    }

    private void showInputSuggestionDialog(HusbandryData husbandryData) {
        // 创建输入框
        EditText inputSuggestionEditText = new EditText(this);
        inputSuggestionEditText.setHint("输入企业建议...");

        // 显示输入框的对话框
        new AlertDialog.Builder(this)
                .setTitle("企业建议")
                .setView(inputSuggestionEditText)
                .setPositiveButton("确定", (dialog, which) -> {
                    String suggestion = inputSuggestionEditText.getText().toString();
                    // 处理输入的建议，例如保存或上传
                    DatabaseTask databaseTask = new DatabaseTask(new DatabaseTask.ResultListener() {
                        @Override
                        public void onqQueryResult(List<HusbandryData> result) {

                        }

                        @Override
                        public void onqInsertResult(List<HusbandryData> result) {

                        }

                        @Override
                        public void onqInsertFailed() {

                        }

                        @Override
                        public void onqDeleteResult(Boolean success) {

                        }
                    });
                    databaseTask.modifyEnterpriseAdvice(husbandryData.getIndex(),suggestion);
                })
                .setNegativeButton("取消", null)
                .show();
    }
}