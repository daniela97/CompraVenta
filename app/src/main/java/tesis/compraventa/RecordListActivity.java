package tesis.compraventa;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static tesis.compraventa.MainActivity.mSQLiteHelper;

public class RecordListActivity extends AppCompatActivity {

    ListView mlistView;
    ArrayList<Model> mList;
    RecordListAdapter mAdapter = null;

    ImageView imageViewIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Record List");

        mlistView = findViewById(R.id.listView);
        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this, R.layout.row, mList);
        mlistView.setAdapter(mAdapter);


        //Creacion tabla
        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS RECORD(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo VARCHAR, descripcion VARCHAR, valor VARCHAR, comuna VARCHAR, categoria VARCHAR, image BLOB) ");

        //get all data from sqlite
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String titulo = cursor.getString(1);
            String descripcion = cursor.getString(2);
            String valor = cursor.getString(3);
            String comuna = cursor.getString(4);
            String categoria = cursor.getString(5);
            byte[] image  = cursor.getBlob(6);
            //add to list
            mList.add(new Model(id, titulo, descripcion, valor, comuna, categoria, image));
        }
        mAdapter.notifyDataSetChanged();
        if (mList.size()==0){
            //if there is no record in table of database which means listview is empty
            Toast.makeText(this, "No record found...", Toast.LENGTH_SHORT).show();
        }

        mlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                //alert dialog to display options of update and delete
                final CharSequence[] items = {"Update", "Delete"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(RecordListActivity.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            //update
                            Cursor c =  MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            //show update dialog
                            showDialogUpdate(RecordListActivity.this, arrID.get(position));
                        }
                        if (i==1){
                            //delete
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.addBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecordListActivity.this, MainActivity.class));
                finish();
            }
        });


    }

    private void showDialogDelete(final int idRecord) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(RecordListActivity.this);
        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure to delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    MainActivity.mSQLiteHelper.deleteData(idRecord);
                    Toast.makeText(RecordListActivity.this, "Delete successfully", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Log.e("error", e.getMessage());
                }
                updateRecordList();
            }
        });
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void showDialogUpdate(Activity activity, final int position){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Update");

        imageViewIcon = dialog.findViewById(R.id.imageViewRecord);
        final EditText etTituloRecord = dialog.findViewById(R.id.etTituloRecord);
        final EditText etDescripcionRecord = dialog.findViewById(R.id.etDescripcionRecord);
        final EditText etValorRecord = dialog.findViewById(R.id.etValorRecord);
        final EditText etComunaRecord = dialog.findViewById(R.id.etComunaRecord);
        final EditText etCategoriaRecord = dialog.findViewById(R.id.etCategoriaRecord);
        Button btnActualizarRecord = dialog.findViewById(R.id.btnActualizarRecord);


        Cursor cursor = mSQLiteHelper.getData("SELECT * FROM RECORD WHERE id="+position);
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);

            String titulo = cursor.getString(1);
            etTituloRecord.setText(titulo);

            String descripcion = cursor.getString(2);
            etDescripcionRecord.setText(descripcion);

            String valor = cursor.getString(3);
            etValorRecord.setText(valor);

            String comuna = cursor.getString(4);
            etComunaRecord.setText(comuna);

            String categoria = cursor.getString(5);
            etCategoriaRecord.setText(categoria);

            byte[] image  = cursor.getBlob(6);
            imageViewIcon.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

            //add to list
            mList.add(new Model(id, titulo, descripcion, valor, comuna, categoria, image));
        }

        //set width of dialog
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.95);
        //set hieght of dialog
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.7);
        dialog.getWindow().setLayout(width,height);
        dialog.show();

        //in update dialog click image view to update image
        imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check external storage permission
                ActivityCompat.requestPermissions(
                        RecordListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });
        btnActualizarRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.mSQLiteHelper.updateData(
                            etTituloRecord.getText().toString().trim(),
                            etDescripcionRecord.getText().toString().trim(),
                            etValorRecord.getText().toString().trim(),
                            etComunaRecord.getText().toString().trim(),
                            etCategoriaRecord.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewIcon),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update Successfull", Toast.LENGTH_SHORT).show();
                }
                catch (Exception error){
                    Log.e("Update error", error.getMessage());
                }
                updateRecordList();
            }
        });

    }

    private void updateRecordList() {
        //get all data from sqlite
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String titulo = cursor.getString(1);
            String descripcion = cursor.getString(2);
            String valor = cursor.getString(3);
            String comuna = cursor.getString(4);
            String categoria = cursor.getString(5);
            byte[] image  = cursor.getBlob(6);

            mList.add(new Model(id,titulo, descripcion,valor,comuna,categoria, image));
        }
        mAdapter.notifyDataSetChanged();
    }


    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 888){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //gallery intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 888);
            }
            else {
                Toast.makeText(this, "Don't have permission to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 888 && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON) //enable image guidlines
                    .setAspectRatio(1,1)// image will be square
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result =CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                //set image choosed from gallery to image view
                imageViewIcon.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);



        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("En Venta");

        mlistView = findViewById(R.id.listView);
        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this, R.layout.row, mList);
        mlistView.setAdapter(mAdapter);




        Cursor cursor = mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String titulo = cursor.getString(1);
            String descripcion = cursor.getString(2);
            String valor = cursor.getString(3);
            String comuna = cursor.getString(4);
            String categoria = cursor.getString(5);
            byte[] image = cursor.getBlob(6);

            //agregar al listado
            mList.add(new Model(id, titulo, descripcion, valor, comuna, categoria, image));


        }

        mAdapter.notifyDataSetChanged();
        if (mList.size() == 0) {
            Toast.makeText(this, "No se encontro", Toast.LENGTH_LONG).show();
        }

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                //alert dialog to display options of update and delete
                final CharSequence[] items = {"Update", "Delete"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(RecordListActivity.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            //update
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            //show update dialog
                            showDialogUpdate(RecordListActivity.this, arrID.get(position));
                        }
                        if (i==1){
                            //delete
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });


    }

    private void showDialogDelete(final int idRecord) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(RecordListActivity.this);
        dialogDelete.setTitle("A punto de eliminar!");
        dialogDelete.setMessage("Estay seguro cabro?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                try {
                    mSQLiteHelper.deleteData(idRecord);
                    Toast.makeText(RecordListActivity.this, "Eliminado ok", Toast.LENGTH_SHORT).show();

                } catch (Exception e ){
                    Log.e("error", e.getMessage());

                }
                updateRecordList();
            }
        });
        dialogDelete.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogDelete.show();
    }

    private void showDialogUpdate(final Activity activity, final int position){

            final Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.update_dialog);
            dialog.setTitle("Actualizar");

            imageViewIcon = dialog.findViewById(R.id.imageViewRecord);
            final EditText etTituloRecord = dialog.findViewById(R.id.etTituloRecord);
            final EditText etDescripcionRecord = dialog.findViewById(R.id.etDescripcionRecord);
            final EditText etComunaRecord = dialog.findViewById(R.id.etComunaRecord);
            final EditText etValorRecord = dialog.findViewById(R.id.etValorRecord);
            final EditText etCategoriaRecord = dialog.findViewById(R.id.etCategoriaRecord);
            Button btnActualizarRecord = dialog.findViewById(R.id.btnActualizarRecord);


        Cursor cursor = mSQLiteHelper.getData("SELECT * FROM RECORD WHERE id="+position);
        mList.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String titulo = cursor.getString(1);
            etTituloRecord.setText(titulo);
            String descripcion = cursor.getString(2);
            etDescripcionRecord.setText(descripcion);
            String valor = cursor.getString(3);
            etValorRecord.setText(valor);
            String comuna = cursor.getString(4);
            etComunaRecord.setText(comuna);
            String categoria = cursor.getString(5);
            etCategoriaRecord.setText(categoria);
            byte[] image = cursor.getBlob(6);
            imageViewIcon.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

            //agregar al listado
            mList.add(new Model(id, titulo, descripcion, valor, comuna, categoria, image));


        }

            int width = (int) (activity.getResources().getDisplayMetrics().widthPixels*0.95);
            int height = (int) (activity.getResources().getDisplayMetrics().heightPixels*0.7);
            dialog.getWindow().setLayout(width,height);
            dialog.show();

            imageViewIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(RecordListActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                           000);

                }
            });
            btnActualizarRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mSQLiteHelper.updateData(
                                etTituloRecord.getText().toString().trim(),
                                etDescripcionRecord.getText().toString().trim(),
                                etComunaRecord.getText().toString().trim(),
                                etValorRecord.getText().toString().trim(),
                                etCategoriaRecord.getText().toString().trim(),
                                MainActivity.imageViewToByte(imageViewIcon),
                                    position
                        );


                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Actualización correcta", Toast.LENGTH_LONG).show();
                    }
                    catch (Exception error){
                        Log.e("Error actualizacion", error.getMessage());
                    }
                    updateRecordList();
                }
            });


        }

    private void updateRecordList() {

        Cursor cursor = mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String titulo = cursor.getString(1);
            String descripcion = cursor.getString(2);
            String valor = cursor.getString(3);
            String comuna = cursor.getString(4);
            String categoria = cursor.getString(5);
            byte[] image = cursor.getBlob(6);

            mList.add(new Model(id, titulo, descripcion, valor, comuna, categoria, image));
        }
        mAdapter.notifyDataSetChanged();
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResults){
        if (requestCode == 000){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //galeria
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 000);
            }
            else {
                Toast.makeText(this, "No hay permiso para acceder al archivo", Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 000 && resultCode == RESULT_OK){
            Uri imageuri = data.getData();
            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                Uri resultUri = result.getUri();
                //Escoger imagen de galeria
                imageViewIcon.setImageURI(resultUri);
            }
            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    }

*/