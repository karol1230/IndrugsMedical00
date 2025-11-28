async function llenarTablasAdministrador() {
    try {
        // 1️⃣ Obtener datos del backend
        const response = await fetch('/admin/datos-tablas'); // Cambia al endpoint real
        const data = await response.json();

        // 2️⃣ Llenar tabla de Órdenes recientes
        const tablaOrdenes = document.querySelector('#tabla1 tbody');
        tablaOrdenes.innerHTML = ''; // Limpiar contenido previo

        data.ordenesRecientes.forEach(orden => {
            const fila = document.createElement('tr');
            fila.innerHTML = `
                <td>${orden.idOrden}</td>
                <td>${orden.pacienteNombre}</td>
                <td>${orden.fechaEntrega}</td>
                <td>${orden.estadoOrden}</td>
            `;
            tablaOrdenes.appendChild(fila);
        });

        // 3️⃣ Llenar tabla de Usuarios recientes
        const tablaUsuarios = document.querySelector('#tabla2 tbody');
        tablaUsuarios.innerHTML = ''; // Limpiar contenido previo

        data.usuariosRecientes.forEach(usuario => {
            const estadoClass = usuario.estado === 'ACTIVO' ? 'bg-success' : 'bg-danger';
            let rolClass = '';
            if(usuario.nombreRol === 'Paciente') rolClass = 'bg-primary';
            else if(usuario.nombreRol === 'Domiciliario') rolClass = 'bg-success';
            else rolClass = 'bg-warning';

            const fila = document.createElement('tr');
            fila.innerHTML = `
                <td><span class="badge ${rolClass}">${usuario.nombreRol}</span></td>
                <td>${usuario.nombre}</td>
                <td><span class="badge ${estadoClass}">${usuario.estado}</span></td>
            `;
            tablaUsuarios.appendChild(fila);
        });

    } catch(e) {
        console.error('Error llenando tablas:', e);
    }
}

// Ejecutar al cargar la página
document.addEventListener('DOMContentLoaded', llenarTablasAdministrador);
