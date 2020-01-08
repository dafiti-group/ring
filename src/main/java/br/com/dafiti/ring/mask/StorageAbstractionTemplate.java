/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.mask;

import br.com.dafiti.ring.model.FileHandler;
import br.com.dafiti.ring.model.ManualInput;
import br.com.dafiti.ring.rest.ApiFilterDTO;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author guilherme.almeida
 */
public abstract class StorageAbstractionTemplate {

    public String tmpFilePath = System.getProperty("user.home") + "/tmp_file/";

    public abstract void createOrUpdateManualInput(ManualInput manualInput, boolean recreate);

    public abstract void saveFile(ManualInput manualInput, JSONDocument JSONDoc, String LoadDateForPartition, FileHandler fileHandler) throws Exception;

    public abstract void deleteManualInput(ManualInput manualInput);

    public abstract File extractCSV(ManualInput manualInput, ApiFilterDTO filter) throws IOException;
}
