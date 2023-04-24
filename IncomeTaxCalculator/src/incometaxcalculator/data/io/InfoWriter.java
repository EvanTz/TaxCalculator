package incometaxcalculator.data.io;

import incometaxcalculator.data.management.Receipt;
import incometaxcalculator.data.management.TaxpayerManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public abstract class InfoWriter implements FileWriter{

    protected String fileType;
    protected int labelsLength;
    protected int receiptLabelsLength;

    public void generateFile(int taxRegistrationNumber) throws IOException {
        TaxpayerManager manager = new TaxpayerManager();
        String[] split_path = manager.getTaxpayer(taxRegistrationNumber).getFullFileName().split("\\\\");
        String path =  String.join("\\\\", Arrays.copyOfRange(split_path, 0, split_path.length - 1))
                + "\\\\";
        PrintWriter outputStream = new PrintWriter(
                new java.io.FileWriter(path+taxRegistrationNumber + "_INFO."+fileType));

        for (int i = 0; i < labelsLength-2; i+=2) {
            outputStream.println(getLabel(i) + chooseField(i, manager, taxRegistrationNumber)
                    + getLabel(i+1));
        }
        outputStream.println();
        outputStream.println(getLabel(8));
        outputStream.println();
        generateTaxpayerReceipts(taxRegistrationNumber, outputStream, manager);

        outputStream.println(getLabel(9));

        outputStream.close();
    }
    protected abstract String getLabel(int index);

    private String chooseField(int index,TaxpayerManager manager, int taxRegistrationNumber){
        return switch (index) {
            case 0 -> manager.getTaxpayerName(taxRegistrationNumber);
            case 2 -> Integer.toString(taxRegistrationNumber);
            case 4 -> manager.getTaxpayerStatus(taxRegistrationNumber);
            case 6 -> manager.getTaxpayerIncome(taxRegistrationNumber);
            default -> "";
        };
    }

    private void generateTaxpayerReceipts(int taxRegistrationNumber, PrintWriter outputStream, TaxpayerManager manager) {

        HashMap<Integer, Receipt> receiptsHashMap = manager.getReceiptHashMap(taxRegistrationNumber);
        Iterator<HashMap.Entry<Integer, Receipt>> iterator = receiptsHashMap.entrySet().iterator();

        while (iterator.hasNext()) {
            HashMap.Entry<Integer, Receipt> entry = iterator.next();
            Receipt receipt = entry.getValue();
            for (int i = 0; i < receiptLabelsLength; i+=2) {
                outputStream.println(getReceiptLabel(i) + chooseReceiptField(i,receipt)
                        + getReceiptLabel(i+1));
            }
            outputStream.println();
        }
    }
    protected abstract String getReceiptLabel(int index);

    private String chooseReceiptField(int index,Receipt receipt){
        return switch (index) {
            case 0 -> getReceiptId(receipt);
            case 2 -> getReceiptIssueDate(receipt);
            case 4 -> getReceiptKind(receipt);
            case 6 -> getReceiptAmount(receipt);
            case 8 -> getCompanyName(receipt);
            case 10 -> getCompanyCountry(receipt);
            case 12 -> getCompanyCity(receipt);
            case 14 -> getCompanyStreet(receipt);
            case 16 -> getCompanyNumber(receipt);
            default -> "";
        };
    }

    private String getReceiptId(Receipt receipt) {
        return Integer.toString(receipt.getId());
    }

    private String getReceiptIssueDate(Receipt receipt) {
        return receipt.getIssueDate();
    }

    private String getReceiptKind(Receipt receipt) {
        return receipt.getKind();
    }

    private String getReceiptAmount(Receipt receipt) {
        return Float.toString(receipt.getAmount());
    }

    private String getCompanyName(Receipt receipt) {
        return receipt.getCompany().getName();
    }

    private String getCompanyCountry(Receipt receipt) {
        return receipt.getCompany().getCountry();
    }

    private String getCompanyCity(Receipt receipt) {
        return receipt.getCompany().getCity();
    }

    private String getCompanyStreet(Receipt receipt) {
        return receipt.getCompany().getStreet();
    }

    private String getCompanyNumber(Receipt receipt) {
        return Integer.toString(receipt.getCompany().getNumber());
    }
}
