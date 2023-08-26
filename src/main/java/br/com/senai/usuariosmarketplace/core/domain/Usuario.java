package br.com.senai.usuariosmarketplace.core.domain;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Component //Torna a classe gerenciada pelo spring
@AllArgsConstructor // Lombok construtor com parametro
@NoArgsConstructor // Lombok construtor sem parametro
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Lombok hashcode and equals
@ToString(onlyExplicitlyIncluded = true)
@Data // lombok get e set das variaveis
public class Usuario {

	@EqualsAndHashCode.Include
	private String login;

	private String senha;

	@ToString.Include
	private String nomeCompleto;

}
