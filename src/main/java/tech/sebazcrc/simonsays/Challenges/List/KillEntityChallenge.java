package tech.sebazcrc.simonsays.Challenges.List;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import tech.sebazcrc.simonsays.Challenges.Challenge;
import tech.sebazcrc.simonsays.Main;
import tech.sebazcrc.simonsays.Utils.GamePlayer;
import tech.sebazcrc.simonsays.Utils.GameState;
import tech.sebazcrc.simonsays.Utils.Utils;

public class KillEntityChallenge extends Challenge {
    private KillEntityType type;

    public KillEntityChallenge(GamePlayer handle, KillEntityType type) {
        super(handle, "Mata un(a): " + type.getName(), new String[] { Utils.format("&7Debes encontrar y matar la siguiente criatura: " + type.getName()) });
        this.type = type;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (getHandle() == null) return;
        if (Main.getInstance().getState() != GameState.PLAYING || (getHandle().getChallenge() == null || !getHandle().getChallenge().getName().equalsIgnoreCase(getName()))) {
            setHandle(null);
            return;
        }

        if (e.getEntity().getType() == type.getType() && e.getEntity().getKiller() != null && e.getEntity().getKiller().getUniqueId().toString().equalsIgnoreCase(getHandle().getUUID().toString())) {
            getHandle().completeChallenge();
            setHandle(null);
        }
    }

    public enum KillEntityType {
        ZOMBIE("Zombi", EntityType.ZOMBIE), BEE("Abeja", EntityType.BEE), SKELETON("Esqueleto", EntityType.SKELETON),
        CREEPER("Creeper", EntityType.CREEPER), VILLAGER("Aldeano", EntityType.VILLAGER), IRON_GOLEM("Gólem de Hierro", EntityType.IRON_GOLEM),
        BAT("Murciélago", EntityType.BAT), CAT("Gato", EntityType.CAT), WOLF("Lobo", EntityType.WOLF);

        private String name;
        private EntityType type;

        KillEntityType(String name, EntityType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public EntityType getType() {
            return type;
        }
    }
}
