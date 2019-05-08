package com.example.iotsecuritysystem;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 注册组件
         */

        final EditText usernameET = (EditText)findViewById(R.id.usernameET);
        final EditText passwordET = (EditText)findViewById(R.id.passwordET);
        Button loginBT = (Button)findViewById(R.id.loginBT);
        passwordET.setInputType(129);   //密码输入不可见

        /**
         * 登录按钮实现，将账户密码发送至服务器数据库匹配，匹配成功则进入
         * 该用户的控制界面
         */
        loginBT.setOnClickListener(new View.OnClickListener() {
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
                            //3.将账户密码发送给服务器端
                            mAccount.append(usernameET.getText().toString());
                            mAccount.append(passwordET.getText().toString());
                            mAccount.append("_1");
                            os.write(mAccount.toString().getBytes());
                            os.flush();
                            mSocket.shutdownOutput();

                            //4.拿到Socket的输入流，即服务器返回的数据
                            is = mSocket.getInputStream();
                            isr = new InputStreamReader(is);
                            br = new BufferedReader(isr);
                            String mString = null;
                            String s = null;

                            //5.若服务器返回true，则允许进入该账户的控制页面
                            while((mString = br.readLine()) != null){
                                s = mString;
                                if(s.equals("true")){
                                    Intent mIntent = new Intent(MainActivity.this,ControlActivity.class);
                                    startActivity(mIntent);

                                }else
                                {
                                    Looper.prepare();
                                    Toast.makeText(MainActivity.this,"账号或密码不正确",Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }

                        }catch (UnknownHostException e){
                            Looper.prepare();
                            Toast.makeText(MainActivity.this,"请检查网络连接情况",Toast.LENGTH_SHORT).show();
                            Looper.loop();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
