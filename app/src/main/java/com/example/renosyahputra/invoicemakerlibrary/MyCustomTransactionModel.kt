package com.example.renosyahputra.invoicemakerlibrary

import com.example.renosyahputra.invoicemakerlib.transaction_model.TransactionDataInterface

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