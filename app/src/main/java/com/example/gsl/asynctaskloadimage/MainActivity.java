package com.example.gsl.asynctaskloadimage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    Toast toast;
    ConnectivityManager connectivityManager = null;
    Button imageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        imageBtn = (Button) findViewById(R.id.imageBtn);

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasNetWorkConnection()){
                    startActivity(new Intent(MainActivity.this,ImageActivity.class));
                }else {
                    if (toast == null){
                        toast = Toast.makeText(MainActivity.this,"请检查网络连接",Toast.LENGTH_SHORT);
                    }else {
                        //do nothing
                    }
                    toast.show();
                }
            }
        });
    }

    /**
     * 是否有网络活动连接
     * @return true 有网络连接，false没有
     */
    private boolean hasNetWorkConnection(){
        NetworkInfo netWorkInfo = connectivityManager.getActiveNetworkInfo();
        return (netWorkInfo != null && netWorkInfo.isAvailable());
    }

}
