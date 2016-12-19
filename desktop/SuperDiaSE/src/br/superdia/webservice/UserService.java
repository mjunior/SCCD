
package br.superdia.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "UserService", targetNamespace = "http://webservice.superdia.br/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface UserService {


    /**
     * 
     * @param arg0
     * @return
     *     returns br.superdia.webservice.Usuario
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "obtemUsuario", targetNamespace = "http://webservice.superdia.br/", className = "br.superdia.webservice.ObtemUsuario")
    @ResponseWrapper(localName = "obtemUsuarioResponse", targetNamespace = "http://webservice.superdia.br/", className = "br.superdia.webservice.ObtemUsuarioResponse")
    @Action(input = "http://webservice.superdia.br/UserService/obtemUsuarioRequest", output = "http://webservice.superdia.br/UserService/obtemUsuarioResponse")
    public Usuario obtemUsuario(
        @WebParam(name = "arg0", targetNamespace = "")
        Usuario arg0);

}
