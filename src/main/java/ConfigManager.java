import java.io.File;
import java.util.HashMap;

/**
 * Created by N.Hartmann on 14.03.2018.
 * Copyright 2017
 */
public class ConfigManager {

    HashMap<String, Config> configs = new HashMap<>();


    public ConfigManager() {

    }
    public Config getConfig(String path) {
        if (configs.containsKey(path)) {
            return configs.get(path);
        }
        try {
            File configfile = new File(path);
            if (!configfile.exists()) {
                if (!configfile.getParentFile().mkdirs()) {
                    System.err.println("Folder exists? or no Access");
                }
                if (!configfile.createNewFile()) {
                    System.err.println("File exists? or no Access");
                }
            }
            Config config = new Config(configfile);
            configs.put(path, config);
            config.reload();
            return config;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public void copyRessource(String path, Class classinstace, String ressource) {
        try {
            File configfile = new File(classinstace.getResource(ressource).toURI());
            if (!configfile.exists()) {
                throw new Exception("No File found in Ressources");
            }
            Config config = new Config(configfile);
            config.reload();

            Config finalconfiginst = getConfig(path);
            finalconfiginst.parseString(config.returnConfigString());
            finalconfiginst.saveToFile();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void saveToFile(String path) {
        if (configs.containsKey(path)) {
            configs.get(path).saveToFile();
        } else {
            getConfig(path);
            saveToFile(path);
        }
    }
}
