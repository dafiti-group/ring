/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.rest;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author guilherme.almeida
 */
public class ApiFilterDTO {
    
    private String manualInput;
    private String operator;
    private String loadDate;

    public String getManualInput() {
        return manualInput;
    }

    public void setManualInput(String manualInput) {
        this.manualInput = manualInput;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(String loadDate) {
        this.loadDate = loadDate;
    }
    
    public boolean ok() {
        
        if(this.manualInput == null || this.manualInput.isEmpty()) {
            return false;
        }
        
        List<String> operators = Arrays.asList("$eq", "$ne", "$lt", "$lte", "$gt", "$gte");
        
        if(this.operator == null || this.operator.isEmpty()) {
            this.operator = "gte";
        }
        
        if(!operators.contains("$" + this.operator)) {
            return false;
        }
        
        if(this.loadDate == null || this.loadDate.isEmpty()) {
            this.loadDate = "2000-01-01 00:00:00";
        }
        
        String regex  = "^[0-9]{4}-[0-9]{2}-[0-9]{1,2} [0-9]{2}:[0-9]{2}:[0-9]{2}$";
        
        Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(loadDate);

                if (!matcher.find()) {
                    return false;
                }
        
        return true;
    }
    
    public String getFilter() {
        return "'{ \"load_date\": { \"$" + operator + "\" :\"" + loadDate + "\"}}'";
    }
   
}
