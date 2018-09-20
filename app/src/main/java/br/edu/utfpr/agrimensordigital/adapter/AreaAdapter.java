package br.edu.utfpr.agrimensordigital.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.utfpr.agrimensordigital.R;
import br.edu.utfpr.agrimensordigital.model.Area;

public class AreaAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Area> lista;

    public AreaAdapter(Context context, List<Area> lista) {
        this.inflater = LayoutInflater.from(context);
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (lista.get(position).getId() != null) {
            return lista.get(position).getId();
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_area, null);
            holder = new ViewHolder();

            holder.img = convertView.findViewById(R.id.imgArea);
            holder.nome = convertView.findViewById(R.id.txtNomeArea);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Area area = lista.get(position);

        if (area.getImagem() != null) {
            byte[] byteArray = Base64.decode(area.getImagem(), 0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            holder.img.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
        }

        holder.nome.setText(area.getNome());

        return convertView;
    }

    private static class ViewHolder {
        private ImageView img;
        private TextView nome;
    }

}
