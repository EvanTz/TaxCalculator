package incometaxcalculator.data.io;

public class XMLLogWriter extends LogWriter {

    private final String[] xmlLabels = {
            "<Name> ", " </Name>",
            "<AFM> ", " </AFM>",
            "<Income> ", " </Income>",
            "<BasicTax> ", " </BasicTax>",
            "<TaxIncrease> ", " </TaxIncrease>",
            "<TaxDecrease> ", " </TaxDecrease>",
            "<TotalTax> ", " </TotalTax>",
            "<Receipts> ", " </Receipts>",
            "<Entertainment> ", " </Entertainment>",
            "<Basic> ", " </Basic>",
            "<Travel> ", " </Travel>",
            "<Health> ", " </Health>",
            "<Other> ", " </Other>"};

    public XMLLogWriter(){
        fileType = "xml";
        attributeLength = xmlLabels.length;
    }

    protected String getLabels(int index) {
        return xmlLabels[index];
    }


}
