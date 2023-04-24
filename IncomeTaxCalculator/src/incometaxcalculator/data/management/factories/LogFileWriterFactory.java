package incometaxcalculator.data.management.factories;

import incometaxcalculator.data.io.LogWriter;
import incometaxcalculator.data.io.TXTLogWriter;
import incometaxcalculator.data.io.XMLLogWriter;
import incometaxcalculator.exceptions.WrongFileFormatException;

public class LogFileWriterFactory {

    public LogWriter createFileWriter(String fileFormat) throws WrongFileFormatException {
        if (fileFormat.equals("txt")) {
            return new TXTLogWriter();
        } else if (fileFormat.equals("xml")) {
            return new XMLLogWriter();
        } else {
            throw new WrongFileFormatException();
        }

    }
}
