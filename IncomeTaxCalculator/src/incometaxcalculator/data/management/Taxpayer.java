package incometaxcalculator.data.management;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import incometaxcalculator.exceptions.WrongReceiptKindException;
import incometaxcalculator.exceptions.WrongTaxpayerStatusException;

public class Taxpayer {

    protected final String fullName;
    protected final int taxRegistrationNumber;
    protected String status;
    protected float income;
    protected final String fullFileName;
    protected ArrayList<Integer> incomeBrackets = new ArrayList<Integer>();
    protected ArrayList<Double> taxBasePerBracket = new ArrayList<Double>();
    protected ArrayList<Double> taxPercentIncreasePerBracket = new ArrayList<Double>();

    private float amountPerReceiptsKind[] = new float[5];
    private int totalReceiptsGathered = 0;
    private HashMap<Integer, Receipt> receiptHashMap = new HashMap<Integer, Receipt>(0);

    private static final  List<String> receiptKinds =
            new ArrayList<String>(Arrays.asList("Entertainment","Basic","Travel","Health","Other"));

    public Taxpayer( String status, String fullName, int taxRegistrationNumber, float income, String fullFileName)
            throws WrongTaxpayerStatusException{
        this.fullName = fullName;
        this.taxRegistrationNumber = taxRegistrationNumber;
        this.income = income;
        this.fullFileName = fullFileName;
        this.status = status;

        loadStatusProperties();
    }
    
    private void loadStatusProperties() throws WrongTaxpayerStatusException{
        checkStatus();
        Properties prop = new Properties();
        String propertiesFileName = "Config\\taxpayerStatusConfig.txt";
        try {
            FileInputStream fis = new FileInputStream(propertiesFileName);
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            // FileNotFoundException catch is optional and can be collapsed
        } catch (IOException ignored) {
        }

        loadProperties(prop);
    }

    private void loadProperties(Properties prop){
        int[] incomeBracketsArray = Stream.of(prop.getProperty("taxpayer."+
                status.replaceAll("\\W","")+
                ".incomeBrackets").split(",")).mapToInt(Integer::parseInt).toArray();

        double[] taxBasePerBracketArray = Stream.of(prop.getProperty("taxpayer."+
                status.replaceAll("\\W","")+
                ".taxBasePerBracket").split(",")).mapToDouble(Double::parseDouble).toArray();

        double[] taxPercentIncreasePerBracketArray = Stream.of(prop.getProperty("taxpayer."+
                status.replaceAll("\\W","")+
                ".taxPercentIncreasePerBracket").split(",")).mapToDouble(Double::parseDouble).toArray();

        loadPropertiesIntoArrays(incomeBracketsArray, taxBasePerBracketArray, taxPercentIncreasePerBracketArray);
    }

    private void loadPropertiesIntoArrays(int[] incomeBracketsArray, double[] taxBasePerBracketArray,
                                      double[] taxPercentIncreasePerBracketArray) {
        incomeBrackets.clear();
        taxBasePerBracket.clear();
        taxPercentIncreasePerBracket.clear();

        for (int i: incomeBracketsArray) {
            incomeBrackets.add(i);
        }
        for (double i : taxBasePerBracketArray) {
            taxBasePerBracket.add(i);
        }
        for (double i : taxPercentIncreasePerBracketArray) {
            taxPercentIncreasePerBracket.add(i);
        }
    }

    private void checkStatus() throws WrongTaxpayerStatusException{
        List<String> statusList = new ArrayList<>(Arrays.asList("Married Filing Jointly",
                "Married Filing Separately", "Single", "Head of Household"));
        if (!statusList.contains(status)) {
            throw new WrongTaxpayerStatusException();
        }
    }

    public double calculateBasicTax(){
        for (Integer incomeBracket : incomeBrackets) {
            if (income < incomeBracket){
                int tempIndex = incomeBrackets.indexOf(incomeBracket)-1;
                return taxBasePerBracket.get(tempIndex) + taxPercentIncreasePerBracket.get(tempIndex) * income;
            }
        }
        return taxBasePerBracket.get(4) + taxPercentIncreasePerBracket.get(4) * (income - incomeBrackets.get(4));
    }

    public void addReceipt(Receipt receipt) throws WrongReceiptKindException {
        if (receiptKinds.contains(receipt.getKind())){
            amountPerReceiptsKind[receiptKinds.indexOf(receipt.getKind())] += receipt.getAmount();
            receiptHashMap.put(receipt.getId(), receipt);
            totalReceiptsGathered++;
        } else {
            throw new WrongReceiptKindException();
        }
    }

    public void removeReceipt(int receiptId) throws WrongReceiptKindException {
        Receipt receipt = receiptHashMap.get(receiptId);
        if (receiptKinds.contains(receipt.getKind())){
            amountPerReceiptsKind[receiptKinds.indexOf(receipt.getKind())] -= receipt.getAmount();
            totalReceiptsGathered--;
            receiptHashMap.remove(receiptId);
        } else {
            throw new WrongReceiptKindException();
        }
    }

    public String getFullName() {
        return fullName;
    }

    public int getTaxRegistrationNumber() {
        return taxRegistrationNumber;
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float newIncome){
        this.income = newIncome;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String newStatus) throws WrongTaxpayerStatusException{
        status = newStatus;
        loadStatusProperties();
    }

    public String getFullFileName() {
        return fullFileName;
    }
    public HashMap<Integer, Receipt> getReceiptHashMap() {
        return receiptHashMap;
    }

    public double getVariationTaxOnReceipts() {
        float totalAmountOfReceipts = getTotalAmountOfReceipts();
        float[] incomePct = {0.2f * income, 0.4f * income, 0.6f * income};
        double[] taxPct = {0.08 , 0.04, -0.15 , -0.3};
        for (int i = 0; i < incomePct.length; i++) {
            if (totalAmountOfReceipts < incomePct[i]){
                return calculateBasicTax() * taxPct[i];
            }
        }
        return calculateBasicTax() * taxPct[taxPct.length-1];
    }
    private float getTotalAmountOfReceipts() {
        int sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += amountPerReceiptsKind[i];
        }
        return sum;
    }

    public int getTotalReceiptsGathered() {
        return totalReceiptsGathered;
    }

    public float getAmountOfReceiptKind(short kind) {
        return amountPerReceiptsKind[kind];
    }

    public double getTotalTax() {
        return calculateBasicTax() + getVariationTaxOnReceipts();
    }

    public double getBasicTax() {
        return calculateBasicTax();
    }

}