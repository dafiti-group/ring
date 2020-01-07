/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.mask;

import java.util.List;

/**
 *
 * @author guilherme.almeida
 * @param <T>
 */
public interface ListObjectGenerator {
    
    public void add(String json) throws Exception;
    
    public List<?> get();
    
}
