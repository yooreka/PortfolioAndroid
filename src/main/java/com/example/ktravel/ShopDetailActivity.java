package com.example.ktravel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ShopDetailActivity extends AppCompatActivity {
//화면에 사용할 뷰
    TextView lblshopname, lblshopphonenum, lbladdress, lblroadaddress, lblhour;
    Button backbtn;

    //텍스트 데이터를 웹에서 다운로드 받아서 출력
    //다운로드 > 파싱 > 출력

    //텍스트 데이터를 출력할 핸들러
    Handler textHandler = new Handler(Looper.getMainLooper()){
     @Override
     public void handleMessage(Message message) {
       //넘어온 데이터 찾아오기
       Map<String,Object> map = (Map<String, Object>)message.obj;
       lblshopname.setText((String)map.get("shopname"));
       lbladdress.setText((String)map.get("address"));
       lblhour.setText((String)map.get("businesshour"));
       lblroadaddress.setText((String)map.get("roadaddress"));
       lblshopphonenum.setText((String)map.get("mobile"));
       //이미지 파일명을 스레드 에게 넘겨서 출력
         //new ImageThread((String)map.get("pictureurl"
     }
     };
    //텍스트 데이터를 가져올 스레드 클래스
    class TextThread extends Thread{
        StringBuilder sb = new StringBuilder();
        @Override
        public void run(){
            try{
                //호추하는 인텐트 가져오기
                Intent intent = getIntent();
                //shopid의 값을 정수로 가져오고 없을 때 1
                int shopid = intent.getIntExtra("shopid", 1);
                URL url = new URL("http://192.168.0.45:8080/portfolio/detail?shopid=" + shopid);
                        //Connetion 객체 만들기
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                //옵션설정
                con.setUseCaches(false);
                con.setConnectTimeout(30000);

                //스트림 객체 생성
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                //문자열 읽기
                while(true){
                    String line = br.readLine();
                    if(line == null){
                        break;
                    }
                    sb.append(line + "\n");
                }

                br.close();
                con.disconnect();
            }catch(Exception e){
                //이 메시지가 보이 서버가 구동 중인지 확인하고 URL은 제대로 입력했는지 확인
                Log.e("다운로드 에러", e.getMessage());
            }
         Log.e("다운로드 받은 문자열", sb.toString());

            try{
                JSONObject object = new JSONObject(sb.toString());
                JSONObject shop = object.getJSONObject("shop");
                String shopname = shop.getString("shopname");
                String businesshour = shop.getString("businesshour");
                String mobile = shop.getString("mobile");
                String roadaddress = shop.getString("roadaddress");
                String address = shop.getString("address");

                //5개의 데이터 하나로 묶기
                Map<String, Object> map = new HashMap<>();
                map.put("shopname", shopname);
                map.put("businesshour", businesshour);
                map.put("mobile", mobile);
                map.put("roadaddress", roadaddress);
                map.put("address", address);

                //핸들러에게 데이터를 전송하고 호출
                Message message = new Message();
                message.obj = map;
                textHandler.sendMessage(message);
            }catch(Exception e){
                Log.e("파싱 에러", e.getMessage());
            }
        }
    }
    Handler imageHandler = new Handler(Looper.getMainLooper()){
    @Override
        public void handleMessage(Message message){
        //스레드가 전달해준 데이터를 이미지 뷰에 출력
        Bitmap bitmap = (Bitmap)message.obj;
          // imgpictureurl.setImageBitmap(bitmap);

    }

    };
     //이미지 다운로드를 위한 스레드
    class ImageThread extends Thread{
        //다운로드 받을 팔일명
         String pictureurl;
         public ImageThread(String pictureurl){
             this.pictureurl = pictureurl;
         }
         @Override
         public void run (){
           try{
               URL url = new URL("http://");
               HttpURLConnection con = (HttpURLConnection)url.openConnection();
               con.setUseCaches(false);
               con.setConnectTimeout(20000);

               //바로출력
               InputStream is = url.openStream();
               Bitmap bitmap = BitmapFactory.decodeStream(is);
               //Message저장
               Message message = new Message();
               message.obj = bitmap;
               imageHandler.sendMessage(message);
           }catch(Exception e){
               Log.e("이미지 다운 실패", e.getMessage());
           }
         }
     }

    @Override
    public void onResume(){
        super.onResume();
        new TextThread().start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        lblshopname = (TextView)findViewById(R.id.lblshopname);
        lblshopphonenum = (TextView)findViewById(R.id.lblshopphonenum);
        lblshopname = (TextView)findViewById(R.id.lblshopname);
        lbladdress = (TextView)findViewById(R.id.lbladdress);
        lblroadaddress = (TextView)findViewById(R.id.lblroadaddress);
        lblhour = (TextView)findViewById(R.id.lblhour);
        backbtn = (Button)findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                //현재 Activity 종료
                finish();
            }
        });

    }
}