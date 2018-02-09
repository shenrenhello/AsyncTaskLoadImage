package com.example.gsl.asynctaskloadimage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by dell on 2018/2/6.
 */

public class ImageActivity extends Activity{

    private ImageView imageView;
    private ProgressBar progressBar;
    private TextView progressText;
    private MyAsyncTask myAsyncTask;
    private String contentType = "unkown";

    private static String URLPATH = "http://pic3.zhongsou.com/image/38063b6d7defc892894.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);

        imageView = (ImageView) findViewById(R.id.imageview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressText = (TextView) findViewById(R.id.progressText);

        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute(URLPATH);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**for thread wait bug:
         click back menu while progressbar running and entry again,
         then you can see bug:progressbar doesn't running until the last thread end from background*/
        if (myAsyncTask != null && myAsyncTask.getStatus() == AsyncTask.Status.RUNNING){
            myAsyncTask.cancel(true);
        }
    }

    class MyAsyncTask extends AsyncTask<String,Integer,Bitmap>{

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("gsl","--------------onProgressUpdate");
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            progressText.setText("Type:"+contentType+"  已加载："+values[0].toString()+"%");
        }

        @Override
        protected void onPreExecute() {
            Log.d("gsl","--------------onPreExecute");
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
Log.d("gsl","--------------doInBackground");
            String url = params[0];
            Bitmap bitmap = null;
            URLConnection connection;
            InputStream in;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try {
                connection = new URL(url).openConnection();
                int totalLength = connection.getContentLength();
Log.d("gsl","--------------totalLength="+totalLength);
                contentType = connection.getContentType();
                int progressLength = 0;
                int perLength = -1;
                byte[] bytes = new byte[1024];
                in = connection.getInputStream();
                while ((perLength = in.read(bytes)) != -1){
Log.d("gsl","--------------while");
                    /**for thread wait bug*/
                    if (isCancelled()){
                        break;
                    }/**bug end*/
                    progressLength += perLength;
                    if (totalLength == 0) {
                        publishProgress(-1);
                    }else {
                        publishProgress((int)((float)progressLength / totalLength * 100));
                        outputStream.write(bytes,0,perLength);
                    }
                    //为更清楚看到图片加载的进度条，此处休眠短暂
                    Thread.sleep(50);
                }

                //BufferedInputStream bufIn = new BufferedInputStream(in);//采取缓冲区策略，可减少对磁盘的多次操作。
                //bitmap = BitmapFactory.decodeStream(bufIn);
                bitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(),0,outputStream.toByteArray().length);

                in.close();

                outputStream.close();
                //bufIn.close();
            } catch (IOException e) {
                Log.d("gsl","--------------IOException"+e);
                e.printStackTrace();
            } catch (InterruptedException e) {
                Log.d("gsl","--------------InterruptedException"+e);
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d("gsl","--------------onPostExecute");
            super.onPostExecute(bitmap);
            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
            imageView.setImageBitmap(bitmap);
        }
    }
}
