/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.repository;

import br.com.dafiti.ring.model.Metadata;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author guilherme.almeida
 */
@Repository
public interface MetadataRepository extends CrudRepository<Metadata, Long> {
    
    
}
