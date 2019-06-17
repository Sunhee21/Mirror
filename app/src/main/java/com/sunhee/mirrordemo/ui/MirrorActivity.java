package com.sunhee.mirrordemo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.sunhee.hugo_annotations.MirrorLog;
import com.sunhee.mirrordemo.R;

public class MirrorActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror);
        b();
    }

    @MirrorLog
    private void b() {
    }
}
