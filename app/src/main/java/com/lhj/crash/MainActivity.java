package com.lhj.crash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.main);
        TextView carsh = (TextView) findViewById(R.id.carsh);
        boolean b = FileUtils.checkFilePathExists(FileUtils.SDPATH);
        StringBuffer buffer = new StringBuffer();
        buffer.append("是否会生成错误日志："+(CrashConfig.HAVE_LOG+""))
                .append("\n\n")
                .append("当前编译模式：")
                .append(BuildConfig.DEBUG ? "debug模式" : "release模式")
                .append("\n\n")
                .append("存放错误日志文件夹是否存在：" + b)
                .append("\n\n")
                .append("存放错误日志文件夹物理路径：")
                .append("\n\n")
                .append(FileUtils.file.getAbsolutePath());
        tv.setText(buffer);
        carsh.setText("点我崩溃");
        carsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CrashConfig.HAVE_LOG){
                    Log.e("mjb", 5 % 0 + "");
//                    throw new RuntimeException("这里是异常");
                }else{
                    Toast.makeText(MainActivity.this,"无错误日志",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
