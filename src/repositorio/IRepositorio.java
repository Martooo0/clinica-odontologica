package repositorio;

import java.util.List;

public interface IRepositorio <T> {

    T registrar(T entidad);
    T buscarPorId(Long id);
    List<T> listarTodos();
    T actualizar(T entidad);
    void eliminar(Long id);
}
