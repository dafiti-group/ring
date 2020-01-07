/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.model;

import br.com.dafiti.ring.option.FileType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 *
 * @author guilherme.almeida
 */
@Entity
public class ManualInput extends Tracker implements Serializable {

    private Long id;
    private String name;
    private FileType fileType;
    private Character delimiterChar;
    private Character quoteChar;
    private Character escapeChar;
    private String lineSeparator;
    private String sheetName;
    private List<Metadata> metadata;
    private Set<DivisionGroup> divisionGroups;
    private DivisionGroup originDivisionGroup;
    
    public ManualInput() {
        
    }

    public ManualInput(DivisionGroup divisionGroup) {
        this.delimiterChar = ';';
        this.quoteChar = '"';
        this.escapeChar = '\\';
        this.lineSeparator = "\\n";
        this.sheetName = "Plan1";
        
        List<Metadata> metadataList = new ArrayList<>();
        metadataList.add(new Metadata(1));
        metadataList.add(new Metadata(2));
        this.metadata = metadataList;
        this.originDivisionGroup = divisionGroup;
        this.divisionGroups = new HashSet<>(Arrays.asList(divisionGroup));
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_manual_input")
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Enumerated(EnumType.STRING)
    public FileType getFileType() {
        return this.fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public Character getDelimiterChar() {
        return delimiterChar;
    }

    public void setDelimiterChar(Character delimiterChar) {
        this.delimiterChar = delimiterChar;
    }

    public Character getQuoteChar() {
        return this.quoteChar;
    }

    public void setQuoteChar(Character quoteChar) {
        this.quoteChar = quoteChar;
    }

    public Character getEscapeChar() {
        return escapeChar;
    }

    public void setEscapeChar(Character escapeChar) {
        this.escapeChar = escapeChar;
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    @OneToMany(mappedBy = "manualInput", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    public List<Metadata> getMetadata() {
        return this.metadata;
    }

    public void setMetadata(List<Metadata> metadata) {
        this.metadata = metadata;
    }
    
    @ManyToMany
    @JoinTable(name = "manual_input_user_division_group",
            joinColumns = @JoinColumn(name = "fk_manual_input"),
            inverseJoinColumns = @JoinColumn(name = "fk_division_group"))
    public Set<DivisionGroup> getDivisionGroups() {
        return this.divisionGroups;
    }
    
    public void setDivisionGroups(Set<DivisionGroup> divisionGroups) {
        this.divisionGroups = divisionGroups;
    }
    
    @ManyToOne
    @JoinColumn(name = "fk_origin_division_group", referencedColumnName = "id_division_group")
    public DivisionGroup getOriginDivisionGroup() {
        return this.originDivisionGroup;
    }
    
    public void setOriginDivisionGroup(DivisionGroup originDivisionGroup) {
        this.originDivisionGroup = originDivisionGroup;
    }

    @Transient
    public String getInfo() {
        StringBuilder info = new StringBuilder();
        info.append("NAME = ").append(this.name);
        info.append("\nmetadata:[");
        boolean addComma = false;
        for (Metadata metadata : this.metadata) {
            if (metadata.getIsActive()) {
                if(addComma) {
                    info.append(",");
                }
                info.append("\n{")
                        .append("  field: \"").append(metadata.getFieldName()).append("\"")
                        .append(", datatype: \"").append(metadata.getDataType()).append("\"")
                        .append(", test: \"").append(metadata.getTest()).append("\"")
                        .append(", threasold: \"").append(metadata.getThreshold()).append("\"")
                        .append(", ordinal_position: ").append(metadata.getOrdinalPosition())
                        .append("}");
                addComma = true;
            }
        }
        info.append("\n]");
        return info.toString();
    }
}
