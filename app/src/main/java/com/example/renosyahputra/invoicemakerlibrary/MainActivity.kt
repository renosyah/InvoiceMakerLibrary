package com.example.renosyahputra.invoicemakerlibrary

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.example.renosyahputra.invoicemakerlib.invoice_maker.InvoiceMakerInit
import com.example.renosyahputra.invoicemakerlib.invoice_maker.InvoiceMakerInit.SMALL_INVOICE
import com.example.renosyahputra.invoicemakerlib.transaction_model.TransactionModel
import com.example.renosyahputra.pdfviewerlibrary.PdfViewer
import com.syahputrareno975.printpdffile.PrintPDFActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File



class MainActivity : AppCompatActivity(),View.OnClickListener,
    InvoiceMakerInit.OnInvoiceMakerInitListener,
    InvoiceMakerInit.OnInvoiceMakerRequestPermissionListener,
    PdfViewer.OnPdfVewerListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        printPdf.setOnClickListener(this)
        printPdfCustom.setOnClickListener(this)
        startActivity(Intent(this@MainActivity, PrintPDFActivity::class.java))
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
        }
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
}
