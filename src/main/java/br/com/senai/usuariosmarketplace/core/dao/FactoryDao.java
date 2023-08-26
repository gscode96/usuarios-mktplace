package br.com.senai.usuariosmarketplace.core.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import br.com.senai.usuariosmarketplace.core.dao.postgres.DaoPostgresUsuario;

@Service //Instancia somente uma vez, criando um "singleton"
public class FactoryDao {

	@Bean
	public DaoUsuario getDaoUsuario() {
		return new DaoPostgresUsuario();
	}

}
