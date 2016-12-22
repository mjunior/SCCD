package br.superdia.restful;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.google.gson.Gson;

import br.superdia.modelo.Produto;
import br.superdia.sessionbean.IDAO;

/*
 * Implementa as funcionalidades do web services para manipular os produtos em estoque.
 */

@Path("produtos")
public class ProdutosResource {
	private Gson gson = new Gson();
	
	@EJB
	private IDAO<Produto> dao;

	public ProdutosResource() {
	}
	
	@Path("inserir")
	@POST
	@Consumes("application/json")
	public void addProduct(String product){
		Produto p = gson.fromJson(product, Produto.class);
		dao.add(p);
	}
	
	//http://localhost:8080/SuperDiaRestFul/rest/produtos/listar
	@Path("listar")
	@GET
	@Produces("application/json")
	public String getAll(){
		return gson.toJson(dao.getAll(Produto.class));
	}
	
	@Path("obter/{id}")
	@GET
	@Produces("application/json")
	public String getById(final @PathParam("id") String id){
		return gson.toJson(dao.getForId(Long.valueOf(id), Produto.class));
	}
	
	@Path("remover/{id}")
	@DELETE
	@Consumes("application/json")
	public void removeProdutc(final @PathParam("id") String id){
		Produto p = dao.getForId(Long.valueOf(id), Produto.class);
		dao.remove(p);
	}
	
	@Path("atualizar")
	@PUT
	@Consumes("application/json")
	public void updateProdutc(String product){
		dao.update(gson.fromJson(product, Produto.class));
	}
}