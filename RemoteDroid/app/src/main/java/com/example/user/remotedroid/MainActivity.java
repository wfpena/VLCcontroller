package com.example.user.remotedroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;

import butterknife.Bind;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Context context;
    ImageButton playPauseButton;
    ImageButton fsButton;
    TextView tv;
    TextView tvTempo;
    int file_length = 100;

    //@Bind(R.id.volBar)
    SeekBar volBar;
    SeekBar seekBar;

    //Button nextButton;
    //Button previousButton;
    //TextView mousePad;

    private boolean isConnected=false;
    private boolean mouseMoved=false;
    private Socket socket;
    private PrintWriter out;
    BufferedReader in;

 /*   private float initX =0;
    private float initY =0;
    private float disX =0;
    private float disY =0;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.makeActionOverflowMenuShown();
        context = this;
        //Get references of all buttons
        playPauseButton = (ImageButton)findViewById(R.id.playPauseButton);
        fsButton = (ImageButton)findViewById(R.id.fsButton);
        volBar = (SeekBar) findViewById(R.id.volBar);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        //setando o máximo da barra de volume no app
        volBar.setMax(125);

        tv = (TextView)findViewById(R.id.textView);
        tvTempo = (TextView)findViewById(R.id.tvTempo);
        playPauseButton.setOnClickListener(this);



        volBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Modificando o volume de acordo com o que o usuário pressionar na barra?
                progressChangedValue = progress;
                tv.setText(progress + "/125");
                out.println("volume " + progress*320/125);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
               // tv.setText(progress + "/125");
              //  out.println("volume " + progress*320/125);
                //StringBuilder instr = new StringBuilder();

/*                String inputLine;
                out.println("get_length");
                ReadDataCon conn = new ReadDataCon();
                conn.execute(in);
                seekBar.setMax(file_length);
                out.println("seek "+ progress);
                tvTempo.setText(progress+"/"+file_length);*/

                //Toast.makeText(MainActivity.this, "Seek bar progress is :" + progressChangedValue,Toast.LENGTH_SHORT).show();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {



            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                String inputLine;
                out.println("get_length");
                ReadDataCon conn = new ReadDataCon();
                conn.execute(in);
                seekBar.setMax(file_length);
                out.println("seek "+ progressChangedValue);
                tvTempo.setText(progressChangedValue+"/"+file_length);

            }
        });

    }

    private void makeActionOverflowMenuShown() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            Log.d("DEG", e.getLocalizedMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_connect) {
            ConnectPhoneTask connectPhoneTask = new ConnectPhoneTask();
            connectPhoneTask.execute(Constants.SERVER_IP); //try to connect to server in another thread
            return true;
        }
        if(id == R.id.action_settings){
            Intent i = new Intent(this,SettingsActivity.class);
            this.startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fsButton:
                if (isConnected && out!=null) {
                    out.println("pause");//send "play" to server
                }
                break;
            case R.id.playPauseButton:
                if (isConnected && out!=null) {
                    out.println("pause");//send "play" to server
                    //Toast.makeText(context,"AAA",Toast.LENGTH_LONG).show();
                    //Toast.makeText(context,"AAA",Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(isConnected && out!=null) {
            try {
                out.println("exit"); //tell server to exit
                socket.close(); //close socket
            } catch (IOException e) {
                Log.e("remotedroid", "Error in closing socket", e);
            }
        }
    }
/*
    public void atualizar(View v){
        String inputLine;
        out.println("get_length");
        ReadDataCon conn = new ReadDataCon();
        conn.execute(in);


    }*/

    public class ConnectPhoneTask extends AsyncTask<String,Void,Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            try {
                InetAddress serverAddr = InetAddress.getByName(params[0]);
                socket = new Socket(serverAddr, Constants.SERVER_PORT);//Open socket on server IP and port
            } catch (IOException e) {
                Log.e("remotedroid", "Error while connecting", e);
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            isConnected = result;
            Toast.makeText(context,isConnected?"Connected to server!":"Error while connecting",Toast.LENGTH_LONG).show();
            try {
                if(isConnected) {
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                            .getOutputStream())), true); //create output stream to send data to server
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                    out.println("1234");
                   // if(in.readLine()!=null)Log.d("DEB1","Teste");
                   // Log.d("DEB2",in.readLine());
                }
            }catch (IOException e){
                Log.e("remotedroid", "Error while creating OutWriter", e);
                Toast.makeText(context,"Error while connecting",Toast.LENGTH_LONG).show();
            }
        }
    }

    public class ReadDataCon extends AsyncTask<BufferedReader,Void,String> {

        @Override
        protected String doInBackground(BufferedReader... params) {
            //String temp = "erro";
            StringBuilder response = new StringBuilder();
           // String response = "erro";
            //String line = null;
           // int i = 0;

            try {
                response.append(in.readLine());
                if(response.toString().contains("Pa")||response.toString().contains("Wel")||response.toString().contains("VLC")) {
                    response.setLength(0);
                    try {
                        response.append(in.readLine());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    if (response.toString().contains("Pa") || response.toString().contains("Wel")||response.toString().contains("VLC")){
                        response.setLength(0);
                        response.append(in.readLine());
                        if (response.toString().contains("Pa") || response.toString().contains("Wel")||response.toString().contains("VLC")) {
                            response.setLength(0);
                            response.append(in.readLine());
                        }
                    }
                }
                   // if(in.readLine()==null) {
                      //  Log.d("DEB",response.toString() );
                    //}
                   // i++;
               // }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result)
        {

            result = result.replaceAll(">","");
            result = result.replaceAll("\\s","");

            file_length = Integer.parseInt(result);
            //Toast.makeText(context,file_length + "",Toast.LENGTH_SHORT).show();
        }
    }

    public void fullS(View v){if (isConnected && out!=null) {
        out.println("f");//send "play" to server
        //Toast.makeText(context,  v.getId()+"",Toast.LENGTH_LONG).show();
        //Toast.makeText(context,"AAA",Toast.LENGTH_LONG).show();
    }}


}