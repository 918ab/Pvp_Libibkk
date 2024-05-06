package main.pvp.Event;

import main.pvp.Pvp;
import main.pvp.Static.Comma;
import main.pvp.Static.ConfigGet;
import main.pvp.Static.DataManager;
import main.pvp.Static.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.List;

public class EventPlayer implements Listener {
    private final String pr = "§x§F§F§E§2§5§9P§x§F§F§C§5§5§5V§x§F§F§A§7§5§1P §f>> ";
    private final Pvp plugin;
    private final ConfigGet configGet;
    public EventPlayer(Pvp plugin, ConfigGet configGet) {
        this.plugin = plugin;
        this.configGet = configGet;
    }
    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals(pr + "기본템설정")) {
            DataManager f = new DataManager(plugin, "Gui.yml");
            for (int i = 0; i <= 53; i++) {
                if (i < 4 || i > 8) {
                    ItemStack item = e.getInventory().getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                        f.set("Gui." + i, item);
                    } else if (item == null && f.get("Gui." + i) != null) {
                        f.remove("Gui." + i);
                    }
                }
            }
            e.getPlayer().sendMessage(pr + "기본템 설정 완료");
        }

    }
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e){
        if(e.getView().getTitle().contains(pr) && !e.getView().getTitle().equals(pr+"기본템설정")){
            e.setCancelled(true);
            if(e.getCurrentItem()!=null && e.getCurrentItem().getType() == Material.GREEN_WOOL) {
                if ((e.getSlot() >= 12 && e.getSlot() <= 16) || (e.getSlot() >= 30 && e.getSlot() <= 34)){
                    DataManager f = new DataManager(plugin, "Data.yml");
                    Player player = (Player) e.getWhoClicked();
                    Integer money = Integer.parseInt(f.get("Money."+e.getView().getPlayer().getName())+"");
                    String ItemName = e.getCurrentItem().getItemMeta().getDisplayName();
                    ItemName = ItemName.replaceAll("§[0-9a-fk-o]","");
                    ItemName = ItemName.replaceAll("\\s*\\(\\d+%\\)","");
                    ItemName = ItemName.replace(",","");
                    Integer ItemMoney = Integer.parseInt(ItemName);
                    if(money < ItemMoney){
                        player.sendMessage(pr+"돈이 부족합니다");
                        return;
                    }
                    if(e.getSlot() >= 12 && e.getSlot() <= 16){
                        player.sendMessage(pr+"§a"+f.get("player1")+"§f님에게 §a"+ Comma.Comma(ItemMoney)+"§f원을 베팅했습니다");
                        f.set("Chose."+player.getName(),f.get("player1"));
                        f.set("Betting."+player.getName(),ItemMoney);
                    }else if(e.getSlot() >= 30 && e.getSlot() <= 34){
                        player.sendMessage(pr+"§a"+f.get("player2")+"§f님에게 §a"+ Comma.Comma(ItemMoney)+"§f원을 베팅했습니다");
                        f.set("Chose."+player.getName(),f.get("player2"));
                        f.set("Betting."+player.getName(),ItemMoney);
                    }
                    Integer bMoney = money - ItemMoney;
                    player.sendMessage(pr+"보유 돈 §c"+Comma.Comma(money)+"§f → §a"+Comma.Comma(bMoney));
                    f.set("Money."+player.getName(),bMoney);
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP,1,1);
                    e.getInventory().close();
                }
            }
        }
    }
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e){
        if(!e.getPlayer().isOp()) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e){
        if(e.getEntity() instanceof Player) {
            DataManager f = new DataManager(plugin, "Data.yml");
            if (f.get("player1") != null && f.get("player2") != null) {
                if (f.get("player1").equals(e.getEntity().getName())) {
                    Player win = Bukkit.getPlayer(f.get("player2").toString());
                    Player lose = Bukkit.getPlayer(f.get("player1").toString());
                    win.setGameMode(GameMode.SPECTATOR);
                    lose.setGameMode(GameMode.SPECTATOR);
                    win.getInventory().clear();
                    lose.getInventory().clear();
                    Bukkit.broadcastMessage(pr + "");
                    Bukkit.broadcastMessage(pr + " 승리 : " + f.get("player2"));
                    Bukkit.broadcastMessage(pr + " 패배 : " + f.get("player1"));
                    Bukkit.broadcastMessage(pr + "");
                    String p = win.getName();
                    f.remove("player1");
                    f.remove("player2");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Player ps : Bukkit.getOnlinePlayers()) {
                                ps.sendTitle(p + "", "님이 이겼습니다!", 0, 40, 0);
                                Integer money = 0;
                                if(f.get("Betting." + ps.getName()) != null) {
                                    String b = f.get("Betting." + ps.getName())+"";
                                    money = Integer.parseInt(b);
                                }
                                String cp = null;
                                if(f.get("Chose." + ps.getName()) != null) {
                                    cp = f.get("Chose." + ps.getName()) + "";
                                }

                                win(ps, p, money, cp);
                            }
                            DataManager  f = new DataManager(plugin, "대진표.yml");
                            for(String s : f.getNames("daejeon")){
                                if(f.get("daejeon."+s+".1").equals(win.getName()) || f.get("daejeon."+s+".2").equals(win.getName())){
                                    f.reaplceValue("win",win.getName(),Integer.parseInt(s), new File(plugin.getDataFolder(),"대진표.yml").getPath());
                                }
                            }
                            cancel();
                        }
                    }.runTaskLater(plugin,40L);

                } else if (f.get("player2").equals(e.getEntity().getName())) {
                    Player win = Bukkit.getPlayer(f.get("player1").toString());
                    Player lose = Bukkit.getPlayer(f.get("player2").toString());
                    win.setGameMode(GameMode.SPECTATOR);
                    lose.setGameMode(GameMode.SPECTATOR);
                    win.getInventory().clear();
                    lose.getInventory().clear();
                    Bukkit.broadcastMessage(pr + "");
                    Bukkit.broadcastMessage(pr + " 승리 : " + win.getName());
                    Bukkit.broadcastMessage(pr + " 패배 : " + lose.getName());
                    Bukkit.broadcastMessage(pr + "");
                    String p = win.getName();
                    f.remove("player1");
                    f.remove("player2");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Player ps : Bukkit.getOnlinePlayers()) {
                                ps.sendTitle(p + "", "님이 이겼습니다!", 0, 40, 0);
                                Integer money = 0;
                                if(f.get("Betting." + ps.getName()) != null) {
                                    String b = f.get("Betting." + ps.getName())+"";
                                    money = Integer.parseInt(b);
                                }
                                String cp = null;
                                if(f.get("Chose." + ps.getName()) != null) {
                                     cp = f.get("Chose." + ps.getName()) + "";
                                }
                                win(ps, p, money, cp);
                            }
                            DataManager  f = new DataManager(plugin, "대진표.yml");
                            for(String s : f.getNames("daejeon")){
                                if(f.get("daejeon."+s+".1").equals(win.getName()) || f.get("daejeon."+s+".2").equals(win.getName())){
                                    f.reaplceValue("win",win.getName(),Integer.parseInt(s), new File(plugin.getDataFolder(),"대진표.yml").getPath());
                                }
                            }
                            cancel();
                        }
                    }.runTaskLater(plugin,40L);
                }
            }
        }
    }
    public void win(Player player, String p1t,Integer money,String cp){
        DataManager f = new DataManager(plugin, "Data.yml");
        f.remove("Chose."+player.getName());
        f.remove("Betting."+player.getName());
        Float mult = Float.parseFloat(configGet.getConfig("Tournament-Multiple"));
        if(cp != null) {
            if (cp.equals(p1t)) {
                Integer value = (int) (money * mult);
                player.sendMessage(pr + "베팅에 성공해, " + Comma.Comma(value) + "원을 얻었습니다");
                Integer haveMoney = Integer.parseInt(f.get("Money." + player.getName()).toString());
                player.sendMessage(pr + "보유 돈 §c" + Comma.Comma(haveMoney) + "§f → §a" + Comma.Comma(haveMoney + value));
                haveMoney = haveMoney + value;
                f.set("Money." + player.getName(), haveMoney);
            } else {
                player.sendMessage(pr + "이런 베팅에 실패했네요..");
            }
        }
    }

}