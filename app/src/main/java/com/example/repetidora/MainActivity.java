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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
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
    Handler h;
    Runnable r;
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
        btnConnect.setText("Conectar a Repetidora");
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
        System.out.println("Estoy en begin");
        stopThread = false;
        thread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stopThread) {
                    try {
                        //waitMs(1000);
                        final int byteCount = inputStream.available();
                        if(byteCount > 0) {
                            readBytesBufferedReader();
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

    //Lee lo que le manda el radio cuando recibe "ATI5\r"
    public void readBytesBufferedReader() throws IOException{
        print("Estoy en readbytes");
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        print("Esto tiene el br: " + br);
        for(int i = 0; i < 21; i++) {
            String line = br.readLine();
            list.add(line);
            System.out.println(line);
            // process line
        }

        System.out.println("Esto tiene la lista: " + list);
        System.out.println();

        updateValues(readValues(list));

    }
    //Función que recibe strings con todos los parámetros y retorna solo los valores
    public ArrayList<String> readValues(ArrayList<String> lista) {

        ArrayList<String> valores = new ArrayList<String>();
        System.out.println("Size: " + lista.size());
        System.out.println("Lista: " + lista);

        for (int i = 0; i < lista.size(); i++) {
            System.out.println("i: " + i);
            System.out.println(lista.get(i));
            System.out.println("Numero leído: " + lista.get(i).substring(lista.get(i).indexOf('=') + 1));
            valores.add(lista.get(i).substring(lista.get(i).indexOf('=') + 1, lista.get(i).length()));

        }
        System.out.println(valores);
        return valores;

    }

    //Actualiza los valores en los spinners
    public void updateValues(final ArrayList<String> arreglo) {

        System.out.println("Arreglo Size: " + arreglo.size());
        if (arreglo.size() > 20) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    repetidoraFragment.etNodeID.setText(arreglo.get(4));
                    repetidoraFragment.etNetID.setText(arreglo.get(3));
                    repetidoraFragment.etPotencia1.setText(arreglo.get(16));
                    repetidoraFragment.etPotencia2.setText(arreglo.get(17));
                    repetidoraFragment.spinnerZona.setSelection(Integer.valueOf(arreglo.get(2)));
                    repetidoraFragment.spinnerRepControl.setSelection(Integer.valueOf(arreglo.get(22)));
                    repetidoraFragment.spinnerAntNodo.setSelection(Integer.valueOf(arreglo.get(14)));
                    repetidoraFragment.spinnerAntCoord.setSelection(Integer.valueOf(arreglo.get(15)));
                    repetidoraFragment.spinnerAntRepNodo.setSelection(Integer.valueOf(arreglo.get(13)));
                    repetidoraFragment.spinnerAntRepCoord.setSelection(Integer.valueOf(arreglo.get(12)));
                }
            });

            System.out.println("Sí modifiqué los valores ! ");

        }else{
            System.out.println("Arreglo menor de 19");
        }
        //sendSave();
        //sendATZ();
    }

    void sendCommand(){
        try {
            System.out.println("Estoy en sendCommand");
            String msg = "+++";
            outputStream.write(msg.getBytes());
        } catch (IOException ex) {
        }
    }

    void sendATI5() throws IOException{
        r = new Runnable() {
            @Override
            public void run(){
                try {
                    System.out.println("Estoy en ATI5");
                    String msg = "ATI5\r";
                    outputStream.write(msg.getBytes());
                } catch (IOException ex) {
                }

            }
        };
        h  = new Handler();
        h.postDelayed(r, 1500);


    }

    void sendSave(){ //Falta checar
        try {
            System.out.println("Estoy en AT&W");
            String msg = "AT&W\r";
            outputStream.write(msg.getBytes());
        } catch (IOException ex) {
        }
    }

    void sendATZ(){
        try {
            System.out.println("Estoy en ATZ");
            String msg = "ATZ\r";
            outputStream.write(msg.getBytes());
        } catch (IOException ex) {
        }
    }

    public void write10(final String s10) throws IOException{
        Runnable r = new Runnable() {
            @Override
            public void run(){

                try {
                    System.out.println("Estoy en el writeS10");
                    String msg1 = "ATS10=" + s10 + "\r";
                    System.out.println(msg1);
                    outputStream.write(msg1.getBytes());

                } catch (IOException ex) {
                }

            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 10);
    }
    public void write11(final String s11) throws IOException{
        Runnable r = new Runnable() {
            @Override
            public void run(){

                try {
                    System.out.println("Estoy en el writeS11");
                    String msg1 = "ATS11=" + s11 + "\r";
                    System.out.println(msg1);
                    outputStream.write(msg1.getBytes());

                } catch (IOException ex) {
                }

            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 10);
    }
    public void write12(final String s12) throws IOException{
        Runnable r = new Runnable() {
            @Override
            public void run(){

                try {
                    System.out.println("Estoy en el writeS12");
                    String msg1 = "ATS12=" + s12 + "\r";
                    System.out.println(msg1);
                    outputStream.write(msg1.getBytes());

                } catch (IOException ex) {
                }

            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 10);
    }

    public void write13(final String s13) throws IOException{
        Runnable r = new Runnable() {
            @Override
            public void run(){

                try {
                    System.out.println("Estoy en el writeS13");
                    String msg1 = "ATS10=" + s13 + "\r";
                    System.out.println(msg1);
                    outputStream.write(msg1.getBytes());

                } catch (IOException ex) {
                }

            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 10);
    }
    public void write0(final String s0) throws IOException{
        Runnable r = new Runnable() {
            @Override
            public void run(){

                try {
                    System.out.println("Estoy en el writeS0");
                    String msg1 = "ATS10=" + s0 + "\r";
                    System.out.println(msg1);
                    outputStream.write(msg1.getBytes());

                } catch (IOException ex) {
                }

            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 10);
    }
    public void write20(final String s20) throws IOException{
        Runnable r = new Runnable() {
            @Override
            public void run(){

                try {
                    System.out.println("Estoy en el writeS20");
                    String msg1 = "ATS10=" + s20 + "\r";
                    System.out.println(msg1);
                    outputStream.write(msg1.getBytes());

                } catch (IOException ex) {
                }

            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 10);
    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnConnect:
                if (!connected) {
                    if (BTinit()) {
                        BTconnect();
                    }
                } else {
                    try {
                        desconectarBluetooth();
                    } catch (IOException ex) {
                    }
                }
                break;
            case R.id.btnSolicitar:
                if(connected){
                    sendCommand();
                    try {
                        sendATI5();
                    } catch (IOException ex) {
                    }

                }else{
                    showToast("Bluetooth desconectado");
                }
                break;

            case R.id.btnConfigurar:
                if(connected){
                    sendCommand();
                    try {
                        sendATI5();
                        write0(repetidoraFragment.S0);
                        write10(repetidoraFragment.S10);
                        write11(repetidoraFragment.S11);
                        write12(repetidoraFragment.S12);
                        write13(repetidoraFragment.S13);
                        write20(repetidoraFragment.S20);
                    } catch (IOException ex) {
                    }
                    sendSave();
                    sendATZ();
                }else{
                    showToast("Bluetooth desconectado");
                }
                break;
        }
    }
    public void print(String message){
        System.out.println(message);
    }

}
