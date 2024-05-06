package main.pvp.Static;

import main.pvp.Pvp;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConfigGet {
    private final Pvp plugin;

    public ConfigGet(Pvp plugin) {
        this.plugin = plugin;
    }
    public List<String> getLore(String key) {
        FileConfiguration config = plugin.getConfig();
        List<String> loreList = config.getStringList(key);
        return loreList;
    }
    public String getConfig(String text){
        FileConfiguration config = plugin.getConfig();
        String s = config.getString(text);
        return s;
    }
    public void setConfig(String text,Object value) {
        FileConfiguration config = plugin.getConfig();
        config.set(text,value);
        plugin.saveConfig();
    }
    public List<String> getNames(String key) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection duelSection = config.getConfigurationSection(key);
        if (duelSection != null) {
            Set<String> keys = duelSection.getKeys(false);
            return new ArrayList<>(keys);
        }
        return new ArrayList<>();
    }


}
