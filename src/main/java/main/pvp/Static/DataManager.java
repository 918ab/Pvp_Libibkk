package main.pvp.Static;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class DataManager {
    private final File configFile;
    private final FileConfiguration config;

    public DataManager(Plugin plugin, String fileName) {
        this.configFile = new File(plugin.getDataFolder(), fileName);
        this.config = YamlConfiguration.loadConfiguration(configFile);

    }

    public void reaplceValue(String name, String value, Integer num, String yamlOutputPath){
        Yaml yaml = new Yaml();
        Map<String, Object> data;
        try (FileReader reader = new FileReader(yamlOutputPath)) {
            data = yaml.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Map<Integer, Object> daejeon = (Map<Integer, Object>) data.get("daejeon");
        if (daejeon == null) {
            daejeon = new HashMap<>();
            data.put("daejeon", daejeon);
        }
        Map<String, Object> nestedMap = (Map<String, Object>) daejeon.get(num);
        if (nestedMap == null) {
            nestedMap = new HashMap<>();
            daejeon.put(num, nestedMap);
        }
        nestedMap.put(name, value);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(yamlOutputPath)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void set(String key, Object value) {

        config.set(key, value);
        saveConfig();
    }

    public Object get(String key) {
        return config.get(key);
    }

    public void remove(String key) {
        config.set(key, null);
        saveConfig();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<String> getNames(String key) {
        ConfigurationSection duelSection = config.getConfigurationSection(key);
        if (duelSection != null) {
            Set<String> keys = duelSection.getKeys(false);
            return new ArrayList<>(keys);
        }
        return new ArrayList<>();
    }
    public void createFileIfNotExists() {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
