package com.example.repetidora;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RepetidoraFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    Spinner spinnerAntRepNodo, spinnerAntRepCoord, spinnerZona, spinnerRepControl;
    ArrayAdapter<String> adapterAntRepNodo, adapterAntRepCoord, adapterZona, adapterRepControl;
    EditText etNodeID, etNetID, etPotencia1, etPotencia2;
    String S0, S20, S12, S10, S11, S13, stringETnodeID, stringETnetID, stringPotencia1, stringPotencia2 = "";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repetidora_fragment, null);

        spinnerAntRepNodo = (Spinner) view.findViewById(R.id.spinnerAntRepNodo);
        spinnerAntRepCoord = (Spinner) view.findViewById(R.id.spinnerAntRepCoord);
        spinnerZona = (Spinner) view.findViewById(R.id.spinnerZona);
        spinnerRepControl = (Spinner) view.findViewById(R.id.spinnerRepControl);
        etNodeID = (EditText) view.findViewById(R.id.etNodeID);
        etNodeID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(etNodeID.getText().toString())){
                    print("etnodeid vacio");
                }else{
                    stringETnodeID = etNodeID.getText().toString();
                    print("etnodeid : " + stringETnodeID );

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etNetID = (EditText) view.findViewById(R.id.etNetID);
        etNetID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(etNetID.getText().toString())){
                    print("etnetid vacio");
                }else{
                    stringETnetID = etNetID.getText().toString();
                    print("etnetid : " + stringETnetID );

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPotencia1 = (EditText) view.findViewById(R.id.etPotencia1);
        etPotencia1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(etPotencia1.getText().toString())){
                    print("potencia1 vacio");
                }else{
                    stringPotencia1 = etPotencia1.getText().toString();
                    print("Potencia 1: " + stringPotencia1 );

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPotencia2 = (EditText) view.findViewById(R.id.etPotencia2);
        etPotencia2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(etPotencia2.getText().toString())){
                    print("potencia2 vacio");
                }else{
                    stringPotencia2 = etPotencia2.getText().toString();
                    print("Potencia 2: " + stringPotencia2 );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fillSpinners();
        return view;
    }


    public void fillSpinners(){
        String[] itemsAntRepNodo = new String[2];
        String[] itemsAntRepCoord = new String[2];
        String[] itemsZona = new String[5];
        String[] itemsRepControl = new String[2];

        itemsAntRepNodo[0] = "1";
        itemsAntRepNodo[1] = "2";
        itemsAntRepCoord[0] = "1";
        itemsAntRepCoord[1] = "2";
        itemsZona[0] = "0";
        itemsZona[1] = "1";
        itemsZona[2] = "2";
        itemsZona[3] = "3";
        itemsZona[4] = "4";
        itemsRepControl[0] = "0";
        itemsRepControl[1] = "1";


        adapterAntRepNodo = new ArrayAdapter<String>(getActivity(), R.layout.spinner, itemsAntRepNodo);
        spinnerAntRepNodo.setAdapter(adapterAntRepNodo);
        spinnerAntRepNodo.setOnItemSelectedListener(this);
        adapterAntRepCoord = new ArrayAdapter<String>(getActivity(), R.layout.spinner, itemsAntRepCoord);
        spinnerAntRepCoord.setAdapter(adapterAntRepCoord);
        spinnerAntRepCoord.setOnItemSelectedListener(this);
        adapterZona= new ArrayAdapter<String>(getActivity(), R.layout.spinner, itemsZona);
        spinnerZona.setAdapter(adapterZona);
        spinnerZona.setOnItemSelectedListener(this);
        adapterRepControl= new ArrayAdapter<String>(getActivity(), R.layout.spinner, itemsRepControl);
        spinnerRepControl.setAdapter(adapterRepControl);
        spinnerRepControl.setOnItemSelectedListener(this);
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ((TextView) adapterView.getChildAt(0)).setTextSize(30);//Tamaño del textview
        print("item seleccionado");
        switch (adapterView.getId()){
            case R.id.spinnerZona:
                print("Esto tiene S0: " + adapterView.getItemAtPosition(i).toString());
                S0 = adapterView.getItemAtPosition(i).toString();
                break;
            case R.id.spinnerRepControl:
                print("Esto tiene S20: " + adapterView.getItemAtPosition(i).toString());
                S20 = adapterView.getItemAtPosition(i).toString();
                break;
            case R.id.spinnerAntRepNodo:
                print("Esto tiene S11: " + adapterView.getItemAtPosition(i).toString());
                S11 = adapterView.getItemAtPosition(i).toString();
                break;
            case R.id.spinnerAntRepCoord:
                print("Esto tiene S13: " + adapterView.getItemAtPosition(i).toString());
                S13 = adapterView.getItemAtPosition(i).toString();
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void print(String message){
        System.out.println(message);
    }
}
