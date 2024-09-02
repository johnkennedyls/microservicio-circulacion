package co.analisys.biblioteca.controller;

import co.analisys.biblioteca.model.LibroId;
import co.analisys.biblioteca.model.Prestamo;
import co.analisys.biblioteca.model.PrestamoId;
import co.analisys.biblioteca.model.UsuarioId;
import co.analisys.biblioteca.service.CirculacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/circulacion")
public class CirculacionController {
    @Autowired
    private CirculacionService circulacionService;

    @Operation(
            summary = "Prestar un libro",
            description = "Este endpoint permite prestar un libro a un usuario. " +
                    "Es necesario proporcionar el ID del usuario y el ID del libro."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro prestado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario o libro no encontrado"),
            @ApiResponse(responseCode = "409", description = "El libro ya está prestado")
    })
    @PostMapping("/prestar")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public void prestarLibro(@RequestParam String usuarioId, @RequestParam String libroId) {
        circulacionService.prestarLibro(new UsuarioId(usuarioId), new LibroId(libroId));
    }

    @Operation(
            summary = "Devolver un libro",
            description = "Este endpoint permite registrar la devolución de un libro prestado. " +
                    "Es necesario proporcionar el ID del préstamo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro devuelto exitosamente"),
            @ApiResponse(responseCode = "400", description = "ID de préstamo inválido"),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    })
    @PostMapping("/devolver")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public void devolverLibro(@RequestParam String prestamoId) {
        circulacionService.devolverLibro(new PrestamoId(prestamoId));
    }

    @Operation(
            summary = "Consultar todos los préstamos",
            description = "Este endpoint permite obtener una lista de todos los préstamos registrados en el sistema. " +
                    "Es importante que el cliente esté registrado previamente en la base de datos, " +
                    "de lo contrario no podrá acceder a esta información."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/prestamos")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_USER')")
    public List<Prestamo> obtenerTodosPrestamos() {
        return circulacionService.obtenerTodosPrestamos();
    }

    @GetMapping("/public/status")
    public String getPublicStatus() {
        return "El servicio de circulación está funcionando correctamente";
    }
}