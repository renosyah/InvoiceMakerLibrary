package com.syahputrareno975.printpdffile.model;

import android.bluetooth.BluetoothClass;
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
}
