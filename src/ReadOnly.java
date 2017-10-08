package src;

/**
 * @author Joshua Nelson
 * @version 0.1 (2017-9-17)
 * Classifier; a simple program to organize a directory
 *
 * Copyright (C) 2017  Joshua Nelson
 */

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.JCommander;

public final class ReadOnly {

    protected final static class Validator {

        protected static Path path;

        final static boolean pathIsValid(String arg) {
            path = filesystem.getPath(arg).toAbsolutePath();
            if (Files.exists(path) && Files.isDirectory(path) && Files.isWritable(path)) {
                return true;
            }

            return false;
        }
    }

    protected static final FileSystem filesystem = FileSystems.getDefault();
    protected static final Runtime runtime = Runtime.getRuntime();
    protected static final Parameters parameters = new Parameters();
    protected static final JCommander instance = makeInstance();

    protected static class EditorNotFoundException extends IOException {
        private static final long serialVersionUID = 7369687033416439591L;

        EditorNotFoundException() {
            super();
        }

        EditorNotFoundException(String message) {
            super(message);
        }
    }

    protected static final String HELP = "Usage: Classifier DIRECTORY\n" + "    -h, --help";

    protected static final String COPYRIGHT = "Copyright (C) 2017  Joshua Nelson\n" + "   \n" + "   \n"
            + "    This program is free software: you can redistribute it and/or modify\n"
            + "    it under the terms of the GNU General Public License as published by\n"
            + "    the Free Software Foundation, either version 3 of the License, or\n"
            + "    (at your option) any later version.\n" + "  \n"
            + "    This program is distributed in the hope that it will be useful,\n"
            + "    but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
            + "    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
            + "    GNU General Public License for more details.\n" + "  \n"
            + "    You should have received a copy of the GNU General Public License\n"
            + "    along with this program.  If not, see <http://www.gnu.org/licenses/>.";

    protected static String OS = System.getProperty("os.name").toLowerCase();

    protected static final Path MASTERCONF = findConf();

    protected static final List<String> getConf(Path path) throws IOException {

        if (path != null) {
            Path LOCALCONF = path.resolve(".classifier.conf");
            if (isSuitableConfig(LOCALCONF)) {
                return Files.readAllLines(LOCALCONF);
            }
        }
        if (isSuitableConfig(MASTERCONF)) {
            return Files.readAllLines(MASTERCONF);
        } else {
            return ReadOnly.DEFAULT;
        }
    }

    protected static final String listToString(List<String> list) {
        String s = "";
        for (String i : list) {
            s += i;
            s += "\n";
        }
        return s;
    }

    protected static final Map<String, List<String>> parseConfigList(List<String> config) {

        Map<String, List<String>> tempMap = new HashMap<String, List<String>>();

        for (String line : config) {
            String[] t = line.split(": "); // sometimes I miss python :(
            String folder = t[0];
            List<String> extensions = Arrays.asList(t[1].split(", "));
            tempMap.put(folder, extensions);
        }
        return tempMap;
    }

    protected static final void checkStackTraceOnError(Exception e) {
        if (parameters.verbose) {
            e.printStackTrace();
        } else {
            System.out.println("Run with -V for stack trace.");
        }
        parameters.exitImmediately = true;
    }

    private static final void moveTo(Path directoryPath, File file) throws IOException {
        File destination = directoryPath.toFile();
        if (!destination.exists()) {
            destination.mkdir();
        } else if (!destination.isDirectory()) {
            throw new IllegalStateException("You have an existing file with the same name as " + destination
                    + ".\nChange the existing file or the config for Classifier.");
        }

        Path filePath = file.toPath();
        Files.move(filePath, directoryPath.resolve(filePath.getFileName()));

    }

    protected static final void classifyByExtension(Map<String, List<String>> config, Path directoryPath) {

        File directory = directoryPath.toFile();

        for (File file : directory.listFiles()) {
            String[] split = file.getName().split("\\.");
            String extension = split[split.length - 1];
            for (String s : config.keySet()) {
                if (config.get(s).contains(extension)) {
                    try {
                        moveTo(directoryPath.resolve(s), file);
                    } catch (IOException e) {
                        System.out.println("Could not move file " + file + " to " + directoryPath);
                    }
                }
            }
        }

    }

    protected static final void edit(Path path) throws IOException {
        File file = path.toFile();
        if (!file.exists()) {
            file.createNewFile();
            new FileOutputStream(file).write(listToString(DEFAULT).getBytes());
        }
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.EDIT)) {
            Desktop.getDesktop().edit(file);
        } else {
            String command = "";
            if (OS.contains("linux")) {
                if (Files.exists(filesystem.getPath("/usr/bin/xdg-open"))) {
                    command = "/usr/bin/xdg-open " + file;
                } else if (Files.exists(filesystem.getPath("/usr/bin/edit"))) {
                    command = "/usr/bin/edit " + file;
                } else {
                    throw new EditorNotFoundException("Cannot find a terminal editor!");
                }
            } else if (OS.contains("windows")) {
                command = "RUNDLL32.EXE SHELL32.DLL,OpenAs_RunDLL " + file;
                // untested, honestly doubt it'll work. no one uses cmd anyway
            } else if (OS.contains("darwin")) { // macintosh
                command = "open " + file;
            }
            runtime.exec(command);
        }

    }

    protected static final List<String> DEFAULT = Arrays.asList(new String[] {
            "Audio: aa, aac, aiff, amr, dvf, flac, gsm, m4a, m4b, m4p, midi, mp3, msv, ogg, ra, wav, wma",
            "Ringtones: m4r, mmf, srt",
            "Videos: 3g2, 3gp, amv, avi, flv, f4a, f4p, f4v, gifv, m4p, m4v, mkv, mp2, mp4, mpeg, mpg, ogv, rm, svi, ts, vob, webm, wmv",
            "Pictures: bmp, bpg, gif, ico, jpeg, jpg, odg, png, psd, rgbe, svg, tiff, webp, vml",
            "Archives: 7z, bz2, cpio, dmg, gz, iso, lz, rar, tar, tgz, xz, zip",
            "Documents: ai, atom, doc, docx, kdb, kdbx, odf, odm, odp, ods, odt, pdf, ppsx, ppt, pptx, pub, qif, rtf, sxw, xls, xlsv, xlsx, xml, xt",
            "Webpages: asp, aspx, cgi, htm, html, xhtml",
            "Programming: a, c, cljs, coffee, class, d, e, el, erb, fth, go, java, js, lua, lisp, m, o, p, php, pl, pm, py, pyc, pyo, r, rb, so, tcl",
            "Plain Text: asc, cer, cfg, conf, crt, css, csv, ini, inf, json, log, md, pem, pub, ppk, ssh, txt, xml, yaml",
            "Books: chm, epub, fb2, mobi", "Packages: deb, ebuild, jar, rpm",
            "Programs: bat, cmd, com, exe, msi, out, sh, vbs" });

    private static Path findConf() {
        Path result = filesystem.getPath(System.getProperty("user.home"));
        if (OS.contains("linux")) {
            return result.resolve(".config").resolve("classifier");
        }
        return result.resolve(".classifier.conf");
    }

    private static final JCommander makeInstance() {

        JCommander newInstance = JCommander.newBuilder().addObject(parameters).build();

        return newInstance;
    }

    private static final boolean isSuitableConfig(Path path) throws IOException {
        if (Files.exists(path) && Files.isReadable(path) && Files.size(path) > 0) {
            return true;
        }
        return false;
    }

}
