package swordofmagic2addon;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;

import static swordofmagic2addon.Function.Log;
import static swordofmagic2addon.Function.decoInv;
import static swordofmagic2addon.System.plugin;

public class Storage {
    private final Player player;
    private Inventory invCache;
    private int currentPage;

    Storage(Player player) {
        this.player = player;
    }

    void openStorage(int page) {
        int MaxPage = plugin.getConfig().getInt("StorageMaxPage");
        if (1 <= page && page <= MaxPage) {
            File storageDirectory = new File(plugin.getDataFolder(), "PlayerStorage");
            File playerFile = new File(plugin.getDataFolder(), "PlayerStorage/" + player.getUniqueId() + ".yml");
            if (!storageDirectory.exists()) {
                try {
                    storageDirectory.mkdir();
                } catch (Exception e) {
                    Log("Error can not create -> " + storageDirectory.getPath());
                }
            }
            if (!storageDirectory.exists()) {
                try {
                    playerFile.createNewFile();
                } catch (Exception e) {
                    Log("Error can not create -> " + playerFile.getPath());
                }
            }
            FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
            Inventory inv = decoInv("Storage [" + page + "]", 6);
            for (int i = 0; i < 54; i++) {
                String jsonData = data.getString("Storage.Page-" + page + ".Slot-" + i, "None");
                Log(i + ": " + jsonData);
                if (!jsonData.equals("None")) {
                    ItemData itemData = ItemData.fromJson(jsonData);
                    inv.setItem(i, itemData.toItemStack());
                }
            }
            player.openInventory(inv);
            invCache = inv;
            currentPage = page;
        } else {
            player.sendMessage("§c[1~" + MaxPage + "]しか利用できません");
        }
    }

    void closeStorage() {
        File playerFile = new File(plugin.getDataFolder(), "PlayerStorage/" + player.getUniqueId() + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
        for (int i = 0; i < 54; i++) {
            String path = "Storage.Page-" + currentPage + ".Slot-" + i;
            if (invCache.getItem(i) != null) {
                data.set(path, new ItemData(invCache.getItem(i)).toJson());
            } else {
                data.set(path, "None");
            }
        }
        try {
            data.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
