package swordofmagic2addon;

import com.google.gson.Gson;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

class EnchantData {
    String Enchantment;
    int Level;

    EnchantData(Enchantment Enchantment, int Level) {
        this.Enchantment = Enchantment.getName();
        this.Level = Level;
    }
}

public class ItemData {
    Material Material;
    byte MaterialData;
    int Amount;
    String Display;
    List<String> Lore;
    List<EnchantData> EnchantData = new ArrayList<>();
    Set<ItemFlag> Flags;
    short Durability;
    boolean Unbreakable;

    ItemData(ItemStack item) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        Material = item.getType();
        Amount = item.getAmount();
        Display = meta.getDisplayName();
        MaterialData = item.getData().getData();
        if (meta.hasLore()) Lore = meta.getLore();
        if (meta.hasEnchants()) for (Map.Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet()) {
            EnchantData.add(new EnchantData(enchant.getKey(), enchant.getValue()));
        }
        Flags = meta.getItemFlags();
        Durability = item.getDurability();
        Unbreakable = meta.isUnbreakable();
    }

    public ItemStack toItemStack() {
        try {
            ItemStack item = new ItemStack(Material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(Display);
            if (Lore != null) meta.setLore(Lore);
            if (EnchantData != null) for (EnchantData enchant : EnchantData) {
                meta.addEnchant(Enchantment.getByName(enchant.Enchantment), enchant.Level, true);
            }
            for (ItemFlag flag : Flags) {
                meta.addItemFlags(flag);
            }
            meta.setUnbreakable(Unbreakable);
            item.setItemMeta(meta);
            item.setData(new MaterialData(Material, MaterialData));
            item.setDurability(Durability);
            item.setAmount(Amount);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(org.bukkit.Material.AIR);
        }
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static ItemData fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ItemData.class);
    }
}
