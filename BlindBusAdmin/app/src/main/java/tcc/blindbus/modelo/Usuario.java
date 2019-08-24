package tcc.blindbus.modelo;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String id;
    private String nome;
    private String login;
    private String senha;
    private String numeroInscricao;

    public Usuario() {
    }

    public String getId() {
        return id;
    }

    public Usuario(String nome, String login, String senha, String numeroInscricao) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.numeroInscricao = numeroInscricao;
    }

    public Usuario(String login, String senha)
    {
        this.login=login;
        this.senha=senha;
    }
    public Usuario(String id,String login,String senha)
    {
        this.id=id;
        this.login=login;
        this.senha=senha;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", login='" + login + '\'' +
                ", senha='" + senha + '\'' +
                ", numeroInscricao='" + numeroInscricao + '\'' +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNumeroInscricao() {
        return numeroInscricao;
    }

    public void setNumeroInscricao(String numeroInscricao) {
        this.numeroInscricao = numeroInscricao;
    }
}
