package com.isi.isicashierlibrary.printer.fastPay;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import com.isi.isiapi.HttpRequest;
import com.isi.isiapi.classes.FiscalPrinter;
import com.isi.isiapi.classes.isicash.IsiCashBill;
import com.isi.isiapi.classes.isicash.IsiCashBillAndElements;
import com.isi.isiapi.classes.isicash.IsiCashElementBill;
import com.isi.isicashierlibrary.R.id;
import com.isi.isicashierlibrary.R.layout;
import com.isi.isicashierlibrary.printer.PrinterInterface;
import com.isi.isicashierlibrary.printer.brand.ERROR_TYPE;
import com.isi.isicashierlibrary.printer.brand.epson.EPSONPrinter;
import com.isi.isicashierlibrary.printer.brand.rch.RCHPrinter;
import com.isi.isicashierlibrary.printer.classes.Payment;
import com.isi.isicashierlibrary.printer.classes.PaymentType;
import com.isi.isicashierlibrary.printer.classes.ReceiptReturn;
import com.isi.isicashierlibrary.printer.preferences.BillQueue;
import com.isi.isicashierlibrary.printer.preferences.FiscalPrinterPreferences;
import com.isi.isilibrary.dialog.Dialog;
import com.isi.isilibrary.dialog.MaterialTextAndListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class FastPay {
    private HttpRequest httpRequest;
    private List<IsiCashElementBill> products;
    private IsiCashBill bill;
    private final Activity context;

    public FastPay(Activity context, HttpRequest httpRequest, List<IsiCashElementBill> products, IsiCashBill bill) {
        this.httpRequest = httpRequest;
        this.products = products;
        this.bill = bill;
        this.context = context;
    }

    public FastPay(Activity context) {
        this.context = context;
    }

    private PrinterInterface getPrinter() {
        PrinterInterface printer = null;
        FiscalPrinter printerPref = FiscalPrinterPreferences.getFiscalprinterPreferences(this.context);
        if (printerPref != null) {
            if (printerPref.type == 0) {
                printer = new RCHPrinter();
            } else {
                printer = new EPSONPrinter();
            }

            ((PrinterInterface)printer).init(this.context);
        }

        return (PrinterInterface)printer;
    }

    public abstract void onSuccessPay();

    public void initFastPay() {
        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ArrayList<Payment> payments = PaymentType.createPayments();
        AlertDialog d = (new Dialog(this.context)).showNormalDialogType((Dialog.DIALOG_TYPE)null, "Scegli metodo di pagamento", (String)null, new MaterialTextAndListener("Annulla", (dialog, which) -> {
            dialog.dismiss();
        }), (MaterialTextAndListener)null, linearLayout, true);
        Iterator var4 = payments.iterator();

        while(var4.hasNext()) {
            Payment p = (Payment)var4.next();
            View inflate = LayoutInflater.from(this.context).inflate(layout.choose_payment_layout, (ViewGroup)null);
            Button button = (Button)inflate.findViewById(id.button_pay);
            button.setText(p.getDescription());
            button.setOnClickListener((v) -> {
                d.dismiss();
                this.pay(p);
            });
            linearLayout.addView(inflate);
        }

        View inflate = LayoutInflater.from(this.context).inflate(layout.choose_payment_layout, (ViewGroup)null);
        Button button = (Button)inflate.findViewById(id.button_pay);
        button.setText("Modifica stampante predefinita");
        button.setOnClickListener((v) -> {
            d.dismiss();
            (new Thread(() -> {
                this.editPrefPrinter((Payment)null);
            })).start();
        });
        linearLayout.addView(inflate);
    }

    private void pay(Payment payment) {
        if (!payment.getCode().equals("T5") && !payment.getCode().equals("T6")) {
            AlertDialog loader = (new Dialog(this.context)).showLoadingDialog("Stampo scontrino");
            (new Thread(() -> {
                try {
                    PrinterInterface printer = this.getPrinter();
                    ReceiptReturn ok;
                    if (printer == null) {
                        ok = null;
                    } else {
                        ok = printer.printReceipt(this.products, payment.getCode(), this.bill, 0, 0.0F, "");
                    }

                    Activity var10000 = this.context;
                    Objects.requireNonNull(loader);
                    var10000.runOnUiThread(loader::dismiss);
                    if (ok == null) {
                        this.context.runOnUiThread(() -> {
                            this.editPrefPrinter(payment);
                        });
                    } else if (ok.getError_type() == ERROR_TYPE.NESSUN_ERRORE) {
                        this.context.runOnUiThread(this::onSuccessPay);
                        this.sendBill(payment.getSubcode(), this.calculateTotal(), ok.getClosureNumber(), ok.getDocumentNumber());
                    } else if (ok.getError_type() == ERROR_TYPE.ERRORE_CHIUSURA) {
                        this.context.runOnUiThread(() -> {
                            (new Dialog(this.context)).showCustomErrorConnectionDialog("Potrebbe essere necessaria una chiusura prima di procedere");
                        });
                    } else {
                        this.context.runOnUiThread(() -> {
                            (new Dialog(this.context)).showCustomErrorConnectionDialog("Problema di connessione con la stampante fiscale");
                        });
                    }
                } catch (Exception var5) {
                    var5.printStackTrace();
                    this.context.runOnUiThread(() -> {
                        (new Dialog(this.context)).showCustomErrorConnectionDialog("Problema con la creazione del conto, riprovare!");
                    });
                }

            })).start();
        } else {
            this.sendBill(payment.getSubcode(), this.calculateTotal(), 0, 0);
            this.onSuccessPay();
        }

    }

    private void editPrefPrinter(Payment payment) {
        List<FiscalPrinter> fiscalPrinterList = this.httpRequest.getFiscalPrinter();
        this.context.runOnUiThread(() -> {
            LinearLayout linearLayout = new LinearLayout(this.context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            AlertDialog d = (new Dialog(this.context)).showNormalDialogType((Dialog.DIALOG_TYPE)null, "Scegli stampante predefinita", (String)null, new MaterialTextAndListener("Annulla", (dialog, which) -> {
                dialog.dismiss();
            }), (MaterialTextAndListener)null, linearLayout, true);
            Iterator var5 = fiscalPrinterList.iterator();

            while(var5.hasNext()) {
                FiscalPrinter p = (FiscalPrinter)var5.next();
                View inflate = LayoutInflater.from(this.context).inflate(layout.choose_payment_layout, (ViewGroup)null);
                Button button = (Button)inflate.findViewById(id.button_pay);
                button.setText(p.name);
                button.setOnClickListener((v) -> {
                    FiscalPrinterPreferences.changeFiscalprinterPreferences(this.context, p);
                    d.dismiss();
                    if (payment != null) {
                        this.pay(payment);
                    } else {
                        this.initFastPay();
                    }

                });
                linearLayout.addView(inflate);
            }

        });
    }

    private float calculateTotal() {
        float total = 0.0F;

        float priceFloat;
        for(Iterator var2 = this.products.iterator(); var2.hasNext(); total += priceFloat) {
            IsiCashElementBill billProduct = (IsiCashElementBill)var2.next();
            priceFloat = (float)billProduct.quantity * billProduct.price;
            if (billProduct.discount_valor != 0.0F) {
                if (billProduct.discount_type == 0) {
                    priceFloat -= billProduct.discount_valor;
                } else {
                    priceFloat -= priceFloat * billProduct.discount_valor / 100.0F;
                }
            }
        }

        if (this.bill.discount_valor != 0) {

            if (this.bill.discount_type == 0) {
                total -= this.bill.discount_valor;

            } else {
                total = total - (total * this.bill.discount_valor) / 100;

            }
        }

        return total;
    }

    private void sendBill(String paymentType, float total, int closureNumber, int documentNumber) {
        IsiCashBillAndElements billAndElements = new IsiCashBillAndElements();
        this.bill.total = total;
        this.bill.payment_type = paymentType;
        this.bill.closure_number = closureNumber;
        this.bill.document_number = documentNumber;
        IsiCashBill temp = new IsiCashBill(this.bill.discount_valor, this.bill.discount_type, this.bill.account_id, this.bill.payment_type, this.bill.total, this.bill.closure_number, this.bill.document_number);
        temp.points_used = this.bill.points_used;
        temp.points_collected = this.bill.points_collected;
        temp.user_id = this.bill.user_id;
        billAndElements.isiCashBill = temp;
        billAndElements.elementBills = new ArrayList(this.products);
        (new Thread(() -> {
            boolean res = this.httpRequest.addBill(billAndElements, true);
            if (!res) {
                BillQueue queue = new BillQueue();
                queue.addBill(billAndElements, this.context);
                ArrayList<IsiCashBillAndElements> eles = queue.getBillQueue(this.context);
                if (eles != null) {
                    eles.removeIf((ele) -> {
                        return this.httpRequest.addBill(ele, false);
                    });
                    queue.overrideBill(eles, this.context);
                }
            }

        })).start();
    }
}

