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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private TextView tvReceivedData;
    private EditText etStudentnumber;
    private Button btnClientConnect;
    private final String serverName = "se2-submission.aau.at";
    private final int serverPort = 20080;
    private Button btnCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvReceivedData = findViewById(R.id.tvRectxt);
        etStudentnumber = findViewById(R.id.etMatrikelnummer);
        btnClientConnect = findViewById(R.id.btnClientConnect);
        btnCalculate = findViewById(R.id.button2);


    }

    public void onClickConnect(View view) {
        String matriculationNumber = etStudentnumber.getText().toString();
        sendToServer(matriculationNumber);
    }
    public void onClickCalculate(View view) {
        String matriculationNumber = etStudentnumber.getText().toString();
        calculateAndDisplayResult(matriculationNumber);

    }
    private void calculateAndDisplayResult(String matriculationNumber) {
        try {

            int moduloResult = Integer.parseInt(matriculationNumber) % 7;

            // Überprüfen Sie, ob es Ziffern in der Matrikelnummer mit einem gemeinsamen Teiler > 1 gibt
            String result = findPairsWithCommonFactor(matriculationNumber);

            // Ergebnis ausgeben
            String output = "Modulo 7: " + moduloResult + "\n" + result;
            tvReceivedData.setText(output);

        } catch (NumberFormatException e) {
            tvReceivedData.setText("Ungültige Matrikelnummer");
        }
    }
    private String findPairsWithCommonFactor(String matriculationNumber) {
        StringBuilder result = new StringBuilder("Ziffern-Paare mit gemeinsamem Teiler > 1 (Indizes):\n");

        // Konvertieren Sie die Matrikelnummer in ein Array von Ziffern
        char[] digits = matriculationNumber.toCharArray();

        // Überprüfen Sie, ob es Ziffern mit einem gemeinsamen Teiler > 1 gibt
        for (int i = 0; i < digits.length - 1; i++) {
            for (int j = i + 1; j < digits.length; j++) {
                int digit1 = Character.getNumericValue(digits[i]);
                int digit2 = Character.getNumericValue(digits[j]);

                // Überprüfen, ob die Ziffern einen gemeinsamen Teiler > 1 haben
                if (hasCommonFactor(digit1, digit2)) {
                    result.append("Ziffern ").append(digit1).append(" und ").append(digit2)
                            .append(" (Indizes ").append(i).append(" und ").append(j).append(")\n");
                }
            }
        }

        return result.toString();
    }

    private boolean hasCommonFactor(int a, int b) {
        // Überprüfen, ob die Ziffern selbst oder 1 gemeinsame Teiler haben
        if (a == b || a == 1 || b == 1) {
            return false;
        }

        // Finden Sie den kleineren der beiden Werte
        int smaller;
        if (a < b) {
            smaller = a;
        } else {
            smaller = b;
        }

        // Überprüfen, ob es einen gemeinsamen Teiler größer als 1 gibt
        for (int i = 2; i <= smaller; i++) {
            if (a % i == 0 && b % i == 0) {
                return true;
            }
        }

        // Keinen gemeinsamen Teiler gefunden
        return false;
    }

    private void sendToServer(final String matriculationNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("se2-submission.aau.at", 20080);

                    // Send matriculation number to server
                    OutputStream outputStream = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(outputStream, true);
                    writer.println(matriculationNumber);

                    // Receive response from server
                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    final String response = reader.readLine();

                    // Close connections
                    writer.close();
                    reader.close();
                    socket.close();

                    // Update UI on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvReceivedData.setText(response);
                        }
                    });
                } catch (IOException e) {
                    final String errorMessage = "Error: " + e.getMessage();

                    // Update UI on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvReceivedData.setText(errorMessage);
                        }
                    });
                }
            }
        }).start();
    }
}