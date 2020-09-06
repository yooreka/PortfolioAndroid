package com.example.ktravel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserActivity extends AppCompatActivity {
    EditText emailinput, pwinput, nicknameinput;
    Button btnjoin, btnlogin, btnmain;

    class ThreadEx extends Thread {
        StringBuilder sb = new StringBuilder();

        @Override
        public void run() {
            //서버와 연동 작업
            try {
                URL url = new URL("http://192.168.0.45:8080/portfolio/join");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setUseCaches(false);
                con.setConnectTimeout(30000);
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestMethod("POST");
                //파일 전송을 위한 설정
                String boundary = UUID.randomUUID().toString();
                con.setRequestProperty("ENCTYPE", "multipart/form-data");
                con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
               //파라미터 만들기
                String lineEnd = "\r\n";
                String [] data = {
                        emailinput.getText().toString().trim(),
                        nicknameinput.getText().toString().trim(),
                        pwinput.getText().toString().trim()};
                String [] dataNames = {"email", "nickname", "userpw"};
                //파라미터 전송
                String delimiter = "--" + boundary + lineEnd;
                StringBuffer postDataBuilder = new StringBuffer();
                for(int i=0; i<data.length; i=i+1){
                    postDataBuilder.append(delimiter);
                    postDataBuilder.append("Content-Disposition: "
                            +"form-data; name=\"" +
                            dataNames[i] + "\"" + lineEnd + lineEnd +
                            data[i] + lineEnd);
                }
                //파일 파라미터 만들기
                //filename 설정하는 부분이 추가됩니다.
                String fileName = "ball.png";
                if(fileName != null){
                    postDataBuilder.append(delimiter);
                    postDataBuilder.append(
                            "Content-Disposition: "
                                    +"form-data; name=\"" +
                                    "profile" + "\";filename=\"" +
                                    fileName + "\"" + lineEnd);

                }

                //파라미터 전송
                DataOutputStream ds =
                        new DataOutputStream(con.getOutputStream());
                ds.write(postDataBuilder.toString().getBytes());

                //파일 전송
                if(fileName != null){
                    ds.writeBytes(lineEnd);
                    //파일의 내용을 읽을 수 있는 스트림 생성
                    InputStream fres =
                            getResources().openRawResource(R.raw.ball);
                    //읽은 내용을 저장할 바이트 배열
                    byte [] buffers = new byte[fres.available()];

                    int length = -1;
                    while((length = fres.read(buffers)) != -1){
                        //읽은 내용을 서버에게 전송
                        ds.write(buffers, 0, length);
                    }
                    ds.writeBytes(lineEnd);
                    ds.writeBytes(lineEnd);
                    ds.writeBytes("--" + boundary + "--" +  lineEnd);
                    fres.close();
                }else{
                    ds.writeBytes(lineEnd);
                    ds.writeBytes("--" + boundary + "--" +  lineEnd);
                }
                ds.flush();
                ds.close();

                //결과 가져오기
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(
                                        con.getInputStream()));
                while(true){
                    String line = br.readLine();
                    if(line == null){
                        break;
                    }
                    sb.append(line + "\n");
                }
                br.close();
                con.disconnect();
                Log.e("다운로드 받은 문자열", sb.toString());

            }catch(Exception e){
                Log.e("다운로드 예외", e.getMessage());
            }

            //데이터 파싱
            Map<String, Object>map = new HashMap<>();
            try{
                JSONObject object = new JSONObject(sb.toString());
                boolean result =
                        object.getBoolean("result");
                boolean emailcheck =
                        object.getBoolean("emailcheck");
                boolean nicknamecheck =
                        object.getBoolean("nicknamecheck");
                map.put("result", result);
                map.put("emailcheck", emailcheck);
                map.put("nicknamecheck", nicknamecheck);

            }catch(Exception e){
                Log.e("파싱 예외", e.getMessage());
            }

            Message message = new Message();
            message.obj = map;
            handler.sendMessage(message);
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message message){
            //서버와 연동한 결과를 가지고 다음 작업을 수행
            Map<String, Object> map =
                    (Map<String, Object>)message.obj;
            Log.e("데이터",map.toString());
            boolean result = (Boolean)map.get("result");
            if(result){
                Toast.makeText(UserActivity.this,
                        "회원가입 성공", Toast.LENGTH_LONG)
                        .show();
            }else{
                boolean emailcheck = (Boolean)map.get("emailcheck");
                if (emailcheck == false) {
                    Toast.makeText(UserActivity.this,
                            "이메일 중복", Toast.LENGTH_LONG)
                            .show();
                }else{
                    Toast.makeText(UserActivity.this,
                            "닉네임 중복", Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        emailinput = (EditText)findViewById(R.id.emailinput);
        pwinput = (EditText)findViewById(R.id.pwinput);
        nicknameinput = (EditText)findViewById(R.id.nicknameinput);

        btnjoin = (Button)findViewById(R.id.btnjoin);
        btnlogin = (Button)findViewById(R.id.btnlogin);
        btnmain = (Button)findViewById(R.id.btnmain);

        btnjoin.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                //데이터 유효성 검사
                boolean result = validationCheck();

                //서버와 연동
                new ThreadEx().start();
            }
        });
    }

    //유효성 검사
    private boolean validationCheck(){
        boolean result = false;
        //입력한 내용을 가져오기
        String email =
                emailinput.getText().toString().trim();
        String pw =
                pwinput.getText().toString().trim();
        String nickname =
                nicknameinput.getText().toString().trim();

        //필수 입력 체크와 정규식을 체크
        String msg = null;
        if(email.length() < 1){
            msg = "email은 필수 입력입니다.";
        }else{
            //정규식 객체 생성
            String regex =
                    "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(email);
            //정규식 패턴과 일치하지 않으면
            if(m.matches() == false){
                msg = "email 형식이 일치하지 않습니다.";
            }
        }
        if(pw.length() < 1){
            msg = "비밀번호는 필수 입력입니다.";
        }else{
            //정규식 객체 생성
            String regex =
                    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%&])" +
                            "[A-Za-z\\d!@#$%&]{8,}";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(pw);
            //정규식 패턴과 일치하지 않으면
            if(m.matches() == false){
                msg = "비밀번호는 영문 대소문자 1개 숫자 1개 특수문자 1개 " +
                        "이상의 조합으로 만들어져야 합니다.";
            }
        }
        if(nickname.length() < 2){
            msg = "별명은 2자 이상이어야 합니다.";
        }else{
            //정규식 객체 생성
            String regex =
                    "[0-9]|[a-z]|[A-Z]|[가-힣]";

            for(int i=0; i<nickname.length(); i=i+1){
                String ch = nickname.charAt(i) + "";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(ch);
                //정규식 패턴과 일치하지 않으면
                if(m.matches() == false){
                    msg = "별명은 숫자 그리고 영문자와 한글만 가능합니다.";
                    break;
                }
            }


        }
        if(msg == null){
            result = true;
        }else{
            Toast.makeText(
                    this, msg, Toast.LENGTH_LONG)
                    .show();
        }

        return result;
    }
}
