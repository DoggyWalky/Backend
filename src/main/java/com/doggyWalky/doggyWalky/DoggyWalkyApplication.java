package com.doggyWalky.doggyWalky;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class DoggyWalkyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoggyWalkyApplication.class, args);
	}

}
