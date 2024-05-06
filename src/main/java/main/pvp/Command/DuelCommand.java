package main.pvp.Command;

import main.pvp.Pvp;
import main.pvp.Static.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.List;

public class DuelCommand implements CommandExecutor {
    private final String pr = "§x§F§F§E§2§5§9P§x§F§F§C§5§5§5V§x§F§F§A§7§5§1P §f>> ";
    private Boolean invisibility = false;
    private final Pvp plugin;
    private final ConfigGet configGet;
    public DuelCommand(Pvp plugin, ConfigGet configGet) {
        this.plugin = plugin;
        this.configGet = configGet;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§f 플레이어만 명령어를 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;
        String arg = null;
        if(args.length == 0){
            arg = "default";
        }else{
            arg = args[0];
        }
        switch (arg){
            case "대진표변환":
                Transform.PvpTransform(Integer.parseInt(configGet.getConfig("Tournament-TransformNumber")),new File(plugin.getDataFolder(),"설문지.txt").getPath(), new File(plugin.getDataFolder(),"대진표.yml").getPath(),player);
                break;
            case "대진표확인":
                this.DuelCheck(player);
                break;
            case "리로드":
                plugin.reloadConfig();
                player.sendMessage(pr+"Config.yml 리로드 완료");
                break;
            case "배수설정":
                if(args.length <= 1){
                    player.sendMessage(pr+"값을 입력해주세요");
                    break;
                }
                String a1 = args[1];
                Float ai = null;
                try{
                    ai = Float.parseFloat(a1);
                } catch (NumberFormatException e){
                    player.sendMessage(pr+"Float 값을 입력해주세요");
                    break;
                }
                player.sendMessage(pr+"베팅 배수를 변경했습니다 §c"+configGet.getConfig("Tournament-Multiple")+" §f→ §a"+ai);
                configGet.setConfig("Tournament-Multiple",ai);
                break;
            case "시작":
                DataManager f = new DataManager(plugin, "대진표.yml");
                if(args.length == 1) {
                    player.sendMessage(pr + "이름을 입력해주세요");
                    break;
                }
                List<String> names = f.getNames("daejeon");
                if(names.contains(args[1])){
                    Player player1 = Bukkit.getPlayer(f.get("daejeon."+args[1]+".1").toString());
                    Player player2 = Bukkit.getPlayer(f.get("daejeon."+args[1]+".2").toString());
                    if(player1 == null && player2 == null){
                        f.reaplceValue("win","X",Integer.parseInt(args[1]), new File(plugin.getDataFolder(),"대진표.yml").getPath());
                        Bukkit.broadcastMessage(pr+"");
                        Bukkit.broadcastMessage(pr+args[1]+"팀 둘다 오프라인 입니다");
                        Bukkit.broadcastMessage(pr+"§c"+f.get("daejeon."+args[1]+".1").toString());
                        Bukkit.broadcastMessage(pr+"§c"+f.get("daejeon."+args[1]+".2").toString());
                        Bukkit.broadcastMessage(pr+"");
                        break;
                    }else if(player1 == null){
                        f.reaplceValue("win",f.get("daejeon."+args[1]+".2").toString(),Integer.parseInt(args[1]), new File(plugin.getDataFolder(),"대진표.yml").getPath());
                        Bukkit.broadcastMessage(pr+"");
                        Bukkit.broadcastMessage(pr+args[1]+"팀 §a"+f.get("daejeon."+args[1]+".2")+" §f승리");
                        Bukkit.broadcastMessage(pr+"§c"+f.get("daejeon."+args[1]+".1").toString());
                        Bukkit.broadcastMessage(pr+"§a"+f.get("daejeon."+args[1]+".2").toString());
                        Bukkit.broadcastMessage(pr+"");
                        break;
                    }else if(player2 == null) {
                        f.reaplceValue("win",f.get("daejeon."+args[1]+".1").toString(),Integer.parseInt(args[1]), new File(plugin.getDataFolder(),"대진표.yml").getPath());
                        Bukkit.broadcastMessage(pr+"");
                        Bukkit.broadcastMessage(pr+args[1]+"팀 §a"+f.get("daejeon."+args[1]+".1")+" §f승리");
                        Bukkit.broadcastMessage(pr+"§a"+f.get("daejeon."+args[1]+".1").toString());
                        Bukkit.broadcastMessage(pr+"§c"+f.get("daejeon."+args[1]+".2").toString());
                        Bukkit.broadcastMessage(pr+"");
                        break;
                    }
                    f = new DataManager(plugin, "Data.yml");
                    f.set("player1",player1.getName());
                    f.set("player2",player2.getName());
                    for (Player ps : Bukkit.getOnlinePlayers()) {
                        ps.sendTitle(" ", "10초간 베팅을 진행해주세요!",0,200,0);
                        ps.playSound(ps, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Player ps : Bukkit.getOnlinePlayers()) {
                                OpenBetting(ps);
                            }
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    DataManager f = new DataManager(plugin, "Data.yml");
                                    Bukkit.broadcastMessage(pr);
                                    Bukkit.broadcastMessage(pr+"베팅 종료");
                                    for (Player ps : Bukkit.getOnlinePlayers()) {
                                        ps.getInventory().close();
                                    }
                                    Integer Player1Money = 0,Player2Money = 0;
                                    Integer Player1Number = 0,Player2Number = 0;
                                    for(String s : f.getNames("Betting")){
                                        if(f.get("Chose."+s)!=null){
                                            if(f.get("player1").equals(f.get("Chose."+s))){
                                                Player1Number++;
                                                Player1Money = Player1Money + Integer.parseInt(f.get("Betting."+s).toString());
                                            }else{
                                                Player2Number++;
                                                Player2Money = Player2Money + Integer.parseInt(f.get("Betting."+s).toString());
                                            }
                                        }
                                    }
                                    Bukkit.broadcastMessage(pr+"§a"+f.get("player1")+" §f("+Player1Number+"명)");
                                    Bukkit.broadcastMessage(pr+" §7→ "+Comma.Comma(Player1Money));
                                    Bukkit.broadcastMessage(pr+"§a"+f.get("player2")+" §f("+Player2Number+"명)");
                                    Bukkit.broadcastMessage(pr+" §7→ "+Comma.Comma(Player2Money));
                                    Bukkit.broadcastMessage(pr+" ");


                                    double x = Double.parseDouble(configGet.getConfig("Tournament-Teleport-Player1.x"));
                                    double y = Double.parseDouble(configGet.getConfig("Tournament-Teleport-Player1.y"));
                                    double z = Double.parseDouble(configGet.getConfig("Tournament-Teleport-Player1.z"));
                                    float yaw = Float.parseFloat(configGet.getConfig("Tournament-Teleport-Player1.yaw"));
                                    float pitch = Float.parseFloat(configGet.getConfig("Tournament-Teleport-Player1.pitch"));
                                    World world = Bukkit.getWorld(configGet.getConfig("Tournament-Teleport-Player1.world"));
                                    Location location1 = new Location(world, x, y, z, yaw, pitch);

                                    x = Double.parseDouble(configGet.getConfig("Tournament-Teleport-Player2.x"));
                                    y = Double.parseDouble(configGet.getConfig("Tournament-Teleport-Player2.y"));
                                    z = Double.parseDouble(configGet.getConfig("Tournament-Teleport-Player2.z"));
                                    yaw = Float.parseFloat(configGet.getConfig("Tournament-Teleport-Player2.yaw"));
                                    pitch = Float.parseFloat(configGet.getConfig("Tournament-Teleport-Player2.pitch"));
                                    world = Bukkit.getWorld(configGet.getConfig("Tournament-Teleport-Player1.world"));
                                    Location location2 = new Location(world, x, y, z, yaw, pitch);
                                    DefualtItem(player1);
                                    DefualtItem(player2);
                                    player1.setGameMode(GameMode.SURVIVAL);
                                    player2.setGameMode(GameMode.SURVIVAL);
                                    player1.teleport(location1);
                                    player2.teleport(location2);
                                    int col = 3;
                                    new BukkitRunnable() {
                                        private  int c = col;
                                        @Override
                                        public void run() {
                                            if(c > 0){
                                                for (Player ps : Bukkit.getOnlinePlayers()) {
                                                    ps.sendTitle(" ",c+"초후 싸우세요!",0,21,0);
                                                    ps.playSound(ps, Sound.ENTITY_PLAYER_LEVELUP,1,1);
                                                }
                                            }else{
                                                for (Player ps : Bukkit.getOnlinePlayers()) {
                                                    ps.sendTitle(" ","시작!",0,20,0);
                                                    ps.playSound(ps, Sound.ENTITY_PLAYER_LEVELUP,1,1);
                                                    cancel();
                                                }
                                            }
                                            c--;

                                        }
                                    }.runTaskTimer(plugin,0L,20L);
                                }
                            }.runTaskLater(plugin, 160L);
                        }
                    }.runTaskLater(plugin, 40L);

                }else{
                    player.sendMessage(pr+"이름을 찾을 수 없습니다");
                }
                break;
            case "월드복구":
                List<String> TournamentWorld = configGet.getNames("TournamentWorld");
                Boolean bf = true;
                for(String s : TournamentWorld){
                    int x1 = Integer.parseInt(configGet.getConfig("TournamentWorld."+s+".x1"));
                    int y1 = Integer.parseInt(configGet.getConfig("TournamentWorld."+s+".y1"));
                    int z1 = Integer.parseInt(configGet.getConfig("TournamentWorld."+s+".z1"));
                    int x2 = Integer.parseInt(configGet.getConfig("TournamentWorld."+s+".x2"));
                    int y2 = Integer.parseInt(configGet.getConfig("TournamentWorld."+s+".y2"));
                    int z2 = Integer.parseInt(configGet.getConfig("TournamentWorld."+s+".z2"));
                    World world = Bukkit.getWorld(configGet.getConfig("TournamentWorld."+s+".world"));
                    Material blockname = null;
                    try{
                        blockname = Material.valueOf(configGet.getConfig("TournamentWorld."+s+".block"));
                    }catch (IllegalArgumentException e){
                        player.sendMessage(pr+"잘못된 이름입니다 ("+configGet.getConfig("TournamentWorld."+s+".block")+")");
                        bf = false;
                        break;
                    }
                    for (int xi = Math.min(x1, x2); xi <= Math.max(x1, x2); xi++) {
                        for (int yi = Math.min(y1, y2); yi <= Math.max(y1, y2); yi++) {
                            for (int zi = Math.min(z1, z2); zi <= Math.max(z1, z2); zi++) {
                                Block block = world.getBlockAt(xi, yi, zi);
                                block.setType(blockname);
                            }
                        }
                    }
                }
                if(bf!=false) {
                    player.sendMessage(pr + "월드복구 완료");
                }
                break;
            case "기본템설정":
                f = new DataManager(plugin, "Gui.yml");
                Inventory gui = Bukkit.createInventory(null, 54, pr+"기본템설정");
                ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(" ");
                item.setItemMeta(meta);
                gui.setItem(4, item);
                gui.setItem(5, item);
                gui.setItem(6, item);
                gui.setItem(7, item);
                gui.setItem(8, item);
                for (int i = 0; i <= 53; i++) {
                    ItemStack getitem = (ItemStack) f.get("Gui."+i);
                    if (getitem != null && getitem.getType() != Material.AIR) {
                        gui.setItem(i,getitem);
                    }
                }
                player.openInventory(gui);
                break;
            case "기본템지급":
                Player target = Bukkit.getPlayer(args[1]);
                DefualtItem(target);
                break;
            case "돈확인":
                if(args.length == 1) {
                    player.sendMessage(pr + "이름을 입력해주세요");
                    break;
                }
                target = Bukkit.getPlayer(args[1]);
                f = new DataManager(plugin, "Data.yml");
                if(f.get("Money."+target.getName()) == null){
                    f.set("Money."+target.getName(),Integer.parseInt(configGet.getConfig("Tournament-PriceDefault")));
                }
                player.sendMessage(pr+" "+args[1]+"님의 돈 : "+ Comma.Comma(Integer.parseInt( f.get("Money."+target.getName()).toString() )));
                break;
            case "돈설정":
                if(args.length == 1) {
                    player.sendMessage(pr + "이름을 입력해주세요");

                    new BukkitRunnable() {
                        DataManager f = new DataManager(plugin, "Data.yml");
                        @Override
                        public void run() {
                            for (Player ps : Bukkit.getOnlinePlayers()) {
                                Bukkit.broadcastMessage(ps.getName());
                                Bukkit.broadcastMessage( f.get("Betting." + ps.getName())+"");
                                //String cp = f.get("Chose." + ps.getName()).toString();
                            }
                            cancel();
                        }
                    }.runTaskLater(plugin,40L);


                    break;
                }
                if(args.length == 2) {
                    player.sendMessage(pr + "액수를 입력해주세요");
                    break;
                }
                target = Bukkit.getPlayer(args[1]);
                f = new DataManager(plugin, "Data.yml");
                Integer zprice = Integer.parseInt(f.get("Money."+target.getName()).toString());
                Integer price = Integer.parseInt(args[2]);
                player.sendMessage(pr+" 돈설정 완료 ("+args[1]+") §c"+Comma.Comma(zprice)+"§f → §a" + Comma.Comma(price));
                f.set("Money."+target.getName(),price);
                break;
            default:
                player.sendMessage(pr+"/토너먼트 대진표변환"); //완성
                player.sendMessage(pr+"/토너먼트 대진표확인"); //완성
                player.sendMessage(pr+"/토너먼트 리로드"); //완성
                player.sendMessage(pr+" ");
                player.sendMessage(pr+"/토너먼트 배수설정 (배수)"); //완성
                player.sendMessage(pr+"/토너먼트 시작 (팀)"); //맨 마지막
                player.sendMessage(pr+"/토너먼트 월드복구"); //완성
                player.sendMessage(pr+" ");
                player.sendMessage(pr+"/토너먼트 돈확인 (닉네임)");
                player.sendMessage(pr+"/토너먼트 돈설정 (닉네임) (금액)");
                player.sendMessage(pr+" ");
                player.sendMessage(pr+"/토너먼트 기본템설정"); //완성
                player.sendMessage(pr+"/토너먼트 기본템지급 (닉네임)"); //완성
                break;
        }
        return false;
    }
    public void DefualtItem(Player target){
        DataManager f = new DataManager(plugin, "Gui.yml");
        for (int i = 0; i <= 53; i++) {
            if(i >= 0 && i <= 3){
                ItemStack items = (ItemStack) f.get("Gui."+i);
                if(items!=null) {
                    if (i == 0) {
                        target.getInventory().setHelmet(items);
                    } else if (i == 1) {
                        target.getInventory().setChestplate(items);
                    } else if (i == 2) {
                        target.getInventory().setLeggings(items);
                    } else if (i == 3) {
                        target.getInventory().setBoots(items);
                    }
                }
            }
            if(i < 0 || i > 8){
                ItemStack items = (ItemStack) f.get("Gui."+i);
                if(items !=null) {
                    target.getInventory().addItem(items);
                }
            }
        }
        target.sendActionBar(pr+" 기본템이 지급 되었습니다");
        target.playSound(target, Sound.ENTITY_PLAYER_LEVELUP,1,1);
    }
    public void OpenBetting(Player player){
        String pr = "§x§F§F§E§2§5§9P§x§F§F§C§5§5§5V§x§F§F§A§7§5§1P §f>> ";
        DataManager f = new DataManager(plugin, "Data.yml");
        Player player1 = Bukkit.getPlayer(f.get("player1").toString());
        Player player2 = Bukkit.getPlayer(f.get("player2").toString());
        Inventory gui = Bukkit.createInventory(null, 45, pr+ "§b"+player1.getName()+"§f VS §b" + player2.getName());
        Integer money = 0;
        if(f.get("Money."+player.getName()) !=null) {
            money = Integer.parseInt(f.get("Money." + player.getName()).toString());
        }else{
            f.set("Money."+player.getName(),Integer.parseInt(configGet.getConfig("Tournament-PriceDefault")));
            money = Integer.parseInt(f.get("Money." + player.getName()).toString());

        }
        Integer p1 = Integer.parseInt(configGet.getConfig("Tournament-Price1"));
        Integer p2 = Integer.parseInt(configGet.getConfig("Tournament-Price2"));
        Integer p3 = Integer.parseInt(configGet.getConfig("Tournament-Price3"));
        Integer p4 = Integer.parseInt(configGet.getConfig("Tournament-Price4"));
        Integer p5 = Integer.parseInt(configGet.getConfig("Tournament-Price5"));

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setOwningPlayer(player.getServer().getOfflinePlayer(player1.getName()));
        skullMeta.setDisplayName("§a"+player1.getName());
        playerHead.setItemMeta(skullMeta);
        gui.setItem(10,playerHead);
        playerHead = new ItemStack(Material.PLAYER_HEAD);
        skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setOwningPlayer(player.getServer().getOfflinePlayer(player2.getName()));
        skullMeta.setDisplayName("§a"+player2.getName());
        playerHead.setItemMeta(skullMeta);
        gui.setItem(28,playerHead);
        Double Mult = Double.parseDouble(configGet.getConfig("Tournament-Multiple").toString());
        ItemStack item = new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§a"+Comma.Comma(money*p1/100)+" §7("+p1+"%)").addLore("§7* 클릭시 "+player1.getName()+"님에게 베팅합니다").addLore("§7* 소유금액의 "+p1+"% 만 베팅할 수 있습니다").addLore("§f현재 배수 : "+Mult).build();
        gui.setItem(12,item);
        item = new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§a"+Comma.Comma(money*p2/100)+" §7("+p2+"%)").addLore("§7* 클릭시 "+player1.getName()+"님에게 베팅합니다").addLore("§7* 소유금액의 "+p2+"% 만 베팅할 수 있습니다").addLore("§f현재 배수 : "+Mult).build();
        gui.setItem(13,item);
        item = new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§a"+Comma.Comma(money*p3/100)+" §7("+p3+"%)").addLore("§7* 클릭시 "+player1.getName()+"님에게 베팅합니다").addLore("§7* 소유금액의 "+p3+"% 만 베팅할 수 있습니다").addLore("§f현재 배수 : "+Mult).build();
        gui.setItem(14,item);
        item = new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§a"+Comma.Comma(money*p4/100)+" §7("+p4+"%)").addLore("§7* 클릭시 "+player1.getName()+"님에게 베팅합니다").addLore("§7* 소유금액의 "+p4+"% 만 베팅할 수 있습니다").addLore("§f현재 배수 : "+Mult).build();
        gui.setItem(15,item);
        item = new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§a"+Comma.Comma(money*p5/100)+" §7("+p5+"%)").addLore("§7* 클릭시 "+player1.getName()+"님에게 베팅합니다").addLore("§7* 소유금액의 "+p5+"% 만 베팅할 수 있습니다").addLore("§f현재 배수 : "+Mult).build();
        gui.setItem(16,item);

        item = new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§a"+Comma.Comma(money*p1/100)+" §7("+p1+"%)").addLore("§7* 클릭시 "+player2.getName()+"님에게 베팅합니다").addLore("§7* 소유금액의 "+p1+"% 만 베팅할 수 있습니다").addLore("§f현재 배수 : "+Mult).build();
        gui.setItem(30,item);
        item = new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§a"+Comma.Comma(money*p2/100)+" §7("+p2+"%)").addLore("§7* 클릭시 "+player2.getName()+"님에게 베팅합니다").addLore("§7* 소유금액의 "+p2+"% 만 베팅할 수 있습니다").addLore("§f현재 배수 : "+Mult).build();
        gui.setItem(31,item);
        item = new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§a"+Comma.Comma(money*p3/100)+" §7("+p3+"%)").addLore("§7* 클릭시 "+player2.getName()+"님에게 베팅합니다").addLore("§7* 소유금액의 "+p3+"% 만 베팅할 수 있습니다").addLore("§f현재 배수 : "+Mult).build();
        gui.setItem(32,item);
        item = new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§a"+Comma.Comma(money*p4/100)+" §7("+p4+"%)").addLore("§7* 클릭시 "+player2.getName()+"님에게 베팅합니다").addLore("§7* 소유금액의 "+p4+"% 만 베팅할 수 있습니다").addLore("§f현재 배수 : "+Mult).build();
        gui.setItem(33,item);
        item = new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§a"+Comma.Comma(money*p5/100)+" §7("+p5+"%)").addLore("§7* 클릭시 "+player2.getName()+"님에게 베팅합니다").addLore("§7* 소유금액의 "+p5+"% 만 베팅할 수 있습니다").addLore("§f현재 배수 : "+Mult).build();
        gui.setItem(34,item);
        player.openInventory(gui);

    }
    private void DuelCheck(Player player) {
        DataManager f = new DataManager(plugin, "대진표.yml");
        List<String> Daejeon = f.getNames("daejeon");

        String s = null;
        for(String name : Daejeon){
            player.sendMessage(pr+"§7["+name+"] §f"+f.get("daejeon."+name+".1")+" §cVS §f"+f.get("daejeon."+name+".2"));
            if(player.getName().equals(f.get("daejeon."+name+".1")) || player.getName().equals(f.get("daejeon."+name+".2"))){
                s = pr+"§7["+name+"] §a"+f.get("daejeon."+name+".1")+" §cVS §a"+f.get("daejeon."+name+".2");
            }
        }
        if(s!= null){
            player.sendMessage(pr+"§a당신 차례:");
            player.sendMessage(s);
        }
    }


}
