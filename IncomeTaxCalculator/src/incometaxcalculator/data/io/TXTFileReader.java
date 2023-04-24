package incometaxcalculator.data.io;

public class TXTFileReader extends FileReader {

    protected int getReceiptId(String[] values){
        if (values[0].equals("Receipt")) {
            if (values[1].equals("ID:")) {
                return Integer.parseInt(values[2].trim());
            }
        }
        return -1;
    }

    protected String getString(String line) {
        String[] values = line.split(" ", 2);
        values[1] = values[1].trim();
        return values[1];
    }

}