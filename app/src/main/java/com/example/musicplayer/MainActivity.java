package com.example.musicplayer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    List musicnamelist = new ArrayList();
    List list;
    String str = "/data/user/0/com.example.musicplayer/files/";
    String mname;
    TextView songname,style;

    //权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public List<String> getFilesAllName(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            Log.e("error", "空目录");
            return null;
        }
        List<String> s = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            String str1 = files[i].getAbsolutePath();
            str1 = str1.substring(43);
            s.add(str1);
        }
        return s;
    }

    //设置下一首
    public void nextmusic() {
            String music;
            int position = list.indexOf(mname) + 1;
            //防越界处理
            if (position < list.size()) {
                music = list.get(position).toString();
            } else {
                music = list.get(position-list.size()).toString();
            }
            mname = music;
            play(music);
    }


    //设置上一首
    public void lastmusic() {
        String music;
        int position = list.indexOf(mname) - 1;
        //防越界处理
        if (position >= 0) {
            music = list.get(position).toString();
        } else {
            music = list.get(list.size()-1).toString();
        }
        mname = music;
        play(music);
    }

    //播放
    public void play(String strr) {
        /* MediaPlayer mediaPlayer = new MediaPlayer();*/
        try {
            int i = 0;
            i++;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(str + strr);
            mediaPlayer.prepare();
            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //循环播放
    public void xunhuan() {
        boolean loop = mediaPlayer.isLooping();
        mediaPlayer.setLooping(!loop);
    }

    //设置进度条
    SeekBar seekBar;
    Handler handler = new Handler();
    Runnable update = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(update,1000);
        }
    };
    //上下文菜单



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(MainActivity.this);

        musicnamelist = getFilesAllName(str);
        setContentView(R.layout.activity_main);
        list = new ArrayList(getFilesAllName(str));

        final ListView listView = findViewById(R.id.songmenu);
        registerForContextMenu(listView);
        Button buttonLast = findViewById(R.id.lastsong);
        Button buttonStart = findViewById(R.id.start);
        Button buttonNext = findViewById(R.id.nextsong);
        final Button buttonShunxu = findViewById(R.id.shunxu);
        Button buttonSuiji = findViewById(R.id.suiji);
        Button buttonXunHuan = findViewById(R.id.xunhuan);

        songname = findViewById(R.id.songname);
         style = findViewById(R.id.setStyle);

        seekBar = findViewById(R.id.seekbar);
        //listview中显示歌曲信息
        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        //listview中设置点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String music = list.get(position).toString();
                mname = list.get(position).toString();
                songname.setText(music);
                style.setText("单曲播放");
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    play(music);
                    handler.post(update);
                }
            }

        });
        //点击开始播放
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        });

        //设置下一首
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextmusic();
                songname.setText(mname);

            }
        });
        //设置上一首
        buttonLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastmusic();
                songname.setText(mname);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser=true&&mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //设置顺序播放
        buttonShunxu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style.setText("顺序播放");

            }
        });
        //设置循环播放
        buttonXunHuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style.setText("循环播放");
                xunhuan();
            }
        });
        //设置随机播放
        buttonSuiji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style.setText("随机播放");
                Double position = Math.random()*list.size();
                mname = list.get(position.intValue()).toString();
                songname.setText(mname);
                play(mname);
            }
        });
        //设置顺序播放
        buttonShunxu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style.setText("顺序播放");
                songname.setText(mname);
                nextmusic();
            }
        });
    }
}
