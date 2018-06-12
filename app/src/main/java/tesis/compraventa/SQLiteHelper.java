
package tesis.compraventa;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


public class SQLiteHelper extends SQLiteOpenHelper{

    //constructor
    SQLiteHelper(Context context,
                 String name,
                 SQLiteDatabase.CursorFactory factory,
                 int version){
        super(context, name, factory, version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //insertData
    public void insertData(String titulo, String descripcion, String valor, String comuna,  String categoria, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        //query to insert record in database table
        String sql = "INSERT INTO RECORD VALUES(NULL, ?, ?, ?, ?, ?, ?)"; //where "RECORD" is table name in database we will create in mainActivity

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, titulo);
        statement.bindString(2, descripcion);
        statement.bindString(3, valor);
        statement.bindString(4, comuna);
        statement.bindString(5, categoria);
        statement.bindBlob(6, image);

        statement.executeInsert();
    }

    //updateData
    public void updateData(String titulo, String descripcion, String valor, String comuna, String categoria,  byte[] image, int id){
        SQLiteDatabase database = getWritableDatabase();
        //query to update record
        String sql = "UPDATE RECORD SET titulo=?, descripcion=?, valor=?, comuna=?, categoria=?, image=? WHERE id=?";

        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, titulo);
        statement.bindString(2, descripcion);
        statement.bindString(3, valor);
        statement.bindString(4, comuna);
        statement.bindString(5, categoria);
        statement.bindBlob(6, image);
        statement.bindDouble(7, (double)id);

        statement.execute();
        database.close();
    }

    //deleteData
    public void deleteData(int id){
        SQLiteDatabase database = getWritableDatabase();
        //query to delete record using id
        String sql = "DELETE FROM RECORD WHERE id=?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);

        statement.execute();
        database.close();
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}



/*
public class SQLiteHelper extends SQLiteOpenHelper {

    //Constructor
    SQLiteHelper (Context context,
                  String name,
                  SQLiteDatabase.CursorFactory factory,
                  int version) {
        super(context, name, factory, version);
    }

    public void queryData (String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //Database
    public void insertData (String titulo, String descripcion, String valor, String comuna, String categoria, byte[] image) {
        SQLiteDatabase database = getWritableDatabase();

        //Insertar datos a la tabla
        String sql = "INSERT INTO RECORD VALUES(NULL, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,titulo);
        statement.bindString(2,descripcion);
        statement.bindString(3,valor);
        statement.bindString(4,comuna);
        statement.bindString(5,categoria);
        statement.bindBlob(6, image);

        statement.executeInsert();
    }

    //Update
    public void updateData(String titulo, String descripcion, String valor, String comuna, String categoria,  byte[] image, int id){
        SQLiteDatabase database = getWritableDatabase();

        //query update
        String sql = "UPDATE RECORD SET titulo=?, descripcion=?, valor=?, comuna=?, categoria=?, image=? WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1,titulo);
        statement.bindString(2,descripcion);
        statement.bindString(3,valor);
        statement.bindString(4,comuna);
        statement.bindString(5,categoria);
        statement.bindBlob(6, image);
        statement.bindDouble(7, (double)id);

        statement.execute();
        database.close();
    }

    //Delete
    public void deleteData (int id){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM RECORD WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);

        statement.execute();
        database.close();
    }


    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i) {

    }
}

*/
