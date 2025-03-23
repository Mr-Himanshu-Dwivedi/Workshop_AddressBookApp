package com.app.AddressBook_Workshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootApplication
public class AddressBookWorkshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(AddressBookWorkshopApplication.class, args);
	}

}