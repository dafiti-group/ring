/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.service;

import br.com.dafiti.ring.model.DivisionGroup;
import br.com.dafiti.ring.repository.DivisionGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author guilherme.almeida
 */
@Service
public class DivisionGroupService {
    
    private final DivisionGroupRepository divisionGroupRepository;
    
    @Autowired
    public DivisionGroupService(DivisionGroupRepository divisionGroupRepository) {
        this.divisionGroupRepository = divisionGroupRepository;
    }
    
    public DivisionGroup save(DivisionGroup divisionGroup) {
        return this.divisionGroupRepository.save(divisionGroup);
    }
    
    public DivisionGroup findByName(String name) {
        return this.divisionGroupRepository.findByName(name);
    }
    
    public DivisionGroup findById(Long id) {
        return this.divisionGroupRepository.findById(id).get();
    }
    
    public DivisionGroup createIfNotExists(String name) {
        DivisionGroup divisionGroup = this.findByName(name);

        if (divisionGroup == null) {
            DivisionGroup newDivisionGroup = new DivisionGroup();
            newDivisionGroup.setName(name);
            divisionGroup = this.save(newDivisionGroup);
        }

        return divisionGroup;
    }
    
    public Iterable<DivisionGroup> findAll() {
        return divisionGroupRepository.findAll();
    }
}
