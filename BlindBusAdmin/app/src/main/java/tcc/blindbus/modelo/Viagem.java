package tcc.blindbus.modelo;

public class Viagem {
    private String instanceBeacon;
    private String nameSpace;
    private String linha;
    private String sentido;
    private String informacoesUsuario;
    private String clienteEncontrado;

    public Viagem() {
    }

    public Viagem(String instanceBeacon, String nameSpace, String linha, String sentido, String informacoesUsuario, String clienteEncontrado) {
        this.instanceBeacon = instanceBeacon;
        this.nameSpace = nameSpace;
        this.linha = linha;
        this.sentido = sentido;
        this.informacoesUsuario = informacoesUsuario;
        this.clienteEncontrado = clienteEncontrado;
    }

    public String getInstanceBeacon() {
        return instanceBeacon;
    }

    public void setInstanceBeacon(String instanceBeacon) {
        this.instanceBeacon = instanceBeacon;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public String getInformacoesUsuario() {
        return informacoesUsuario;
    }

    public void setInformacoesUsuario(String informacoesUsuario) {
        this.informacoesUsuario = informacoesUsuario;
    }

    public String getClienteEncontrado() {
        return clienteEncontrado;
    }

    public void setClienteEncontrado(String clienteEncontrado) {
        this.clienteEncontrado = clienteEncontrado;
    }
}
