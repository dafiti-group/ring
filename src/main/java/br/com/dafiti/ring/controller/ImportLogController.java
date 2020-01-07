/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.controller;

import br.com.dafiti.ring.model.ImportLog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author guilherme.almeida
 */
@Controller
@RequestMapping(path="/log")
public class ImportLogController {
    
    /**
     * show log text of the execution of a process
     * 
     * @param model
     * @param importlog
     * @return
     */
    @GetMapping(path = "/import/{id}")
    public String getLog(Model model,
            @PathVariable(value = "id") ImportLog importlog) {
        
        model.addAttribute("log", importlog);
        return "importlog/view";
    }
}
