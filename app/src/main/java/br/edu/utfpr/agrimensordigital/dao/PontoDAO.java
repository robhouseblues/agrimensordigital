package br.edu.utfpr.agrimensordigital.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.agrimensordigital.model.Ponto;

public class PontoDAO {

    private SQLiteDatabase db;

    private static final String TABLE_NAME = "pontos";

    public PontoDAO(Context context) {
        db = DatabaseHandler.getInstance(context).getWritableDatabase();
    }

    private void incluir(Ponto ponto) {
        ContentValues registro = new ContentValues();

        registro.put("latitude", ponto.getLatitude());
        registro.put("longitude", ponto.getLongitude());
        registro.put("id_area", ponto.getIdArea());

        db.insert(TABLE_NAME, null, registro);
    }

    public void incluir(List<Ponto> pontos) {
        for (Ponto p : pontos) {
            incluir(p);
        }
    }

    public List<Ponto> listarPorArea(Integer idArea) {
        List<Ponto> pontos = new ArrayList<>();

        Cursor registros = db.query(TABLE_NAME, null, "id_area=?", new String[]{String.valueOf(idArea)}, null, null, null);

        while (registros.moveToNext()) {
            Ponto ponto = new Ponto();

            ponto.setId(registros.getInt(registros.getColumnIndex("id")));
            ponto.setLatitude(registros.getDouble(registros.getColumnIndex("latitude")));
            ponto.setLongitude(registros.getDouble(registros.getColumnIndex("longitude")));
            ponto.setIdArea(registros.getInt(registros.getColumnIndex("id_area")));

            pontos.add(ponto);
        }

        registros.close();

        return pontos;
    }

    public void excluirPorArea(Integer idArea) {
        db.delete(TABLE_NAME, "id_area=?", new String[]{String.valueOf(idArea)});
    }

}
