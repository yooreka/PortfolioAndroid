package com.example.portfolio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomDetailActivity extends AppCompatActivity {
 TextView textView2, textView3, textView4, textView5 ;
 ImageView detailImg;
Intent intent;
Bitmap bitmap;

    class DetailThread extends Thread {
        String jsonString = null;
        StringBuilder sb = new StringBuilder();
        public void run(){
            try {
                intent = getIntent();
                int contentid = intent.getIntExtra("contentid", 0);
                URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey=bcgZx%2BNS6vjxiskUoogaZOW7Q59DvyU12YsbSBTkj4mhWb8gXSKpehLFaJaB6%2BzsS%2FazsuuXyfJuj5rPph62UA%3D%3D&contentTypeId=12&contentId="+contentid+"&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y&_type=json");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setConnectTimeout(20000);

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while(true){
                    String line = br.readLine();
                   if(line == null){
                      break;
                   }
                    sb.append(line + "\n");
                }
                con.disconnect();
                br.close();
                jsonString = sb.toString();

            }catch(Exception e)
            {
                Log.e("데이터 오지않음", e.getMessage());
                 e.printStackTrace();
            }
            try{
                ArrayList<Map<String, Object>>detailList = new ArrayList<>();
                if(jsonString != null && jsonString.trim().length()>0){
                    JSONObject detailData = new JSONObject(jsonString);
                    JSONObject response = detailData.getJSONObject("response");
                    JSONObject body = response.getJSONObject("body");
                    JSONObject items = body.getJSONObject("items");
                    JSONObject item = items.getJSONObject("item");

                   //Log.e("parsingdata", jsonString);
                    Map<String, Object> map = new HashMap<>();
                    intent = getIntent();
                    String title = intent.getStringExtra("title");

                    try {
                      int zipcode = Integer.parseInt(item.getString("zipcode"));
                      String overview = item.getString("overview");




                    map.put("zipcode", zipcode);
                    map.put("overview", overview);
                    map.put("title", title);
                  }catch(Exception e){
                      Log.e("항목 예외", e.getMessage());
                              e.printStackTrace();
                  }
                    detailList.add(map);
                  // Log.e("파싱결과", detailData.toString());
                }else {
                    System.out.println("다운로드 받은 문자열이 없음");
                    //프로그램 조욜
                    System.exit(0);
                }

                Message message = new Message();
                message.obj = detailList;
                DetailHandler.sendMessage(message);


            }catch(Exception e){
                Log.e("공통정보 파싱예외", e.getMessage());
                e.printStackTrace();

            }
        }
    }
    Handler DetailHandler = new Handler(Looper.getMainLooper()){
      public void handleMessage(Message message) {
          ArrayList<Map> result = (ArrayList<Map>) message.obj;
          Log.e("result data :", result.toString());
          String a = "";
          String c = "";
          String d = "";
          try {
              for (Map map : result) {
                  String overview = (String) map.get("overview");
                  String title = (String) map.get("title");
                  int zipcode = (Integer) (map.get("zipcode"));
                  c += overview;
                  a += zipcode;
                  d += title;
              }
              textView2.setText(String.valueOf(a));
              textView4.setText(c);
              textView5.setText(d);
          }catch (Exception e){
              Log.e("Map 추출 오류", e.getMessage());
              e.printStackTrace();
          }
      }
    };

    class ImageThread1 extends Thread{
        public void run(){
            intent = getIntent();
            String firstimage = intent.getStringExtra("firstimage");

            if(firstimage==null){
                detailImg.setImageResource(R.drawable.no_detail_img);
            }
            else if(firstimage!=null){
                try {
                    URL url = new URL(firstimage);
                    InputStream is = url.openStream();
                    bitmap = BitmapFactory.decodeStream(is);

                    Message message = new Message();
                    message.obj = bitmap;
                    imgHandler.sendMessage(message);
                } catch (Exception e) {
                    System.out.println("이미지 다운 실패" + e.getMessage());
                    e.printStackTrace();
                }
            }

        }
    }
    Handler imgHandler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message message){
            bitmap = (Bitmap)message.obj;
            detailImg.setImageBitmap(bitmap);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_detail);
        detailImg = findViewById(R.id.detailImg);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);

     new DetailThread().start();

      new ImageThread1().start();

    }
}