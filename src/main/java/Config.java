import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by N.Hartmann on 14.03.2018.
 * Copyright 2017
 */
public class Config {
    private LinkedHashMap<String, String> comments = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, LinkedHashMap<String, Object>> sections = new LinkedHashMap<>();
    private ArrayList<String> header = new ArrayList<>();

    private File configfile;
    private AtomicReference<String> currentsection = new AtomicReference<>("<<main>>");
    private final String MAINSECTIONID = "<<main>>";
    private final String NEWLN = "\n";

    public Config(File configfile) {
        this.configfile = configfile;
    }

    private void parseConfigFile() {
        if (configfile == null) {
            return;
        }
        //TODO: System.out.println("Loading: "+configfile.getAbsolutePath());
        try {
            parse(new BufferedReader(new FileReader(configfile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void parseString(String string) {
        parse(new BufferedReader(new StringReader(string)));
    }

    private void parse(BufferedReader reader) {
        //TODO: System.out.println("Parsing " + configfile.getAbsolutePath());
        ArrayList<String> lines = new ArrayList<>();
        try {
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            createnewSection(MAINSECTIONID);
            for (int i = 0; i < lines.size(); i++) {
                String current = lines.get(i);
                if (i == 0 && current.startsWith("#")) {
                    int checkline = 0;
                    ArrayList<String> headerlist = new ArrayList<>();
                    while (lines.get(checkline) != null && lines.get(checkline).startsWith("#") && !lines.get(checkline).trim().equals("")) {
                        headerlist.add(lines.get(checkline));
                        checkline++;
                    }
                    setHeader(headerlist.toArray(new String[]{}));
                    i = i + checkline;
                }
                if (!processSection(current, currentsection, lines, i)) {
                    processComment(current, lines, i);
                    processKeyValue(current, currentsection, lines, i);
                }
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveToFile() {
        if (configfile == null || !configfile.exists()) {
            return;
        }
        //TODO: System.out.println("Saving: " + configfile.getAbsolutePath());
        try {
            PrintWriter writer = new PrintWriter(configfile, "UTF-8");
            writer.println(returnConfigString());
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void reload() {
        if (configfile == null || !configfile.exists()) {
            return;
        }
        parseConfigFile();
    }

    private void newlines(int count, StringBuilder builder) {
        for (int i = 0; i < count; i++) {
            builder.append(NEWLN);
        }
    }

    public String returnConfigString() {
        StringBuilder builder = new StringBuilder();
        builder.append(printHeader());
        builder.append("\n");
        if (sections.get(MAINSECTIONID) != null) {
            for (String key : sections.get(MAINSECTIONID).keySet()) {
                if (getCommentbyKey(key) != null) {
                    ConfigUtils.appendNewLine(builder);
                    builder.append(getCommentbyKey(key));
                    newlines(1, builder);
                    builder.append(key).append(": ").append(Serilizer.convertObjecttoString(sections.get(MAINSECTIONID).get(key), "    ")).append(NEWLN);
                    newlines(1, builder);
                } else {
                    builder.append(key).append(": ").append(Serilizer.convertObjecttoString(sections.get(MAINSECTIONID).get(key), "    ")).append(NEWLN);
                }
            }
        }
        for (String section : sections.keySet()) {
            buildSection(builder, section, "", "");
        }
        return builder.toString().replaceAll("\n\n\n", "\n\n");
    }

    private void buildSection(StringBuilder builder, String section, String indent, String parent) {
        if (!section.equals(MAINSECTIONID)) {
            String realsection = section;
            if (parent.length() > 0) {
                realsection = parent + "." + section;
            }
            if (!section.contains(".")) {
                ConfigUtils.appendNewLine(builder);
                if (getCommentbyKey(realsection) != null) {
                    builder.append(indent).append(getCommentbyKey(realsection));
                    newlines(1, builder);
                }
                builder.append(indent).append(section).append(":");
                newlines(1, builder);
                for (String key : sections.get(realsection).keySet()) {
                    String realsectionkey = key;
                    if (realsection.length() > 0) {
                        realsectionkey = realsection + "." + key;
                    }
                    if (getCommentbyKey(realsectionkey) != null) {
                        newlines(1, builder);
                        builder.append(indent).append("    ")
                                .append(getCommentbyKey(realsectionkey));
                        newlines(1, builder);
                        builder.append(indent).append("    ")
                                .append(key).append(": ")
                                .append(Serilizer.convertObjecttoString(sections.get(realsection).get(key), "    "));
                        newlines(2, builder);
                    } else {
                        builder.append(indent).append("    ")
                                .append(key).append(": ")
                                .append(Serilizer.convertObjecttoString(sections.get(realsection).get(key), "    "));
                        newlines(1, builder);
                    }
                }
                checkforchildrenSection(builder, section, indent);
            }
        }
    }

    private void checkforchildrenSection(StringBuilder builder, String section, String indent) {
        for (String inner : sections.keySet()) {
            if (inner.contains(".")) {
                String[] innersections = inner.split("\\.");
                if (innersections.length == 2 && innersections[0].equals(section)) {
                    buildSection(builder, innersections[1], indent + "    ", section);
                }
            }
        }
    }

    private void createnewSection(String name) {
        if (!sections.containsKey(name)) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            currentsection.set(name);
            sections.put(name, map);
        } else {
            currentsection.set(name);
        }
    }

    private String getKeyofLine(String current) {
        if (!current.contains("#") && current.contains(":")) {
            return current.substring(0, current.indexOf(":")).trim();
        }
        if (!current.contains("#") && !current.contains(":")) {
            return current.trim();
        }
        return null;
    }

    private void processComment(String current, ArrayList<String> lines, int linenumber) {
        if (current.contains("#") && lines.get(linenumber + 1) != null && !lines.get(linenumber + 1).contains("#")) {
            String nextline = lines.get(linenumber + 1);
            comments.put(getKeyofLine(nextline), current.trim());
        }
    }

    private boolean processSection(String current, AtomicReference<String> currentsection, ArrayList<String> lines, int linenumber) {
        if (current.startsWith("#")) {
            return false;
        }
        if (!currentsection.get().equals(MAINSECTIONID)) {
            if (lines.size() > linenumber + 1 && lines.get(linenumber + 1).length() > 0 && !lines.get(linenumber + 1).startsWith(" ")) {
                createnewSection(MAINSECTIONID);
                return true;
            } else if (lines.size() > linenumber + 2 && lines.get(linenumber + 2).length() > 0 && !lines.get(linenumber + 2).startsWith(" ")) {
                createnewSection(MAINSECTIONID);
                return true;
            }
        }
        if (current.contains(":")) {
            String value = current.substring(current.indexOf(":") + 1, current.length()).trim();
            if (value.length() == 0 && lines.size() > linenumber + 1 && !lines.get(linenumber + 1).contains("-")) {
                if (!current.startsWith(" ")) {
                    createnewSection(current.substring(0, current.indexOf(":")));
                } else {
                    createnewSection(currentsection.get() + "." + current.substring(0, current.indexOf(":")).trim());
                }
                return true;
            }
        }
        return false;
    }

    private boolean checkNormalValue(String current) {
        return current.contains(":") && !current.contains("#") && !current.contains("-");
    }

    private void processKeyValue(String current, AtomicReference<String> currentsection, ArrayList<String> lines, int linenumber) {
        String key = getKeyofLine(current);
        if (key != null) {
            if (checkNormalValue(current)) {
                String value = current.substring(current.indexOf(":") + 1, current.length()).trim();
                if (value.length() > 0 && sections.get(currentsection.get()) != null) {
                    Object valueobject = Serilizer.getObjectfromLine(value);
                    sections.get(currentsection.get()).put(key, valueobject);
                }
                if (value.length() == 0 && lines.size() > linenumber + 1 && lines.get(linenumber + 1).contains("-")) {
                    ArrayList<Object> valuelist = new ArrayList<>();
                    int currentlineint = linenumber + 1;
                    while (lines.size() > currentlineint && lines.get(currentlineint).contains("-")) {
                        valuelist.add(Serilizer.getObjectfromLine(lines.get(currentlineint).replaceAll("-", "").trim()));
                        currentlineint++;
                    }
                    sections.get(currentsection.get()).put(key, valuelist.toArray());
                }
            }
        }
    }

    private String printHeader() {
        int longestline = 0;
        for (String headerline : header) {
            if (headerline.length() > longestline) {
                longestline = headerline.length();
            }
        }
        longestline = longestline + 6;
        StringBuilder sb = new StringBuilder();
        int headerline = 0;
        if (longestline % 2 > 0) {
            headerline = longestline;
        } else {
            headerline = longestline + 1;
        }
        sb.append("#").append(ConfigUtils.createLine(headerline)).append("#").append("\n");
        for (String line : header) {
            int linelenght = line.length();
            String left = ConfigUtils.createFiller((longestline - linelenght) / 2);
            String right = "";
            if (linelenght % 2 > 0) {
                right = ConfigUtils.createFiller(((longestline - linelenght) / 2) - 1);
            } else {
                right = ConfigUtils.createFiller(((longestline - linelenght) / 2));
            }
            sb.append("#").append(left).append(line).append(right).append("#").append("\n");
        }
        sb.append("#").append(ConfigUtils.createLine(headerline)).append("#");
        return sb.toString();
    }


    private String getCommentbyKey(String key) {
        if (comments.containsKey(key)) {
            return comments.get(key);
        }
        return null;
    }

    public void createSection(String name) {
        if (!sections.containsKey(name)) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            sections.put(name, map);
        }
    }

    private void addComment(String name, String comment) {
        comments.put(name, "#" + comment);
    }

    public void addHeader(String[] header) {
        configureHeader(header);
    }

    public void setHeader(String[] header) {
        this.header.clear();
        configureHeader(header);
    }

    private void configureHeader(String[] header) {
        for (int i = 0; i < header.length; i++) {
            if (!header[i].contains("___")) {
                header[i] = header[i];
                if (header[i].contains("#")) {
                    this.header.add(header[i].replaceAll("#", "").trim());
                } else {
                    this.header.add(header[i].trim());
                }
            }
        }
    }

    public Object getOrDef(String optionpath, Object value) {
        if (get(optionpath) == null) {
            set(optionpath, value);
        }
        return value;
    }

    public Object getOrDef(String optionpath, Object value, String comment) {
        if (get(optionpath) == null) {
            set(optionpath, value);
        }
        if (getCommentbyKey(optionpath) == null) {
            addComment(optionpath, comment);
        }
        return get(optionpath);
    }


    public Object get(String optionpath) {
        String[] option = optionpath.split("\\.");
        StringBuilder orginalpath = new StringBuilder();
        String key;
        if (option.length > 1) {
            key = option[option.length - 1];
        } else {
            key = option[0];
        }
        while (optionpath.contains(".")) {
            String[] options = optionpath.split("\\.", 0);
            if (orginalpath.length() > 0) {
                orginalpath.append(".");
            }
            orginalpath.append(options[0]);
            optionpath = optionpath.replace(options[0] + ".", "");
        }
        if (sections.get(orginalpath.toString()) != null) {
            return sections.get(orginalpath.toString()).get(key);
        }
        return null;
    }

    public void set(String optionpath, Object value, String comment) {
        addComment(optionpath, comment);
        set(optionpath, value);
    }

    public void set(String optionpath, Object value) {
        String[] option = optionpath.split("\\.");
        StringBuilder orginalpath = new StringBuilder();
        String key;
        if (option.length > 1) {
            key = option[option.length - 1];
        } else {
            key = option[0];
        }
        while (optionpath.contains(".")) {
            String[] options = optionpath.split("\\.", 0);
            if (orginalpath.length() > 0) {
                orginalpath.append(".");
            }
            orginalpath.append(options[0]);
            createSection(orginalpath.toString());
            optionpath = optionpath.replace(options[0] + ".", "");
        }
        if (sections.get(orginalpath.toString()) != null) {
            sections.get(orginalpath.toString()).put(key, value);
        }
    }

    public boolean containsKey(String path) {
        String key;
        String[] option = path.split("\\.");
        if (option.length > 1) {
            key = option[option.length - 1];
        } else {
            key = option[0];
        }
        boolean found = false;
        if (sections.containsKey(path) || sections.containsKey(path.replace("." + key, ""))) {
            found = true;
        }
        for (String section : sections.keySet()) {
            if (section.equals(path) || section.equals(key)) {
                found = true;
            }
        }
        return found;
    }

}
