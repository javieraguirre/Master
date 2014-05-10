package com.domotica.mj;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException; // No lo usamos. Preferimos pillar solo las excepciones generales con Exception.
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MJ extends Activity implements OnClickListener
	{
	private static final int REQUEST_ENABLE_BT = 0;
	private BluetoothAdapter mBtAdapter;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver()
		{
        @Override
        public void onReceive(Context arg0, Intent arg1)
        	{
        	String action = arg1.getAction();
        	if (BluetoothDevice.ACTION_FOUND.equals(action))
        		{
        		BluetoothDevice device = arg1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        		if (device.getBondState() != BluetoothDevice.BOND_BONDED) { Toast.makeText(getApplicationContext(), device.getName() + " " + device.getAddress(), Toast.LENGTH_LONG).show(); }
        		}
        		else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) { Toast.makeText(getApplicationContext(), "Fin de busqueda", Toast.LENGTH_SHORT).show(); }
        	}
		};
	

	// Declaramos los botones y elementos que usaremos:

	private Button button_scanner;
	private Button button_send;
	private Button button_v1;
	private Button button_v2;
	private Button button_pto;
	private Button button_closeconn;
	private EditText editTextDat;
	private EditText editTextSys; // Este no tiene uso de momento; esta ahi para dejar de abusar del Toast proximamente.
	private ToggleButton toggleButtonWT12;
	private ToggleButton toggleButtonPenEzurio;
	private ToggleButton toggleButtonBT10;
	private ToggleButton toggleButtonOtro;
	
	private String mac_destino;
	private BluetoothDevice device;
	private Object socket_device;
	private DataOutputStream tmpOut;
	private DataInputStream tmpIn;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_mj);
        
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null)	{ Toast.makeText(this, "Bluetooth no disponible",Toast.LENGTH_LONG).show();	finish(); return; }
		if (!mBtAdapter.isEnabled()) { Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); startActivityForResult(enableIntent, REQUEST_ENABLE_BT); }
		
        
		// "Listeners" de la botonera:
		
        this.button_scanner = (Button)findViewById(R.id.button_scan); this.button_scanner.setOnClickListener(this);
        this.button_send = (Button)findViewById(R.id.button_send); this.button_send.setOnClickListener(this);
        this.button_closeconn = (Button)findViewById(R.id.button_closeconn); this.button_closeconn.setOnClickListener(this);        
        this.button_v1 = (Button)findViewById(R.id.button_v1); this.button_v1.setOnClickListener(this);
        this.button_v2 = (Button)findViewById(R.id.button_v2); this.button_v2.setOnClickListener(this);
        this.button_pto = (Button)findViewById(R.id.button_pto); this.button_pto.setOnClickListener(this);
        
        this.toggleButtonWT12 = (ToggleButton)findViewById(R.id.toggleButtonWT12); this.toggleButtonWT12.setOnClickListener(this);
        this.toggleButtonPenEzurio = (ToggleButton)findViewById(R.id.toggleButtonPenEzurio); this.toggleButtonPenEzurio.setOnClickListener(this);
        this.toggleButtonBT10 = (ToggleButton)findViewById(R.id.toggleButtonBT10); this.toggleButtonBT10.setOnClickListener(this);
        this.toggleButtonOtro = (ToggleButton)findViewById(R.id.toggleButtonOtro); this.toggleButtonOtro.setOnClickListener(this);
        
        this.editTextDat = (EditText)findViewById(R.id.editTextDat);
        this.editTextSys = (EditText)findViewById(R.id.editTextSys);
    	}
	
    
    // Comportamiento de la botonera. Incluye los comandos que se envian al Arduino, y la conexion y desconexion de los dispositivos:
    
	public void onClick(View v) {
		String command = null;
		
		switch(v.getId()){
		case (R.id.button_scan):
			startScanner();
			break;
		case (R.id.toggleButtonWT12):
			closeConnection(); toggleButtonWT12.setChecked(true);
			command = "00:07:80:46:d2:57"; doConnect(command);
		    break;
		case (R.id.toggleButtonPenEzurio):
			closeConnection(); toggleButtonPenEzurio.setChecked(true);
			command = "00:80:98:98:70:FF"; doConnect(command);
		    break;
		case (R.id.toggleButtonBT10):
			closeConnection(); toggleButtonBT10.setChecked(true);
			command = "00:07:80:49:8c:59"; doConnect(command);
		    break;
		case (R.id.toggleButtonOtro): command = editTextDat.getText().toString(); editTextDat.setText("");
			closeConnection(); toggleButtonOtro.setChecked(true);
			doConnect(command);
			getInfo();
		    break;
		case (R.id.button_v1):
			command = "@v1"; sendCommand(command);
			getInfo();
		    break;
		case (R.id.button_v2):
			command = "@v2"; sendCommand(command);
			getInfo();
		    break;
		case (R.id.button_pto):
			command = "@p"; sendCommand(command);
			getInfo();
		    break;
		case (R.id.button_send):
			sendCommand(editTextDat.getText().toString());
		    break;
		case (R.id.button_closeconn):
			closeConnection();
		    break;
			}
		}
	
	// Busqueda de dispositivos BT:
	
	public void startScanner(){

        mBtAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);  // Evento descubrimiento dispositivo
        this.registerReceiver(mReceiver, filter);    // Registra evento en BroadcastReceiver.
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);    // Evento fin de busqueda de dispositivo.
        this.registerReceiver(mReceiver, filter);    // Registra evento en BroadcastReceiver.
		}

	
	public void doConnect(String s)
		{
		this.mac_destino = s;		

		//CREANDO SOCKET. El UUID es el perfil SPP común para todos los dispositivos SPP
		try	{ this.device = mBtAdapter.getRemoteDevice(this.mac_destino); }
		catch (Exception e) {	Toast.makeText(this, "Fallo en getRemoteDevice: el dispositivo remoto no esta presente.", Toast.LENGTH_LONG).show(); closeConnection(); return; }
		try { this.socket_device = this.device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")); }
		catch (Exception e) {	Toast.makeText(this, "Fallo en createRFcommSocket.", Toast.LENGTH_LONG).show(); closeConnection(); return; }

		try {	//CREANDO CONEXION
			((BluetoothSocket) this.socket_device).connect();
			Toast.makeText(this, "Conectado", Toast.LENGTH_LONG).show();
		}
		catch (IllegalArgumentException e) {
			Toast.makeText(this, "Fallo: MAC no válido.", Toast.LENGTH_LONG).show(); Log.w("MJ", "error");
			closeConnection(); return;
		}
		catch (Exception e) { Toast.makeText(this, "Fallo en socket_device: el dispositivo remoto no tiene abierto ningun puerto.", Toast.LENGTH_LONG).show(); closeConnection(); return;	}
	}


	// Envio de comandos; recibe en el String s lo que se va a enviar:
	
	private void sendCommand(String s) {
		byte[] buffer;
		try { tmpOut = new DataOutputStream(((BluetoothSocket) this.socket_device).getOutputStream()); }
		catch (Exception e) { Toast.makeText(this, "Fallo en DataOutputStream.BluetoothSocket", Toast.LENGTH_LONG).show(); return; }
		try {
			buffer = s.getBytes();
			tmpOut.write(buffer);
			tmpOut.flush();
			editTextDat.setText("");
			}
		catch (Exception e) { Toast.makeText(this, "Fallo en DataOutputStream.Write", Toast.LENGTH_LONG).show(); return; }
		}
	

	
	// Recibir datos:
	
	public void getInfo() {
			char[] buffer_receive = new char[3];
			if (tmpIn != null)
				{
				try {
					tmpIn = new DataInputStream(((BluetoothSocket) this.socket_device).getInputStream());
					}
				catch (Exception e) { Toast.makeText(this, "Fallo en DataInputStream", Toast.LENGTH_LONG).show(); return;}
				try {
					buffer_receive[0] = (char) tmpIn.readByte();
					buffer_receive[1] = (char) tmpIn.readByte();
					buffer_receive[2] = (char) tmpIn.readByte();
					editTextSys.setText("Dato recibido: " + buffer_receive.toString());
					}
				catch (Exception e){ Toast.makeText(this, "Fallo en buffer_receive", Toast.LENGTH_LONG).show(); return;}
				}
			}


	// Cerrar conexiones a la vez que ponemos a apagamos los toggleButtons:
	
	private void closeConnection()
		{
		resetToggleButtons();
		if (socket_device != null) {
			try { ((BluetoothSocket) this.socket_device).close(); }
			catch (Exception e) { Toast.makeText(this, "Fallo al cerrar conexión.", Toast.LENGTH_LONG).show(); e.printStackTrace(); }	}
		
		else { Toast.makeText(this, "No hay ninguna conexión abierta", Toast.LENGTH_LONG).show(); }
		}
	
	
	private void resetToggleButtons() {
		toggleButtonWT12.setChecked(false);
		toggleButtonPenEzurio.setChecked(false);
		toggleButtonBT10.setChecked(false);
		toggleButtonOtro.setChecked(false);	}
} //end class MJ