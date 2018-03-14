import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
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

    public Config(File configfile) {
        this.configfile = configfile;
    }

    public void parseConfigFile() {
        if (configfile == null) {
            return;
        }
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
        System.out.println("---Parsing "+configfile.getName()+"----");
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
                    while (lines.get(checkline) != null && lines.get(checkline).startsWith("#") && !lines.get(checkline).trim().equals("")) {
                        header.add(lines.get(checkline));
                        checkline++;
                    }
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
        System.out.println("Saving to file: " + configfile.getAbsolutePath());
        try {
            PrintWriter writer = new PrintWriter(configfile, "UTF-8");
            writer.println(returnConfigString());
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String returnConfigString() {
        StringBuilder builder = new StringBuilder();
        for (String line : header) {
            builder.append(line).append("\n");
        }
        builder.append("\n");
        for (String key : sections.get(MAINSECTIONID).keySet()) {
            if (getCommentbyKey(key) != null) {
                ConfigUtils.appendNewLine(builder);
                builder.append("    ").append(getCommentbyKey(key)).append("\n");
                builder.append("    ").append(key).append(": ").append(Serilizer.convertObjecttoString(sections.get(MAINSECTIONID).get(key), "    ")).append("\n");
                builder.append("\n");
            } else {
                builder.append("    ").append(key).append(": ").append(Serilizer.convertObjecttoString(sections.get(MAINSECTIONID).get(key), "    ")).append("\n");
            }
        }
        for (String section : sections.keySet()) {
            if (!section.equals(MAINSECTIONID)) {
                ConfigUtils.appendNewLine(builder);
                if (getCommentbyKey(section) != null) {
                    builder.append(getCommentbyKey(section)).append("\n");
                }
                builder.append(section).append("\n");
                for (String key : sections.get(section).keySet()) {
                    if (getCommentbyKey(key) != null) {
                        builder.append("\n");
                        builder.append("    ").append(getCommentbyKey(key)).append("\n");
                        builder.append("    ").append(key).append(": ").append(Serilizer.convertObjecttoString(sections.get(section).get(key), "    ")).append("\n\n");
                    } else {
                        builder.append("    ").append(key).append(": ").append(Serilizer.convertObjecttoString(sections.get(section).get(key), "    ")).append("\n");
                    }
                }
            }
        }
        return builder.toString().replaceAll("\n\n\n", "\n\n");
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
        if (current.startsWith("#") || current.startsWith(" ") || current.contains(":")) {
            return false;
        }
        if (!currentsection.get().equals(MAINSECTIONID)) {
            if (lines.size() > linenumber+1 && lines.get(linenumber+1).length() > 0 && !lines.get(linenumber+1).startsWith(" ")) {
                createnewSection(MAINSECTIONID);
                return true;
            } else if (lines.size() > linenumber+2 && lines.get(linenumber+2).length() > 0 && !lines.get(linenumber+2).startsWith(" ")) {
                createnewSection(MAINSECTIONID);
                return true;
            }
        } else {
            if (lines.size() > linenumber+1 && lines.get(linenumber+1).startsWith(" ")) {
                createnewSection(current);
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
                if (value.length() == 0 && lines.size() > linenumber+1 && lines.get(linenumber + 1).contains("-")) {
                    ArrayList<Object> valuelist = new ArrayList<>();
                    int currentlineint = linenumber+1;
                    while (lines.size() > currentlineint && lines.get(currentlineint).contains("-")) {
                        valuelist.add(Serilizer.getObjectfromLine(lines.get(currentlineint).replaceAll("-", "").trim()));
                        currentlineint++;
                    }
                    sections.get(currentsection.get()).put(key, valuelist.toArray());
                }
            }
        }
    }

    public HashMap<String, String> getComments() {
        return comments;
    }

    public File getConfigfile() {
        return configfile;
    }

    public ArrayList<String> getHeader() {
        return header;
    }

    public String printHeader() {
        StringBuilder sb = new StringBuilder();
        for (String line : header) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public String getCommentbyKey(String key) {
        if (comments.containsKey(key)) {
            return comments.get(key);
        }
        return null;
    }

    public HashMap<String, Object> getKeyValuePairs(String section) {
        return sections.get(section);
    }

    public LinkedHashMap<String, LinkedHashMap<String, Object>> getSections() {
        return sections;
    }
}
