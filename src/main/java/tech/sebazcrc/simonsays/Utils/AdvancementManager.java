package tech.sebazcrc.simonsays.Utils;

import hu.trigary.advancementcreator.Advancement;
import hu.trigary.advancementcreator.AdvancementFactory;
import hu.trigary.advancementcreator.shared.ItemObject;
import hu.trigary.advancementcreator.trigger.TickTrigger;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import tech.sebazcrc.simonsays.Main;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class AdvancementManager {

    private final AdvancementFactory factory;
    private final NamespacedKey rootKey;
    private NamespacedKey lastKey;

    private final Set<NamespacedKey> advancements;
    private static AdvancementManager instance;

    public AdvancementManager() {
        this.factory = new AdvancementFactory(Main.getInstance(), true, false);
        this.advancements = new HashSet<>();
        this.rootKey = new NamespacedKey(Main.getInstance(), "si/root");
    }

    public void loadRootAdvance() {
        if (Bukkit.getAdvancement(rootKey) == null) {
            this.factory.getRoot(rootKey.getKey(), "Logros custom", "Logros especiales de Simón dice", Material.DIAMOND, "block/cobblestone")
                    .setAnnounce(false)
                    .setFrame(Advancement.Frame.CHALLENGE)
                    .setToast(false);

            Bukkit.reloadData();
        }
    }

    public void addNewAdvancement(String name, String description, String key, Material mat) {
        String s = key.toLowerCase().replace("!", "").replace("?", "").replace("¡", "").replace("¿", "").replace(" ", "_");
        NamespacedKey k = new NamespacedKey(Main.getInstance(), "si/" + s.substring(0, (Math.min(s.length(), 8))));
        if (lastKey != null) Bukkit.getUnsafe().removeAdvancement(lastKey);

        Advancement advancement = new Advancement(k, new ItemObject().setItem(mat),
                new TextComponent(name), new TextComponent(description))
                .addTrigger("tickt", new TickTrigger())
                .setAnnounce(false)
                .setToast(true)
                .setFrame(Advancement.Frame.GOAL)
                .makeChild(rootKey);
        advancement.activate(true);

        lastKey = k;
        advancements.add(k);
    }

    public void unloadAdvancements(boolean reload) {
        for (NamespacedKey key : advancements) {
            disable(false, key);
            Bukkit.getLogger().log(Level.INFO, "Desactivando el desafío: " + key.getNamespace());
        }

        if (reload) {
            Bukkit.reloadData();
        }
    }

    private void disable(boolean reload, NamespacedKey id) {

        try {
            if (Bukkit.getAdvancement(id) != null) {
                Bukkit.getUnsafe().removeAdvancement(id);
                if (reload) {
                    Bukkit.reloadData();
                }
            }

        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error disabling advancement: " + id, e);
        }
    }

    public static AdvancementManager getInstance() {
        if (instance == null) instance = new AdvancementManager();
        return instance;
    }
}
