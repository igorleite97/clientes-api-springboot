package br.com.seatecnologia.clientesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Ponto de entrada da aplicação.
 *
 * @EnableFeignClients instrui o Spring a escanear as interfaces
 * anotadas com @FeignClient (como o ViaCepClient que vamos criar).
 * Sem essa anotação o Feign simplesmente não funciona — erro silencioso
 * que só aparece quando você tenta injetar o client em algum service.
 */
@SpringBootApplication
@EnableFeignClients
public class ClientesApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientesApiApplication.class, args);
    }
}