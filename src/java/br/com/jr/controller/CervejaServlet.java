package br.com.jr.controller;

import br.com.jr.model.Estoque;
import br.com.jr.model.rest.Cervejas;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

@WebServlet(value = "/cervejas/*")
public class CervejaServlet extends HttpServlet {

    private Estoque estoque = new Estoque();
    private static JAXBContext context;

    static {
        try {
            context = JAXBContext.newInstance(Cervejas.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, java.io.IOException {
        try {
            String acceptHeader = req.getHeader("Accept");
            String requestUri = this.obtemIdentificador(req);

            if (acceptHeader == null || acceptHeader.contains("application/xml")) {
                escreveXML(req, resp);
            } else if (acceptHeader.contains("application/json")) {
                escreveJSON(req, resp);
            } else {
                resp.sendError(415); // Formato não suportado
            }
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    private String obtemIdentificador(HttpServletRequest req)
            throws RecursoSemIdentificadorException {
        String requestUri = req.getRequestURI();
        String[] pedacosDaUri = requestUri.split("/");
        boolean contextoCervejasEncontrado = false;
        for (String contexto : pedacosDaUri) {
            if (contexto.equals("cervejas")) {
                contextoCervejasEncontrado = true;
                continue;
            }
            if (contextoCervejasEncontrado) {
                try {
                    return URLDecoder.decode(contexto, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return URLDecoder.decode(contexto);
                }
            }
        }
        throw new RecursoSemIdentificadorException("Recurso sem identificador");
    }

    private Object localizaObjetoASerEnviado(HttpServletRequest req) {
        Object objeto = null;
        try {
            String identificador = obtemIdentificador(req);
            objeto = estoque.recuperarCervejaPeloNome(identificador);
        } catch (RecursoSemIdentificadorException e) {
            Cervejas cervejas = new Cervejas();
            cervejas.setCervejas(new ArrayList<>(estoque.listarCervejas()));
            objeto = cervejas;
        }
        return objeto;
    }

    private void escreveXML(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Object objetoAEscrever = localizaObjetoASerEnviado(req);

        if (objetoAEscrever == null) {
            resp.sendError(404); //objeto não encontrado
            return;
        }

        try {
            resp.setContentType("application/xml;charset=UTF-8");
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(objetoAEscrever, resp.getWriter());
        } catch (JAXBException e) {
            resp.sendError(500); //Erro interno inesperado
        }
    }

    private void escreveJSON(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Object objetoAEscrever = localizaObjetoASerEnviado(req);
        if (objetoAEscrever == null) {
            resp.sendError(404); //objeto não encontrado
            return;
        }

        try {
            String json = new Gson().toJson(objetoAEscrever);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");
            resp.getWriter().write(json);
        } catch (Exception e) {
            resp.sendError(500);
        }
    }

    private static class RecursoSemIdentificadorException extends Exception {

        public RecursoSemIdentificadorException(String message) {
        }
    }

}
