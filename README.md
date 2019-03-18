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

                InvoiceMakerInit.newIntance()
                    .setContext(this@MainActivity)
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



