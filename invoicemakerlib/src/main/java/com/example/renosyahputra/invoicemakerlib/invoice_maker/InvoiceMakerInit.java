package com.example.renosyahputra.invoicemakerlib.invoice_maker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import com.example.renosyahputra.invoicemakerlib.fragmentDialogRequestPermission.FragmentDialogRequestPermission;
import com.example.renosyahputra.invoicemakerlib.transaction_model.TransactionDataInterface;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;

public class InvoiceMakerInit {

    public static String SMALL_INVOICE = "SMALL_INVOICE";


    private static InvoiceMakerInit _instance;
    private Context context;
    private TransactionDataInterface transactionModel;
    private OnInvoiceMakerInitListener onInvoiceMakerInitListener;
    private OnInvoiceMakerRequestPermissionListener onInvoiceMakerRequestPermissionListener;
    private String folderTarget = "invoice";
    private Rectangle pagesize = PageSize.A4;

    public static InvoiceMakerInit newInstance() {
        _instance = new InvoiceMakerInit();
        return _instance;
    }

    public InvoiceMakerInit setContext(Context context) {
        _instance.context = context;
        return _instance;
    }

    public InvoiceMakerInit setTransactionModel(TransactionDataInterface transactionModel) {
        _instance.transactionModel = transactionModel;
        return _instance;
    }

    public InvoiceMakerInit setFolderTarget(String folderTarget) {
        _instance.folderTarget = folderTarget;
        return _instance;
    }

    public InvoiceMakerInit setOnInvoiceMakerInitListener(OnInvoiceMakerInitListener onInvoiceMakerInitListener) {
        _instance.onInvoiceMakerInitListener = onInvoiceMakerInitListener;
        return _instance;
    }

    public InvoiceMakerInit setOnInvoiceMakerRequestPermissionListener(OnInvoiceMakerRequestPermissionListener onInvoiceMakerRequestPermissionListener) {
        _instance.onInvoiceMakerRequestPermissionListener = onInvoiceMakerRequestPermissionListener;
        return _instance;
    }

    public InvoiceMakerInit setCustomPage(String namePage) {
        if (namePage.equals(SMALL_INVOICE)) {
            _instance.pagesize = new Rectangle(226, PageSize.POSTCARD.getHeight());
        }
        return _instance;
    }

    private InvoiceMakerInit() {
        super();
    }

    public void makePDF(String fileName){

        if (checkIsNull() || fileName == null){
            return;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            FragmentDialogRequestPermission fragmentDialogRequestPermission = new FragmentDialogRequestPermission();
            fragmentDialogRequestPermission.setOnInvoiceMakerRequestPermissionListener(this.onInvoiceMakerRequestPermissionListener);

            try {

                Activity activity = (Activity) this.context;
                fragmentDialogRequestPermission.show(activity.getFragmentManager(), "dialog");

            }catch (ClassCastException e){
                e.printStackTrace();
            }

            return;
        }

        File folderTarget = new File(Environment.getExternalStorageDirectory(),this.folderTarget);
        if (!folderTarget.exists()){
            folderTarget.mkdir();
        }

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(new File(folderTarget,fileName));

            Document document = new Document(this.pagesize);

            PdfWriter.getInstance(document,fileOutputStream);

            HTMLWorker htmlWorker = new HTMLWorker(document);

            document.open();
            htmlWorker.startDocument();
            htmlWorker.parse(new StringReader(this.transactionModel.toHTML()));
            htmlWorker.endDocument();

            document.close();
            htmlWorker.close();
            fileOutputStream.close();

        }catch (FileNotFoundException e){
            this.onInvoiceMakerInitListener.onException(e);

        }catch (DocumentException d){
            this.onInvoiceMakerInitListener.onException(d);

        }catch (IOException i){
            this.onInvoiceMakerInitListener.onException(i);

        }

        this.onInvoiceMakerInitListener.onInvoiceCreated(new File(folderTarget,fileName));


    }

    private Boolean checkIsNull(){
        return (context == null
                || transactionModel == null
                || onInvoiceMakerInitListener  == null
                || onInvoiceMakerRequestPermissionListener == null
        );
    }


    public interface OnInvoiceMakerInitListener{
        void onInvoiceCreated(@NonNull File file);
        void onException(@NonNull Exception e);
    }

    public interface OnInvoiceMakerRequestPermissionListener{
        void onPermissionResult(@NonNull Boolean isGranted);
    }
}
