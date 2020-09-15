package com.example.ktravel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PracticeActivity2 extends AppCompatActivity {
    TextView display;
    int pageNo = 1;
    int totalcnt;

    class ThreadEx extends Thread{
        String jsonString = null;

        StringBuilder sb = new StringBuilder();
        public void run(){
            try{
                URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=bcgZx%2BNS6vjxiskUoogaZOW7Q59DvyU12YsbSBTkj4mhWb8gXSKpehLFaJaB6%2BzsS%2FazsuuXyfJuj5rPph62UA%3D%3D&contentTypeId=&areaCode=&sigunguCode=&cat1=A01&cat2=A0101&cat3=A01011200&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=5&pageNo="+pageNo+"&_type=json");
                HttpURLConnection con =(HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setConnectTimeout(30000);


                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while(true){
                    String line = br.readLine();
                    if(line == null){
                        break;
                    }
                    sb.append(line + "\n");
                }
                br.close();
                con.disconnect();
                jsonString = sb.toString();
            }catch(Exception e){
                Log.e("다운로드 예외", e.getMessage());
            }
            List<Map<String, Object>> list = new ArrayList<>();
            try {
                //텍스트가 존재하는 경우만 수행
                if(jsonString != null && jsonString.trim().length() > 0) {
                    //첫번쨰는 JSON객체
                    JSONObject mainData = new JSONObject(jsonString);
                    //System.out.println(mainData);
                    //hoppin이라는 key의 값을 객체로 가져오기
                    JSONObject response = mainData.getJSONObject("response");
                    //System.out.println(hoppin);
                    JSONObject body = response.getJSONObject("body");

                    JSONObject items = body.getJSONObject("items");

                    // System.out.println(movies);
                    JSONArray item = items.getJSONArray("item");
                    //System.out.println(item);
                    for(int i=0; i<item.length(); i++) {
                        JSONObject imsi = item.getJSONObject(i);

                        //Map으로 생성
                        Map<String, Object> map = new HashMap<>(); //System.out.println(imsi);
                       try{

                        String title = imsi.getString("title");
                        String firstimage = imsi.getString("firstimage");

                        map.put("title", title);
                        map.put("firstimage", firstimage);
                       }catch(Exception e){
                           Log.e("firstimage null", e.getMessage());
                       }

                        //list에 추가
                        list.add(map);



                        }

                }else {
                    System.out.println("다운로드 받은 문자열이 없음");
                    //프로그램 조욜
                    System.exit(0);
                }
                //출력하기 위해서 핸들러를 호출
                Message message = new Message();
                message.obj = list;
                handler.sendMessage(message);

                }catch(Exception e){
                Log.e("파싱 예외", e.getMessage());
                e.printStackTrace();
            }
        }
    }
    Handler handler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message message){
            List<Map> result = (ArrayList<Map>)message.obj;
            String r = "";
           System.out.println(result);
            for(Map map : result){

                r += map + "\n";
            }
            display.setText(r);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice2);

        display = (TextView)findViewById(R.id.display);
    }
    @Override
    public void onResume(){
        super.onResume();
        new ThreadEx().start();
    }
}