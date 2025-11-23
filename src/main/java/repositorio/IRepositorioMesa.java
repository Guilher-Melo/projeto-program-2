package repositorio;

import java.util.List;
import modelo.Mesa;

public interface IRepositorioMesa {
    Mesa buscarPorNumero(int numero);
    void atualizar(Mesa mesa);
    void cadastrar(Mesa mesa);
    void remover(Mesa mesa); 
    List<Mesa> listarTodas();
}