package incometaxcalculator.data.io;


public class XMLFileReader extends FileReader {

    protected int getReceiptId(String[] values){
        if (values[0].equals("<ReceiptID>")) {
            return Integer.parseInt(values[1].trim());
        }
        return -1;
    }

    protected String getString(String line) {
        String[] valueWithTail = line.split(" ", 2);
        String[] valueReversed = new StringBuilder(valueWithTail[1]).reverse().toString().trim()
                .split(" ", 2);
        return new StringBuilder(valueReversed[1]).reverse().toString();
    }

}
