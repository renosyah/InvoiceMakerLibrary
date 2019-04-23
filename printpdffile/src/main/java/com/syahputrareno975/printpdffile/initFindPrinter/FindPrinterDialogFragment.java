package com.syahputrareno975.printpdffile.initFindPrinter;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.syahputrareno975.printpdffile.FindBluetoothDeviceActivity;
import com.syahputrareno975.printpdffile.R;
import com.syahputrareno975.printpdffile.model.BluetoothDeviceDataModel;

public class FindPrinterDialogFragment extends DialogFragment {

    private Context context;
    private Boolean printerDeviceOnly = true;
    private FindPrinterInit.OnFindPrinterInitListener listener;
    private int RequestCode = 203;

    public void setPrinterDeviceOnly(Boolean printerDeviceOnly) {
        this.printerDeviceOnly = printerDeviceOnly;
    }

    public void setListener(FindPrinterInit.OnFindPrinterInitListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.empty_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        context = getActivity();

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent i = new Intent(context, FindBluetoothDeviceActivity.class);
        i.putExtra("printer_only",this.printerDeviceOnly);
        startActivityForResult(i,RequestCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode && Activity.RESULT_OK == resultCode && listener != null && data != null){
            BluetoothDeviceDataModel result = (BluetoothDeviceDataModel)
                    data.getSerializableExtra("printer_data");

            listener.onChoosed(result);
        }

        getDialog().dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (listener != null){
            listener.onFinish();
        }
        super.onDismiss(dialog);
    }
}
