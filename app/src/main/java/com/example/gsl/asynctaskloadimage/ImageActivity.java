package com.example.gsl.asynctaskloadimage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    class MyAsyncTask extends AsyncTask<String,Integer,Bitmap>{

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            progressText.setText("Type:"+contentType+"  已加载："+values[0].toString()+"%");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = null;
            URLConnection connection;
            InputStream in;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try {
                connection = new URL(url).openConnection();
                int totalLength = connection.getContentLength();
                contentType = connection.getContentType();
                int progressLength = 0;
                int perLength = -1;
                byte[] bytes = new byte[1024];
                in = connection.getInputStream();
                while ((perLength = in.read(bytes)) != -1){
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
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
            imageView.setImageBitmap(bitmap);
        }
    }
}
