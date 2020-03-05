package com.example.repetidora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private static final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static BluetoothDevice device;
    public static BluetoothSocket socket;
    public static OutputStream outputStream;
    public static InputStream inputStream;
    boolean connected = false;
    static RepetidoraFragment repetidoraFragment;
    static boolean socketConectado;
    boolean stopThread;
    static String s;

    static String a;
    static String b;

    static Thread thread;
    static Handler handler = new Handler();
    static String tokens[];

    TextView tvConect;
    Button btnConnect, btnSolicitar, btnConfigurar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initItems();
        repetidoraFragment =  new RepetidoraFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, repetidoraFragment);
        transaction.commit();
    }
    public void initItems(){
        tvConect = (TextView) findViewById(R.id.tvConnect);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnSolicitar = (Button) findViewById(R.id.btnSolicitar);
        btnConfigurar = (Button) findViewById(R.id.btnConfigurar);

        btnConnect.setOnClickListener(this);
        btnSolicitar.setOnClickListener(this);
        btnConfigurar.setOnClickListener(this);


    }

    //Identifica el device BT
    public boolean BTinit()
    {
        boolean found = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) //Checks if the device supports bluetooth
        {
            Toast.makeText(getApplicationContext(), "Este dispositivo no soporta bluetooth", Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled()) //Checks if bluetooth is enabled. If not, the program will ask permission from the user to enable it
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter,0);
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Favor de conectar un dispositivo", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(BluetoothDevice iterator : bondedDevices)
            {

                //Suponiendo que solo haya un bondedDevice
                device = iterator;
                found = true;
                //Toast.makeText(getApplicationContext(), "Conectado a: " + device.getName(), Toast.LENGTH_SHORT).show();
            }
        }
        return found;
    }
    //Conexión al device BT
    public boolean BTconnect()
    {
        try
        {
            conectar();

        }
        catch(IOException e)
        {
            Toast.makeText(getApplicationContext(), "Conexión no exitosa", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            connected = false;
        }

        return connected;
    }

    public void conectar() throws IOException{
        socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Crea un socket para manejar la conexión
        socket.connect();
        socketConectado = true;
        Log.d("Socket ", String.valueOf(socket.isConnected()));
        Toast.makeText(getApplicationContext(), "Conexión exitosa", Toast.LENGTH_SHORT).show();
        connected = true;
        tvConect.setText("Conectado a " + device.getName());
        btnConnect.setText("Desconectar módulo Bluetooth");
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        beginListenForData();


    }

    public void desconectarBluetooth() throws IOException{
        //Desconectar bluetooth
        if(socketConectado){
            System.out.println("Socket Conectado");
            inputStream.close();
            outputStream.close();
            outputStream = null;
            inputStream = null;
            socket.close();
            socket = null;
        }
        resetFields();
        connected = false;
        tvConect.setText("");
        btnConnect.setText("Conectar a módulo Bluetooth");
        device = null;
        stopThread = true;
        socketConectado = false;
    }
    public void resetFields(){
        repetidoraFragment.etNodeID.setText("");
        repetidoraFragment.etNetID.setText("");
        repetidoraFragment.etPotencia1.setText("");
        repetidoraFragment.etPotencia2.setText("");
        //Falta resetear los spinners " "
    }


    void beginListenForData() {
        stopThread = false;
        thread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stopThread) {
                    try {
                        //waitMs(1000);
                        final int byteCount = inputStream.available();
                        if(byteCount > 0) {
                            byte[] packetBytes = new byte[byteCount];
                            inputStream.read(packetBytes);
                            s = new String(packetBytes);
                            System.out.println("Linea: " + s);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    readMessage(s);
                                }
                            });
                        }
                    }
                    catch (IOException ex) {
                        stopThread = true;
                    }
                }
                System.out.println("Stop thread es true");
            }
        });
        thread.start();
    }

    public static void readMessage(String frase)  {
        System.out.println("Estoy en el readMessage: ");
        System.out.println(frase);
        tokens = frase.split(",");
        System.out.println("Tokens: " + Arrays.toString(tokens));
        //Status
        if (frase.contains("s,") && tokens.length >= 8) {


        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnConnect:
                if (!connected) {
                    if (BTinit()) {

                        BTconnect();
                        /*
                        try{
                            askGPS();
                        }catch(IOException e){

                        }
                        */

                    }
                } else {
                    try {
                        desconectarBluetooth();
                    } catch (IOException ex) {
                    }
                }
                break;
        }
    }

}
