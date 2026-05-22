package repositorio;

import java.util.List;

public interface IRepositorio <T> {

    T guardar(T entidad);
    T buscarPorId(Long id);
    List<T> buscarTodos();
    T actualizar(T entidad);
    void eliminar(Long id);
}
