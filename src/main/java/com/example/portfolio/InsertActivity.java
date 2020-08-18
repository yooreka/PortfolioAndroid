package com.example.portfolio;

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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

public class InsertActivity extends AppCompatActivity {
 EditText shopnameinput, addressinput, roadaddressinput, businesshourinput, mobileinput;
 Button btninsert;




    class InsertThread extends Thread{
        public void run () {
            try {
                //upload 할 주소 만들기
                URL url = new URL("http://192.168.0.45:8080/portfolio/insert");
                //서버에게 넘겨줄 문자열 파라미터 생성
              /*  String[] data = {shopnameinput.getText().toString().trim(),
                        addressinput.getText().toString().trim(),
                        roadaddressinput.getText().toString().trim(),
                        businesshourinput.getText().toString().trim(),
                        mobileinput.getText().toString().trim()};


                String[] dataName = {"shopname", "address", "roadaddress", "businesshour", "mobile"};
                //파라미터 전송에 필요한 변수를 생성
                String lineEnd = "\r\n";
                //파일 업로드를 할 때는 boundary 값이 있어야 함
                //랜덤하게 생성하는 것을 권장
                //String boundary  ="androidinsert";
                String boundary = UUID.randomUUID().toString();
               */
                Log.e("shopname", shopnameinput.getText().toString());
                Log.e("address", addressinput.getText().toString());
                Log.e("roadaddress", roadaddressinput.getText().toString());
                Log.e("mobile", mobileinput.getText().toString());
                Log.e("businesshour", businesshourinput.getText().toString());
                //업로드 옵션을 설정
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(30000);
                con.setUseCaches(false);
                con.setDoInput(true);
                con.setDoOutput(true);

                String data = URLEncoder.encode("shopname", "UTF-8") + "=" +
                        URLEncoder.encode(shopnameinput.getText().toString().trim(), "UTF-8");
                data += "&" + URLEncoder.encode("address", "UTF-8") + "=" +
                        URLEncoder.encode(addressinput.getText().toString().trim(), "UTF-8");
                data += "&" + URLEncoder.encode("roadaddress", "UTF-8") + "=" +
                        URLEncoder.encode(roadaddressinput.getText().toString().trim(), "UTF-8");
                data += "&" + URLEncoder.encode("mobile", "UTF-8") + "=" +
                        URLEncoder.encode(mobileinput.getText().toString().trim(), "UTF-8");
                data += "&" + URLEncoder.encode("businesshour", "UTF-8") + "=" +
                        URLEncoder.encode(businesshourinput.getText().toString().trim(), "UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(data);
                wr.flush();
                //파일 업로드 옵션 설정
                //con.setRequestProperty("ENCTYPE", "multipart/form-data");
                //con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                //문자열 파라미터를 전송
               /* String delimiter = "--" + boundary + lineEnd;
                StringBuffer postDataBuilder = new StringBuffer();

                for (int i = 0; i < data.length; i = i + 1) {
                    postDataBuilder.append(delimiter);
                    postDataBuilder.append("Content-Disposition: form-data; name=\"" +
                            dataName[i] + "\"" + lineEnd + lineEnd + data[i] + lineEnd);
                }

                 */
                //파라미터 전송
               // DataOutputStream ds = new DataOutputStream(con.getOutputStream());
                //ds.write(postDataBuilder.toString().getBytes());

                //업로드할 파일이 있는 경우에만 작성
              /*  String fileName = "ball.png";
                if(fileName != null){
                    postDataBuilder.append(delimiter);
                    postDataBuilder.append("Content-Disposition:form-data;name=\""+
                            "pictureurl" + "\";filename=\"" + fileName + "\"")+
                            lineEnd);
                }*/

              /*  //파일 업로드
                if(fileName != null){
                    ds.writeBytes(lineEnd);
                    //파일 읽어오기 - id에 해당하는 파일을 raw 디렉토리에 복사
                    InputStream fres = getResources().openRawResource(R.raw.ball);
                    byte [] buffer = new byte[fres.available()];
                    int length = -1;
                    //파일의 내용을 ㅇ릭어서 읽은 내용이 있으면 그 내용을 ds에 기록
                    while((length = fres.read(buffer))!=-1){
                        ds.write(buffer, 0, length);
                    }
                    ds.writeBytes(lineEnd);
                    ds.writeBytes(lineEnd);
                    ds.writeBytes("--"+boundary+"--"+lineEnd);
                    fres.close();
                }else{
                    ds.writeBytes(lineEnd);
                    ds.writeBytes("=="+boundary+"--"+lineEnd);
                }
                */
                /*   ds.writeBytes(lineEnd);
                    ds.writeBytes("=="+boundary+"--"+lineEnd);

                ds.flush();
                ds.close();
                 */
                //서버로 부터의 응답 가져오기
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
               while(true){ String line = br.readLine();
                if(line == null){
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
            inserHandler.sendMessage(message);

        }catch(Exception e){
                Log.e("업로드 예외", e.getMessage());
                e.printStackTrace();
            }

        }
    }

    Handler inserHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {

            Log.e("msg", "dd");
            boolean result = (Boolean) message.obj;
            if (result == true) {
                Toast.makeText(InsertActivity.this, "삽입성공", Toast.LENGTH_SHORT).show();
                //키보드 내리기
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(shopnameinput.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(addressinput.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(roadaddressinput.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mobileinput.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(businesshourinput.getWindowToken(), 0);


            } else {
                Toast.makeText(InsertActivity.this, "삽입실패", Toast.LENGTH_SHORT).show();
            }
        }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_insert);

            shopnameinput = (EditText) findViewById(R.id.shopnameinput);
            addressinput = (EditText) findViewById(R.id.addressinput);
            businesshourinput = (EditText) findViewById(R.id.businesshourinput);
            roadaddressinput = (EditText) findViewById(R.id.roadaddressinput);
            mobileinput = (EditText) findViewById(R.id.mobileinput);
            btninsert = (Button) findViewById(R.id.btninsert);


            btninsert.setOnClickListener(new Button.OnClickListener() {

                public void onClick(View view) {
                    //유효성 검사
                    if (shopnameinput.getText().toString().trim().length() < 1) {
                        Toast.makeText(InsertActivity.this, "이름은 필수 입력입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (addressinput.getText().toString().trim().length() < 1) {
                        Toast.makeText(InsertActivity.this, "주소는 필수 입력입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (roadaddressinput.getText().toString().trim().length() < 1) {
                        Toast.makeText(InsertActivity.this, "도로명 주소 필수 입력입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (businesshourinput.getText().toString().trim().length() < 1) {
                        Toast.makeText(InsertActivity.this, "영업시은 필수 입력입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (mobileinput.getText().toString().trim().length() < 1) {
                        Toast.makeText(InsertActivity.this, "전화번호는 필수 입력입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new InsertThread().start();
                }
            });
        }
    }