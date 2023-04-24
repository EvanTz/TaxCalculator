package incometaxcalculator.data.management;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import incometaxcalculator.data.io.*;
import incometaxcalculator.data.management.factories.InfoFileReaderFactory;
import incometaxcalculator.data.management.factories.LogFileWriterFactory;
import incometaxcalculator.data.management.factories.InfoFileCreationFactory;
import incometaxcalculator.exceptions.ReceiptAlreadyExistsException;
import incometaxcalculator.exceptions.WrongFileEndingException;
import incometaxcalculator.exceptions.WrongFileFormatException;
import incometaxcalculator.exceptions.WrongReceiptDateException;
import incometaxcalculator.exceptions.WrongReceiptKindException;
import incometaxcalculator.exceptions.WrongTaxpayerStatusException;

public class TaxpayerManager {

    private static HashMap<Integer, Taxpayer> taxpayerHashMap = new HashMap<Integer, Taxpayer>(0);
    private static HashMap<Integer, Integer> receiptOwnerTRN = new HashMap<Integer, Integer>(0);

    public void createTaxpayer(String fullName, int taxRegistrationNumber, String status, float income, String fullFilePath)
            throws WrongTaxpayerStatusException {

        taxpayerHashMap.put(taxRegistrationNumber,
                new Taxpayer(status, fullName, taxRegistrationNumber, income, fullFilePath));
    }

    public void createReceipt(int receiptId, String issueDate, float amount, String kind,
                              String companyName, String country, String city, String street, int number,
                              int taxRegistrationNumber) throws WrongReceiptKindException, WrongReceiptDateException {

        Receipt receipt = new Receipt(receiptId, issueDate, amount, kind,
                new Company(companyName, country, city, street, number));
        taxpayerHashMap.get(taxRegistrationNumber).addReceipt(receipt);
        receiptOwnerTRN.put(receiptId, taxRegistrationNumber);
    }

    public void removeTaxpayer(int taxRegistrationNumber) {
        Taxpayer taxpayer = taxpayerHashMap.get(taxRegistrationNumber);
        taxpayerHashMap.remove(taxRegistrationNumber);
        HashMap<Integer, Receipt> receiptsHashMap = taxpayer.getReceiptHashMap();
        Iterator<HashMap.Entry<Integer, Receipt>> iterator = receiptsHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<Integer, Receipt> entry = iterator.next();
            Receipt receipt = entry.getValue();
            receiptOwnerTRN.remove(receipt.getId());
        }
    }

    public void addReceipt(int receiptId, String issueDate, float amount, String kind,
                           String companyName, String country, String city, String street, int number,
                           int taxRegistrationNumber)
            throws IOException, WrongReceiptKindException, WrongReceiptDateException, ReceiptAlreadyExistsException {

        if (containsReceipt(receiptId)) {
            throw new ReceiptAlreadyExistsException();
        }
        createReceipt(receiptId, issueDate, amount, kind, companyName, country, city, street, number,
                taxRegistrationNumber);
        updateFiles(taxRegistrationNumber);
    }

    public void removeReceipt(int receiptId) throws IOException, WrongReceiptKindException {
        taxpayerHashMap.get(receiptOwnerTRN.get(receiptId)).removeReceipt(receiptId);
        updateFiles(receiptOwnerTRN.get(receiptId));
        receiptOwnerTRN.remove(receiptId);
    }


    public void changeStatus(String newStatus, int taxRegistrationNumber) throws WrongTaxpayerStatusException,IOException{
        taxpayerHashMap.get(taxRegistrationNumber).setStatus(newStatus);
        updateFiles(taxRegistrationNumber);
    }

    
    public void changeIncome(float newIncome, int taxRegistrationNumber) throws IOException{
        taxpayerHashMap.get(taxRegistrationNumber).setIncome(newIncome);
        updateFiles(taxRegistrationNumber);
    }

    private void updateFiles(int taxRegistrationNumber) throws IOException {
        InfoFileCreationFactory fileCreationFactory = new InfoFileCreationFactory();
        ArrayList<InfoWriter> fileWriters =  fileCreationFactory.createInfoFile(taxRegistrationNumber);
        for (InfoWriter infoFile : fileWriters) {
            infoFile.generateFile(taxRegistrationNumber);
        }
    }

    public void saveLogFile(int taxRegistrationNumber, String fileFormat, String filePath)
            throws IOException, WrongFileFormatException {
      
        LogFileWriterFactory fileWriterFactory = new LogFileWriterFactory();
        LogWriter writer = fileWriterFactory.createFileWriter(fileFormat);

        writer.setFilePath(filePath);
        writer.generateFile(taxRegistrationNumber);
    }

    public boolean containsTaxpayer(int taxRegistrationNumber) {
        return taxpayerHashMap.containsKey(taxRegistrationNumber);
    }

    public boolean containsAnyTaxpayer() {
        return !taxpayerHashMap.isEmpty();
    }

    public boolean containsReceipt(int id) {
        return receiptOwnerTRN.containsKey(id);
    }

    public Taxpayer getTaxpayer(int taxRegistrationNumber) {
        return taxpayerHashMap.get(taxRegistrationNumber);
    }

    public void loadTaxpayer(String fileName)
            throws NumberFormatException, IOException, WrongFileFormatException, WrongFileEndingException,
            WrongTaxpayerStatusException, WrongReceiptKindException, WrongReceiptDateException {

        InfoFileReaderFactory infoFileReaderFactory = new InfoFileReaderFactory();
        FileReader reader = infoFileReaderFactory.createFileReader(fileName);

        reader.readFile(fileName);
    }

    public String getTaxpayerName(int taxRegistrationNumber) {
        return taxpayerHashMap.get(taxRegistrationNumber).getFullName();
    }

    public String getTaxpayerStatus(int taxRegistrationNumber) {
        return taxpayerHashMap.get(taxRegistrationNumber).getStatus();
    }

    public String getTaxpayerIncome(int taxRegistrationNumber) {
        return "" + taxpayerHashMap.get(taxRegistrationNumber).getIncome();
    }

    public double getTaxpayerVariationTaxOnReceipts(int taxRegistrationNumber) {
        return taxpayerHashMap.get(taxRegistrationNumber).getVariationTaxOnReceipts();
    }

    public int getTaxpayerTotalReceiptsGathered(int taxRegistrationNumber) {
        return taxpayerHashMap.get(taxRegistrationNumber).getTotalReceiptsGathered();
    }

    public float getTaxpayerAmountOfReceiptKind(int taxRegistrationNumber, short kind) {
        return taxpayerHashMap.get(taxRegistrationNumber).getAmountOfReceiptKind(kind);
    }

    public double getTaxpayerTotalTax(int taxRegistrationNumber) {
        return taxpayerHashMap.get(taxRegistrationNumber).getTotalTax();
    }

    public double getTaxpayerBasicTax(int taxRegistrationNumber) {
        return taxpayerHashMap.get(taxRegistrationNumber).getBasicTax();
    }

    public HashMap<Integer, Receipt> getReceiptHashMap(int taxRegistrationNumber) {
        return taxpayerHashMap.get(taxRegistrationNumber).getReceiptHashMap();
    }

}