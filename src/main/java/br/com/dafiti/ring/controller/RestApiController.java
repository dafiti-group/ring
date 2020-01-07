/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.controller;

import br.com.dafiti.ring.rest.ApiFilterDTO;
import br.com.dafiti.ring.model.ManualInput;
import br.com.dafiti.ring.model.User;
import br.com.dafiti.ring.rest.ApiAuthentication;
import br.com.dafiti.ring.service.ManualInputService;
import br.com.dafiti.ring.service.StorageManagerService;
import br.com.dafiti.ring.service.TokenService;
import br.com.dafiti.ring.service.UserService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author guilherme.almeida
 */
@RestController
@RequestMapping("/api")
public class RestApiController {

    private final ManualInputService manualInputService;
    private final StorageManagerService storageManagerService;
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    @Autowired
    public RestApiController(AuthenticationManager authManager,
            StorageManagerService storageManagerService,
            ManualInputService manualInputService,
            UserService userService,
            TokenService tokenService) {

        this.manualInputService = manualInputService;
        this.storageManagerService = storageManagerService;
        this.userService = userService;
        this.authManager = authManager;
        this.tokenService = tokenService;
    }

    /**
     * validate user and return a token
     * user must have role LORD
     * 
     * @param apiAuth
     * @return
     */
    @RequestMapping(value = "/auth", method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Credentials> Authenticate(@RequestBody ApiAuthentication apiAuth) {
        
        User user = userService.findByUsername(apiAuth.getUsername());
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }
        Long hasPermission = user.getRoles().stream()
                .filter(f -> f.getName().equals("LORD"))
                .count();
        if(hasPermission == 0) {
            return ResponseEntity.badRequest().build();
        }

        UsernamePasswordAuthenticationToken loggedIn = new UsernamePasswordAuthenticationToken(apiAuth.getUsername(), apiAuth.getPassword());

        try {
            Authentication authetication = authManager.authenticate(loggedIn);
            
            String token = tokenService.generateToken(authetication);
                    
            return ResponseEntity.ok(new Credentials(token));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     *
     * @param filter
     * @return
     */
    @GetMapping(path = "/get")
    public ResponseEntity DowloadFile(ApiFilterDTO filter) {

        InputStreamResource resource = null;
        File file = null;

        try {

            if (filter.ok()) {
                ManualInput manualInputObj = manualInputService.FindByName(filter.getManualInput());
                file = storageManagerService.extractCSV(manualInputObj, filter);
                
            } else {
                return ResponseEntity.notFound().build();
            }
            
            resource = new InputStreamResource(new FileInputStream(file));

        } catch (IOException ex) {
            Logger.getLogger(RestApiController.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }
    
    
    
    private class Credentials {
        
        private String token;
        private String authenticationType;
        
        public Credentials(String token) {
            this.token = token;
            this.authenticationType = "Bearer";
        }
        
        public String getToken() {
            return this.token;
        }
        
        public void setToken(String token) {
            this.token = token;
        }
        
        public String getAuthenticationType() {
            return this.authenticationType;
        }
        
        public void setAuthenticationType(String authenticationType) {
            this.authenticationType = authenticationType;
        }
    }
    
}
