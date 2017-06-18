package com.example.user.remotedroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

//Atividade para configurar o IP e a porta usada pelo VLC
public class SettingsActivity extends AppCompatActivity {

    EditText etIp;//IP
    EditText etPorta;//Port

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Inserindo os EditTexts
        etIp = (EditText)findViewById(R.id.etIP);
        etPorta = (EditText)findViewById(R.id.etPorta);


    }

    public void Conectar(View v){
        Constants.setServerIp(etIp.getText().toString());
        Constants.setServerPort(etPorta.getText().toString());
    }
}
