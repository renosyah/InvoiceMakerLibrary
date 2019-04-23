package com.syahputrareno975.printpdffile.initFindPrinter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import com.syahputrareno975.printpdffile.model.BluetoothDeviceDataModel;

public class FindPrinterInit {

    private static FindPrinterInit _instance;
    private Context context;
    private Boolean printerDeviceOnly = true;
    private OnFindPrinterInitListener listener;

    public static FindPrinterInit newInstance() {
        _instance = new FindPrinterInit();
        return _instance;
    }

    public FindPrinterInit setContext(Context context) {
        _instance.context = context;
        return _instance;
    }
    public FindPrinterInit allBluetoothDevice() {
        _instance.printerDeviceOnly = false;
        return _instance;
    }

    public FindPrinterInit setOnFindPrinterInitListener(OnFindPrinterInitListener listener) {
        _instance.listener = listener;
        return _instance;
    }

    private FindPrinterInit() {
    }

    public void findDevice(){

        FindPrinterDialogFragment dialog = new FindPrinterDialogFragment();
        dialog.setListener(this.listener);
        dialog.setPrinterDeviceOnly(this.printerDeviceOnly);

        try {

            Activity activity = (Activity) this.context;
            dialog.show(activity.getFragmentManager(), "printer_find_dialog");

        }catch (ClassCastException ignored){

        }

    }

    public interface OnFindPrinterInitListener {
        void onFinish();
        void onChoosed(@NonNull BluetoothDeviceDataModel bluetoothDeviceData);
    }
}
