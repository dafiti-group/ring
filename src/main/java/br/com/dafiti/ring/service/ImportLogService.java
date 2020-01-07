/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.service;

import br.com.dafiti.ring.model.ImportLog;
import br.com.dafiti.ring.model.ManualInput;
import br.com.dafiti.ring.option.ImportLogStatus;
import br.com.dafiti.ring.repository.ImportLogRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author guilherme.almeida
 */
@Service
public class ImportLogService {
    
    private final ImportLogRepository importLogRepository;
    
    @Autowired
    public ImportLogService(ImportLogRepository importLogRepository) {
        this.importLogRepository = importLogRepository;
    }
    
    public ImportLog save(ImportLog importLog) {
        return this.importLogRepository.save(importLog);
    }
    
    public List<ImportLog> findByManualInputOrderByCreatedAtDesc(ManualInput manualInput, Pageable pageable) {
        return this.importLogRepository.findByManualInputOrderByCreatedAtDesc(manualInput, pageable);
    }
    
    public ImportLog updateLogText(ImportLog importLog, ImportLogStatus status, boolean finilized, String textToAppend) {
        
        if(status != null) {
            importLog.setStatus(status);
        }
        importLog.setFinalized(finilized);
        
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String log_date_time = dateTimeFormatter.format(now);
        
        String currentLog = importLog.getText();
        String newLog = currentLog + "\n"
                + log_date_time + " - "
                + textToAppend.replace("\n", "\n" + log_date_time + " - ");
        
        importLog.setText(newLog);
        
        return this.save(importLog);
    }
}
