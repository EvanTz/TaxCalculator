package incometaxcalculator.data.io;

import incometaxcalculator.data.management.TaxpayerManager;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class LogWriter implements FileWriter{

    protected String fileType;

    private String filePath = "";
    protected int attributeLength;
    private static final short ENTERTAINMENT = 0;
    private static final short BASIC = 1;
    private static final short TRAVEL = 2;
    private static final short HEALTH = 3;
    private static final short OTHER = 4;

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }
    public void generateFile(int taxRegistrationNumber) throws IOException {
        TaxpayerManager manager = new TaxpayerManager();

        PrintWriter outputStream = new PrintWriter(
                new java.io.FileWriter(filePath+"\\"+taxRegistrationNumber + "_LOG."+fileType));

        for (int i = 0; i < attributeLength; i+=2) {
            if (i!= 8) {
                outputStream.
                        println(getLabels(i)
                                + chooseField(i, manager, taxRegistrationNumber)
                                + getLabels(i + 1));
            }
            else if (manager.getTaxpayerVariationTaxOnReceipts(taxRegistrationNumber) > 0) {
                outputStream
                        .println(getLabels(i)
                                + chooseField(i, manager, taxRegistrationNumber)
                                + getLabels(i + 1));
                i+=2;
            }
            else{
                i+=2;
                outputStream
                        .println(getLabels(i)
                                + chooseField(i, manager, taxRegistrationNumber)
                                + getLabels(i + 1));
            }
        }

        outputStream.close();
    }
    private String chooseField(int index,TaxpayerManager manager, int taxRegistrationNumber){
        return switch (index) {
            case 0 -> manager.getTaxpayerName(taxRegistrationNumber);
            case 2 -> Integer.toString(taxRegistrationNumber);
            case 4 -> manager.getTaxpayerIncome(taxRegistrationNumber);
            case 6 -> Double.toString(manager.getTaxpayerBasicTax(taxRegistrationNumber));
            case 8, 10 -> Double.toString(manager.getTaxpayerVariationTaxOnReceipts(taxRegistrationNumber));
            case 12 -> Double.toString(manager.getTaxpayerTotalTax(taxRegistrationNumber));
            case 14 -> Integer.toString(manager.getTaxpayerTotalReceiptsGathered(taxRegistrationNumber));
            case 16 -> Float.toString(manager.getTaxpayerAmountOfReceiptKind(taxRegistrationNumber, ENTERTAINMENT));
            case 18 -> Float.toString(manager.getTaxpayerAmountOfReceiptKind(taxRegistrationNumber, BASIC));
            case 20 -> Float.toString(manager.getTaxpayerAmountOfReceiptKind(taxRegistrationNumber, TRAVEL));
            case 22 -> Float.toString(manager.getTaxpayerAmountOfReceiptKind(taxRegistrationNumber, HEALTH));
            case 24 -> Float.toString(manager.getTaxpayerAmountOfReceiptKind(taxRegistrationNumber, OTHER));
            default -> "";
        };
    }

    protected abstract String getLabels(int index);
}
