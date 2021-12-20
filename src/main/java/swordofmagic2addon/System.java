package swordofmagic2addon;

import com.google.gson.Gson;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static swordofmagic2addon.Function.Log;
import static swordofmagic2addon.PlayerData.playerData;
import static swordofmagic2addon.System.plugin;

public final class System extends JavaPlugin implements Listener {

    static Plugin plugin;

    static final BaseComponent[] StorageHelp = TextComponent.fromLegacyText("/storage <No>");

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        plugin = this;
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerData playerdata = playerData(player);
            if (cmd.getName().equalsIgnoreCase("storage")) {
                /*
                if (args.length > 0) {
                    try {
                        int page = Integer.parseInt(args[0]);
                        playerData(player).Storage.openStorage(page);
                    } catch (NumberFormatException e) {
                        player.sendMessage(StorageHelp);
                    }
                } else {
                    player.sendMessage(StorageHelp);
                }
                 */
                return true;
            } else if (cmd.getName().equalsIgnoreCase("newBind")) {
                Bind bind = playerData(player).Bind;
                if (args.length == 2) {
                    try {
                        int id = Integer.parseInt(args[0]);
                        if (0 <= id && id <= 6) {
                            playerdata.Bind.BindList[id] = args[1];
                            player.sendMessage("§b§l[スロット" + id + "]§a§lに§e§l[" + args[1] + "]§a§lを設定しました");
                        } else bind.Help();
                    } catch (Exception e) {
                        bind.Help();
                    }
                } else bind.Help();
                return true;
            } else if (cmd.getName().equalsIgnoreCase("castMode")) {
                if (playerdata.Bind.castMode.isLegacy()) {
                    playerdata.Bind.castMode = CastMode.SkillAPI;
                } else {
                    playerdata.Bind.castMode = CastMode.Legacy;
                }
                player.sendMessage("§e§lキャストモード§7: §a§l" + playerdata.Bind.castMode);
                return true;
            }
        }
        return false;
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        playerData(event.getPlayer()).load();
    }

    @EventHandler
    void onQuit(PlayerQuitEvent event) {
        playerData(event.getPlayer()).save();
    }

    @EventHandler
    void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getView().getTitle().contains("Storage ")) {
            playerData(player).Storage.closeStorage();
        }
    }

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = playerData(player);
        Action action = event.getAction();
        EquipmentSlot handler = event.getHand();
        Bind bind = playerData.Bind;
        if (handler == EquipmentSlot.HAND && bind.castMode.isLegacy()) {
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if (player.isSneaking()) {
                    bind.cast(4);
                } else {
                    bind.cast(0);
                }
            } else if ((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
                if (player.isSneaking()) {
                    bind.cast(3);
                }
            }
        }
    }

    @EventHandler
    void onOffHandSwitch(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = playerData(player);
        Bind bind = playerData.Bind;
        if (bind.castMode.isLegacy()) {
            if (player.isSneaking()) {
                bind.cast(5);
            } else {
                bind.cast(1);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = playerData(player);
        Bind bind = playerData.Bind;
        if (bind.castMode.isLegacy()) {
            if (player.isSneaking()) {
                bind.cast(6);
            } else {
                bind.cast(2);
            }
            event.setCancelled(true);
        }
    }
}

class PlayerData {
    static HashMap<Player, PlayerData> playerData = new HashMap<>();
    static PlayerData playerData(Player player) {
        playerData.putIfAbsent(player, new PlayerData(player));
        return playerData.get(player);
    }

    private final Player player;
    private final File directory;
    private final File playerFile;
    private final Gson gson = new Gson();
    Storage Storage;
    Bind Bind;

    PlayerData(Player player) {
        this.player = player;
        directory = new File(plugin.getDataFolder(), "PlayerData");
        playerFile = new File(plugin.getDataFolder(), "PlayerData/" + player.getUniqueId() + ".yml");
        Storage = new Storage(player);
        Bind = new Bind(player);
    }

    boolean file() {
        if (!directory.exists()) {
            try {
                directory.mkdir();
            } catch (Exception e) {
                Log("Error can not create -> " + directory.getPath());
                return false;
            }
        }
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (Exception e) {
                Log("Error can not create -> " + playerFile.getPath());
                return false;
            }
        }
        return true;
    }

    void save() {
        if (file()) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
            data.set("CastMode", Bind.castMode.toString());
            for (int i = 0; i < 7; i++) {
                if (Bind.BindList[i] != null) {
                    data.set("Bind." + i, Bind.BindList[i]);
                } else {
                    data.set("Bind." + i, "None");
                }
            }
            try {
                data.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void load() {
        if (file()) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
            Bind.castMode = CastMode.valueOf(data.getString("CastMode", "SkillAPI"));
            for (int i = 0; i < 7; i++) {
                Bind.BindList[i] = data.getString("Bind." + i, "None");
            }
        }
    }
}
