package com.example.myapplication1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import android.widget.ListView;
import java.util.ArrayList;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.LayoutInflater;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Main_Window main_window = new Main_Window();
    private News_Window news_window = new News_Window();
    private List_Window list_window = new List_Window();
    private My_Window my_window = new My_Window();

    private BottomNavigationView bottomNavigationView;

    // db 불러오는 부분
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (!(connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isConnected())){
            new AlertDialog.Builder(this)
                    .setMessage("인터넷과 연결된 상태가 아닙니다.")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            finishAffinity();
                            System.exit(0);
                        }
                    }).show();
        }

        //메뉴
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        getSupportFragmentManager().beginTransaction().replace(R.id.layout_main_frame, main_window).commitAllowingStateLoss();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.main_menu: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.layout_main_frame, main_window).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.news_menu: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.layout_main_frame, news_window).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.list_menu: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.layout_main_frame, list_window).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.my_menu: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.layout_main_frame, my_window).commitAllowingStateLoss();
                        break;
                    }
                }
                return true;
            }
        });
    }
}