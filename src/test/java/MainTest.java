import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    }

    @Test
    public void aaa_parseRessoureConfig() {
        manager = new ConfigManager();
        manager.copyRessource("C:\\Users\\N.Hartmann\\workspace\\ymlconfig\\config.conf", Config.class, "test.yml");
        config = manager.getConfig("C:\\Users\\N.Hartmann\\workspace\\ymlconfig\\config.conf");
        assertNotNull( manager);
        assertNotNull( config);
        config.addHeader(new String[] {"AncientRegions test Config", "Ich bin eine TestConfiguraton"});
        config.set("testnested.testnestedsection.läuft", "hahahaha");
        config.set("newtest.lolvalue", "hahahaha");
        config.set("newtest.test", 56);
        config.set("testnested.zweitesection.keytoll", 56765, "Ich bin ein toller Kommentar");
        config.set("testnested.zweitesection.liste", new Object[]{3456, "tatatata", false});
        config.set("testnested.zweitesection.zeweiteliste", new ArrayList<>(Arrays.asList(9999, "hjdjdhjf", "jhjdhds", true)));
        config.saveToFile();
        assertNotNull(config.get("testnested.testnestedsection.läuft"));
        assertNotNull(config.get("newtest.lolvalue"));
        assertNotNull(config.get("newtest.test"));
        assertNotNull(config.get("testnested.zweitesection.keytoll"));
        assertNotNull(config.get("testnested.zweitesection.liste"));
        assertNotNull(config.get("testnested.zweitesection.zeweiteliste"));
        assertNotNull(config.getOrDef("newtest.lustig", 55555,
                "ICH bin ein ganz langer kommentar und mache jetzt alles kaputt damm damm dammmmmmmm. Aber villeicht auch nicht"));
        assertNotNull(config.getOrDef("a2Section.tada6", 55555,
                "LOL"));
        config.saveToFile();
        config.reload();
        assertSame(true, config.containsKey("newtest.test"));
        assertSame(true, config.containsKey("newtest"));
        assertSame(false, config.containsKey("test2"));
    }
}
