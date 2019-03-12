package br.com.barboza.alexandre.loginmysql_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TelaInicial extends AppCompatActivity {
    private TextView txtNome;
    private TextView txtId;
    private String nome_usuario;
    private String id_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.tela_inicial);
            txtNome = (TextView) findViewById(R.id.txtNome);
            txtId = (TextView) findViewById(R.id.txtId);

            nome_usuario = getIntent().getExtras().getString("nome_usuario");
            id_usuario = getIntent().getExtras().getString("id_usuario");

            txtNome.setText(nome_usuario);
            txtId.setText(id_usuario);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
