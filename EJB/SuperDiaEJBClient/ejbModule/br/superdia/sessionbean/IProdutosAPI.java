package br.superdia.sessionbean;

import java.util.List;

import br.superdia.modelo.Produto;

public interface IProdutosAPI {
	public List<Produto> getAll(String url);
}
