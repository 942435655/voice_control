package com.example.administrator.tab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2016/7/24.
 * 作用是：
 */


public class WebDetect extends Fragment {


    private Handler m_Handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 5:
                    myImageView.setImageBitmap(bitmap);
                    m_RefreshImage.run();
                    break;
            }
        }
    };
    public Bitmap bitmap;
    private ImageView myImageView;
    private Runnable m_RefreshImage;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web_detect, container, false);

        myImageView = (ImageView) view.findViewById(R.id.image);

        m_RefreshImage = new Runnable() {
            public void run() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bitmap = getPicture("http://192.168.80.255:8080/?action=snapshot");

                        try {
                            Thread.sleep(200);//线程休眠两秒钟
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                     //   Log.e("WebDetect", ">>>>>>");

                        //发送一个Runnable对象
                        m_Handler.sendEmptyMessage(5);
                    }
                }).start();
            }

        };
        m_RefreshImage.run();


        return view;

    }

    public Bitmap getPicture(String path) {
        Bitmap bm = null;
        URL url;

       // Log.e("WebDetect", "getPicture");

        try {
            url = new URL(path);//创建URL对象
            URLConnection conn = url.openConnection();//获取URL对象对应的连接
            conn.connect();//打开连接
            InputStream is = conn.getInputStream();//获取输入流对象
            bm = BitmapFactory.decodeStream(is);//根据输入流对象创建Bitmap对象
        } catch (MalformedURLException e1) {
            e1.printStackTrace();//输出异常信息
        } catch (IOException e) {
            e.printStackTrace();//输出异常信息
        }


        return bm;
    }


}