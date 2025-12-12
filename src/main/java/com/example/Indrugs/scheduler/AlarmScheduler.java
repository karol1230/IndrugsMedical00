package com.example.Indrugs.scheduler;

import com.example.Indrugs.entities.Control;
import com.example.Indrugs.repositorios.ControlRepository;
import com.example.Indrugs.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AlarmScheduler {

    @Autowired
    private ControlRepository controlRepository;

    @Autowired
    private EmailService emailService;

    // Se ejecuta cada 1 minuto
    @Scheduled(fixedRate = 60000)
    public void enviarAlarmas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Control> controles = controlRepository.findAll();

        for (Control c : controles) {
            if (c.getProximoEnvio() == null || c.getFechaFinTratamiento() == null) continue;
            if (ahora.isAfter(c.getFechaFinTratamiento())) continue;

            if (!c.getProximoEnvio().isAfter(ahora)) {
                String nombreMedicamento = c.getIdMedicamento() != null ?
                        c.getIdMedicamento().getNombreMedicamento() : "Tu medicamento";

                String html = """
                    <html><body>
                    <h2 style='color:#00796b;'>Recordatorio de Medicación</h2>
                    <p>Este es un recordatorio para tomar tu medicamento.</p>
                    <ul>
                        <li><b>Medicamento:</b> %s</li>
                        <li><b>Hora:</b> %s</li>
                        <li><b>Frecuencia:</b> Cada %s horas</li>
                    </ul>
                    </body></html>
                    """.formatted(nombreMedicamento, c.getAlarmaControl(), c.getFrecuenciaMedic());

                try {
                    emailService.enviarCorreoHtml(c.getUsuario().getCorreo(),
                            "Recordatorio de medicación", html);

                    int frecuenciaHoras = 1;
                    try {
                        frecuenciaHoras = Integer.parseInt(c.getFrecuenciaMedic());
                    } catch (Exception ignored) {}

                    c.setUltimoEnvio(ahora);
                    c.setProximoEnvio(c.getProximoEnvio().plusHours(frecuenciaHoras));
                    controlRepository.save(c);

                } catch (Exception e) {
                    System.out.println("Error enviando correo: " + e.getMessage());
                }
            }
        }
    }
}

