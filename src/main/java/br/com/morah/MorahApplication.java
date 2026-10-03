package br.com.morah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação.
 *
 * @SpringBootApplication liga três coisas de uma vez:
 *  - a configuração automática (Spring monta Tomcat, JPA, Jackson... sozinho);
 *  - o escaneamento de componentes: ele procura @Service, @RestController etc. neste pacote
 *    (br.com.morah) e em todos os subpacotes. Por isso a classe precisa ficar na raiz.
 */
@SpringBootApplication
public class MorahApplication {

    public static void main(String[] args) {
        SpringApplication.run(MorahApplication.class, args);
    }
}
