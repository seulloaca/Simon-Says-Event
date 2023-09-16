package tech.sebazcrc.simonsays.Challenges.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTameEvent;
import tech.sebazcrc.simonsays.Challenges.Challenge;
import tech.sebazcrc.simonsays.Main;
import tech.sebazcrc.simonsays.Utils.GamePlayer;
import tech.sebazcrc.simonsays.Utils.GameState;
import tech.sebazcrc.simonsays.Utils.Utils;

public class TameAnimalChallenge extends Challenge {

    private TameAnimalType type;

    public TameAnimalChallenge(GamePlayer handle, TameAnimalType t) {
        super(handle, "Doma un " + t.getName(), new String[] {Utils.format("&7Debes encontrar y domar un &a " + t.getName())});
        this.type = t;
    }

    @EventHandler
    public void onTame(EntityTameEvent e) {
        if (getHandle() == null) return;
        if (Main.getInstance().getState() != GameState.PLAYING || (getHandle().getChallenge() == null || !getHandle().getChallenge().getName().equalsIgnoreCase(getName()))) {
            setHandle(null);
            return;
        }

        if (e.getOwner() instanceof Player && e.getOwner().getUniqueId().toString().equalsIgnoreCase(getHandle().getUUID().toString()) && e.getEntity().getType() == type.getType()) {
            getHandle().completeChallenge();
            setHandle(null);
        }
    }

    public enum TameAnimalType {
        WOLF("Lobo", EntityType.WOLF), CAT("Gato", EntityType.CAT);

        private String name;
        private EntityType type;

        TameAnimalType(String name, EntityType type) {
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
