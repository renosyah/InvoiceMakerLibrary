# Invoice Maker Library

ini adalah library simple untuk membuat invoice dalam bentuk file pdf,dari isi file pdf tersebut di view dengan menggunakan [PDF Viewer](https://github.com/renosyah/EBookLibrary) dari EBookLibrary dan berikut ini adalah tampilan : 

![GitHub Logo](/image/image_1.jpg)


cara pakai : 

* instalaltion : 

tambahkan ke build.gradle :
```

	allprojects {
    		repositories {
        		maven { url "https://dl.bintray.com/renosyah/maven" }
        		...
       			...
    		}
	}

```


tambahkan ke app.gradle :
```

	dependencies {
    		...	
    		implementation 'com.github.renosyah:Invoice-Maker-Library:1.0.0'
	}

```


* membuat invoice dari data transaksi : 

```
		val item = ArrayList<TransactionModel.Item>()
                item.add(TransactionModel.Item("Burger",4,100))
                item.add(TransactionModel.Item("SandWitch",2,200))
                val transaction = TransactionModel(TransactionModel.DateTransaction(9,12,2019),item,
                    TransactionModel.OtherData("My Invoice For My Fast Food","$"))

                InvoiceMakerInit.newInstance()
                    .setContext(this)
                    .setFolderTarget("myinvoice")
                    .setTransactionModel(transaction)
                    .setOnInvoiceMakerInitListener(object : InvoiceMakerInit.OnInvoiceMakerInitListener{
                        override fun onInvoiceCreated(file: File) {
                            
                            // file PDF yg telah dibuat
                            
                        }

                        override fun onException(e: java.lang.Exception) {
                            
                            // exception yg didapat pada saat membuat file     
                        
                        }

                    })
                    .setOnInvoiceMakerRequestPermissionListener(object : InvoiceMakerInit.OnInvoiceMakerRequestPermissionListener{
                        override fun onPermissionResult(isGranted: Boolean) {
                            
                            // check apakan permission di setujui
                            
                        }

                    }).makePDF("invoice1001.pdf")


```



## Hacking time

setiap transaksi pasti memiliki invoice yg berbeda-beda, berikut adalah bagaimana cara membuat custom model dan invoice

* membuat custom class untuk transaksi dan item

```
class MyCustomTransactionModel(var items: ArrayList<Item>) : TransactionDataInterface {

    override fun toHTML(): String {
        val html_top = "<html>\n" +
                "<head></head>\n" +
                "<body style='font-family:Andale Mono, monospace'>\n" +
                "\n" +
                "<table border='0'>\n" +
                "<td colspan='2'><h2 style='text-align:center'>My Custom Invoice</h2></td>\n" +
                "</tr>\n" +
                "<tr style='text-align:center;'>\n" +
                "<td>&nbsp; Item &nbsp;</td><td>&nbsp; Total</td>\n" +
                "</tr>"

        var html_center = ""
        for (item in items){
            html_center += "<tr style='font-size:12px;text-align:center;'>\n" +
                    "<td>" + item.name + "</td>\n" +
                    "<td>" + item.total + "</td></tr>"
        }

        val html_bottom = "</table></body></html>"

        return html_top + html_center + html_bottom
    }

    class Item(var name : String,var total : Int)
}

```

* menggunakan custom class tadi sebagai data invoice

```

	val items = ArrayList<MyCustomTransactionModel.Item>()
                items.add(MyCustomTransactionModel.Item("burger",400))
                items.add(MyCustomTransactionModel.Item("sandwitch",400))
	val transaction = MyCustomTransactionModel(items)

	InvoiceMakerInit.newInstance()
                    .setContext(this)
                    .setFolderTarget("myinvoice")
                    .setTransactionModel(transaction)
                    .setOnInvoiceMakerInitListener(object : InvoiceMakerInit.OnInvoiceMakerInitListener{
                        override fun onInvoiceCreated(file: File) {
                            
                            // file PDF yg telah dibuat
                            
                        }

                        override fun onException(e: java.lang.Exception) {
                            
                            // exception yg didapat pada saat membuat file     
                        
                        }

                    })
                    .setOnInvoiceMakerRequestPermissionListener(object : InvoiceMakerInit.OnInvoiceMakerRequestPermissionListener{
                        override fun onPermissionResult(isGranted: Boolean) {
                            
                            // check apakan permission di setujui
                            
                        }

                    }).makePDF("invoice1001_custom.pdf")

```


* hasil
berikut hasil dari custom invoice : 


![GitHub Logo](/image/image_1_custom.jpg)









