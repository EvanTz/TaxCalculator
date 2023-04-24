package incometaxcalculator.data.io;

public class TXTLogWriter extends LogWriter {

    private final String[] txtLabels = {
            "Name: ", "",
            "AFM: ", "",
            "Income: ", "",
            "Basic Tax: ", "",
            "Tax Increase: ", "",
            "Tax Decrease: ", "",
            "Total Tax: ", "",
            "TotalReceiptsGathered: ", "",
            "Entertainment: ", "",
            "Basic: ", "",
            "Travel: ", "",
            "Health: ", "",
            "Other: ", ""};

    public TXTLogWriter(){
        fileType = "txt";
        attributeLength = txtLabels.length;
    }

    protected String getLabels(int index) {
        return txtLabels[index];
    }

}
