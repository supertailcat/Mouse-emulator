package com.example.pccontroler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private Object lock;
    private Boolean longClick = false;
    private String message;
    private Button btn_shutdown;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_wheel_up;
    private Button btn_wheel_down;
    private Button btn_up;
    private Button btn_down;
    private Button btn_left;
    private Button btn_right;
    private TextView tv_info;
    private Socket socket = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lock = new Object();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_shutdown = this.findViewById(R.id.btn_shutdown);
        btn_shutdown.setOnClickListener(MainActivity.this);
        btn_1 = this.findViewById(R.id.btn_1);
        btn_1.setOnClickListener(MainActivity.this);
        btn_2 = this.findViewById(R.id.btn_2);
        btn_2.setOnClickListener(MainActivity.this);
        btn_3 = this.findViewById(R.id.btn_3);
        btn_3.setOnClickListener(MainActivity.this);
        btn_wheel_up = this.findViewById(R.id.btn_wheel_up);
        btn_wheel_up.setOnClickListener(MainActivity.this);
        btn_wheel_down = this.findViewById(R.id.btn_wheel_down);
        btn_wheel_down.setOnClickListener(MainActivity.this);
        tv_info = this.findViewById(R.id.tv_info);

        btn_up = this.findViewById(R.id.btn_up);
        btn_up.setOnClickListener(MainActivity.this);
        btn_up.setOnLongClickListener(MainActivity.this);
        btn_down = this.findViewById(R.id.btn_down);
        btn_down.setOnClickListener(MainActivity.this);
        btn_down.setOnLongClickListener(MainActivity.this);
        btn_left = this.findViewById(R.id.btn_left);
        btn_left.setOnClickListener(MainActivity.this);
        btn_left.setOnLongClickListener(MainActivity.this);
        btn_right = this.findViewById(R.id.brn_right);
        btn_right.setOnClickListener(MainActivity.this);
        btn_right.setOnLongClickListener(MainActivity.this);
    }

    @Override
    protected void onStart() {
        tv_info.setText("等待连接...");
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (socket == null || socket.isClosed()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket("192.168.137.1", 8866);
                        tv_info.setText("连接成功");
                        SendThread sendThread = new SendThread(socket);
                        Thread thread = new Thread(sendThread);
                        thread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        synchronized (lock) {
            message = "close";
            lock.notify();
        }
        super.onPause();
    }


    @Override
    public void onClick(View view) {
        synchronized (lock) {
            if (longClick) {
                message = "stop";
                longClick = false;
            }
            else {
                switch (view.getId()) {
                    case R.id.btn_shutdown:
                        message = "shutdown";
                        break;
                    case R.id.btn_1:
                        message = "lift click";
                        break;
                    case R.id.btn_2:
                        message = "wheel click";
                        break;
                    case R.id.btn_3:
                        message = "right click";
                        break;
                    case R.id.btn_wheel_up:
                        message = "wheel up";
                        break;
                    case R.id.btn_wheel_down:
                        message = "wheel down";
                        break;
                    case R.id.btn_up:
                        message = "up";
                        break;
                    case R.id.btn_down:
                        message = "down";
                        break;
                    case R.id.btn_left:
                        message = "left";
                        break;
                    case R.id.brn_right:
                        message = "right";
                        break;
                }
            }
            lock.notify();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        longClick = true;
        synchronized (lock) {
            switch (view.getId()) {
                case R.id.btn_up:
                    message = "keepup";
                    break;
                case R.id.btn_down:
                    message = "keepdown";
                    break;
                case R.id.btn_left:
                    message = "keepleft";
                    break;
                case R.id.brn_right:
                    message = "keepright";
                    break;
            }
            lock.notify();
        }
        return false;
    }


    class SendThread implements Runnable {
        Socket socket;
        SendThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            OutputStream outputStream = null;
            try {
                outputStream = socket.getOutputStream();
                while(true) {
                    synchronized (lock) {
                        lock.wait();
                        outputStream.write(message.getBytes());
                        outputStream.flush();
                        if (message.equals("close")) {
                            socket.close();
                            return;
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}