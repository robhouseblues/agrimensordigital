package br.edu.utfpr.agrimensordigital.service;

import android.content.Context;

import java.util.List;

import br.edu.utfpr.agrimensordigital.dao.AreaDAO;
import br.edu.utfpr.agrimensordigital.model.Area;

public class AreaService {

    private AreaDAO areaDAO;
    private PontoService pontoService;

    public AreaService(Context context) {
        this.areaDAO = new AreaDAO(context);
        this.pontoService = new PontoService(context);
    }

    public void incluir(Area area) {
        Long idArea = areaDAO.incluir(area);
        pontoService.incluir(area.getPontos(), idArea);
    }

    public void excluir(Area area) {
        areaDAO.excluir(area.getId());
        pontoService.excluirPorArea(area.getId());
    }

    public List<Area> listar() {
        return areaDAO.listar();
    }

    public Area buscarPorId(Integer idArea) {
        Area area = areaDAO.buscarPorId(idArea);

        if (area != null) {
            area.setPontos(pontoService.listarPorArea(idArea));
        }

        return area;
    }

}
