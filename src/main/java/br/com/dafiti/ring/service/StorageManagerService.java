/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.service;

import br.com.dafiti.ring.module.AWSS3StorageModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author guilherme.almeida
 */
@Service
public class StorageManagerService extends AWSS3StorageModule {
    
    /*@Autowired
    public StorageManagerService(@Value("${mongo.datasource.uri}") String datasourceUri) throws NoSuchAlgorithmException {
        super(datasourceUri);
    }*/
    

    @Autowired
    public StorageManagerService(@Value("${aws.s3.bucket.region}") String clientRegion,
            @Value("${aws.s3.bucket.name}") String bucketName,
            @Value("${aws.s3.bucket.key}") String keyName,
            @Value("${aws.s3.access.key}") String accessKey,
            @Value("${aws.s3.secret.key}") String secretKey) {
        super(clientRegion, bucketName, keyName, accessKey, secretKey);
    }
    
}
