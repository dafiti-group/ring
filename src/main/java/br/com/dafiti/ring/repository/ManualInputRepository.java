/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.repository;

import br.com.dafiti.ring.model.ManualInput;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author guilherme.almeida
 */

public interface ManualInputRepository extends CrudRepository<ManualInput, Long> {
    
    ManualInput findByName(String name);
    
    Optional<ManualInput> findById(Long id);

    public List<ManualInput> findByNameContaining(String search);
    
}
