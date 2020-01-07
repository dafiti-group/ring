/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.module;

import br.com.dafiti.ring.mask.ListObjectGenerator;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/**
 *
 * @author guilherme.almeida
 */
public class MongoDocumentsGenerator implements ListObjectGenerator {
    
    List<Document> documents;
    
    public MongoDocumentsGenerator() {
        documents = new ArrayList();
    }

    @Override
    public void add(String json) throws Exception {
        documents.add(Document.parse(json));
    }

    @Override
    public List get() {
        return documents;
    }
    
}
