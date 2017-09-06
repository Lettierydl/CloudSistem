/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.controller;

import com.cs.sis.util.Arquivo;
import com.cs.sis.util.VariaveisDeConfiguracaoUtil;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

/**
 *
 * @author Lettiery
 */
public class EmailController {

    private String emails = ""; 
    public static final String ASSUNTO_RELATORIOS = "Relatórios";
    private static final String HOST_NAME = "smtp.gmail.com";
    private static final String EMAIL_REMETENTE = "brdistribuidoradecimento@gmail.com";
    private static final String EMAIL_SENHA = "9242drof";

    public EmailController(){
        Arquivo a = new Arquivo();
        emails = (String)a.lerConfiguracaoSistema(VariaveisDeConfiguracaoUtil.EMAILS_ADMINISTRACAO);
    }
    
    public void  enviarEmails(File anexo, String assunto, String texto) throws EmailException {
        for (String email : emails.split(";")) {
            MultiPartEmail e = criarEmail(email,
                    "[BR DISTRIBUIDORA] "
                    + assunto,
                    msg(email, assunto));
            e.attach(this.criarAnexo(anexo, anexo.getName(), anexo.getName()));
            
            try{
                System.out.println(e.send());//e.send();
            }catch(Exception | Error er){
                er.printStackTrace();
            }
        }
    }
    
    public void  enviarEmails(List<File> anexos, String assunto, String texto) throws EmailException {
        for (String email : emails.split(";")) {
            MultiPartEmail e = criarEmail(email,
                    "[BR DISTRIBUIDORA] "
                    + assunto,
                    msg(email, assunto));
            for (File f : anexos) {
                e.attach(this.criarAnexo(f, f.getName(), f.getName()));
            }
            try{
                System.out.println(e.send());//e.send();
            }catch(Exception | Error er){
                er.printStackTrace();
            }
        }
    }

    private MultiPartEmail criarEmail(String destinatario, String assunto, String msg) throws EmailException {
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName(HOST_NAME); // o servidor SMTP para envio do e-mail
        email.addTo(destinatario); //destinatário
        email.setFrom(EMAIL_REMETENTE); // remetente
        email.setSubject(assunto); // assunto do e-mail
        email.setMsg(msg); //conteudo do e-mail
        email.setAuthentication(EMAIL_REMETENTE, new StringBuffer(EMAIL_SENHA).reverse().toString());
        email.setSmtpPort(465);
        email.setSSL(true);
        email.setTLS(true);
        return email;
    }
    private String msg(String email, String assunto) {
        return msg(email, assunto, 0);
    }

    
    private String msg(String email, String assunto, double debito) {
        Arquivo a = new Arquivo();
        String s = a.lerMessange(assunto);
        if (s.isEmpty()) {
            salvarMenssagensPadrao();
            s = a.lerMessange(assunto);
        }
        s = s.replace("<TITULO>", "BR Distribuidora")
                .replace("<SALDACAO>", saldacao());
        return s;
    }
    
    public void salvarMenssagensPadrao() {
        Arquivo a = new Arquivo();
        a.salvarMessange("<SALDACAO>\n\n"
                + "Gostariamos de relatar as operações ocorridas nessa semana\n"
                + "em anexo, pode ser encontrado o delatório detalhado.\n"
                + "\nNo caso de dúvidas, contate-nos.\n\n"
                + "<TITULO>\n\n", ASSUNTO_RELATORIOS);
        
        
        
    }
    
    private String saldacao() {
        int hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hora < 13) {
            return "Bom Dia";
        } else if (hora < 18) {
            return "Boa Tade";
        } else {
            return "Boa Noite";
        }
    }


    private EmailAttachment criarAnexo(File anexo, String descricao, String nome) {
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(anexo.getAbsolutePath()); //caminho da imagem
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription(descricao);
        attachment.setName(nome);
        return attachment;
    }
}
