package com.example.renosyahputra.invoicemakerlib.transaction_model;

import java.io.Serializable;
import java.util.ArrayList;

public class TransactionModel implements TransactionDataInterface {

    private DateTransaction dateTransaction = new DateTransaction();
    private ArrayList<Item> items = new ArrayList<>();
    private int Total = 0;
    private OtherData otherDataTransaction = new OtherData();

    public int getTotal() {
        return Total;
    }

    public String toHTML(){
        String html = "";
        String top = "<html>\n" +
                "<head></head>\n" +
                "<body style='font-family:Andale Mono, monospace'>\n" +
                "\n" +
                "<table border='0'>\n" +
                "<td colspan='4'><h2 style='text-align:center'>"+this.otherDataTransaction.TitleInvoice+"</h2></td>\n" +
                "</tr>\n" +
                "<tr style='text-align:center;'>\n" +
                "<td>&nbsp; Item &nbsp;</td><td>&nbsp; Price &nbsp;</td><td>&nbsp; Quantity &nbsp;</td><td>&nbsp; SubTotal</td>\n" +
                "</tr>";

        String middle = "";
        for (TransactionModel.Item item : this.items){
            middle += "<tr style='font-size:12px'>\n" +
                    "<td>"+item.getItemName()+"</td>\n" +
                    "<td style='text-align:center'>"+this.otherDataTransaction.getCurrency()+" "+item.getPrice()+"</td>\n" +
                    "<td style='text-align:center'>"+item.getQuantity()+"</td>\n" +
                    "<td style='text-align:right'>"+this.otherDataTransaction.getCurrency()+" "+item.getSubTotal()+"</td>\n" +
                    "</tr>";
        }

        String bottom = "<tr>\n" +
                "<td></td><td colspan='2' style='text-align:right'>Total : </td><td style='text-align:right'><b>"+this.otherDataTransaction.getCurrency()+" "+this.getTotal()+"</b></td>\n" +
                "</tr>\n" +
                "</table>\n" +
                "</body>\n" +
                "</html>";

        html = top + middle + bottom;

        return html;
    }

    public TransactionModel() {
        super();
    }


    public TransactionModel(DateTransaction dateTransaction, ArrayList<Item> items, OtherData otherDataTransaction) {
        this.dateTransaction = dateTransaction;
        this.items = items;
        this.otherDataTransaction = otherDataTransaction;
        this.Total = 0;
        for (Item item : items) {
            this.Total += item.getSubTotal();
        }
    }

    public DateTransaction getDateTransaction() {
        return dateTransaction;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public OtherData getOtherDataTransaction() {
        return otherDataTransaction;
    }

    public static class DateTransaction implements Serializable {
        private int Day = 0;
        private int Month = 0;
        private int Year = 0;

        public DateTransaction() {
            super();
        }

        public DateTransaction(int day, int month, int year) {
            Day = day;
            Month = month;
            Year = year;
        }

        public int getDay() {
            return Day;
        }

        public int getMonth() {
            return Month;
        }

        public int getYear() {
            return Year;
        }
    }

    public static class Item implements Serializable {
        private String ItemName = "";
        private int Quantity = 0;
        private int Price = 0;
        private int SubTotal = 0;

        public Item() {
            super();
        }

        public Item(String itemName, int quantity, int price, int subTotal) {
            ItemName = itemName;
            Quantity = quantity;
            Price = price;
            SubTotal = subTotal;
        }

        public Item(String itemName, int quantity, int price) {
            ItemName = itemName;
            Quantity = quantity;
            Price = price;
            SubTotal = quantity * price;
        }

        public String getItemName() {
            return ItemName;
        }

        public int getQuantity() {
            return Quantity;
        }

        public int getPrice() {
            return Price;
        }

        public int getSubTotal() {
            return SubTotal;
        }
    }

    public static class OtherData implements Serializable {
        private String TitleInvoice = "Invoice";
        private String Currency = "$";

        public OtherData() {
            super();
        }

        public OtherData(String titleInvoice, String currency) {
            TitleInvoice = titleInvoice;
            Currency = currency;
        }

        public String getTitleInvoice() {
            return TitleInvoice;
        }

        public String getCurrency() {
            return Currency;
        }
    }
}
