package tech.sebazcrc.simonsays.Challenges.List;

import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import tech.sebazcrc.simonsays.Challenges.Challenge;
import tech.sebazcrc.simonsays.Main;
import tech.sebazcrc.simonsays.Utils.GamePlayer;
import tech.sebazcrc.simonsays.Utils.GameState;
import tech.sebazcrc.simonsays.Utils.Utils;

public class SearchBiomeChallenge extends Challenge {

    private SimonBiome biome;

    public SearchBiomeChallenge(GamePlayer handle, SimonBiome b) {
        super(handle, "Encuentra el bioma: " + b.getName(), new String[] {Utils.format("&7Debes buscar y encontrar el siguiente bioma: &e" + b.getName())});
        biome = b;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (getHandle() == null) return;
        if (Main.getInstance().getState() != GameState.PLAYING || (getHandle().getChallenge() == null || !getHandle().getChallenge().getName().equalsIgnoreCase(getName()))) {
            setHandle(null);
            return;
        }

        if (e.getPlayer().getUniqueId().toString().equalsIgnoreCase(getHandle().getUUID().toString()) && e.getTo().getBlock().getBiome().name().contains(biome.name())) {
            getHandle().completeChallenge();
            setHandle(null);
        }
    }

    public enum SimonBiome {
        PLAINS("Pradera", Biome.PLAINS), DESERT("Desierto", Biome.DESERT),
        TAIGA("Taiga", Biome.TAIGA), OCEAN("Oceáno", Biome.OCEAN), RIVER("Río", Biome.RIVER),
        SOUL_SAND_VALLEY("Soul Sand Valley", Biome.SOUL_SAND_VALLEY), CRIMSON_FOREST("Crimson Forest", Biome.CRIMSON_FOREST),
        WARPED_FOREST("Warped Forest", Biome.WARPED_FOREST);

        private String name;
        private Biome handle;

        SimonBiome(String name, Biome handle) {
            this.name = name;
            this.handle = handle;
        }

        public String getName() {
            return name;
        }

        public Biome getHandle() {
            return handle;
        }
    }
}
