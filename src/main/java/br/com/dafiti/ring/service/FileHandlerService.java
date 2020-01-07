/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.service;

import br.com.dafiti.ring.mask.JSONDocument;
import br.com.dafiti.ring.model.FileHandler;
import br.com.dafiti.ring.model.ImportLog;
import br.com.dafiti.ring.model.ManualInput;
import br.com.dafiti.ring.option.ImportLogStatus;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author guilherme.almeida
 */
@Service
public class FileHandlerService extends JSONDocument {

    private final ImportLogService importLogService;

    @Autowired
    public FileHandlerService(ImportLogService importLogService,
            @Value("${ring.json.file.type}") String jsonType) {
        super(jsonType);
        this.importLogService = importLogService;
    }

    public FileHandler getFile(ManualInput manualInput, InputStream inputStream, ImportLog log) {

        String stringLog = "Setting CSV reader..."
                + "\nQUOTE = " + manualInput.getQuoteChar()
                + "\nDELIMITER = " + manualInput.getDelimiterChar()
                + "\nESPACE = " + manualInput.getEscapeChar()
                + "\nLINE_SEPRATOR = " + manualInput.getLineSeparator()
                + "\nParsing CSV file..";

        importLogService.updateLogText(log,
                 null,
                 Boolean.FALSE,
                 stringLog);
        return parseCSVFile(manualInput, inputStream, log);

        /*switch(manualInput.getFileType()) {
            case CSV:
                return parseCSVFile(file);
            case XLSX:
                
                break;
        }*/
        //return new ArrayList<>();
    }

    private FileHandler parseCSVFile(ManualInput manualInput, InputStream inputStream, ImportLog log) {

        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setDelimiter(manualInput.getDelimiterChar());
        settings.getFormat().setQuote(manualInput.getQuoteChar());
        settings.getFormat().setQuoteEscape(manualInput.getEscapeChar());
        settings.getFormat().setLineSeparator(manualInput
                .getLineSeparator()
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\r", "\r")
                .replace("\\b", "\b"));

        CsvParser parser = new CsvParser(settings);
        try {
            return new FileHandler(parser.parseAll(inputStream));
        } catch (Exception ex) {
            importLogService.updateLogText(log,
                     ImportLogStatus.ERROR,
                     Boolean.TRUE,
                     "ERROR:" + ex.toString());
            Logger.getLogger(FileHandlerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new FileHandler();
    }

    

}
