import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.HashMap;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertSame;

/**
 * Created by N.Hartmann on 14.03.2018.
 * Copyright 2017
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainTest {
    private Config config;
    private ConfigManager manager;

    @Test
    public void aaa_parseRessoureConfig() {
        if (manager == null) {
            manager = new ConfigManager();
        }
        manager.copyRessource("C:\\Users\\N.Hartmann\\workspace\\ymlconfig\\config.conf", Config.class, "test.yml");
        config = manager.getConfig("C:\\Users\\N.Hartmann\\workspace\\ymlconfig\\config.conf");
        assertNotNull("should not be null", config);
    }

    @Test
    @Ignore
    public void aab_testKeyComments() {
        System.out.println("---Start Printing---");
        for (String section: config.getSections().keySet()) {
            HashMap<String, Object> keyvaluepairs = config.getKeyValuePairs(section);
            for (String key : keyvaluepairs.keySet()) {
                System.out.println("S: "+section+"  K: "+key+"  V: "+keyvaluepairs.get(key));
                if (config.getCommentbyKey(key) != null) {
                    System.out.println("C: "+config.getCommentbyKey(key));
                }
            }
        }
    }
    @Test
    @Ignore
    public void aac_testHeader() {
        System.out.println("----header----");
        String header = config.printHeader();
        assertNotSame(0, header.length());
        System.out.println(header);
    }
    @Test
    @Ignore
    public void aad_save() {
        manager.saveToFile("C:\\Users\\N.Hartmann\\workspace\\ymlconfig\\config.conf");
    }
}
