package br.edu.utfpr.agrimensordigital.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.agrimensordigital.model.Area;

public class AreaDAO {

    private SQLiteDatabase db;

    private static final String TABLE_NAME = "areas";

    public AreaDAO(Context context) {
        db = DatabaseHandler.getInstance(context).getWritableDatabase();
    }

    public Long incluir(Area area) {
        Long id;

        ContentValues registro = new ContentValues();

        registro.put("nome", area.getNome());
        registro.put("perimetro", area.getPerimetro());
        registro.put("area", area.getArea());
        registro.put("imagem", area.getImagem());

        id = db.insert(TABLE_NAME, null, registro);

        return id;
    }

    public void excluir(Integer id) {
        db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }

    public List<Area> listar() {
        List<Area> areas = new ArrayList<>();

        Cursor registros = db.query(TABLE_NAME, null, null, null, null, null, null);

        while (registros.moveToNext()) {
            Area area = new Area();

            area.setId(registros.getInt(registros.getColumnIndex("id")));
            area.setNome(registros.getString(registros.getColumnIndex("nome")));
            area.setPerimetro(registros.getDouble(registros.getColumnIndex("perimetro")));
            area.setArea(registros.getDouble(registros.getColumnIndex("area")));
            area.setImagem(registros.getString(registros.getColumnIndex("imagem")));

            areas.add(area);
        }

        registros.close();

        return areas;
    }

    public Area buscarPorId(Integer idArea) {
        Cursor registros = db.query(TABLE_NAME, null, "id=?", new String[]{String.valueOf(idArea)}, null, null, null);

        if (registros.moveToNext()) {
            Area area = new Area();

            area.setId(registros.getInt(registros.getColumnIndex("id")));
            area.setNome(registros.getString(registros.getColumnIndex("nome")));
            area.setPerimetro(registros.getDouble(registros.getColumnIndex("perimetro")));
            area.setArea(registros.getDouble(registros.getColumnIndex("area")));
            area.setImagem(registros.getString(registros.getColumnIndex("imagem")));

            registros.close();

            return area;
        } else {
            registros.close();

            return null;
        }
    }

}
