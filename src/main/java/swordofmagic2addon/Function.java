package swordofmagic2addon;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public final class Function {
    static Inventory decoInv(String name, int size) {
        return Bukkit.createInventory(null, size*9, name);
    }

    static void Log(String str) {
        Bukkit.getLogger().info("[S2A]" + str);
    }
}
