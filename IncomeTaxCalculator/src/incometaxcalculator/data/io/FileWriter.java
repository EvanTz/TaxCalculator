package incometaxcalculator.data.io;

import java.io.IOException;

public interface FileWriter {
    void generateFile(int taxRegistrationNumber) throws IOException;

}