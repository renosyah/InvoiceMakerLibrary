package com.example.renosyahputra.invoicemakerlibrary

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.renosyahputra.invoicemakerlib.invoice_maker.InvoiceMakerInit
import com.example.renosyahputra.invoicemakerlib.invoice_maker.InvoiceMakerInit.SMALL_INVOICE
import com.example.renosyahputra.invoicemakerlib.transaction_model.TransactionModel
import com.example.renosyahputra.pdfviewerlibrary.PdfViewer
import com.syahputrareno975.printpdffile.initFindPrinter.FindPrinterInit
import com.syahputrareno975.printpdffile.model.BluetoothDeviceDataModel
import com.syahputrareno975.printpdffile.model.BluetoothDeviceDataModel.connectToBluetoothDevice
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import com.syahputrareno975.printpdffile.task.BluetoothPrinter





class MainActivity : AppCompatActivity(),View.OnClickListener,
    InvoiceMakerInit.OnInvoiceMakerInitListener,
    InvoiceMakerInit.OnInvoiceMakerRequestPermissionListener,
    PdfViewer.OnPdfVewerListener,
    FindPrinterInit.OnFindPrinterInitListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        printPdf.setOnClickListener(this)
        printPdfCustom.setOnClickListener(this)
        findPrinterDevice.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v){
            printPdf -> {

                val item = ArrayList<TransactionModel.Item>()
                item.add(TransactionModel.Item("Burger",4,100))
                item.add(TransactionModel.Item("SandWitch",2,200))

                val transaction = TransactionModel(TransactionModel.DateTransaction(9,12,2019),item,
                    TransactionModel.OtherData("Invoice","$"))

                InvoiceMakerInit.newInstance()
                    .setContext(this@MainActivity)
                    .setFolderTarget("myinvoice")
                    .setCustomPage(SMALL_INVOICE)
                    .setTransactionModel(transaction)
                    .setOnInvoiceMakerInitListener(this)
                    .setOnInvoiceMakerRequestPermissionListener(this)
                    .makePDF("invoice1001.pdf")


            }

            printPdfCustom -> {

                val items = ArrayList<MyCustomTransactionModel.Item>()
                items.add(MyCustomTransactionModel.Item("burger",400))
                items.add(MyCustomTransactionModel.Item("sandwitch",400))
                val transaction = MyCustomTransactionModel(items)


                InvoiceMakerInit.newInstance()
                    .setContext(this@MainActivity)
                    .setFolderTarget("myinvoice")
                    .setTransactionModel(transaction)
                    .setOnInvoiceMakerInitListener(this)
                    .setOnInvoiceMakerRequestPermissionListener(this)
                    .makePDF("invoice1001_custom.pdf")
            }


            findPrinterDevice -> {

                FindPrinterInit.newInstance()
                    .setContext(this@MainActivity)
                    .setOnFindPrinterInitListener(this)
                    .findDevice()

            }
        }
    }


    override fun onChoosed(bluetoothDeviceData: BluetoothDeviceDataModel) {
        Toast.makeText(this@MainActivity,"Print Invoice Use : Device Name : ${bluetoothDeviceData.name},Device Address : ${bluetoothDeviceData.address}",Toast.LENGTH_SHORT).show()

        val item = ArrayList<TransactionModel.Item>()
        item.add(TransactionModel.Item("Burger",4,100))
        item.add(TransactionModel.Item("SandWitch",2,200))

        val transaction = TransactionModel(TransactionModel.DateTransaction(9,12,2019),item,
            TransactionModel.OtherData("Invoice","$"))

        val mPrinter = BluetoothPrinter(connectToBluetoothDevice(this@MainActivity,bluetoothDeviceData))
        mPrinter.connectPrinter(object : BluetoothPrinter.PrinterConnectListener {

            override fun onConnected() {

                mPrinter.printText("--------------------------------")
                mPrinter.addNewLine()
                mPrinter.setAlign(BluetoothPrinter.ALIGN_CENTER)
                mPrinter.printText(transaction.otherDataTransaction.titleInvoice)
                mPrinter.addNewLine()
                mPrinter.printText("--------------------------------")
                mPrinter.addNewLine()

                mPrinter.setAlign(BluetoothPrinter.ALIGN_RIGHT)
                for (i in item){

                    mPrinter.addNewLine()
                    mPrinter.printText("${i.itemName}   ")
                    mPrinter.printText("${i.quantity}   ")
                    mPrinter.printText("${transaction.otherDataTransaction.currency}${i.price}   ")
                    mPrinter.printText("${transaction.otherDataTransaction.currency} ${i.subTotal}")
                    mPrinter.addNewLine()

                }

                mPrinter.printText("--------------------------------")
                mPrinter.addNewLine()
                mPrinter.addNewLine()

                mPrinter.setAlign(BluetoothPrinter.ALIGN_RIGHT)
                mPrinter.printText("Total : ${transaction.total}")

                mPrinter.feedPaper()

                mPrinter.finish()
            }

            override fun onFailed() {
                Log.d("BluetoothPrinter", "Conection failed")
            }

        })


    }

    override fun onInvoiceCreated(file: File) {

        PdfViewer.newInstance()
            .setContext(this@MainActivity)
            .setPdfFile(file)
            .setOnPdfVewerListener(this)
            .viewPDF()


    }
    override fun onFinishView() {
        Toast.makeText(this@MainActivity,"finish view invoice",Toast.LENGTH_SHORT).show()
    }

    override fun onException(e: Exception) {
        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionResult(isGranted: Boolean) {
        Toast.makeText(this@MainActivity,if (isGranted) "Granted" else "not granted",Toast.LENGTH_SHORT).show()
    }
    override fun onFinish() {
        Toast.makeText(this@MainActivity,"Finish find Printer",Toast.LENGTH_SHORT).show()
    }
}
