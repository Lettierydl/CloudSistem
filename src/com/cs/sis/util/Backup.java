package com.cs.sis.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Backup {

    private static ResultSet res;
    private static Connection con;
    private Statement st;
    private int BUFFER = 99999;
    private List<File> aquivos_destinos;

    private String nomeArquivoDestinoDefalt;
    private boolean compactarBackup = false;// nao foi testado com zip o restore

    private String mysqlBinPath = "";

    private String host = "localhost";
    private String port = "3306";
    private String user = "root";
    private String password = "mysql";
    private String db = "cloudsistem";
    private Arquivo a;

    public Backup() {
        try {
            a = new Arquivo();
            nomeArquivoDestinoDefalt = a.getBackup().getCanonicalPath();
            compactarBackup = (Boolean) a.lerConfiguracaoSistema(VariaveisDeConfiguracaoUtil.COMPACTAR_BACKUP);
            aquivos_destinos = (List<File>) a.lerConfiguracaoSistema(VariaveisDeConfiguracaoUtil.ARQUIVOS_DEFALT_BACKUP);
            if (aquivos_destinos.isEmpty()) {
                throw new IOException();
            }
        } catch (NullPointerException| ClassCastException | IOException e) {
            try {
                this.salvarConfiguracoes(nomeArquivoDestinoDefalt, compactarBackup);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        if (Arquivo.osname.startsWith("Windows")) {
            mysqlBinPath = this.getMysqlPath(user, password, db) + Arquivo.separador
                    + "bin" + Arquivo.separador;
            nomeArquivoDestinoDefalt = System.getProperty("user.dir")
                    .substring(0, 3) + Arquivo.separador;
        } else {
            //nomeArquivoDestinoDefalt = "/Users/"+ System.getProperty("user.name") + Arquivo.separador;
            mysqlBinPath = this.getMysqlPath(user, password, db) + Arquivo.separador
                    + "bin" + Arquivo.separador;
        }
    }

    /*
     * nomeArquivosDestinoDefalt pode conter mais de um arquivo separador por ;
     * compactarBackup falg que determina se o arquivo vai ser salvo compacato(true) ou nao (false)
     * */
    public void salvarConfiguracoes(String nomeArquivosDestinoDefalt, boolean compactarBackup) throws IOException {
        List<File> list = new ArrayList<File>();
        for (String path : nomeArquivosDestinoDefalt.split(";")) {
            list.add(new File(path));
        }
	a.addConfiguracaoSistema(VariaveisDeConfiguracaoUtil.COMPACTAR_BACKUP, compactarBackup);
        a.addConfiguracaoSistema(VariaveisDeConfiguracaoUtil.ARQUIVOS_DEFALT_BACKUP, list);
        
    }

    public String getData() {
        String Mysqlpath = mysqlBinPath;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"
                    + db, user, password);
            st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Process run = null;
        try {
            /*
             * System.out.println(Mysqlpath + "mysqldump --host=" + host +
             * " --port=" + port + " --user=" + user + " --password=" + password
             * + " --compact --complete-insert --extended-insert " +
             * "--skip-comments --skip-triggers " + db);
             */
            run = Runtime.getRuntime().exec(
                    Mysqlpath + "mysqldump --host=" + host + " --port=" + port
                    + " --user=" + user + " --password=" + password
                    + "  " + "--skip-comments --skip-triggers " + db);
        } catch (IOException ex) {
			// Logger.getLogger(Backup.class.getName()).log(Level.SEVERE, null,
            // ex);
        }

        InputStream in = run.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        StringBuffer temp = new StringBuffer();

        int count;
        char[] cbuf = new char[BUFFER];

        try {

            while ((count = br.read(cbuf, 0, BUFFER)) != -1) {
                temp.append(cbuf, 0, count);
            }
        } catch (IOException ex) {
            Logger.getLogger(Backup.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        try {

            br.close();
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Backup.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return temp.toString();
    }

    public String getMysqlPath(String user, String password, String db) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"
                    + db, user, password);
            st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String a = "";

        try {
            res = st.executeQuery("select @@basedir");
            while (res.next()) {
                a = res.getString(1);
            }
        } catch (Exception eee) {
            eee.printStackTrace();
        }
        return a;

    }

    public String criarBackup() throws IOException {
        byte[] data = this.getData().getBytes();
        File filedst;
        FileOutputStream dest;
        //String arquivos [] = nomeArquivoDestinoDefalt.replace("?",  Arquivo.separador).split(";");
        if (compactarBackup) {
            /*if(!arquivos[0].endsWith(Arquivo.separador)){
             arquivos[0] += Arquivo.separador;
             }*/
            filedst = new File(aquivos_destinos.get(0).getCanonicalPath() + Arquivo.separador
                    + "Backup_CloudSystem.zip");
            if (!aquivos_destinos.get(0).exists()) {
                aquivos_destinos.get(0).mkdirs();
            }
            dest = new FileOutputStream(filedst);
            ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            zip.setMethod(ZipOutputStream.DEFLATED);
            zip.setLevel(Deflater.BEST_COMPRESSION);
            zip.putNextEntry(new ZipEntry("cloudsistem_"
                    + new SimpleDateFormat("dd_MM_yyyy").format(new Date())
                    + ".sql"));
            zip.write(data);
            zip.close();
            dest.close();
        } else {
            filedst = new File(aquivos_destinos.get(0).getCanonicalPath() + Arquivo.separador
                    + "cloudsistem_"
                    + new SimpleDateFormat("dd_MM_yyyy").format(new Date())
                    + ".sql");
            if (!aquivos_destinos.get(0).exists()) {
                aquivos_destinos.get(0).mkdirs();
            }
            dest = new FileOutputStream(filedst);
            dest.write(data);
            dest.close();
        }

        String arq = filedst + "\n";
        List<File> copy = new ArrayList<File>(aquivos_destinos);
        copy.remove(0);
        for (File aq : copy) {
            try {
                Arquivo.copyFile(filedst, new File(aq + Arquivo.separador + filedst.getName()));
                arq += aq.getCanonicalPath() + Arquivo.separador + filedst.getName() + "\n";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return arq;
    }
    
    
    public String criarBackup(File destino) throws IOException {
        byte[] data = this.getData().getBytes();
        File filedst;
        FileOutputStream dest;
        //String arquivos [] = nomeArquivoDestinoDefalt.replace("?",  Arquivo.separador).split(";");
        if (compactarBackup) {
            /*if(!arquivos[0].endsWith(Arquivo.separador)){
             arquivos[0] += Arquivo.separador;
             }*/
            filedst = new File(aquivos_destinos.get(0).getCanonicalPath() + Arquivo.separador
                    + "Backup_CloudSystem.zip");
            if (!aquivos_destinos.get(0).exists()) {
                aquivos_destinos.get(0).mkdirs();
            }
            dest = new FileOutputStream(filedst);
            ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            zip.setMethod(ZipOutputStream.DEFLATED);
            zip.setLevel(Deflater.BEST_COMPRESSION);
            zip.putNextEntry(new ZipEntry("cloudsistem_"
                    + new SimpleDateFormat("dd_MM_yyyy").format(new Date())
                    + ".sql"));
            zip.write(data);
            zip.close();
            dest.close();
        } else {
            filedst = new File(aquivos_destinos.get(0).getCanonicalPath() + Arquivo.separador
                    + "cloudsistem_"
                    + new SimpleDateFormat("dd_MM_yyyy").format(new Date())
                    + ".sql");
            if (!aquivos_destinos.get(0).exists()) {
                aquivos_destinos.get(0).mkdirs();
            }
            dest = new FileOutputStream(filedst);
            dest.write(data);
            dest.close();
        }
        
        Arquivo.copyFile(filedst, destino);
        
        String arq = filedst + "\n";
        List<File> copy = new ArrayList<File>(aquivos_destinos);
        copy.remove(0);
        for (File aq : copy) {
            try {
                Arquivo.copyFile(filedst, new File(aq + Arquivo.separador + filedst.getName()));
                arq += aq.getCanonicalPath() + Arquivo.separador + filedst.getName() + "\n";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return arq;
    }

    public boolean restoreBanco(File tempFile) throws IOException {
        File restore = tempFile;
        File tempDirectory = null;
        String f[] = tempFile.getName().split(".");
        try{
            if (f[f.length - 1].equalsIgnoreCase("zip")) {
                tempDirectory = File.createTempFile("TempRestoreBackupCloudSistem", Arquivo.separador);
                tempDirectory.mkdir();
                extrairZip(tempFile, tempDirectory);
                restore = tempDirectory.listFiles()[0];
            }
        }catch(ArrayIndexOutOfBoundsException ar){}

        String Mysqlpath = mysqlBinPath;
        try {
            // prepara o comando
            StringBuilder sbComando = new StringBuilder(Mysqlpath);
            sbComando.append("mysql");

            sbComando.append(" -u");
            sbComando.append(user);

            /*
             * Se o valor da senha for diferente de vazio, adiciona o parametro
             * de senha, caso contrário não adiciona, pois pode gerar um arquivo
             * vazio
             */
            if (!password.isEmpty()) {
                // o valor do parâmetro -p não tem espaço!
                sbComando.append(" -p");
                sbComando.append(password);
            }

            sbComando.append(" ");
            sbComando.append(db);

            sbComando.append(" < ");
            sbComando.append(restore.getCanonicalPath());

			// imprime o comando
            // System.out.println(sbComando.toString());
            // executa o comando
            Runtime.getRuntime().exec(sbComando.toString());

        } catch (IOException ex) {
            Logger.getLogger(Backup.class.getName())
                    .log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try{
                tempDirectory.deleteOnExit();
            }catch(NullPointerException ne){}
        }
        return true;
    }

    public void extrairZip(File arquivoZip, File diretorio)
            throws ZipException, IOException {
        ZipFile zip = null;
        File arquivo = null;
        InputStream is = null;
        OutputStream os = null;
        byte[] buffer = new byte[BUFFER];
        try {
            // cria diretório informado, caso não exista
            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }
            if (!diretorio.exists() || !diretorio.isDirectory()) {
                throw new IOException("Informe um diretório válido");
            }
            zip = new ZipFile(arquivoZip);
            @SuppressWarnings("rawtypes")
            Enumeration e = zip.entries();
            while (e.hasMoreElements()) {
                ZipEntry entrada = (ZipEntry) e.nextElement();
                arquivo = new File(diretorio, entrada.getName());
				// se for diretório inexistente, cria a estrutura
                // e pula pra próxima entrada
                if (entrada.isDirectory() && !arquivo.exists()) {
                    arquivo.mkdirs();
                    continue;
                }
                // se a estrutura de diretórios não existe, cria
                if (!arquivo.getParentFile().exists()) {
                    arquivo.getParentFile().mkdirs();
                }
                try {
                    // lê o arquivo do zip e grava em disco
                    is = zip.getInputStream(entrada);
                    os = new FileOutputStream(arquivo);
                    int bytesLidos = 0;
                    if (is == null) {
                        throw new ZipException("Erro ao ler a entrada do zip: "
                                + entrada.getName());
                    }
                    while ((bytesLidos = is.read(buffer)) > 0) {
                        os.write(buffer, 0, bytesLidos);
                    }
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception ex) {
                        }
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public List<File> getAquivos_destinos() {
        return aquivos_destinos;
    }

    public void setAquivos_destinos(List<File> aquivos_destinos) {
        this.aquivos_destinos = aquivos_destinos;
    }

    public String getNomeArquivoDestinoDefalt() {
        if (aquivos_destinos.size() > 1) {
            String arq_defalt = "";
            for (File ar : aquivos_destinos) {
                arq_defalt += ar + ";";
            }
            return arq_defalt;
        }
        return nomeArquivoDestinoDefalt;
    }

    public boolean isCompactarBackup() {
        return compactarBackup;
    }

}
