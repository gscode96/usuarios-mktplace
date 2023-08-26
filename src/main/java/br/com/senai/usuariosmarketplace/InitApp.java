package br.com.senai.usuariosmarketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import br.com.senai.usuariosmarketplace.core.service.UsuarioService;

@SpringBootApplication // configura para iniciar no springboot
public class InitApp {
	
	@Autowired // instancia o objeto
	private UsuarioService service;


	public static void main(String[] args) {
		SpringApplication.run(InitApp.class, args);

	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			System.out.println(service.buscarPor("alan.duarte").getNomeCompleto());
		};

	}

}
