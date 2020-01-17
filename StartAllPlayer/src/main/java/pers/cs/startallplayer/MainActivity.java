package pers.cs.startallplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startPlay(View view) {

        Intent intent = new Intent();

//        intent.setDataAndType(Uri.parse("http://192.168.31.75:8080/1111.mp4"),"video/*");
        intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2019/06/27/mp4/190627231412433967.mp4"),"video/*");
        startActivity(intent);

    }
}
