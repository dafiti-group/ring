/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.service;

import br.com.dafiti.ring.model.FileHandler;
import br.com.dafiti.ring.model.ImportLog;
import br.com.dafiti.ring.model.ManualInput;
import br.com.dafiti.ring.option.ImportLogStatus;
import br.com.dafiti.ring.repository.ManualInputRepository;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author guilherme.almeida
 */
@Service
public class ManualInputService {

    private final ManualInputRepository manualInputRepository;
    private final FileHandlerService fileHandlerService;
    private final StorageManagerService storageManagerService;
    private final ImportLogService importLogService;

    @Autowired
    public ManualInputService(ManualInputRepository manualInputRepository,
            FileHandlerService fileHandlerService,
            StorageManagerService storageManagerService,
            ImportLogService importLogService) {
        this.manualInputRepository = manualInputRepository;
        this.fileHandlerService = fileHandlerService;
        this.importLogService = importLogService;
        this.storageManagerService = storageManagerService;
    }

    /**
     * saves a manual input
     *
     * @param manualInput
     * @return
     */
    public ManualInput save(ManualInput manualInput) {

        manualInput.getMetadata()
                .stream()
                .forEach(metadata -> {
                    metadata.setManualInput(manualInput);
                });

        return manualInputRepository.save(manualInput);
    }

    /**
     * return a manual input filtering by name
     *
     * @param name
     * @return
     */
    public ManualInput FindByName(String name) {
        return manualInputRepository.findByName(name);
    }

    /**
     *
     * @return
     */
    public Iterable<ManualInput> findAll() {
        return manualInputRepository.findAll();
    }

    /**
     * delete a manual input
     *
     * @param manualInput
     */
    public void delete(ManualInput manualInput) {
        manualInputRepository.delete(manualInput);
    }

    /**
     * return a manual input filtering by id
     *
     * @param id
     * @return
     */
    public ManualInput findById(Long id) {
        return manualInputRepository.findById(id).get();
    }

    /**
     * process the file to get the content and insert into mongoDB collection
     * and MySql table
     *
     * @param manualInput
     * @param inputStream
     * @param log
     */
    @Async
    public void process(ManualInput manualInput, InputStream inputStream, ImportLog log) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime logCreatedAt = LocalDateTime.parse(log.getCreatedAt().toString().subSequence(0, 19), dateTimeFormatter);//now();
        String loadDate = dateTimeFormatter.format(logCreatedAt);

        try {
            FileHandler file = fileHandlerService.getFile(manualInput, inputStream, log);
            if (log.getFinalized()) {
                return;
            }
            storageManagerService.saveFile(manualInput, fileHandlerService, loadDate, file);
            if (log.getFinalized()) {
                return;
            }

            importLogService.updateLogText(log,
                     ImportLogStatus.SUCCESS,
                     true,
                     "PROCESS ENDED!");
        } catch (Exception e) {
            e.printStackTrace();
            importLogService.updateLogText(log,
                     ImportLogStatus.ERROR,
                     true,
                     "ERROR:" + e.toString() + "\nPROCESS ENDED!");
        }

    }

}
