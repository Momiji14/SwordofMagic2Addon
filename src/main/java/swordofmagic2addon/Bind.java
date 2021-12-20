package swordofmagic2addon;

import org.bukkit.entity.Player;

public class Bind {

    private final Player player;
    String[] BindList = new String[8];
    CastMode castMode = CastMode.SkillAPI;


    public Bind(Player player) {
        this.player = player;
    }

    void cast(int id) {
        if (BindList[id] != null && !BindList[id].equals("None")) {
            player.performCommand("class:class cast " + BindList[id]);
        } else {
            player.sendMessage("§b§l[スロット" + id + "]§a§lは設定されていません");
        }
    }

    String view(int id) {
        if (BindList[id] != null && !BindList[id].equals("None")) {
            return "§e§l" + BindList[id];
        } else return "§7§l未設定";
    }

    void Help() {
        player.sendMessage("§e§lキャストモードの切り替え§7: §a§l/castmode");
        player.sendMessage("§e§lLegacyキャストの登録§7: §a§l/newBind <0~6> <Skill_Name>");
        player.sendMessage("§7・§e§l0§7: §a§l右クリック -> " + view(0));
        player.sendMessage("§7・§e§l1§7: §a§lオフハンド -> " + view(1));
        player.sendMessage("§7・§e§l2§7: §a§lドロップ -> " + view(2));
        player.sendMessage("§7・§e§l3§7: §a§l左クリック+スニーク -> " + view(3));
        player.sendMessage("§7・§e§l4§7: §a§l右クリック+スニーク -> " + view(4));
        player.sendMessage("§7・§e§l5§7: §a§lオフハンド+スニーク -> " + view(5));
        player.sendMessage("§7・§e§l6§7: §a§lドロップ+スニーク -> " + view(6));
    }
}

enum CastMode {
    SkillAPI,
    Legacy,
    ;

    boolean isLegacy() {
        return this == CastMode.Legacy;
    }
}
