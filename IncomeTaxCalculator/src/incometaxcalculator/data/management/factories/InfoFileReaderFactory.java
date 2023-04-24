package incometaxcalculator.data.management.factories;

import incometaxcalculator.data.io.FileReader;
import incometaxcalculator.data.io.TXTFileReader;
import incometaxcalculator.data.io.XMLFileReader;
import incometaxcalculator.exceptions.WrongFileEndingException;

public class InfoFileReaderFactory {

    public FileReader createFileReader(String fileName) throws WrongFileEndingException {
        String[] ending = fileName.split("\\.");
        if (ending[1].equals("txt")) {
            return new TXTFileReader();
        } else if (ending[1].equals("xml")) {
            return new XMLFileReader();
        } else {
            throw new WrongFileEndingException();
        }

    }
}
