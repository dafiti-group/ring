/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.mask;

import br.com.dafiti.ring.model.FileHandler;
import br.com.dafiti.ring.model.ManualInput;
import br.com.dafiti.ring.model.Metadata;
import br.com.dafiti.ring.option.DataType;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author guilherme.almeida
 */
public class JSONDocument {
    
    private final String jsonType;
    protected String lineSeparator;
    
    public JSONDocument(String jsonType) {
        this.jsonType = jsonType.toLowerCase();
        if(this.jsonType.equals("jsonarray")) {
            lineSeparator = ",";
        } else {
            lineSeparator = "\n";
        }
    }

    /**
     * @param manualInput
     * @param file
     * @param loadDate
     * @return
     */
    public String generateDocuments(ManualInput manualInput, FileHandler file, String loadDate) {

        // String of documents to return in the end of function
        StringBuilder documents = new StringBuilder();
        documents.append(this.jsonType.equals("jsonarray") ? "[" : "");

        // get the header of the file as list to be easer to work with
        List<String> header = Arrays.asList(file.getHeader());
        // get the metadata filtering only the required fields expected in file
        List<Metadata> metadataList = manualInput.getMetadata()
                .stream()
                .filter(f -> f.getIsActive())
                .collect(Collectors.toList());
        // This List will be used to store the position of columns in file
        // That are the same setted in ManualInput's Metadata
        // this way the user can input a file with many column but the process with be able to get only the required ones
        // and ignore the columns are not part of setted in metadata
        ArrayList<Integer> fieldIndex = new ArrayList<>();

        for (int i = 0; i < metadataList.size(); i++) {
            Metadata metadata = metadataList.get(i);
            String fieldName = metadata.getFieldName();
            if (header.contains(fieldName)) {
                fieldIndex.add(header.indexOf(fieldName));
            } else {
                return null;
            }
        }

        // creates an array with the number of columns in metadata adding 3 more indexs
        // the 3 aditional fields refers to the default columns to generate tables and collections for manual input
        // partition_field, business_ley and load_date
        String[] jsonLine = new String[metadataList.size() + 3];
        
        int fileRowQty = file.getData().size();

        for (int line = 0; line < fileRowQty; line++) {

            String[] row = file.getData().get(line);
            // validate if the row has the same length of header
            if (row.length != header.size()) {
                return null;
            }
            String businessKey = "";

            for (int column = 0; column < fieldIndex.size(); column++) {

                String field = header.get(fieldIndex.get(column));
                String value = row[fieldIndex.get(column)];
                Metadata metadata = metadataList.get(fieldIndex.get(column));
                DataType dt = metadata.getDataType();

                if (metadataList.get(metadataList.indexOf(metadata)).getIsBusinessKey()) {
                    businessKey += value;
                }
                // validate numerical data type
                // I chose write a json string to be easer to understand
                try {
                    if (value == null) {
                        jsonLine[column] = "\"" + field + "\": null";
                    } else if (dt.equals(DataType.INTEGER)) {
                        jsonLine[column] = "\"" + field + "\": " + Integer.parseInt(value);
                    } else if (dt.equals(DataType.DECIMAL)) {
                        jsonLine[column] = "\"" + field + "\": " + Double.parseDouble(value);
                    } else {
                        jsonLine[column] = "\"" + field + "\": \"" + value.replace("\"", "\\\"") + "\"";
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            // add 3 default fields: partition_field, business_ley and load_date
            jsonLine[metadataList.size()] = "\"partition_field\":\"FULL\"";
            // define business key with hash
            if (businessKey.isEmpty()) {
                businessKey = line + loadDate;
            }

            HashFunction hf = Hashing.farmHashFingerprint64();
            Long businessKeyHashFingerPrint = hf.hashBytes(businessKey.getBytes()).asLong();
            jsonLine[metadataList.size() + 1] = "\"business_key\":" + businessKeyHashFingerPrint;
            // set load_date
            jsonLine[metadataList.size() + 2] = "\"load_date\":\"" + loadDate + "\"";

            String jsonToParse = "{" + String.join(", ", jsonLine) + "}";

            try {
                documents.append(jsonToParse);
                if(line < fileRowQty - 1) {
                    documents.append(this.lineSeparator);
                }
            } catch (Exception e) {
                return null;
            }
        }
        
        
        return documents.append(this.jsonType.equals("jsonarray") ? "]" : "\n").toString();
    }

    /**
     * @param manualInput
     * @param file
     * @param loadDate
     * @param listObjectGenerator
     */
    public void generateDocuments(ManualInput manualInput, FileHandler file, String loadDate, ListObjectGenerator listObjectGenerator) {

        // get the header of the file as list to be easer to work with
        List<String> header = Arrays.asList(file.getHeader());
        // get the metadata filtering only the required fields expected in file
        List<Metadata> metadataList = manualInput.getMetadata()
                .stream()
                .filter(f -> f.getIsActive())
                .collect(Collectors.toList());
        // This List will be used to store the position of columns in file
        // That are the same setted in ManualInput's Metadata
        // this way the user can input a file with many column but the process with be able to get only the required ones
        // and ignore the columns are not part of setted in metadata
        ArrayList<Integer> fieldIndex = new ArrayList<>();

        for (int i = 0; i < metadataList.size(); i++) {
            Metadata metadata = metadataList.get(i);
            String fieldName = metadata.getFieldName();
            if (header.contains(fieldName)) {
                fieldIndex.add(header.indexOf(fieldName));
            } else {
                return;
            }
        }

        // creates an array with the number of columns in metadata adding 3 more indexs
        // the 3 aditional fields refers to the default columns to generate tables and collections for manual input
        // partition_field, business_ley and load_date
        String[] jsonLine = new String[metadataList.size() + 3];

        for (int line = 0; line < file.getData().size(); line++) {

            String[] row = file.getData().get(line);
            // validate if the row has the same length of header
            if (row.length != header.size()) {
                return;
            }
            String businessKey = "";

            for (int column = 0; column < fieldIndex.size(); column++) {

                String field = header.get(fieldIndex.get(column));
                String value = row[fieldIndex.get(column)];
                Metadata metadata = metadataList.get(fieldIndex.get(column));
                DataType dt = metadata.getDataType();

                if (metadataList.get(metadataList.indexOf(metadata)).getIsBusinessKey()) {
                    businessKey += value;
                }
                // validate numerical data type
                // I chose write a json string to be easer to understand
                try {
                    if (value == null) {
                        jsonLine[column] = "\"" + field + "\": null";
                    } else if (dt.equals(DataType.INTEGER)) {
                        jsonLine[column] = "\"" + field + "\": " + Integer.parseInt(value);
                    } else if (dt.equals(DataType.DECIMAL)) {
                        jsonLine[column] = "\"" + field + "\": " + Double.parseDouble(value);
                    } else {
                        jsonLine[column] = "\"" + field + "\": \"" + value.replace("\"", "\\\"") + "\"";
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return;
                }
            }

            // add 3 default fields: partition_field, business_ley and load_date
            jsonLine[metadataList.size()] = "\"partition_field\":\"FULL\"";
            // define business key with hash
            if (businessKey.isEmpty()) {
                businessKey = line + loadDate;
            }

            HashFunction hf = Hashing.farmHashFingerprint64();
            Long businessKeyHashFingerPrint = hf.hashBytes(businessKey.getBytes()).asLong();
            jsonLine[metadataList.size() + 1] = "\"business_key\":" + businessKeyHashFingerPrint;
            // set load_date
            jsonLine[metadataList.size() + 2] = "\"load_date\":\"" + loadDate + "\"";

            String jsonToParse = "{" + String.join(", ", jsonLine) + "}";

            try {
                listObjectGenerator.add(jsonToParse);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

    }

}
