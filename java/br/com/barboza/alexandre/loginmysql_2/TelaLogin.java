package br.com.barboza.alexandre.loginmysql_2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TelaLogin extends AppCompatActivity {
    private EditText editEmail1;
    private EditText editSenha1;
    private Button btnLogin;
    private TextView txtCadastro;
    private String url = "";
    private String param = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_login);
        editEmail1 = (EditText) findViewById(R.id.editEmail1);
        editSenha1 = (EditText) findViewById(R.id.editSenha1);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtCadastro = (TextView) findViewById(R.id.txtCadastro);
        txtCadastro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent Cadastro = new Intent(TelaLogin.this, TelaCadastro.class);
                startActivity(Cadastro);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    String email = editEmail1.getText().toString();
                    String senha = editSenha1.getText().toString();
                    if (email.isEmpty() || senha.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Campo não preenchido!", Toast.LENGTH_SHORT).show();
                    } else {
                        url = "http://alexandrebarboza.freevar.com/test/login.php";
                        param = "email=" + email + "&senha=" + senha;
                        new RecebeDados().execute(url);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Nenhuma conexão encontrada!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private class RecebeDados extends AsyncTask<String, Void, String> {
        private String dados;
        private ProgressDialog status;

        @Override
        protected String doInBackground(String... params) {
            return Conexao.postDados(params[0], param);
        }

        @Override
        protected void onPreExecute() {
            status = new ProgressDialog(TelaLogin.this);
            status.setMessage("Conectando ao servidor...");
            status.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            status.setCancelable(false);
            status.show();
            final int totalProgressTime = 1000;
            final Thread t = new Thread() {

                @Override
                public void run() {
                    int jumpTime = 0;
                    while(jumpTime < totalProgressTime) {
                        try {
                            sleep(100);
                            jumpTime += 1;
                            status.setProgress(jumpTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            t.start();
        }

        @Override
        protected void onPostExecute(String result) {
            status.cancel();
            if (result == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TelaLogin.this);
                builder.setTitle("Erro do servidor:");
                builder.setMessage("Não foi possível conectar com a URL do aplicativo!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.setCancelable(false);
                builder.create();
                builder.show();
                return;
            }
            if (result.contains("Login_OK")) {
                String[] dados = result.split(",");
                Intent abreInicio = new Intent(TelaLogin.this, TelaInicial.class);
                abreInicio.putExtra("id_usuario", dados[1]);
                abreInicio.putExtra("nome_usuario", dados[2]);
                startActivity(abreInicio);
            } else if (result.contains("Login_Erro")) {
                Toast.makeText(getApplicationContext(), "Usuário ou senha incorretos!", Toast.LENGTH_SHORT).show();
            } else if (result.contains("Conexao_Erro")){
                Toast.makeText(getApplicationContext(), "Não foi possível conectar!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

