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
