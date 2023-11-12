package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
public class FunkoSpringApplication implements CommandLineRunner {

    public static void main(String[] args) {

        SpringApplication.run(FunkoSpringApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Aquí podemos ejecutar código al arrancar la aplicación
        System.out.println("🟢 Servidor arrancado 🚀");
    }
}
