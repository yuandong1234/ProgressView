package com.yuong.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private ProgressView progressView;
    private Button btn;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        progressView = findViewById(R.id.progressView);
        btn = findViewById(R.id.button);
        editText = findViewById(R.id.editText);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString().trim();
                if(!TextUtils.isEmpty(text)){
                    float value = Float.parseFloat(text);
                    progressView.setPercentage(value);
                    editText.setText("");
                }

            }
        });
    }
}
