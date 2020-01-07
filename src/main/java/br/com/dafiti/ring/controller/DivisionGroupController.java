/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.controller;

import br.com.dafiti.ring.model.DivisionGroup;
import br.com.dafiti.ring.service.DivisionGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author guilherme.almeida
 */
@Controller
@RequestMapping("/group")
public class DivisionGroupController {
    
    private final DivisionGroupService divisionGroupService;
    
    @Autowired
    public DivisionGroupController(DivisionGroupService divisionGroupService) {
        this.divisionGroupService = divisionGroupService;
    }
    
    
    /**
     * creates a new group
     * 
     * @param model
     * @return
     */
    @GetMapping(path = "/create")
    public String createGroup(Model model) {
        
        model.addAttribute("group", new DivisionGroup());
        
        return "group/edit";
    }
    
    /**
     * list all groups
     * 
     * @param model
     * @return
     */
    @GetMapping(path = "/list")
    public String listGroups(Model model) {
        
        model.addAttribute("groups", divisionGroupService.findAll());
        
        return "group/list";
    }
    
    /**
     * open a group in view mode
     * 
     * @param model
     * @param divisionGroup
     * @return
     */
    @GetMapping(path = "/view/{id}")
    public String viewGroup(Model model,
            @PathVariable(value = "id") DivisionGroup divisionGroup) {
        
        model.addAttribute("group", divisionGroup);
        return "group/view";
    }
    
    /**
     * open a group in edit mode
     * 
     * @param model
     * @param divisionGroup
     * @return
     */
    @GetMapping(path = "/edit/{id}")
    public String editGroup(Model model,
            @PathVariable(value = "id") DivisionGroup divisionGroup) {
        
        model.addAttribute("group", divisionGroup);
        return "group/edit";
    }
    
    /**
     * saves the group object
     * 
     * @param model
     * @param manualInputUserGroup
     * @return
     */
    @PostMapping(path = "/save")
    public String saveGroup(Model model,
            @ModelAttribute DivisionGroup divisionGroup) {
        
        DivisionGroup newGroup = divisionGroupService.save(divisionGroup);
        
        return "redirect:/group/view/" + newGroup.getId();
    }
    
}
