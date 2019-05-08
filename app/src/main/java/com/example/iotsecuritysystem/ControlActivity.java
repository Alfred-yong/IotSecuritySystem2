package com.example.iotsecuritysystem;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        EditText mEditText = (EditText)findViewById(R.id.editText);
        Button mButton = (Button)findViewById(R.id.button2);
        final TextView mTextView = (TextView)findViewById(R.id.vCode);
        mEditText.setEnabled(false);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        super.run();
                        Socket mSocket = null;
                        OutputStream os = null;
                        InputStream is = null;
                        InputStreamReader isr = null;
                        BufferedReader br = null;
                        StringBuffer mAccount = new StringBuffer();
                        try{
                            //1.创建监听指定服务器地址以及端口号
                            mSocket = new Socket("47.101.194.247",5000);
                            //2.拿到客户端的socket对象的输出流发送给服务器数据
                            os = mSocket.getOutputStream();
                            //3.将指令发送给服务器端
                            mAccount.append("_2");
                            os.write(mAccount.toString().getBytes());
                            os.flush();
                            mSocket.shutdownOutput();

                            //4.拿到Socket的输入流，即服务器返回的数据
                            is = mSocket.getInputStream();
                            isr = new InputStreamReader(is);
                            br = new BufferedReader(isr);
                            String mString = null;
                            String s = null;
                            while((mString = br.readLine()) != null){
                                s = mString;
                                Looper.prepare();
                                Toast.makeText(ControlActivity.this,"生成的随机数为："+s+",有效期为3分钟",Toast.LENGTH_LONG).show();
                                mTextView.setText(s);
                                Looper.loop();
                            }
                            mSocket.shutdownInput();

                        }catch(UnknownHostException e){
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }finally {
                            //关闭IO资源
                            try {
                                br.close();
                                isr.close();
                                is.close();
                                os.close();
                                mSocket.close();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            }.start();



            }
        });
    }
}
