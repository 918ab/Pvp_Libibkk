package main.pvp.Command;

import main.pvp.Pvp;
import main.pvp.Static.ConfigGet;
import main.pvp.Static.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DuelCommandTab implements TabCompleter {
    private final Pvp plugin;
    public DuelCommandTab(Pvp plugin) {
        this.plugin = plugin;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new ArrayList<>();
        List<String> candidates = new ArrayList<>();
        if(strings.length==1){
            String input = strings[0].toLowerCase();
            candidates.add("대진표변환");
            candidates.add("대진표확인");
            candidates.add("리로드");
            candidates.add("배수설정");
            candidates.add("시작");
            candidates.add("월드복구");
            candidates.add("기본템설정");
            candidates.add("기본템지급");
            candidates.add("돈확인");
            candidates.add("돈설정");
            candidates.remove(commandSender.getName());
            for (String candidate : candidates) {
                if (candidate.toLowerCase().startsWith(input)) {
                    completions.add(candidate);
                }
            }
        }
        if(strings.length==2){
            String input = strings[1].toLowerCase();
            if(strings[0].equals("기본템지급") || strings[0].equals("돈설정") || strings[0].equals("돈확인")){
                List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
                for(Player p : players) {
                    candidates.add(p.getName()+"");
                }
            }
            if(strings[0].equals("시작")){
                DataManager f = new DataManager(plugin, "대진표.yml");
                for(String daejeon : f.getNames("daejeon") ){
                    candidates.add(daejeon);
                }

            }
            if(strings[0].equals("배수설정")){
                candidates.add("(값)");
            }
            for (String candidate : candidates) {
                if (candidate.toLowerCase().startsWith(input)) {
                    completions.add(candidate);
                }
            }
        }

        return completions;
    }
}
