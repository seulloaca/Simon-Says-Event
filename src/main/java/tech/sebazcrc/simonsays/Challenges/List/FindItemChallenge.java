package tech.sebazcrc.simonsays.Challenges.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import tech.sebazcrc.simonsays.Challenges.Challenge;
import tech.sebazcrc.simonsays.Main;
import tech.sebazcrc.simonsays.Utils.GamePlayer;
import tech.sebazcrc.simonsays.Utils.GameState;
import tech.sebazcrc.simonsays.Utils.Utils;

public class FindItemChallenge extends Challenge {

    private PickupItemType type;

    public FindItemChallenge(GamePlayer handle, PickupItemType type) {
        super(handle, "Encontrar " + type.getName(), new String[] {Utils.format("&7Debes encontrar y recolectar el siguiente objeto (del suelo): &b" + type.getColor() + Utils.format("&l") +type.getName()) });
        this.type = type;
    }

    @EventHandler
    public void onTake(EntityPickupItemEvent e) {
        if (getHandle() == null) return;
        if (Main.getInstance().getState() != GameState.PLAYING || (getHandle().getChallenge() == null || !getHandle().getChallenge().getName().equalsIgnoreCase(getName()))) {
            setHandle(null);
            return;
        }

        if (e.getEntity() instanceof Player && getHandle().getUUID().toString().equalsIgnoreCase(e.getEntity().getUniqueId().toString())) {
            ItemStack s = e.getItem().getItemStack();
            if (s.getType() == type.getType()) {
                getHandle().completeChallenge();
                setHandle(null);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (getHandle() == null) return;
        if (Main.getInstance().getState() != GameState.PLAYING || (getHandle().getChallenge() == null || !getHandle().getChallenge().getName().equalsIgnoreCase(getName()))) {
            setHandle(null);
            return;
        }

        if (e.getWhoClicked() instanceof Player && getHandle().getUUID().toString().equalsIgnoreCase(e.getWhoClicked().getUniqueId().toString())) {
            ItemStack s = e.getCurrentItem();
            if (s != null && s.getType() == type.getType()) {
                getHandle().completeChallenge();
                setHandle(null);
            }
        }
    }

    public enum PickupItemType {

        COBBLESTONE("Piedra labrada", "&7", Material.COBBLESTONE), DIRT("Tierra", "&7", Material.DIRT), OAK_PLANKS("Madera de roble", "&7", Material.OAK_PLANKS),
        SAND("Arena", "&e", Material.SAND), STICK("Palo", "&7", Material.STICK), ENDER_PEARL("Perla de ender", "&d", Material.ENDER_PEARL),
        EMERALD("Esmeralda", "&a", Material.EMERALD), DIAMOND("Diamante", "&b", Material.DIAMOND), GOLD_INGOT("Lingote de oro", "&e", Material.GOLD_INGOT),
        IRON_INGOT("Lingote de hierro", "&f", Material.IRON_INGOT), IRON_BLOCK("Bloque de hierro", "&f", Material.IRON_BLOCK), OAK_LOG("Tronco de roble", "&7", Material.OAK_LOG), OBSIDIAN("Obsidiana", "&0", Material.OBSIDIAN), BONE_BLOCK("Bloque de hueso", "&f", Material.BONE_BLOCK);

        private String name;
        private String color;
        private Material type;

        PickupItemType(String name, String color, Material m) {
            this.name = name;
            this.color = Utils.format(color);
            this.type = m;
        }

        public Material getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }
    }
}
