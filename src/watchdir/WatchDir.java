/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package watchdir;

/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import eventlog.EEventLogException;
import eventlog.EventJournalFactory;
import eventlog.IEventJournal;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example to watch a directory (or tree) for changes to files.
 */
public class WatchDir {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
    private boolean trace = false;
    private static IEventJournal log;
    public static String current_path;
    public static String LOG_LVL;
    public static String EDI_PATH;
    public static String EMAIL_FROM;
    public static String DBNAME;
    public static String DBUSER;
    public static String DBPASSWORD;
    public static String DBPORT;
    public static String DBCONNECTIONSTRING;
    private static int count;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                log.logEvent(Level.INFO, String.format("register: %s\n", dir));
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    WatchDir(Path dir, boolean recursive) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.recursive = recursive;

        if (recursive) {
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done.");
        } else {
            register(dir);
        }

        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() throws FileNotFoundException, SQLException, IOException {

        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                count++;
                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.println("Count : "+count);
                if (count == 2) {
                    System.out.format("%s: %s\n", event.kind().name(), child);

                    log.logEvent(Level.INFO, String.format("%s: %s\n", event.kind().name(), child));

                    ms_dbManager dbm = new ms_dbManager(DBCONNECTIONSTRING, DBUSER, DBPASSWORD, DBNAME);
                    log.logEvent(Level.FINER, "Ceneccted to database : " + DBNAME);
                    String _NrDok = new parseEdi(String.format("%s", dir.resolve(name))).read();
                    log.logEvent(Level.FINER, "Pobrano numer dokumentu : " + _NrDok);
                    String fileName = String.format("%s", dir.resolve(name));
                    log.logEvent(Level.FINER, "Plik do wysłania  : " + fileName);
                    String emial_to = dbm.getEmailByNrDok(_NrDok);
                    log.logEvent(Level.FINER, "mail pod kótry ma zastać wysłany dokumnet   : " + emial_to);
                    if (emial_to.length() > 1) {
                        log.logEvent(Level.FINER, "Tworzę obiekt SendEmailFrame ");
                        try {
                            SendEmailFrame em = new SendEmailFrame(emial_to, _NrDok, fileName, log);
                            //em.setName(emial_to);
                            //em.setNrdok(_NrDok);
                            log.logEvent(Level.FINER, "wyświetalam  SendEmailFrame ");
                            em.setF(child.getFileName().toFile());
                             count =  0;
                        } catch (Exception e) {
                            log.logEvent(Level.FINER, "błąd przy towrzeniu obkektu SendEmailFrame  ", e);
                        }
                        //em.showF();
                    } else {
                        log.logEvent(Level.FINE, "Brak adresu email pomijam wysyłanie dokumentu  ");
                    }
                    log.logEvent(Level.INFO, "Koniec send email ");
                }
                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
                
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                count = 0;

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
           
        }
        
    }

    static void usage() {
        System.err.println("usage: java WatchDir [-r] dir");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException, FileNotFoundException, SQLException {
        boolean recursive = false;
        /* parse arguments
        if (args.length == 0 || args.length > 2) {
            usage();
        }
        boolean recursive = false;
        int dirArg = 0;
        if (args[0].equals("-r")) {
            if (args.length < 2) {
                usage();
            }
            recursive = true;
            dirArg++;
        }*/
        Properties prop = new Properties();
        try {

            InputStream input = new FileInputStream("config.properties");

            prop.load(input);
            LOG_LVL = prop.getProperty("log_level", "");
            EDI_PATH = prop.getProperty("edi_path", "");
            EMAIL_FROM = prop.getProperty("mail_smtp_from", "");
            DBNAME = prop.getProperty("dbname", "pcmarket");
            DBCONNECTIONSTRING = prop.getProperty("dbconnectstring", "jdbc:sqlserver://localhost");
            DBUSER = prop.getProperty("dbuser", "sa");
            DBPASSWORD = prop.getProperty("dbpassword", "");
            DBPORT = prop.getProperty("dbport", "1433");

        } catch (IOException ex) {
            System.err.println("brak pliku config.properties");
            System.exit(1);
        }

        Path currentRelativePath = Paths.get("");
        current_path = currentRelativePath.toAbsolutePath().toString();
        try {
            log = EventJournalFactory.createEventJournal("T", current_path + "\\WatchDir_log", 2000000, 64, Level.parse(LOG_LVL));
        } catch (EEventLogException ex) {
            Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
        }
        // register directory and process its events
        Path dir = Paths.get(EDI_PATH);
        new WatchDir(dir, recursive).processEvents();
    }
}
