package com.example.repetidora;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RepetidoraFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    Spinner spinnerAntNodo, spinnerAntCoord, spinnerAntRepNodo, spinnerAntRepCoord, spinnerZona, spinnerRepControl;
    ArrayAdapter<String> adapterAntNodo, adapterAntCoord, adapterAntRepNodo, adapterAntRepCoord, adapterZona, adapterRepControl;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repetidora_fragment, null);
        spinnerAntNodo = (Spinner) view.findViewById(R.id.spinnerAntNodo);
        spinnerAntCoord = (Spinner) view.findViewById(R.id.spinnerAntCoord);
        spinnerAntRepNodo = (Spinner) view.findViewById(R.id.spinnerAntRepNodo);
        spinnerAntRepCoord = (Spinner) view.findViewById(R.id.spinnerAntRepCoord);
        spinnerZona = (Spinner) view.findViewById(R.id.spinnerZona);
        spinnerRepControl = (Spinner) view.findViewById(R.id.spinnerRepControl);
        fillSpinners();
        return view;
    }


    public void fillSpinners(){
        String[] itemsAntNodo = new String[2];
        String[] itemsAntCoord = new String[2];
        String[] itemsAntRepNodo = new String[2];
        String[] itemsAntRepCoord = new String[2];
        String[] itemsZona = new String[5];
        String[] itemsRepControl = new String[2];

        itemsAntNodo[0] = "1";
        itemsAntNodo[1] = "2";
        itemsAntCoord[0] = "1";
        itemsAntCoord[1] = "2";
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

        adapterAntNodo = new ArrayAdapter<String>(getActivity(), R.layout.spinner, itemsAntNodo);
        spinnerAntNodo.setAdapter(adapterAntNodo);
        spinnerAntNodo.setOnItemSelectedListener(this);
        adapterAntCoord = new ArrayAdapter<String>(getActivity(), R.layout.spinner, itemsAntCoord);
        spinnerAntCoord.setAdapter(adapterAntCoord);
        spinnerAntCoord.setOnItemSelectedListener(this);
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

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
