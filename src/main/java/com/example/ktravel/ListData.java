package com.example.ktravel;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListData {
    public Drawable mImageView;
    public String mTextView1;
    public String mTextView2;

    class ThreadEx extends Thread{
        String jsonString = null;
        StringBuilder sb = new StringBuilder();

        public void run () {
            try {
                URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=bcgZx%2BNS6vjxiskUoogaZOW7Q59DvyU12YsbSBTkj4mhWb8gXSKpehLFaJaB6%2BzsS%2FazsuuXyfJuj5rPph62UA%3D%3D&contentTypeId=12&areaCode=&sigunguCode=&cat1=A01&cat2=A0101&cat3=A01011200&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo=1&_type=json");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setConnectTimeout(30000);
                con.setUseCaches(false);

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
                Log.e("데이터파싱 예외", e.getMessage());
            }
            StringBuilder sb2 = new StringBuilder();
            try{
                if(jsonString != null && jsonString.trim().length()>0){
                    JSONObject data = new JSONObject(jsonString);
                    JSONObject response = data.getJSONObject("response");
                    JSONObject body = response.getJSONObject("body");
                    JSONObject items = body.getJSONObject("items");
                    JSONArray item = items.getJSONArray("item");
                    for(int i=0; i<item.length(); i++){

                        JSONObject imsi = item.getJSONObject(i);
                        try {
                            String title = "Title :" + imsi.getString("title");
                            sb2.append(title + "\n");
                            String addr1 = "Addr1 :" +imsi.getString("addr1");
                            sb2.append(addr1 + "\n");


                        }catch(Exception e){
                            Log.e("파싱오류", e.getMessage());
                        }

                    }

                }else{
                    System.out.println("다운로드 받은 문자열 없음");
                    System.exit(0);
                }
                Message message = new Message();
                //message.obj = list;

                handler.sendMessage(message);
            }catch(Exception e){
                Log.e("저장 에러", e.getMessage());
                e.printStackTrace();
            }


        }
    }

    List<ImageView> imageViewList = new ArrayList<>();

    Handler handler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message message) {
            ArrayList<Map> result = (ArrayList<Map>) message.obj;
            String r = "";
            for (Map map : result) {
                String title = (String)map.get("title");

                r += title;
            }

        }
    };
}
