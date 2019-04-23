package com.syahputrareno975.printpdffile.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;

import java.io.Serializable;
import java.util.UUID;

public class BluetoothDeviceDataModel implements Serializable {
    public String name = "";
    public String address = "";
    public int type = 0;
    public int bondState = 0;


    public BluetoothDeviceDataModel(String name, String address, int type, int bondState) {
        this.name = name == null ? "Unknown Device" : name;
        this.address = address;
        this.type = type;
        this.bondState = bondState;
    }



    public static BluetoothDevice connectToBluetoothDevice(Context context, BluetoothDeviceDataModel b) {
        BluetoothAdapter bluetoothAdapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BluetoothManager bluetoothManager = (BluetoothManager)
                    context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return bluetoothAdapter.getRemoteDevice(b.address);
    }
}
