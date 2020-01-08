/*
 * Copyright (c) 2020 Dafiti Group
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
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
            @Value("${ring.json.file.type}") String jsonType,
            @Value("${ring.enable.native.data.validation}") boolean useNativeValidation) {
        super(jsonType, useNativeValidation);
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
            Logger.getLogger(FileHandlerService.class.getName()).log(Level.ALL, null, ex);
        }
        return new FileHandler();
    }

    

}
