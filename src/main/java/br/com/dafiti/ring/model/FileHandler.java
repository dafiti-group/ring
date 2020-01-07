/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.model;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author guilherme.almeida
 */
@Component
public class FileHandler {
    
    private String[] header;
    private List<String[]> data;
    
    public FileHandler(){
        
    }
    
    public FileHandler(List<String[]> data) {
        this.header = data.remove(0);
        this.data = data;
        
        for(int i = 0; i < this.header.length; i++) {
            this.header[i] = this.header[i].trim().toLowerCase();
        }
    }
    
    public int getColumnCount() {
        return header.length;
    }
    
    public List<String[]> getData() {
        return this.data;
    }
    
    public String[] getHeader() {
        return this.header;
    }
}
