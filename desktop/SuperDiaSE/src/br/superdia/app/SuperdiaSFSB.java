package br.superdia.app;

import static javax.swing.JOptionPane.CLOSED_OPTION;
import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showOptionDialog;

import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.validator.routines.CreditCardValidator;

import br.superdia.webservice.ClientService;
import br.superdia.webservice.ClientServiceService;
import br.superdia.webservice.ItemVenda;
import br.superdia.webservice.Produto;
import br.superdia.webservice.UserService;
import br.superdia.webservice.UserServiceService;
import br.superdia.webservice.Usuario;

public class SuperdiaSFSB {

	private final String NOME_PROGRAMA = "Caixa Super Dia";
	private final String ADICONA_PRODUTO = "Adicionar Produto";
	private final String LISTA_PRODUTO = "Lista Produto";
	private final String LISTA_PRODUTO_CAIXA = "Lista Produto Caixa";
	private final String REMOVE_PRODUTO = "Remove Produto";
	private final String FINALIZA_COMPRA = "Finaliza Produto";
	private final String LOGIN = "Login";

	private ClientService client;
	private Produto produto;
	private Usuario usuario;
	private Produto permitido;
	private Integer codigo;

	public SuperdiaSFSB() {
		produto = new Produto();
		usuario = new Usuario();
		try {
			criaConexao();
			login();
			if (usuario == null)
				msgErro("Usuário Inválido", NOME_PROGRAMA + "-" + FINALIZA_COMPRA);
			else
				menu();
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void main(String[] args) {
		new SuperdiaSFSB();
	}

	/**
	 * Obtem conexão com webService disponibilizado pelo SuperDiaEAR
	 * Com todas as funções do Carrinho.
	 * 
	  	client.addProdutoCarrinho(Long id);
		client.cleanCarrinho();

		client.getCarrinho();
		client.getProdutos();
		client.removeProdutoCarrinho(Long id);

		Ainda não esta funcionando... Irei Criar o WebService para 
		utilizar a questões do usuario.
		client.endsBuy(Usuario usuario);
	 */
	private void criaConexao(){
		ClientServiceService service = new ClientServiceService();
		client = service.getClientServicePort();
	}

	private void menu(){

		int opcao;
		String opcoes[] = {"Adicionar", "Listar Caixa", "Remover", "Finalizar Compra", "Sair"};

		do {
			opcao = showOptionDialog(null, "Escolha um comando abaixo.", NOME_PROGRAMA, DEFAULT_OPTION, QUESTION_MESSAGE, null, opcoes, opcoes[0]);

			if (opcao != CLOSED_OPTION && opcao != 4) {

				if (opcao == 0)
					adiciona();
				else 
					if (client.getCarrinho().isEmpty()) {
						msgInfo("O caixa está vazio", NOME_PROGRAMA + LISTA_PRODUTO_CAIXA);
					}
					else 
						switch(opcao) {
						case 1:
							listaCaixa();
							break;
						case 2: 
							remove();
							break;
						case 3: 
							finalizaCompra(usuario); 
							break;
						}
			}
			else  
				login();
		}while(opcao != CLOSED_OPTION || opcao==4);
		System.out.println("Teste");
	}

	public void login() {
		UserServiceService userServiceService = new UserServiceService();
		UserService userService = userServiceService.getUserServicePort();

		do {
			String login = lerString("Login: ", "Você deve fornecer o login", NOME_PROGRAMA + "-" + LOGIN, false);
			if (login == null) System.exit(0);

			usuario.setUsuario(login);

			String senha = lerString("Senha: ", "Você deve fornecer a senha", NOME_PROGRAMA + "-" + LOGIN, false);

			usuario.setSenha(senha);

			usuario = userService.obtemUsuario(usuario);

			if (usuario == null) return;

			if (!(usuario.getPerfil().equalsIgnoreCase(Perfil.ADMINISTRADOR.getPerfil()) || usuario.getPerfil().equalsIgnoreCase(Perfil.CAIXA.getPerfil())))
				msgErro(usuario.getUsuario() + " é do perfil: " + usuario.getPerfil() + " - não tem acesso ao Caixa!", NOME_PROGRAMA + "-" + LOGIN);
		}while(!(usuario.getPerfil().equalsIgnoreCase(Perfil.ADMINISTRADOR.getPerfil()) || usuario.getPerfil().equalsIgnoreCase(Perfil.CAIXA.getPerfil())));
	}

	public Produto adiciona() {
		if (client.getProdutos().isEmpty()) {
			msgInfo("Nenhum produto cadastrado!!!", NOME_PROGRAMA + LISTA_PRODUTO);
		}
		else {
			listaProduto();

			List<Produto> produtos = client.getProdutos();


			codigo = lerNumeroInteiro("Informe o Código do Produto que deseja adicionar ao carrinho: ", 
					"Você deve fornecer o produto a ser adicionado", NOME_PROGRAMA + "-" + 
							ADICONA_PRODUTO, false);
			
			if (codigo == null) return null;
			
			Integer quantidade = lerNumeroInteiro("Informe a Quantidade de Produto que deseja adicionar ao carrinho: ", 
					"Você deve A quantidade de produto a ser adicionado", NOME_PROGRAMA + "-" + 
							ADICONA_PRODUTO, false);

			if (quantidade == null) return null;

			permitido = null;
			produtos.forEach(p -> {if(p.getId() == codigo.longValue()) permitido = p;});

			if (permitido != null) {
				int op = showConfirmDialog(null, String.format("Código: %d\nNome: %s\nDescrição: %s\nPreço: %1.2f\nVendido Por: %s\nEstoque Mínimo: %d\n"
						+ "Quantidade em Estoque: %d\n\nDeseja adicionar este produto?", permitido.getId(), 
						permitido.getNome(), permitido.getDescricao(), permitido.getPreco(), 
						permitido.getVendidoPor(), permitido.getEstoqueMinimo(), permitido.getQuantidadeEstoque()),
						NOME_PROGRAMA + "-" + ADICONA_PRODUTO, YES_NO_OPTION, QUESTION_MESSAGE);
				if(op == YES_OPTION) {
					ItemVenda itemVenda = new ItemVenda();
					itemVenda.setProduto(permitido);
					itemVenda.setQuantidade(quantidade.longValue());
					client.addProdutoCarrinho(itemVenda);
					msgInfo("Produto adicionado com sucesso!", NOME_PROGRAMA + "-" + ADICONA_PRODUTO);
				}
				else
					msgInfo("Operação Cancelada", NOME_PROGRAMA + "-" + ADICONA_PRODUTO);
			}else
				msgInfo("Código não registrado...", NOME_PROGRAMA + "-" + ADICONA_PRODUTO);
		}
		return produto;

	}

	public void listaCaixa() {
		List<ItemVenda> itemVendas = client.getCarrinho();
		String lista = "";
		JTextArea listaJT = new JTextArea(10, 50);

		for (int i = 0; i < itemVendas.size(); i++) {
			lista += "Código: " + itemVendas.get(i).getProduto().getId() + "\n";
			lista += "Nome: " + itemVendas.get(i).getProduto().getNome() + "\n";
			lista += "Descrição: " + itemVendas.get(i).getProduto().getDescricao() + "\n";
			lista += "Preço: R$" + itemVendas.get(i).getProduto().getPreco() + "\n";
			lista += "Vendido por: " + itemVendas.get(i).getProduto().getVendidoPor() + "\n";
			lista += "Estoque Mínimo: " + itemVendas.get(i).getProduto().getEstoqueMinimo() + "\n";
			lista += "Quantidade em Estoque: " + itemVendas.get(i).getProduto().getQuantidadeEstoque() + "\n\n\n";
		}
		listaJT.setText(lista);
		msgInfo(new JScrollPane(listaJT), NOME_PROGRAMA + "-" + LISTA_PRODUTO_CAIXA);
	}

	public void listaProduto() {
		List<Produto> produtos = client.getProdutos();
		String lista = "";
		JTextArea listaJT = new JTextArea(10, 50);

		for (int i = 0; i < produtos.size(); i++) {
			lista += "Código: " + produtos.get(i).getId() + "\n";
			lista += "Nome: " + produtos.get(i).getNome() + "\n";
			lista += "Descrição: " + produtos.get(i).getDescricao() + "\n";
			lista += "Preço: R$" + produtos.get(i).getPreco() + "\n";
			lista += "Vendido por: " + produtos.get(i).getVendidoPor() + "\n";
			lista += "Estoque Mínimo: " + produtos.get(i).getEstoqueMinimo() + "\n";
			lista += "Quantidade em Estoque: " + produtos.get(i).getQuantidadeEstoque() + "\n\n\n";
		}
		listaJT.setText(lista);
		msgInfo(new JScrollPane(listaJT), NOME_PROGRAMA + "-" + LISTA_PRODUTO);
	}

	public void remove(){
		listaCaixa();

		List<ItemVenda> itemVendas = client.getCarrinho();

		codigo = lerNumeroInteiro("Informe o Código do Produto que deseja remover do carrinho: ", 
				"Você deve fornecer o produto a ser adicionado", NOME_PROGRAMA + "-" + 
						REMOVE_PRODUTO, false);
		
		if(codigo == null) return;

		permitido = null;
		itemVendas.forEach(p -> {if(p.getProduto().getId() == codigo.longValue()) permitido = p.getProduto();});

		if (permitido != null) {
			int op = showConfirmDialog(null, String.format("Código: %d\nNome: %s\nDescrição: %s\nPreço: %1.2f\nVendido Por: %s\nEstoque Mínimo: %d\n"
					+ "Quantidade em Estoque: %d\n\nDeseja remover este produto?", permitido.getId(), 
					permitido.getNome(), permitido.getDescricao(), permitido.getPreco(), 
					permitido.getVendidoPor(), permitido.getEstoqueMinimo(), permitido.getQuantidadeEstoque()),
					NOME_PROGRAMA + "-" + REMOVE_PRODUTO, YES_NO_OPTION, QUESTION_MESSAGE);
			if(op == YES_OPTION) {
				ItemVenda itemVenda = new ItemVenda();
				itemVenda.setProduto(permitido);
				itemVenda.setQuantidade(1L);
				client.removeProdutoCarrinho(itemVenda);
				msgInfo("Produto removido com sucesso!", NOME_PROGRAMA + "-" + REMOVE_PRODUTO);
			}
			else
				msgInfo("Operação Cancelada",  NOME_PROGRAMA + "-" + REMOVE_PRODUTO);
		}else
			msgInfo("Código não registrado...", NOME_PROGRAMA + "-" + REMOVE_PRODUTO);
	}

	public void finalizaCompra(Usuario usuario) {
		if(cartao()){
			client.endsBuy(usuario);
			client.cleanCarrinho();
			msgInfo("Compra Finalizada", NOME_PROGRAMA + "-" + FINALIZA_COMPRA);
			msgInfo("Total: R$" + client.getCarrinho(), NOME_PROGRAMA + "-" + FINALIZA_COMPRA);
			client = null;
			criaConexao();
		}
	}
	
	public Boolean cartao() {
		CreditCardValidator creditCardValidator = new CreditCardValidator();
		
		String nome = lerString("Nome do Titular: ", "Você deve fornecer o nome", NOME_PROGRAMA + "-" + FINALIZA_COMPRA, false);
		if (nome == null) return false;
		
		String numeroCartao = lerString("Número do Cartão: ", "Você deve fornecer o número do cartão!", NOME_PROGRAMA + "-" + FINALIZA_COMPRA, false);
		if (numeroCartao == null) return false;
		
		if(creditCardValidator.isValid(numeroCartao)){
			msgInfo("Transação Autorizada!", NOME_PROGRAMA + "-" + FINALIZA_COMPRA);
			return true;
		}else{
			msgErro("Cartão Inválido", NOME_PROGRAMA + "-" + FINALIZA_COMPRA);
			return false;
		}
	}

	public String lerString(String prompt, String msgErro, String modulo, boolean vazia) {
		String string;

		do {
			string = showInputDialog(null, prompt, modulo, QUESTION_MESSAGE);

			if (string == null) break;

			if (string.equals("") && !vazia)
				showMessageDialog(null, msgErro, modulo, ERROR_MESSAGE);
		} while(string.equals("") && !vazia);
		return string;
	}

	public Double lerNumeroReal(String prompt, String msgErro, String modulo, boolean vazio) {
		String valor;

		do {
			valor = showInputDialog(null, prompt, modulo, QUESTION_MESSAGE);

			if (valor == null) return null;

			if (valor.equals("") && !vazio)
				showMessageDialog(null, msgErro, modulo, ERROR_MESSAGE);
		} while (valor.equals("") && !vazio);

		// COnverte a string lida pra double.
		return Double.parseDouble(valor);
	}

	public Integer lerNumeroInteiro(String prompt, String msgErro, String modulo, boolean vazio) {
		String valor;

		do {
			valor = showInputDialog(null, prompt, modulo, QUESTION_MESSAGE);

			if (valor == null) return null;

			if (valor.equals("") && !vazio)
				showMessageDialog(null, msgErro, modulo, ERROR_MESSAGE);
		} while (valor.equals("") && !vazio);

		// COnverte a string lida pra inteiro.
		return Integer.parseInt(valor.trim());
	}

	private void msgInfo(Object mensagem, String titulo) {
		showMessageDialog(null, mensagem, titulo, INFORMATION_MESSAGE);
	}

	private void msgErro(Object mensagem, String titulo) {
		showMessageDialog(null, mensagem, titulo, ERROR_MESSAGE);
	}
}
