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
import android.widget.Toast;

public class TelaCadastro extends AppCompatActivity {
    private EditText editNome;
    private EditText editEmail2;
    private EditText editSenha2;
    private Button btnEnviar;
    private Button btnCancelar;
    private String url = "";
    private String param = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_cadastro);
        editNome = (EditText) findViewById(R.id.editNome);
        editEmail2 = (EditText) findViewById(R.id.editEmail2);
        editSenha2 = (EditText) findViewById(R.id.editSenha2);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnEnviar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    String nome  = editNome.getText().toString();
                    String email = editEmail2.getText().toString();
                    String senha = editSenha2.getText().toString();
                    if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Campo não preenchido!", Toast.LENGTH_SHORT).show();
                    } else {
                        url = "http://alexandrebarboza.freevar.com/test/registro.php";
                        param = "nome="+nome+"&email="+email+"&senha="+senha;
                        new RecebeDados().execute(url);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Nenhuma conexão encontrada!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private class RecebeDados extends AsyncTask<String, Void, String> {
        private ProgressDialog status;

        @Override
        protected String doInBackground(String... params) {
            return Conexao.postDados(params[0], param);
        }

        @Override
        protected void onPreExecute() {
            status = new ProgressDialog(TelaCadastro.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(TelaCadastro.this);
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
            if (result.contains("Registro_OK")) {
                Toast.makeText(getApplicationContext(), "Registro efetuado com êxito!", Toast.LENGTH_SHORT).show();
                Intent abreInicio = new Intent(TelaCadastro.this, TelaInicial.class);
                String[] dados = result.split(",");
                abreInicio.putExtra("id_usuario", dados[1]);
                abreInicio.putExtra("nome_usuario", dados[2]);
                startActivity(abreInicio);
            } else if (result.contains("Registro_Erro")) {
                Toast.makeText(getApplicationContext(), "Erro registrando dados!", Toast.LENGTH_SHORT).show();
            } else if (result.contains("Email_Erro")) {
                Toast.makeText(getApplicationContext(), "Endereço de e-mail já existe!", Toast.LENGTH_SHORT).show();
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
