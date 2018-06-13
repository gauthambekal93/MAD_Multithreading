package com.example.gauth.mad_multithreading;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ExecutorService threadPool;
    public static Integer PROGRESS = 1;
    public static Integer STOP = 2;
    ImageView downloadedImg;
Handler handler;
ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadedImg = (ImageView) findViewById(R.id.imageView);
        threadPool = Executors.newFixedThreadPool(4);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setMax(100);

        handler=new Handler(new Handler.Callback(){
            @Override
    public boolean handleMessage(Message msg)
    {
       // progressBar.setProgress(10);//have to be worked!!
        //Log.i("Message","received");
        if(msg.what==100) {
            progressBar.setProgress(msg.what);
            Bitmap myBitmap = (Bitmap) msg.obj;
            Log.d("Message", msg.obj.toString());
            downloadedImg.setImageBitmap(myBitmap);
            progressBar.setProgress(0);
        }else if(msg.what<= 99){
            progressBar.setProgress(msg.what);
        }
     return true;
    }
});
    }

    //call the child thread
    public void downloadImage1(View view)
    {Log.i("button1","pressed");
        progressBar.setProgress(0);
        threadPool.execute(new downloadImage1());
    }
    public void downloadImage2(View view)
    {
        progressBar.setProgress(0);
        new  downloadImage2().execute(  "https://cdn.pixabay.com/photo/2017/12/31/06/16/boats-3051610_960_720.jpg");
    }


    //child thread begins here
    class downloadImage1 implements Runnable {
        String[] imageURL =new String[1];
        @Override
        public void run() {
            Log.i("Button ", "Pressed");
            imageURL[0] = "https://cdn.pixabay.com/photo/2017/12/31/06/16/boats-3051610_960_720.jpg";
            Bitmap myBitmap=  getImageBitmap(imageURL[0]);
            Message message=new Message();
            Log.d("Message", myBitmap.toString());
            for(int i=1;i<=99;i++)
            {
                Message message2=new Message();
            message2.what=i;
              //  message2.obj=i;
                handler.sendMessage(message2);
            }
            message.what=100;
            message.obj=myBitmap;  //Object is a super class will take anything
            handler.sendMessage(message);
            Log.i("Message","sent");
        }

        Bitmap getImageBitmap (String...strings){
            try {
                URL url = new URL(strings[0]);
                Log.i("URL IS",strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public class downloadImage2 extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
           for (int i=0;i<=99;i++) {
               publishProgress(i);
           }
               try {
                    URL url = new URL(strings[0]);
                Log.i("URL IS",strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();  not needed
        }

        @Override
        protected void onPostExecute(Bitmap myBitmap) {
            downloadedImg.setImageBitmap(myBitmap);
            progressBar.setProgress(100);
            progressBar.setProgress(0);
         //   super.onPostExecute(bitmap); not needed
        }
    }
}

