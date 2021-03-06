package br.superdia.modelo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class Venda implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "venda_id", sequenceName = "venda_seq", allocationSize = 1)
	@GeneratedValue(generator = "venda_id", strategy = GenerationType.SEQUENCE)
	private Long id;
	@Temporal(TemporalType.DATE)
	private Calendar data;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<ItemVenda> itemVendas;
	@ManyToOne
	private Usuario usuario;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Calendar getData() {
		return data;
	}
	
	public void setData(Calendar data) {
		this.data = data;
	}
	
	public List<ItemVenda> getProdutos() {
		return itemVendas;
	}
	
	public void setProdutos(List<ItemVenda> itemVendas) {
		this.itemVendas = itemVendas;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}	
}

