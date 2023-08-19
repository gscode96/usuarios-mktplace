package br.com.senai.usuariosmarketplace.core.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import br.com.senai.usuariosmarketplace.core.dao.DaoUsuario;
import br.com.senai.usuariosmarketplace.core.dao.FactoryDao;
import br.com.senai.usuariosmarketplace.core.domain.Usuario;

public class UsuarioService {

	private DaoUsuario dao;

	public UsuarioService() {
		this.dao = FactoryDao.getInstance().getDaoUsuario();

	}

	public String removerAcentoDo(String nomeCompleto) {
		// metodo para remover ascento
		return Normalizer.normalize(nomeCompleto, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

	}

	public List<String> fracionar(String nomeCompleto) {
		// metodo para remover artigos
		List<String> nomeFracionado = new ArrayList<String>();
		if (nomeCompleto != null && !nomeCompleto.isBlank()) {
			String[] partesDoNome = nomeCompleto.split(" ");
			for (String parte : partesDoNome) {
				boolean isNaoContemArtigo = !parte.equalsIgnoreCase("de") && !parte.equalsIgnoreCase("e")
						&& !parte.equalsIgnoreCase("da") && !parte.equalsIgnoreCase("do")
						&& !parte.equalsIgnoreCase("dos") && !parte.equalsIgnoreCase("das");
				if (isNaoContemArtigo) {
					nomeFracionado.add(parte.toLowerCase());
				}
			}
		}
		return nomeFracionado;
	}

	public String gerarLoginPor(String nomeCompleto) {
		nomeCompleto = removerAcentoDo(nomeCompleto);
		List<String> partesDoNome = fracionar(nomeCompleto);
		String loginGerado = null;
		Usuario usuarioEncontrado = null;
		if (!partesDoNome.isEmpty()) {
			for (int i = 1; i < partesDoNome.size(); i++) {
				// i maior que zero para pular o jose
				loginGerado = partesDoNome.get(0) + "." + partesDoNome.get(i);
				// buscando o login no banco para ver se existe
				usuarioEncontrado = dao.buscarPor(loginGerado);
				if (usuarioEncontrado == null) {
					return loginGerado;
				}

			}
			// caso exista todas as combinações no banco de nome e sobrenome vai concatenar
			// com o sequencial e buscar no banco
			int proximoSequencial = 0;
			String loginDisponivel = null;
			// vai rodar ate encontrar combinação que nao exista
			while (usuarioEncontrado != null) {
				loginDisponivel = loginGerado + ++proximoSequencial;
				usuarioEncontrado = dao.buscarPor(loginDisponivel);

			}
			loginGerado = loginDisponivel;
		}
		return loginGerado;
	}
	
	public String gerarHashDa(String senha) {
		//gerando hash da senha
		return new DigestUtils(MessageDigestAlgorithms.MD5).digestAsHex(senha);
		
	}
}
