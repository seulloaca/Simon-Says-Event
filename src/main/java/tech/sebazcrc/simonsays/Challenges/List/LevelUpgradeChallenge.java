package tech.sebazcrc.simonsays.Challenges.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import tech.sebazcrc.simonsays.Challenges.Challenge;
import tech.sebazcrc.simonsays.Main;
import tech.sebazcrc.simonsays.Utils.GamePlayer;
import tech.sebazcrc.simonsays.Utils.GameState;
import tech.sebazcrc.simonsays.Utils.Utils;

public class LevelUpgradeChallenge extends Challenge {
    public LevelUpgradeChallenge(GamePlayer handle) {
        super(handle, "Sube un nivel de experiencia", new String[]{Utils.format("Deberás subir un nivel de experiencia.")});
    }

    @EventHandler
    public void onLevelUp(PlayerLevelChangeEvent e) {
        if (getHandle() == null) return;
        if (Main.getInstance().getState() != GameState.PLAYING || (getHandle().getChallenge() == null || !getHandle().getChallenge().getName().equalsIgnoreCase(getName()))) {
            setHandle(null);
            return;
        }

        if (e.getNewLevel() > e.getOldLevel() && e.getPlayer().getUniqueId().toString().equalsIgnoreCase(getHandle().getUUID().toString())) {
            getHandle().completeChallenge();
            setHandle(null);
        }
    }
}
