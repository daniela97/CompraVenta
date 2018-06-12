package tesis.compraventa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Model> recordList;

    public RecordListAdapter(Context context, int layout, ArrayList<Model> recordList) {
        this.context = context;
        this.layout = layout;
        this.recordList = recordList;
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int i) {
        return recordList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder{
        ImageView imgIcon;
        TextView txtTitulo, txtDescripcion, txtComuna, txtValor, txtCategoria;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtTitulo = row.findViewById(R.id.txtTitulo);
            holder.txtDescripcion = row.findViewById(R.id.txtDescripcion);
            holder.txtValor = row.findViewById(R.id.txtValor);
            holder.txtComuna = row.findViewById(R.id.txtComuna);
            holder.txtCategoria = row.findViewById(R.id.txtCategoria);
            holder.imgIcon = row.findViewById(R.id.imgIcon);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder)row.getTag();
        }

        Model model = recordList.get(i);

        holder.txtTitulo.setText(model.getTitulo());
        holder.txtDescripcion.setText(model.getDescripcion());
        holder.txtValor.setText(model.getValor());
        holder.txtComuna.setText(model.getComuna());
        holder.txtCategoria.setText(model.getCategoria());

        byte[] recordImage = model.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage, 0, recordImage.length);
        holder.imgIcon.setImageBitmap(bitmap);

        return row;
    }
}

/*
public class RecordListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Model> recordList;

    public RecordListAdapter(Context context, int layout, ArrayList<Model> recordList) {
        this.context = context;
        this.layout = layout;
        this.recordList = recordList;
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int i) {
        return recordList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtTitulo, txtComuna, txtValor;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null ){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtTitulo = row.findViewById(R.id.txtTitulo);
            holder.txtComuna = row.findViewById(R.id.txtComuna);
            holder.txtValor = row.findViewById(R.id.txtValor);
            holder.imageView = row.findViewById(R.id.imgIcon);
            row.setTag(holder);

        } else {
            holder = (ViewHolder)row.getTag();

        }
        Model model = recordList .get(i);

        holder.txtTitulo.setText(model.getTitulo());
        holder.txtComuna.setText(model.getComuna());
        holder.txtValor.setText(model.getValor());

        byte[] recordImage = model.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage, 0, recordImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
*/
