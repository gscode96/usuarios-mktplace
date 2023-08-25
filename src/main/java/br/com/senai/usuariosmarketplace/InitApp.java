package br.com.senai.usuariosmarketplace;

import br.com.senai.usuariosmarketplace.core.domain.Usuario;

public class InitApp {

	public static void main(String[] args) {
	
		Usuario usuario = new Usuario("Jose.silva", "Jose da silva", "jose123");
		System.out.println(usuario.getLogin());
		
	}

}
