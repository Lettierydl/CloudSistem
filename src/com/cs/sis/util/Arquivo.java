/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cs.sis.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leo
 */
public class Arquivo {

    public static String osname = System.getProperty("os.name");
    private static String defalt;
    public static final String separador = System.getProperty("file.separator");
    private static final String CONFIGURACAO_SISTEMA = "Configuracao.csi";
    private static File central;
    private static File temp;
    private static File config;
    private static File backup;
    private static File relatorio;

    public Arquivo() {
        if (Arquivo.osname.startsWith("Windows")) {
            Arquivo.defalt = System.getProperty("user.dir").substring(0, 3);
        } else {
            Arquivo.defalt = "/Users/" + System.getProperty("user.name");
        }
        central = new File(defalt + separador + "CloudSistem" + separador);
        temp = new File(central + separador + "Temporarios" + separador);
        backup = new File(central + separador + "Backup" + separador);
        relatorio = new File(central + separador + "Relatorios" + separador);
        config = new File(central + separador + "Configs" + separador);
        config.mkdir();
        temp.mkdirs();
        backup.mkdir();
        relatorio.mkdirs();
    }

    private synchronized Object recuperarArquivo(File pasta, String nome) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(pasta + separador + nome));
            Object c = in.readObject();
            in.close();
            return c;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    private synchronized void salvarArquivo(Object c, File pasta, String nome) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(pasta + separador + nome));
            out.writeObject(c);
            out.close();
        } catch (Exception e) {
        }

    }

    public void salvarTemporaria(Object c, String nome) {
        this.salvarArquivo(c, temp, nome);
    }

    public Object recuperarTemporaria(String nome) {
        return this.recuperarArquivo(temp, nome);
    }

    public void salvarRegistro(Object c, String nome) {
        this.salvarArquivo(c, central, nome);
    }

    public Object recuperarRegistro(String nome) {
        return this.recuperarArquivo(central, nome);
    }
    
    public File getRelatorio(){
        return Arquivo.relatorio;
    }
    
    public File getBackup(){
        return Arquivo.backup;
    }
    
    /*
    public List<Object> lerConfiguracoesBackup(){
        return (List<Object>) recuperarArquivo(central, CONFIGURACAO_BACKUP);
    }
    
    public void salvarConfiguracoesBackup(List<Object> config){
        salvarArquivo(config, central, CONFIGURACAO_BACKUP);
    }*/
    
    public Map<String, Object> lerConfiguracoesSistema(){
        Map<String, Object> conf = (Map<String, Object>) recuperarArquivo(config, CONFIGURACAO_SISTEMA);
        if(conf == null || conf.isEmpty()){
            salvarConfiguracoesDefalt();
            conf = (Map<String, Object>) recuperarArquivo(config, CONFIGURACAO_SISTEMA);
        }
        return conf;
    }
    
    public Object lerConfiguracaoSistema(String chave){
        Map<String, Object> conf = (Map<String, Object>) recuperarArquivo(config, CONFIGURACAO_SISTEMA);
        if(conf == null || conf.isEmpty()){
            salvarConfiguracoesDefalt();
            conf = (Map<String, Object>) recuperarArquivo(config, CONFIGURACAO_SISTEMA);
        }
        return conf.get(chave);
    }
    
    public void addConfiguracaoSistema(String chave, Object value){
        Map<String, Object> conf;
        try{
            conf = lerConfiguracoesSistema();
            conf.put(chave, value);
        }catch(Exception e){
            conf = new HashMap<String, Object>();
            conf.put(chave, value);
        }
        salvarArquivo(conf, config, CONFIGURACAO_SISTEMA);
    }
   public static byte[] lerBytesPDF(File f) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(f);
        //System.out.println(file.exists() + "!!");
        //InputStream in = resource.openStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum); //no doubt here is 0
                //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

    public static void criarPDF(byte[] byts, File destino) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(destino);
        fos.write(byts);
        fos.flush();
        fos.close();
    }

    public String lerMessange(String assunto) {
        Object o = recuperarArquivo(config, assunto + ".txt");
        if (o != null) {
            return o.toString();
        } else {
            salvarArquivo("", config, assunto + ".txt");
            return "";
        }
    }

    public void salvarMessange(String msg, String assunto) {
        salvarArquivo(msg, config, assunto + ".txt");
    }
    public static void copyFile(File source, File destination) throws IOException {
        if (destination.exists()){
            destination.delete();
        }
        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destinationChannel = new FileOutputStream(destination).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(),
                    destinationChannel);
        } finally {
            if (sourceChannel != null && sourceChannel.isOpen())
                sourceChannel.close();
            if (destinationChannel != null && destinationChannel.isOpen())
                destinationChannel.close();
       }
   }

    private void salvarConfiguracoesDefalt() {
        Map<String, Object> conf = new HashMap();
        conf.put(VariaveisDeConfiguracaoUtil.IP_DO_BANCO, "localhost");
        conf.put(VariaveisDeConfiguracaoUtil.ID_DO_CAIXA, "1");
        conf.put(VariaveisDeConfiguracaoUtil.ARQUIVOS_DEFALT_BACKUP, backup.getAbsoluteFile());
        conf.put(VariaveisDeConfiguracaoUtil.COMPACTAR_BACKUP, true);
        conf.put(VariaveisDeConfiguracaoUtil.EXTRATEGIA_DE_CONEXAO, "Local");
        conf.put(VariaveisDeConfiguracaoUtil.QUANTIDADE_CAIXA, "2");
        conf.put(VariaveisDeConfiguracaoUtil.ATIVAR_LIMITE_REGISTRO_MOSTRADOS, true);
        conf.put(VariaveisDeConfiguracaoUtil.EMAILS_ADMINISTRACAO, "belosmarvilar@gmail.com;neidevilarc@gmail.com");
        salvarArquivo(conf, config, CONFIGURACAO_SISTEMA);
    }
}
