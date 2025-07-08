package com.example.lenguajesactvi2parcial2;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class activity_listausuarios extends AppCompatActivity {

    ListView listUsuarios;
    ArrayAdapter<String> adapter;
    ArrayList<String> lista;
    ArrayList<Usuario> usuarios;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listausuarios);
        listUsuarios = findViewById(R.id.listUsuarios);
        dbHelper = new DBHelper(this);
        cargarUsuarios();

        listUsuarios.setOnItemClickListener((parent, view, position, id) -> {
            Usuario usuario= usuarios.get(position);
            mostarDialago(usuario);
        });
    }

    private void cargarUsuarios() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios", null);
        lista = new ArrayList<>();
        usuarios = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);
            String email = cursor.getString(2);
            usuarios.add(new Usuario(id, nombre, email));
            lista.add(nombre + " - " + email);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listUsuarios.setAdapter(adapter);

    }

    private void mostarDialago(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar o Eliminar");

        final EditText inputNombre = new EditText(this);
        inputNombre.setText(usuario.getNombre());
        final EditText inputEmail = new EditText(this);
        inputEmail.setText(usuario.getEmail());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputNombre);
        layout.addView(inputEmail);
        builder.setView(layout);

        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            actualizarUsuario(usuario.id, inputNombre.getText().toString(), inputEmail.getText().toString());
        });

        builder.setNegativeButton("Eliminar", (dialog, which) -> {
            eliminarUsuario(usuario.id);
        });

        builder.setNeutralButton("Cancelar", null);
        builder.show();

    }

    private void actualizarUsuario(int id, String nombre, String email) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("email", email);
        db.update("usuarios", values, "id = ?", new String[]{String.valueOf(id)});
        cargarUsuarios();
    }

    private void eliminarUsuario(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("usuarios", "id = ?", new String[]{String.valueOf(id)});
        cargarUsuarios();
    }


}
