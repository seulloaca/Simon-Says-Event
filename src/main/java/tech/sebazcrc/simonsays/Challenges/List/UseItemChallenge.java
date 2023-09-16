package tech.sebazcrc.simonsays.Challenges.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import tech.sebazcrc.simonsays.Challenges.Challenge;
import tech.sebazcrc.simonsays.Main;
import tech.sebazcrc.simonsays.Utils.GamePlayer;
import tech.sebazcrc.simonsays.Utils.GameState;
import tech.sebazcrc.simonsays.Utils.Utils;

public class UseItemChallenge extends Challenge {
    private UsableItem item;

    public UseItemChallenge(GamePlayer handle, UsableItem item) {
        super(handle, "Usa un(a) " + item.getName(), new String[]{
                (item.isBlock() ? Utils.format("&7Interactúa con el siguiente bloque: &b" + item.getName()) : Utils.format("&7Consigue y utiliza el siguiente objeto: &b" + item.getName()))
        });
        this.item = item;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (getHandle() == null) return;
        if (Main.getInstance().getState() != GameState.PLAYING || (getHandle().getChallenge() == null || !getHandle().getChallenge().getName().equalsIgnoreCase(getName()))) {
            setHandle(null);
            return;
        }
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player p = e.getPlayer();
        if (p.getUniqueId().toString().equalsIgnoreCase(getHandle().getUUID().toString())) {
            if (item.isBlock()) {
                if (e.getClickedBlock() != null && e.getClickedBlock().getType() == item.getMaterial()) {
                    getHandle().completeChallenge();
                    setHandle(null);
                }
            } else {
                if (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() == item.getMaterial()) {
                    getHandle().completeChallenge();
                    setHandle(null);
                }
            }
        }
    }

    public enum UsableItem {
        CRAFTING_TABLE("Mesa de trabajo", true, Material.CRAFTING_TABLE), ENDER_PEARL("Perla de ender", false, Material.ENDER_PEARL),
        WATER_BUCKET("Cubo de agua", false, Material.WATER_BUCKET), BUCKET("Cubo (vacía)", false, Material.BUCKET),
        LAVA_BUCKET("Cubo de lava", false, Material.LAVA_BUCKET), SMITHING_TABLE("Mesa de herrería", true, Material.SMITHING_TABLE),
        FURNACE("Horno (normal)", true, Material.FURNACE), ENCHANTING_TABLE("Mesa de encantamientos", true, Material.ENCHANTING_TABLE);

        private String name;
        private boolean block;
        private Material material;

        UsableItem(String name, boolean block, Material material) {
            this.name = name;
            this.block = block;
            this.material = material;
        }

        public String getName() {
            return name;
        }

        public boolean isBlock() {
            return block;
        }

        public Material getMaterial() {
            return material;
        }
    }
}
