package com.example.portfolio;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class CustomAdapter extends BaseAdapter {


   Bitmap bitmap;
    Context context;
    List<Map<String, Object>> list;
    //생성자
    public CustomAdapter(Context context, List<Map<String, Object>>list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);

    }

    @Override
    public long getItemId(int i) {
        return i;
    }





    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final int pos = i;
        //view는 재사용하기 때문에 null일때만 만들면 됨
        if(view == null){

            LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_view_custom, viewGroup, false);
        }

        //이미지 출력
        final ImageView  img = (ImageView)view.findViewById(R.id.img);
        final String firstimage1 = (String)list.get(i).get("firstimage");

        final Handler  drawHandler = new Handler(Looper.getMainLooper()){
            public void handleMessage(Message message){
                bitmap = (Bitmap)message.obj;
                if(firstimage1==null){
                    img.setImageResource(R.drawable.no_detail_img);
                }else {
                    img.setImageBitmap(bitmap);
                }
            }
        };
        class ImgThread extends Thread{
            public void run(){
                try {
                    URL url = new URL(firstimage1);
                    InputStream is = url.openStream();
                    bitmap = BitmapFactory.decodeStream(is);

                    Message message = new Message();
                    message.obj = bitmap;
                    drawHandler.sendMessage(message);

                }catch(Exception e){

                }
            }
        }
        new ImgThread().start();




        //텍스트 뷰
        TextView text = (TextView)view.findViewById(R.id.text);
        String titleText = (String)list.get(i).get("title");
        text.setText(titleText);

        TextView addr1 = view.findViewById(R.id.text2);
        String addr1Text = (String)list.get(i).get("addr1");
        addr1.setText(addr1Text);

        return view;

    }


}