package br.com.seatecnologia.clientesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Ponto de entrada da aplicação.
 */
@SpringBootApplication
@EnableFeignClients
public class ClientesApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientesApiApplication.class, args);
    }
}