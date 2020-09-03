package com.example.portfolio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    EditText emailinput, pwInput;
    Button btnlogin, btnlogout;
    ImageView profileImage;
    String profileUrl;
    String email, nickname, logindate, userpw;

    ImageView imgProfile;

    class ThreadEx extends Thread {
        //다운로드 받을 문자열을 저장할 변수
        String json;

        public void run() {
            try {
                URL url = new URL(
                        "http://192.168.0.45:8080/portfolio/login");
                HttpURLConnection con =
                        (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setReadTimeout(10000);
                con.setConnectTimeout(10000);
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setUseCaches(false);

                //파라미터 생성
                String data = URLEncoder.encode("email", "UTF-8") + "=" +
                        URLEncoder.encode(emailinput.getText().toString().trim(), "UTF-8");
                data += "&" + URLEncoder.encode("userpw", "UTF-8") + "=" +
                        URLEncoder.encode(pwInput.getText().toString().trim(), "UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(data);
                wr.flush();


                //결과 가져오기
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(
                                        con.getInputStream()));
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
                json = sb.toString();
                Log.e("다운로드 받은 문자열", json);


            } catch (Exception e) {
                Log.e("로그인예외", e.getMessage());
                Message message = new Message();
                message.obj = " 로그인처리 에러";
                message.what = 0;
                handler.sendMessage(message);
            }

                if (json != null) {
                    try {
                        JSONObject object = new JSONObject(json);
                       // Log.e("데이터", object.toString());
                        boolean result = object.getBoolean("result");
                        if (result == true) {
                            profileUrl = object.getString("profile");
                            email = object.getString("email");
                            nickname = object.getString("nickname");
                            logindate = object.getString("logindate");

                        }
                        try {
                            FileOutputStream fos = openFileOutput("login.txt",
                                    Context.MODE_PRIVATE);
                            String str = email + ":" + nickname + ":" + profileUrl;
                            fos.write(str.getBytes());
                            fos.close();
                        } catch (Exception e) { e.getMessage();
                        e.printStackTrace();
                        }
               /* Map<String, Object> map = new HashMap<>();
                JSONObject object = new JSONObject(json);

                map.put("result", (Boolean)object.getBoolean("result"));
                //로그인 성공한 경우에만 나머지 데이터를 읽어옵니다.
                if((Boolean)object.getBoolean("result") == true) {
                    map.put("nickname", (String) object.getString("nickname"));
                    map.put("profile", (String) object.getString("profile"));
                    map.put("email", (String) object.getString("email"));
                }

                Message message = new Message();
                message.obj = map;
                handler.sendMessage(message);
*/
                        Message message = new Message();
                        message.obj = result;
                        message.what = 1;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        Log.e("파싱 예외", e.getMessage());
                        e.printStackTrace();
                        Message message = new Message();
                        message.obj = "JSON 파싱 에러";
                        message.what = 0;
                        handler.sendMessage(message);
                    }
                }

            }
        }




        //메시지 출력을 위한 핸들러
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        String msg = (String) message.obj;
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        boolean result = (Boolean) message.obj;
                        if (result == true) {
                            Toast.makeText(LoginActivity.this, "로그인 성공",
                                    Toast.LENGTH_SHORT).show();
                            new ImageThread().start();
                            //키보드 관리 객체 가져오기
                            InputMethodManager imm = (InputMethodManager) getSystemService(
                                    INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(emailinput.getWindowToken(), 0);
                            imm.hideSoftInputFromWindow(pwInput.getWindowToken(), 0);
                            emailinput.setText("");
                            pwInput.setText("");
                        } else {
                            Toast.makeText(LoginActivity.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        Bitmap bit = (Bitmap) message.obj;
                        if (bit == null) {
                            Toast.makeText(LoginActivity.this, "bitmap is null",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            profileImage.setImageBitmap(bit);
                        }
                        break;
                }
            }
        };

        /*Map<String, Object> map =
                (Map<String, Object>)message.obj;
        Boolean result = (Boolean)map.get("result");
        if(result == true){
            Toast.makeText(LoginActivity.this,
                    "로그인 성공",
                    Toast.LENGTH_LONG).show();
            //회원정보를 파일에 저장
            String email = (String)map.get("email");
            String nickname = (String)map.get("nickname");
            String profile = (String)map.get("profile");
            //파일에 저장할 문자열 생성
            String str = email + ":" + nickname + ":"
                    + profile;
            try {
                FileOutputStream fos = openFileOutput(
                        "login.txt", Context.MODE_PRIVATE);
                fos.write(str.getBytes());
                fos.flush();
                fos.close();
                //로그아웃이나 로그인 실패했을 때는
                //delete("login.txt")를 호출
                //login.txt가 존재하면 로그인 된 상태이고
                //존재하지 않으면 로그인 이 안된 상태가 됩니다.
            }catch(Exception e){
                Log.e("파일 저장 예외", e.getMessage());
            }

            //이미지 다운로드 받는 스레드 생성
            new ImageThread(profile).start();

        }else{
            Toast.makeText(LoginActivity.this,
                    "로그인 실패",
                    Toast.LENGTH_LONG).show();
        }
    }
};
*/
        //이미지 파일을 다운로드 받는 스레드
        class ImageThread extends Thread{
            public void run() {
                try {
                    //파일 다운로드를 위한 스트림을 생성
                    InputStream is =
                            new URL(
                                    "http://192.168.0.45:8080/portfolio/profile/" +
                                            profileUrl).openStream();
                    //파일로 저장
                /*
                FileOutputStream fos = openFileOutput(
                        profile, Context.MODE_PRIVATE);
                while(true){
                    byte []  b = new byte[1024];
                    int length = is.read(b);
                    if(length <= 0){
                        break;
                    }
                    fos.write(b, 0, length);
                    fos.flush();
                }
                fos.close();
                is.close();
                */

                    Bitmap bit = BitmapFactory.decodeStream(is);
                    is.close();
                    Message message = new Message();
                    message.obj = bit;
                    message.what = 2;
                    imageHandler.sendMessage(message);

                } catch (Exception e) {
                    Log.e("이미지 다운로드 실패", e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        Handler imageHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                Bitmap bit = (Bitmap) message.obj;
                profileImage.setImageBitmap(bit);

            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            emailinput = (EditText)findViewById(R.id.emailinput);
            pwInput = (EditText)findViewById(R.id.pwinput);
            profileImage = (ImageView) findViewById(R.id.profileimage);
            btnlogin = (Button)findViewById(R.id.btnlogin);
            btnlogout = (Button)findViewById(R.id.btnlogout);

            btnlogin.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {
                    Message errorMessage = new Message();
                    String email = emailinput.getText().toString().trim();
                    String userpw = pwInput.getText().toString().trim();
                    if (email.length() == 0) {
                        errorMessage.obj = "email은 비어있을 수 없습니다.";
                        handler.sendMessage(errorMessage);
                        return;
                    } else {
                        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
                        Pattern p = Pattern.compile(regex);
                        Matcher m = p.matcher(email);
                        if (m.matches() == false) {
                            errorMessage.obj = "email 형식과 일치하지 않습니다.";
                            errorMessage.what = 0;
                            handler.sendMessage(errorMessage);
                            return;
                        }
                    }

                    if (userpw.length() == 0) {
                        errorMessage.obj = "비밀번호는 비어있을 수 없습니다.";
                        errorMessage.what = 0;
                        handler.sendMessage(errorMessage);
                        return;
                    } else {
                        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%&])" +
                                "[A-Za-z\\d!@#$%&]{8,}";
                        Pattern p = Pattern.compile(regex);
                        Matcher m = p.matcher(userpw);
                        if (m.matches() == false) {
                            errorMessage.obj = "비밀번호는 영문 대소문자 1개 이상 특수문자 1개 숫자 1개 이상으로 만들어져야 합니다.";
                            errorMessage.what = 0;
                            handler.sendMessage(errorMessage);
                            return;
                        }
                    }
                    Intent intent = new Intent(LoginActivity.this, LoginResultActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("userpw", userpw);
                    startActivity(intent);


                    new ThreadEx().start();
                }
            });
            btnlogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message message = new Message();
                    if (deleteFile("login.txt")) {
                        message.obj = "로그아웃에 성공했습니다.";

                    } else {
                        message.obj = "로그인을 한 적이 없습니다.";
                    }
                    message.what = 0;
                    handler.sendMessage(message);
                }
            });



        }

}