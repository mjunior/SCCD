package br.superdia.managebean;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.superdia.modelo.Produto;
import br.superdia.sessionbean.IDAO;

/**
 * Classe responsavel pelo gerenciamento de estoque. 
 */
@ManagedBean
@SessionScoped
public class ProdutoMB {
	@EJB()
	private IDAO<Produto> iProduto;
	
	private Produto produto = new Produto();
	
	public void gravaProduto(){
		if(produto.getId() == null)
			iProduto.add(produto);
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	
}// class
