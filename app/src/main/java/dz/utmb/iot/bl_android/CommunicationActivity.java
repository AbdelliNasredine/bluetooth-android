package dz.utmb.iot.bl_android;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import static android.view.View.OnClickListener;

public class CommunicationActivity extends AppCompatActivity implements OnClickListener {

    // attributes
    private static final String LF = "\n";
    public static final UUID SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "Communication Activity";
    private BluetoothDevice pairedDevice;
    private BluetoothSocket bluetoothSocket;
    private CommunicationThread communicationThread = null;


    // ui objects
    private EditText sendEditText;
    private Button sendButton;
    private TextView resultText;
    private ProgressDialog progressDialog;

    // On Send Button method
    @Override
    public void onClick(View view) {
        String command = sendEditText.getText().toString().concat(LF);
        if(!command.isEmpty()) {
            sendEditText.getText().clear();
            try {
                bluetoothSocket.getOutputStream().write(command.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // CommunicationThread
    private class CommunicationThread implements Runnable {

        private boolean stop = false;
        private Thread thread;

        public CommunicationThread() {
            thread = new Thread(this, "Comunication Thread");
            thread.start();
        }

        private boolean isRunning() {
            return thread.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;
            try {
                inputStream = bluetoothSocket.getInputStream();
                while (!stop) {
                    byte[] buffer = new byte[1024];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String input = new String(buffer, 0, i);
                        Log.i(TAG, "msg = " + input);
                        append(input);
                    }
                    Thread.sleep(200);
                }
            } catch (Exception e) {

            }
        }

        public void stop() {
            stop = true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunication);

        // setup of ui objects:
        resultText = (TextView) findViewById(R.id.tx_text);
        sendButton = (Button) findViewById(R.id.tx_send);
        sendEditText = (EditText) findViewById(R.id.tx_message);

        sendButton.setOnClickListener(this);

        // getting paired device information form intent:
        pairedDevice = getIntent().getExtras().getParcelable("DEVICE");
        append(pairedDevice.toString());

    }

    private void append(String message) {
        resultText.append(message);
    }

    // lifecycle methods
    @Override
    protected void onPause() {
        if(bluetoothSocket != null) {
            new BluetoothDisconectTask().execute();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (bluetoothSocket == null) {
            new BluetoothConnectTask().execute();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // Bluetooth Connect / Disconnect AsyncTask
    private class BluetoothConnectTask extends AsyncTask<Void, Void, Void> {
        private boolean isConnectionSuccessful = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CommunicationActivity.this,
                    "Wait", "Connecting...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (bluetoothSocket == null) {
                    bluetoothSocket = pairedDevice.createInsecureRfcommSocketToServiceRecord(SERVICE_UUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();
                }
            } catch (IOException ex) {
                isConnectionSuccessful = false;
                ex.printStackTrace();
                try {
                    bluetoothSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!isConnectionSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device", Toast.LENGTH_LONG)
                        .show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Connected to device", Toast.LENGTH_LONG)
                        .show();
                // starting reading data sent from esp (staring thread)
                communicationThread = new CommunicationThread();
            }
            progressDialog.dismiss();
        }
    }

    private class BluetoothDisconectTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (communicationThread != null) {
                communicationThread.stop();
                while (communicationThread.isRunning()) ;
                communicationThread = null;
            }
            try {
                bluetoothSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

}