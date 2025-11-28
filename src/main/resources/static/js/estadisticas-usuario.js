async function cargarEstadisticasUsuarios() {
    try {
        const response = await fetch('/admin/estadisticas-usuarios');
        const data = await response.json();
        const ctx = document.getElementById('graficoUsuarios').getContext('2d');

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['Pacientes', 'Medicamentos', 'Órdenes Completadas', 'Activos', 'Inactivos'],
                datasets: [{
                    label: 'Cantidad',
                    data: [
                        data.pacientes,
                        data.cantidadMedicamentos,
                        data.ordenesCompletadas,
                        data.activos,
                        data.inactivos
                    ],
                    borderWidth: 1
                }]
            },
            options: { responsive: true, scales: { y: { beginAtZero: true } } }
        });
    } catch (e) {
        console.error("Error cargando estadísticas:", e);
    }
}

cargarEstadisticasUsuarios();
