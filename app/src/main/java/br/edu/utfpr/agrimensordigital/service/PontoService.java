package br.edu.utfpr.agrimensordigital.service;

import android.content.Context;

import java.util.List;

import br.edu.utfpr.agrimensordigital.dao.PontoDAO;
import br.edu.utfpr.agrimensordigital.model.Ponto;

public class PontoService {

    private PontoDAO pontoDAO;

    public PontoService(Context context) {
        this.pontoDAO = new PontoDAO(context);
    }

    public void incluir(List<Ponto> pontos, Long idArea) {
        for (Ponto p : pontos) {
            p.setIdArea(idArea.intValue());
        }

        pontoDAO.incluir(pontos);
    }

    public List<Ponto> listarPorArea(Integer idArea) {
        return pontoDAO.listarPorArea(idArea);
    }

    public void excluirPorArea(Integer idArea) {
        pontoDAO.excluirPorArea(idArea);
    }

}
