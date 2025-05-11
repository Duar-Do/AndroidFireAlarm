package com.example.flamealert;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    // Replace with your HC-05 MAC address
    private static final String DEVICE_ADDRESS = "HC-05";  // <- UPDATE THIS
    private static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Button connectBtn, disconnectBtn;
    private Switch alarmSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        connectBtn = findViewById(R.id.button);
        disconnectBtn = findViewById(R.id.button2);
        alarmSwitch = findViewById(R.id.switch1);

        connectBtn.setOnClickListener(view -> connectBluetooth());

        disconnectBtn.setOnClickListener(view -> disconnectBluetooth());

        alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sendCommand("F");  // F for Fire alarm ON
            } else {
                sendCommand("S");  // S for Stop alarm
            }
        });
    }

    private void connectBluetooth() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(DEVICE_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();

            Toast.makeText(this, "Connected to Bluetooth", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void disconnectBluetooth() {
        try {
            if (outputStream != null) outputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();
            Toast.makeText(this, "Bluetooth disconnected", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Disconnection failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sendCommand(String command) {
        try {
            if (outputStream != null) {
                outputStream.write(command.getBytes());
            } else {
                Toast.makeText(this, "Not connected to device", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Failed to send command", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
