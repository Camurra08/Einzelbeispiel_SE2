package com.example.se2_einzelbeispiel_orhanmutlu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private TextView tvReceivedData;
    private EditText etStudentnumber;
    private Button btnClientConnect;
    private String serverName = "se2-submission.aau.at";
    private int serverPort = 20080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvReceivedData = findViewById(R.id.tvRectxt);
        etStudentnumber = findViewById(R.id.etMatrikelnummer);
        btnClientConnect = findViewById(R.id.btnClientConnect);


    }
    public void onClickConnect(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(serverName, serverPort);

                    BufferedReader br_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String txtFromServer = br_input.readLine();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvReceivedData.setText(txtFromServer);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }
}