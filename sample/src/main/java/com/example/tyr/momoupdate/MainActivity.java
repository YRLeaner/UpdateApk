package com.example.tyr.momoupdate;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tyr.updatelibrary.update.UpdateManager;

public class MainActivity extends AppCompatActivity {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersion();
            }
        });

        checkVersion();

    }

    private void checkVersion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("升级提示");
        builder.setMessage("发现新版本，请及时更新");
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              /*  Intent intent = new Intent(MainActivity.this, UpdateService.class);
                intent.putExtra("apkUrl", "https://www.imooc.com/mobile/mukewang.apk");
                intent.putExtra("apkStorage","/updateService/Qj.apk");
                startService(intent);*/

                UpdateManager.getInstance().startUpdate(MainActivity.this,"https://www.imooc.com/mobile/mukewang.apk",
                        "/updateService/Qj.apk");
            }
        });
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        builder.create().show();
    }
}
