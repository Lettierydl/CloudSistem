/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Funcionalidades;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * @author leo
 */
public class Arquivo {

    public static String osname = System.getProperty("os.name");
    private static String defalt;
    public static final String separador = System.getProperty("file.separator");
    private static final String CONFIGURACAO_BACKUP = "Configuracao_backup.csi";
    private static File central;
    private static File temp;
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
    
    public List<Object> lerConfiguracoesBackup(){
        return (List<Object>) recuperarArquivo(central, CONFIGURACAO_BACKUP);
    }
    
    public void salvarConfiguracoesBackup(List<Object> config){
        salvarArquivo(config, central, CONFIGURACAO_BACKUP);
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
}
