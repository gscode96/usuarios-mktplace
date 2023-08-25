package br.com.senai.usuariosmarketplace.core.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import br.com.senai.usuariosmarketplace.core.dao.DaoUsuario;
import br.com.senai.usuariosmarketplace.core.dao.FactoryDao;
import br.com.senai.usuariosmarketplace.core.domain.Usuario;

public class UsuarioService {

	private DaoUsuario dao;

	public UsuarioService() {
		this.dao = FactoryDao.getInstance().getDaoUsuario();

	}

	public Usuario criarUsuarioPor(String nomeCompleto, String senha) {

		this.validar(nomeCompleto, senha);
		String login = gerarLoginPor(nomeCompleto);
		String senhaCriptografada = gerarHashDa(senha);
		Usuario novoUsuario = new Usuario(login, senhaCriptografada, nomeCompleto);
		this.dao.inserir(novoUsuario);
		Usuario usuarioSalvo = dao.buscarPor(login);
		return usuarioSalvo;
	}

	public Usuario atualizarPor(String login, String nomeCompleto, String senhaAntiga, String senhaNova) {
		// metodo para atualizar o cadastro do usuario
		Preconditions.checkArgument(!Strings.isNullOrEmpty(login), "O Login é obrigatório para a atualização!");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(senhaAntiga),
				"A senha antiga é obrigatoria para a atualização!");
		this.validar(nomeCompleto, senhaNova);
		Usuario usuarioSalvo = dao.buscarPor(login);
		Preconditions.checkNotNull(usuarioSalvo, "Não foi encontrado usuário vinculado ao login informado");
		String senhaAntigaCriptografada = gerarHashDa(senhaAntiga);
		boolean isSenhaValida = senhaAntigaCriptografada.equals(usuarioSalvo.getSenha());
		Preconditions.checkArgument(isSenhaValida, "A senha antiga não confere!");
		Preconditions.checkArgument(!senhaAntiga.equals(senhaNova), "A senha nova não pode ser igual a senha anterior");
		String senhaNovaCriptografada = gerarHashDa(senhaNova);
		Usuario usuarioAlterado = new Usuario(login, senhaNovaCriptografada, nomeCompleto);
		this.dao.alterar(usuarioAlterado);
		usuarioAlterado = dao.buscarPor(login);
		return usuarioAlterado;
	}

	public Usuario buscarPor(String login) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(login), "O login é obrigatório!");
		Usuario usuarioEncontrado = dao.buscarPor(login);
		Preconditions.checkNotNull(usuarioEncontrado, "Não foi encontrado usuário vinculado ao login informado!");
		return usuarioEncontrado;

	}

	private String removerAcentoDo(String nomeCompleto) {
		// metodo para remover ascento
		return Normalizer.normalize(nomeCompleto, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

	}

	private List<String> fracionar(String nomeCompleto) {
		// metodo para remover artigos
		List<String> nomeFracionado = new ArrayList<String>();
		if (!Strings.isNullOrEmpty(nomeCompleto)) {
			// removendo espaço em branco
			nomeCompleto = nomeCompleto.trim();
			String[] partesDoNome = nomeCompleto.split(" ");
			for (String parte : partesDoNome) {
				boolean isNaoContemArtigo = !parte.equalsIgnoreCase("de") && !parte.equalsIgnoreCase("e")
						&& !parte.equalsIgnoreCase("da") && !parte.equalsIgnoreCase("do")
						&& !parte.equalsIgnoreCase("dos") && !parte.equalsIgnoreCase("das");
				if (isNaoContemArtigo) {
					// colocando em minusculo
					nomeFracionado.add(parte.toLowerCase());
				}
			}
		}
		return nomeFracionado;
	}

	private String gerarLoginPor(String nomeCompleto) {
		nomeCompleto = removerAcentoDo(nomeCompleto);
		List<String> partesDoNome = fracionar(nomeCompleto);
		String loginGerado = null;
		Usuario usuarioEncontrado = null;
		if (!partesDoNome.isEmpty()) {
			for (int i = 1; i < partesDoNome.size(); i++) {
				// i maior que zero para pular o "jose"
				loginGerado = partesDoNome.get(0) + "." + partesDoNome.get(i);

				if (loginGerado.length() > 40) {
					loginGerado = loginGerado.substring(0, 40);
				}
				// buscando o login no banco para ver se existe
				usuarioEncontrado = dao.buscarPor(loginGerado);
				if (usuarioEncontrado == null) {

					return loginGerado;
				}

			}
			// caso exista todas as combinações no banco de nome e sobrenome, ira concatenar
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

	private String gerarHashDa(String senha) {
		// gerando hash da senha
		return new DigestUtils(MessageDigestAlgorithms.SHA3_256).digestAsHex(senha);

	}

	@SuppressWarnings("deprecation")
	private void validar(String senha) {

		// Utilizado guava para verificar se a string é vazia e lançar excessao
		// Utilizado guava para verificar se contem letra buscando de a até z e
		// se for maior que zero receber true e tambem verificar se contem numero e
		// receber true tambem
		boolean isSenhaValida = !Strings.isNullOrEmpty(senha) && senha.length() >= 6 && senha.length() <= 15;
		Preconditions.checkArgument(isSenhaValida, "A senha é obrigatoria e deve conter entre 6 e 15 caracteres");
		boolean isContemLetra = CharMatcher.inRange('a', 'z').countIn(senha.toLowerCase()) > 0;
		boolean isContemNumero = CharMatcher.inRange('0', '9').countIn(senha) > 0;
		boolean isCaracacterInvalido = !CharMatcher.javaLetterOrDigit().matchesAllOf(senha);
		Preconditions.checkArgument(isContemLetra && isContemNumero && !isCaracacterInvalido,
				"A senha deve conter letras e numeros e não deve possuir espaços vazios.");

		/*
		 * Antes foi tilizado for para percorrer a string e verificar se contem numero e
		 * letra para caso nao conter lançar excessao
		 * 
		 * for (int i = 0; i < senha.length(); i++) { // verifica se tem numero e letra
		 * na string passada
		 * 
		 * if (Character.isDigit(senha.charAt(i))) { isContemNumero = true; } else if
		 * (Character.isAlphabetic(senha.charAt(i))) { isContemLetra = true; } else {
		 * isCaracacterInvalido = true; }
		 * 
		 * }
		 */
		// caso nao contenha lança excessao
	}

	private void validar(String nomeCompleto, String senha) {
		List<String> partesDoNome = fracionar(nomeCompleto);
		boolean isNomeCompleto = partesDoNome.size() > 1;
		boolean isNomeValido = !Strings.isNullOrEmpty(nomeCompleto) && isNomeCompleto && nomeCompleto.length() >= 5
				&& nomeCompleto.length() <= 120;
		Preconditions.checkArgument(isNomeValido,
				"O nome é obrigatorio e deve conter entre 5 e 120 caracteres e conter sobrenome também");
		this.validar(senha);

	}

}
