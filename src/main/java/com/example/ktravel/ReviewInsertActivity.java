package com.example.ktravel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;


public class ReviewInsertActivity extends AppCompatActivity {
    EditText  textinput;
    Button insertbtn;

    class RinsertThread extends Thread {
        public void run() {
            try {
                URL url = new URL("http://192.168.0.45:8080/portfolio/srinsert");




                Log.e("shopmessage", textinput.getText().toString());
                //서버에게 넘겨줄 문자열 파라미터를 생성

                String[] data = {textinput.getText().toString().trim()};
                String[] dataName = {"shopmessage"};

                //파라미터 전송에 필요한 변수를 생성
                String lineEnd = "\r\n";
                //파일 업로드를 할 때는 boundary 값이 있어야 합니다.
                //랜덤하게 생성하는 것을 권장
                //String boundary = "androidinsert";
                String boundary = UUID.randomUUID().toString();
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(30000);
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);

                //문자열 파라미터를 전송
                String delimiter = "--" + boundary + lineEnd;
                StringBuffer postDataBuilder = new StringBuffer();

                for (int i = 0; i < data.length; i = i + 1) {
                    postDataBuilder.append(delimiter);
                    postDataBuilder.append(
                            "Content-Disposition: form-data; name=\"" +

                                    dataName[i] + "\"" + lineEnd + lineEnd + data[i] + lineEnd);

                }
                //파라미터 전송



                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line + "\n");

                }
                br.close();
                con.disconnect();
                //JSON 파싱
                JSONObject object = new JSONObject(sb.toString());
                boolean result = object.getBoolean("result");
                //핸들러에게 결과 전송
                Message message = new Message();
                message.obj = result;
                inserthandler.sendMessage(message);


            } catch (Exception e) {
                Log.e("업로드 실패", e.getMessage());
            }
        }
    }

    Handler inserthandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {

            boolean result = (Boolean) message.obj;
            if (result == true) {
                Toast.makeText(ReviewInsertActivity.this, "삽입성공", Toast.LENGTH_SHORT).show();
                //키보드 내리기
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);


                imm.hideSoftInputFromWindow(textinput.getWindowToken(), 0);



            } else {
                Toast.makeText(ReviewInsertActivity.this, "삽입실패", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_insert);



        textinput = (EditText)findViewById(R.id.textinput);

        insertbtn = (Button) findViewById(R.id.insertbtn);


        insertbtn.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {


                new RinsertThread().start();
            }
        });
    }
}