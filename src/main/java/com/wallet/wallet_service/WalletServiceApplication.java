package com.wallet.wallet_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WalletServiceApplication {

	public static void main(String[] args) {
		System.out.println(System.getenv("JWT_SECRET"));
		System.out.println("Starting Wallet Service Application...");
		SpringApplication.run(WalletServiceApplication.class, args);
	}

}
