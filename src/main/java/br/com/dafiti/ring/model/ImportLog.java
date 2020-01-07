/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.model;

import br.com.dafiti.ring.option.ImportLogStatus;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author guilherme.almeida
 */
@Entity
public class ImportLog extends Tracker implements Serializable {
    
    private Long id;
    private String text;
    private ImportLogStatus status;
    private ManualInput manualInput;
    private Boolean finalized;
    private String createdBy;
    
    public ImportLog() {
        this.finalized = false;
        this.status = ImportLogStatus.RUNNING;
        this.text = "";
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_import_log")
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(columnDefinition = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ImportLogStatus getStatus() {
        return status;
    }

    public void setStatus(ImportLogStatus status) {
        this.status = status;
    }

    @ManyToOne
    @JoinColumn(name = "fk_manual_input")
    public ManualInput getManualInput() {
        return manualInput;
    }
    
    public void setManualInput(ManualInput manualInput) {
        this.manualInput = manualInput;
    }

    public Boolean getFinalized() {
        return finalized;
    }

    public void setFinalized(Boolean finalized) {
        this.finalized = finalized;
    }
    
    public String getCreatedBy() {
        return this.createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
}
