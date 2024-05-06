package main.pvp;

import main.pvp.Command.DuelCommand;
import main.pvp.Command.DuelCommandTab;
import main.pvp.Event.EventPlayer;
import main.pvp.Static.ConfigGet;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Pvp extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("[PVP] Pvp Plugin Enable");
        getCommand("토너먼트").setExecutor(new DuelCommand(this,new ConfigGet(this)));
        getCommand("토너먼트").setTabCompleter(new DuelCommandTab(this));
        File configFile = new File(getDataFolder(), "config.yml");
        getServer().getPluginManager().registerEvents(new EventPlayer(this,new ConfigGet(this)), this);
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
    }



    @Override
    public void onDisable() {
        Bukkit.getLogger().info("[PVP] Pvp Plugin Disable");
    }
}
