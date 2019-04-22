package com.syahputrareno975.printpdffile;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.syahputrareno975.printpdffile.model.BluetoothDeviceDataModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class PrintPDFActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private Intent intent;

    private IntentFilter intentfilter;

    private TextView textListEmpty;
    private ListView deviceListView;
    private SwipeRefreshLayout printerListRefresh;

    private ArrayList<BluetoothDeviceDataModel> deviceList = new ArrayList<BluetoothDeviceDataModel>();
    private BluetoothAdapter bluetoothAdapter;

    private ProgressDialog progressDialog;
    private int PERMISSION_REQUEST_CODE = 142;

    private File pdfFile;
    private String dataToPrint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_pdf);
        initWidget();
    }

    private void initWidget() {

        this.context = this;
        this.intent = getIntent();

        if (intent.hasExtra("file_path")) {
            this.pdfFile = new File(intent.getStringExtra("file_path"));
        }

        if (intent.hasExtra("data_string")){
            this.dataToPrint = intent.getStringExtra("data_string");
        }

        this.deviceListView = findViewById(R.id.printerList);
        this.deviceListView.setOnItemClickListener(this);

        this.printerListRefresh = findViewById(R.id.printerListRefresh);
        this.printerListRefresh.setOnRefreshListener(this);

        this.textListEmpty = findViewById(R.id.textListEmpty);
        this.textListEmpty.setVisibility(View.GONE);

        checkIfdeviceListEmpty();

        this.intentfilter = new IntentFilter();
        this.intentfilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        this.intentfilter.addAction(BluetoothDevice.ACTION_FOUND);
        this.intentfilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.intentfilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(this.receiveConectedBluetoothDeviceData, this.intentfilter);

        if (!isCoarsePermissionIsGranted()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            this.bluetoothAdapter = bluetoothManager.getAdapter();

        } else {

            this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        setUpShowProgressDialog();

        if (!this.bluetoothAdapter.isEnabled()){
            showDialogIfBluetoothIsOff();
            return;
        }

        startScanning();
    }

    private void startScanning() {
        this.bluetoothAdapter.startDiscovery();
    }

    private boolean isCoarsePermissionIsGranted() {
        boolean isGranted = true;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            isGranted = false;
        }
        return isGranted;
    }

    private void showDialogIfBluetoothIsOff(){

        new AlertDialog.Builder(context)
                .setTitle("Bluetooth Require")
                .setMessage("please turn on your bluetooth to start scan other bluetooth device")
                .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bluetoothAdapter.enable();
                        startScanning();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();

    }

    private void checkIfdeviceListEmpty(){
        this.textListEmpty.setVisibility(isListIsEmpty() ? View.VISIBLE : View.GONE);
        this.deviceListView.setVisibility(isListIsEmpty() ? View.GONE : View.VISIBLE);
    }

    private void setUpShowProgressDialog() {

        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setTitle("Find Bluetooth Device");
        this.progressDialog.setMessage("Finding nearby bluetooth device");
        this.progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bluetoothAdapter.cancelDiscovery();
                dialog.dismiss();
            }
        });
        this.progressDialog.setCancelable(false);

    }

    private void showDialogDeviceNotFound() {

        new AlertDialog.Builder(context)
                .setTitle("No Device Found")
                .setMessage("Device Not Found,you want to scan again?")
                .setPositiveButton("Scan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startScanning();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private Boolean isListIsEmpty() {
        return this.deviceList.isEmpty();
    }

    private void setAdapter() {

        ArrayList<String> listString = new ArrayList<>();
        for (BluetoothDeviceDataModel b : this.deviceList) {
            listString.add(b.name + "\n" + b.address);
        }
        this.deviceListView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, listString));

        checkIfdeviceListEmpty();
    }

    private final BroadcastReceiver receiveConectedBluetoothDeviceData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                ParcelUuid parcelUuid = (ParcelUuid) intent.getParcelableExtra(BluetoothDevice.EXTRA_UUID);

                BluetoothDeviceDataModel data = new BluetoothDeviceDataModel(device.getUuids(),
                        device.getBluetoothClass(),
                        device.getName(),
                        device.getAddress(),
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? device.getType() : 0,
                        device.getBondState());

                if (!isAlreadyInList(deviceList, data)) {
                    deviceList.add(data);
                    setAdapter();
                }

            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                if (bluetoothAdapter.isEnabled()){
                    startScanning();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                progressDialog.show();

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progressDialog.dismiss();

                if (isListIsEmpty()) {
                    showDialogDeviceNotFound();
                }
                checkIfdeviceListEmpty();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            Intent i = new Intent(context, this.getClass());
            i.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(i);
            finish();
        }
    }


    @Override
    protected void onResume() {
        registerReceiver(this.receiveConectedBluetoothDeviceData, this.intentfilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(this.receiveConectedBluetoothDeviceData);
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        if (!this.bluetoothAdapter.isEnabled()){
            showDialogIfBluetoothIsOff();
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle("Print")
                .setMessage("Print this data with "+deviceList.get(position).name+"?")
                .setPositiveButton("Print", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (pdfFile != null) {
                            findBT(deviceList.get(position));
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();

    }

    @Override
    public void onRefresh() {
        printerListRefresh.setRefreshing(!printerListRefresh.isRefreshing());
        if (!this.bluetoothAdapter.isEnabled()){
            showDialogIfBluetoothIsOff();
            return;
        }
        startScanning();
    }


    private Boolean isAlreadyInList(ArrayList<BluetoothDeviceDataModel> list, BluetoothDeviceDataModel b) {
        boolean isAlreadyIList = false;
        for (BluetoothDeviceDataModel inList : list) {
            if (inList.address!= null && b.address!= null && inList.address.equals(b.address)) {
                isAlreadyIList = true;
                break;
            }
        }
        return isAlreadyIList;
    }


    // --------------- //

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    void findBT(BluetoothDeviceDataModel b) {
        mmDevice = bluetoothAdapter.getRemoteDevice(b.address);
        if (mmDevice == null){
            Toast.makeText(context,"cannot connected to : "+b.name,Toast.LENGTH_SHORT).show();
            return;
        }
        openBT();

    }

    private void openBT() {
        try {

            UUID uuid = UUID.randomUUID();
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("IoexceptionopenBT",e.getMessage());
        }


    }

    private void beginListenForData() {
        try {
            final Handler handler = new Handler();
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {

                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException e) {
                            stopWorker = true;
                            Log.e("IoexceptionbeginListen",e.getMessage());

                        } catch (NullPointerException e){
                            Log.e("NullbeginListenForData",e.getMessage());
                        }
                    }
                }
            });
            workerThread.start();

        } catch (Exception e) {
            Log.e("exceptionbeginListen",e.getMessage());
        }

        sendData();

    }


    void sendData(){
        try {

            mmOutputStream.write(this.dataToPrint == null ?
                    readPDF(this.pdfFile).getBytes() :
                    this.pdfFile == null ?
                            this.dataToPrint.getBytes() :
                    "".getBytes());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exceptionbsendData",e.getMessage());
        }

        closeBT();
    }

    void closeBT()  {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();

            // tell the user bluetooth is close
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exceptioncloseBT",e.getMessage());
        }
    }

    String readPDF(File file){

        String parsedText="";
        try {
            PdfReader reader = new PdfReader(file.getPath());
            int n = reader.getNumberOfPages();
            for (int i = 0; i <n ; i++) {
                parsedText += PdfTextExtractor.getTextFromPage(reader, i+1)+"\n";
            }
            reader.close();

        } catch (Exception e) {

        }

        Log.e("print result",parsedText);

        return parsedText;
    }

}
