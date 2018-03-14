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


    private void getConfig() {
        if (manager == null) {
            manager = new ConfigManager();
            manager.copyRessource("C:\\Users\\N.Hartmann\\workspace\\ymlconfig\\config.conf", Config.class, "test.yml");
        }
        if (config == null) {
            config = manager.getConfig("C:\\Users\\N.Hartmann\\workspace\\ymlconfig\\config.conf");
        }
    }

    @Test
    public void aaa_parseRessoureConfig() {
        getConfig();
        assertNotNull("should not be null", manager);
        assertNotNull("should not be null", config);
    }
}
