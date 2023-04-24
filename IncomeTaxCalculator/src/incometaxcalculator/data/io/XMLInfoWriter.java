package incometaxcalculator.data.io;

public class XMLInfoWriter extends InfoWriter {
    private final String[] xmlLabels = {
            "<Name> ", " </Name>",
            "<AFM> ", " </AFM>",
            "<Status> ", " </Status>",
            "<Income> ", " </Income>",
            "<Receipts>", "</Receipts>"};
    private final String[] xmlReceiptLabels = {
            "<ReceiptID> ", " </ReceiptID>",
            "<Date> ", " </Date>",
            "<Kind> ", " </Kind>",
            "<Amount> ", " </Amount>",
            "<Company> ", " </Company>",
            "<Country> ", " </Country>",
            "<City> ", " </City>",
            "<Street> ", " </Street>",
            "<Number> ", " </Number>"};

    public XMLInfoWriter(){
        fileType = "xml";
        labelsLength = xmlLabels.length;
        receiptLabelsLength = xmlReceiptLabels.length;
    }

    protected String getLabel(int index) {
        return xmlLabels[index];
    }

    protected String getReceiptLabel(int index) {
        return xmlReceiptLabels[index];
    }

}