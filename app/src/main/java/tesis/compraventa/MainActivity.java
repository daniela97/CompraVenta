package tesis.compraventa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    EditText metTitulo, metDescripcion, metValor, metComuna, metCategoria;
    Button mbtnEnviar, mbtnLista;
    ImageView mimageView;


    final int REQUEST_CODE_GALLERY = 999;

  public static SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Publicar");

        metTitulo = findViewById(R.id.etTitulo);
        metDescripcion = findViewById(R.id.etDescripcion);
        metValor = findViewById(R.id.etValor);
        metComuna = findViewById(R.id.etComuna);
        metCategoria = findViewById(R.id.etCategoria);
        mbtnEnviar = findViewById(R.id.btnEnviar);
        mbtnLista = findViewById(R.id.btnLista);
        mimageView = findViewById(R.id.imageView);


        //Creacion database
        mSQLiteHelper = new SQLiteHelper(this, "RECORDDB.sqlite", null, 1 );

            //Creacion tabla
            mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS RECORD(id INTEGER PRIMARY KEY AUTOINCREMENT, titulo VARCHAR, descripcion VARCHAR, valor VARCHAR, comuna VARCHAR, categoria VARCHAR, image BLOB) ");


        //Seleccion de imagen
        mimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY);

            }
        });

        //sqlite
        mbtnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mSQLiteHelper.insertData(
                            metTitulo.getText().toString().trim(),
                            metDescripcion.getText().toString().trim(),
                            metValor.getText().toString().trim(),
                            metComuna.getText().toString().trim(),
                            metCategoria.getText().toString().trim(),
                            imageViewToByte(mimageView)
                    );
                    Toast.makeText(MainActivity.this, "Imagen agregada", Toast.LENGTH_LONG).show();
                    metTitulo.setText("");
                    metDescripcion.setText("");
                    metValor.setText("");
                    metComuna.setText("");
                    metCategoria.setText("");
                    mimageView.setImageResource(R.drawable.addphoto);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        //Mostrar lista
        mbtnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, RecordListActivity.class));
            }
        });

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
            if (requestCode == REQUEST_CODE_GALLERY){
                    if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        //galeria
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
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

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK){
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
                mimageView.setImageURI(resultUri);
            }
            else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}


