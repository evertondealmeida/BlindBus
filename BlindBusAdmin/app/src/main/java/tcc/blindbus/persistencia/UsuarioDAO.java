package tcc.blindbus.persistencia;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

import tcc.blindbus.modelo.Usuario;

public class UsuarioDAO extends DAO {

    public UsuarioDAO(Context context){
        super(context);
    }

    @Override
    protected String getTableName() {
        return "usuario";
    }

    @Override
    protected String getCreateTableScript() {
        return "CREATE TABLE usuario(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "nome TEXT NOT NULL," +
                "login TEXT NOT NULL, " +
                "senha TEXT NOT NULL," +
                "numeroInscricao CHAR NOT NULL);";
    }

    @Override
    protected String getUpgradeTableScript() {
        return "DROP TABLE IF EXISTS usuario";
    }

    public void inserirUsuario(Usuario usuario)
    {
        ContentValues values = new ContentValues();
        values.put("nome",usuario.getNome());
        values.put("login",usuario.getLogin());
        values.put("senha",usuario.getSenha());
        values.put("numeroInscricao",usuario.getNumeroInscricao().toString());
        inserir(values);
    }

    public Usuario validaUsuario(String login, String senha)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT id FROM usuario WHERE login=? AND senha=?",new String[]{login,senha});
        if(cursor.getCount()>0) {
            Usuario usuario;
            cursor.moveToFirst();
            usuario = new Usuario();
            usuario.setId(cursor.getString(0));
            cursor.close();
            db.close();
            return usuario;
        }
        return null;
    }
    public Usuario carregaUsuario(String id) {
        SQLiteDatabase db = getReadableDatabase();
        Usuario usuario;
        Cursor cursor = db.rawQuery("SELECT id, nome, login, senha, numeroInscricao FROM usuario WHERE id=?", new String[]{id});
        cursor.moveToFirst();
        usuario = new Usuario();
        usuario.setId(cursor.getString(0));
        usuario.setNome(cursor.getString(1));
        usuario.setLogin(cursor.getString(2));
        usuario.setSenha(cursor.getString(3));
        usuario.setNumeroInscricao(cursor.getString(4));
        cursor.close();
        db.close();
        return usuario;
    }

}
