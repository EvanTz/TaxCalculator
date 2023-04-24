package incometaxcalculator.data.io;

public class TXTInfoWriter extends InfoWriter {
    private final String[] txtLabels = {
            "Name: ", "",
            "AFM: ", "",
            "Status: ", "",
            "Income: ", "",
            "Receipts: ", ""};
    private final String[] txtReceiptLabels = {
            "Receipt ID: ", "",
            "Date: ", "",
            "Kind: ", "",
            "Amount: ", "",
            "Company: ", "",
            "Country: ", "",
            "City: ", "",
            "Street: ", "",
            "Number: ", ""};

    public TXTInfoWriter(){
        fileType = "txt";
        labelsLength = txtLabels.length;
        receiptLabelsLength = txtReceiptLabels.length;
    }

    protected String getLabel(int index) {
        return txtLabels[index];
    }

    protected String getReceiptLabel(int index) {
        return txtReceiptLabels[index];
    }

}
