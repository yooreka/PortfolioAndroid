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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MountainDetailActivity extends AppCompatActivity {
    TextView textView2,textView4, textView5 ;
    ImageView detailImg;
    Intent intent;
    Bitmap bitmap;
    Button mapbtn;
    MapView mapView;
    String b = "";
    String e = "";

    class DetailThread extends Thread {
        String jsonString = null;
        StringBuilder sb = new StringBuilder();
        public void run(){
            try {
                intent = getIntent();
                int contentid = intent.getIntExtra("contentid", 1);
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
                    Double mapx = intent.getDoubleExtra("mapx",0);
                    Double mapy = intent.getDoubleExtra("mapy",0);
                    map.put("mapx", mapx);
                    map.put("mapy", mapy);
                    try {

                        Object zipcode = item.getString("zipcode");
                        String overview = item.getString("overview");
                        map.put("zipcode", zipcode);
                        map.put("overview", overview);
                        map.put("title", title);
                    }catch(Exception e){
                        Log.e("항목 예외", e.getMessage());
                        e.printStackTrace();
                    }
                    detailList.add(map);
                     Log.e("파싱결과", detailData.toString());
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
            //Log.e("result data :", result.toString());
            String a = "";
            String c = "";
            String d = "";

            try {
                for (Map map : result) {
                    String overview = (String) map.get("overview");
                    String title = (String)map.get("title");
                    Object zipcode = map.get("zipcode");
                    double mapx = (Double)map.get("mapx");
                    double mapy = (Double)map.get("mapy");
                    String[] spt = overview.split("<br \\/>");
                    for(int i=0; i<spt.length; i++){
                        c += spt[i];
                    }

                    a += zipcode;
                    d += title;
                    b += mapx;
                    e += mapy;
                }
                textView2.setText("우편번호" + String.valueOf(a));
                textView4.setText("설명:" + c.substring(0,c.length()-4));
                textView5.setText(d);


            }catch (Exception e1){
                Log.e("Map 추출 오류", e1.getMessage());
                e1.printStackTrace();
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
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        textView2 = findViewById(R.id.textView2);
        mapbtn = findViewById(R.id.mapbtn);
        mapbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView = new MapView(MountainDetailActivity.this);
                ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
                mapViewContainer.addView(mapView);
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(e),Double.parseDouble(b)), true);
            }
        });

        new DetailThread().start();

        new ImageThread1().start();



    }
}