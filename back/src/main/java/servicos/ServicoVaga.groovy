package servicos

import database.ServicoConectarBanco
import modelos.Vaga

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class ServicoVaga {
    private ServicoConectarBanco servicoConectar

    ServicoVaga(){
        servicoConectar = new ServicoConectarBanco()
    }

    String montarQueryBuscarPorCnpj() {
        return "SELECT id_vaga, descricao_vaga, titulo_vaga, local_vaga " +
                "FROM linlketinder.vaga WHERE cnpj_empresa=?"
    }

    String montarQueryBuscarTodos() {
        return "SELECT id_vaga descricao_vaga, titulo_vaga, local_vaga " +
                "FROM linlketinder.vaga "
    }

    void salvarImformacao(String comado, Vaga vaga){
        Connection conn = servicoConectar.conectar()
        PreparedStatement salvar = conn.prepareStatement(comado);

        salvar.setString(1, vaga.getDescricao())
        salvar.setString(2, vaga.getTitulo())
        salvar.setString(3, vaga.getLocal())
        salvar.setString(4, vaga.getCnpj_empresa())

        salvar.executeUpdate();
        salvar.close();
        servicoConectar.desconectar(conn);
    }

    void criar(Vaga vaga) {
        String INSERIR = "INSERT INTO linlketinder.vaga" +
                "(descricao_vaga, titulo_vaga, local_vaga, cnpj_empresa)\n" +
                "VALUES ( ?, ?, ?, ?)"
        try {
            salvarImformacao(INSERIR, vaga)
        } catch (Exception exception) {
            exception.printStackTrace();
            System.err.println("Erro em criar" )
            System.exit(-42);
        }
    }

     def listar(String cnpj_vaga) {
        try {
            Connection conn = servicoConectar.conectar();
            PreparedStatement vaga = conn.prepareStatement(
                    montarQueryBuscarPorCnpj(),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            )

            vaga.setString(1, cnpj_vaga)
            ResultSet res = vaga.executeQuery();
            res.last();
            int qtd = res.getRow();
            res.beforeFirst();

            def vagas = []
            if(qtd > 0) {
                while (res.next()) {
                    Vaga v = new Vaga (
                            res.getInt(1),
                            res.getString(2),
                            res.getString(3),
                            res.getString(4)
                    )
                    vagas.add(v)
                }
            }
            return vagas
        }catch(Exception exception){
            exception.printStackTrace();
            System.err.println("Erro em listar" )
            System.exit(-42);
        }
    }

    void deletar(Integer id_vaga, String cnpj_empresa) {
        String DELETAR = "DELETE FROM linlketinder.vaga WHERE cnpj_empresa =? AND id_vaga =?"

        try {
            Connection conn = servicoConectar.conectar();
            PreparedStatement vaga = conn.prepareStatement(
                    montarQueryBuscarPorCnpj(),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            )
            vaga.setString(1, cnpj_empresa)

            ResultSet res = vaga.executeQuery();
            res.last();
            int qtd = res.getRow();
            res.beforeFirst();

            if (qtd > 0) {
                PreparedStatement del = conn.prepareStatement(DELETAR)
                del.setString(1, cnpj_empresa)
                del.setInt(2, id_vaga)
                del.executeUpdate()
                del.close()
                servicoConectar.desconectar(conn)
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            System.err.println("Erro em deletar");
            System.exit(-42);
        }
    }

}
