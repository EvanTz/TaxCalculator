package incometaxcalculator.data.management.factories;

import incometaxcalculator.data.io.InfoWriter;
import incometaxcalculator.data.io.TXTInfoWriter;
import incometaxcalculator.data.io.XMLInfoWriter;
import incometaxcalculator.data.management.TaxpayerManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class InfoFileCreationFactory {

    public ArrayList<InfoWriter> createInfoFile(int taxRegistrationNumber) throws IOException {
        String path  = getPath(taxRegistrationNumber);
        ArrayList<InfoWriter> infoWriters = new ArrayList<>();

        if (new File(path + taxRegistrationNumber + "_INFO.xml").exists()) {
            infoWriters.add(new XMLInfoWriter());
        } else {
            infoWriters.add(new TXTInfoWriter());
            return infoWriters;
        }
        if (new File(path + taxRegistrationNumber + "_INFO.txt").exists()) {
            infoWriters.add(new TXTInfoWriter());
        }

        return infoWriters;
    }

    private String getPath(int taxRegistrationNumber){
        TaxpayerManager manager = new TaxpayerManager();
        String[] split_path = manager.getTaxpayer(taxRegistrationNumber).getFullFileName().split("\\\\");
        return String.join("\\\\",Arrays.copyOfRange(split_path, 0, split_path.length - 1))
                + "\\\\";
    }

}
