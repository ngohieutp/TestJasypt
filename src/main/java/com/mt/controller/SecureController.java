package com.mt.controller;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("secure")
public class SecureController {

    @Autowired
    Environment environment;

    @Value("${application.encrypted}")
    private String property;

    public String getProperty() {
        return property;
    }

    @Qualifier("encryptorBean")
    @Autowired
    StringEncryptor encryptor;

    public String getPasswordUsingEnvironment(Environment environment) {
        return environment.getProperty("application.encrypted");
    }

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String showValue() throws Exception {
        return String.format("%s-%s", property, getPasswordUsingEnvironment(environment));
    }

    @RequestMapping(value = "/encrypt", method = RequestMethod.GET)
    public String encrypt(@RequestParam String value) throws Exception {
        return encryptor.encrypt(value);
    }

    @RequestMapping(value = "/decrypt", method = RequestMethod.GET)
    public String decrypt(@RequestParam String value) throws Exception {
        return encryptor.decrypt(value);
    }
}
