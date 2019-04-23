package com.syahputrareno975.printpdffile.task;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import com.syahputrareno975.printpdffile.model.BluetoothDeviceDataModel;

public class ConnectToPrinter extends AsyncTask<Void, Void, BluetoothSocket> {

    private BluetoothDeviceDataModel bluetoothDeviceData;
    private OnConnectToPrinterListener listener;


    public ConnectToPrinter(BluetoothDeviceDataModel bluetoothDeviceData, OnConnectToPrinterListener listener) {
        this.bluetoothDeviceData = bluetoothDeviceData;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected BluetoothSocket doInBackground(Void... voids) {
        return null;
    }



    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        super.onPostExecute(bluetoothSocket);
    }

    public interface OnConnectToPrinterListener {
        void onConnected(BluetoothSocket socket);
        void onFailed();
    }
}
