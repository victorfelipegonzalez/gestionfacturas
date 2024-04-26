package com.gestionfacturas.gestionfacturasapi.services;

import com.gestionfacturas.gestionfacturasapi.models.ClienteModel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private JavaMailSender sender;

    private boolean sendEmail(String email, String subject, String textMessage,byte[] pdfBytes){
        boolean send = false;
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper;
        try{
            helper = new MimeMessageHelper(message,true);
            helper.setTo(email);
            helper.setText(textMessage);
            helper.setSubject(subject);

            helper.addAttachment("factura.pdf", new ByteArrayResource(pdfBytes));

            sender.send(message);
            send = true;
            LOGGER.info("FACTURA ENVIADA");
        }catch (MessagingException e){
            LOGGER.error("Hubo un error al envia email",e);
        }
        return send;
    }

    //Método con el contedido del Mail //
    public boolean sendBill(ClienteModel cliente, byte[] pdfBytes){
        String subject = "Factura de trabajos realizados";
        String contenido = "<html><body>" +
                "<p>Buenos días, "+cliente.getNombre_cliente() + "</p>" +
                "<p>Le adjutamos la factura correspondiente a los trabajos realizados</p>" +
                "<p>Gracias por confiar en nosotros</p>" +
                "<p>Un saludo</p>" +
                "</body></html>";
        return sendEmail(cliente.getCorreo_cliente(),subject,contenido,pdfBytes);
    }
}
