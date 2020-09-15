package com.example.ktravel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CampingActivity extends AppCompatActivity {
    ListView listView;
    boolean lastItemVisibleFlag = false;
    int pageNo = 1;
    ArrayList<Map<String, Object>> data;
    CustomAdapter adapter;

    class ThreadEx extends Thread{
        String jsonString = null;
        StringBuilder sb = new StringBuilder();
        public void run(){
            try{
                URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey=bcgZx%2BNS6vjxiskUoogaZOW7Q59DvyU12YsbSBTkj4mhWb8gXSKpehLFaJaB6%2BzsS%2FazsuuXyfJuj5rPph62UA%3D%3D&contentTypeId=28&areaCode=&sigunguCode=&cat1=A03&cat2=A0302&cat3=A03021700&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=12&pageNo="+pageNo+"&_type=json");
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

            try {
                //텍스트가 존재하는 경우만 수행
                if(jsonString != null && jsonString.trim().length() > 0) {
                    //첫번쨰는 JSON객체
                    JSONObject mainData = new JSONObject(jsonString);
                    //System.out.println(mainData);
                    JSONObject response = mainData.getJSONObject("response");
                    JSONObject body = response.getJSONObject("body");
                    JSONObject items = body.getJSONObject("items");
                    JSONArray item = items.getJSONArray("item");
                    //System.out.println(item);
                    for(int i=0; i<item.length(); i++) {
                        JSONObject imsi = item.getJSONObject(i);

                        //Map으로 생성
                        Map<String, Object> map = new HashMap<>(); //System.out.println(imsi);
                        try{

                            String title = imsi.getString("title");
                            String addr1 = imsi.getString("addr1");
                            String firstimage = imsi.optString("firstimage");
                            int contentid =Integer.parseInt(imsi.getString("contentid"));
                            int contenttypeid = Integer.parseInt(imsi.getString("contenttypeid"));
                            double mapx = Double.parseDouble(imsi.getString("mapx"));
                            double mapy = Double.parseDouble(imsi.getString("mapy"));
                            map.put("title", title);
                            map.put("addr1", addr1);
                            map.put("firstimage", firstimage);
                            map.put("contentid", contentid);
                            map.put("contenttypeid", contenttypeid);
                            map.put("mapx", mapx);
                            map.put("mapy", mapy);

                        }catch(Exception e){
                            Log.e("firstimage null", e.getMessage());
                            e.getMessage();
                        }

                        //list에 추가
                        data.add(map);
                    }
                    // Log.e("이미지 데이터", data.toString());

                }else {
                    System.out.println("다운로드 받은 문자열이 없음");
                    //프로그램 조욜
                    System.exit(0);
                }
                //출력하기 위해서 핸들러를 호출
                Message message = new Message();
                message.obj = data;
                handler.sendMessage(message);

            }catch(Exception e){
                Log.e("파싱 예외", e.getMessage());
                e.printStackTrace();
            }
        }
    }
    Handler handler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message message){
            data = (ArrayList<Map<String, Object>>)message.obj;
            // Log.e("데이터", data.toString());
            adapter.notifyDataSetChanged();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_detail);
        data = new ArrayList<>();
        listView = findViewById(R.id.listView);
        adapter = new CustomAdapter(this, data);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Map<String, Object> map = data.get(i);
                    //Log.e("데이터2", map.toString() );
                    Intent intent = new Intent(CampingActivity.this, CampingDetailActivity.class);
                    intent.putExtra("contentid", (Integer)map.get("contentid"));
                    intent.putExtra("firstimage", (String)map.get("firstimage"));
                    intent.putExtra("title", (String)map.get("title"));
                    intent.putExtra("mapx", (Double)map.get("mapx"));
                    intent.putExtra("mapy", (Double)map.get("mapy"));
                    // Log.e("이미지", (String)map.get("firstimage"));
                    startActivity(intent);
                }catch (Exception e){
                    Log.e("데이터 전송 실패", e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        listView.setOnScrollListener(new ListView.OnScrollListener(){

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollstate) {
                if(scrollstate==ListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag == true) {
                    pageNo = pageNo + 1;
                    new ThreadEx().start();
                }else if(data.size()>65){
                    Toast.makeText(CampingActivity.this, "더 이상 데이터가 없습니다.", Toast.LENGTH_LONG).show();
                }
            }



            @Override
            public void onScroll(AbsListView absListView,  int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(totalItemCount > 0 &&
                        firstVisibleItem + visibleItemCount
                                >= totalItemCount){
                    lastItemVisibleFlag = true;
                }

            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();

        if(data == null || data.size() < 1){
            new ThreadEx().start();

        }
    }

}

