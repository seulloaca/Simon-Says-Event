package tech.sebazcrc.simonsays.Challenges.List;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import tech.sebazcrc.simonsays.Challenges.Challenge;
import tech.sebazcrc.simonsays.Main;
import tech.sebazcrc.simonsays.Utils.GamePlayer;
import tech.sebazcrc.simonsays.Utils.GameState;
import tech.sebazcrc.simonsays.Utils.Utils;

public class ShootArrowChallenge extends Challenge {
    public ShootArrowChallenge(GamePlayer handle) {
        super(handle, "Dispara una flecha", new String[] {Utils.format("&7Dispara una flecha a cualquier lugar.")});
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent e) {
        if (getHandle() == null) return;
        if (Main.getInstance().getState() != GameState.PLAYING || (getHandle().getChallenge() == null || !getHandle().getChallenge().getName().equalsIgnoreCase(getName()))) {
            setHandle(null);
            return;
        }

        if (e.getEntity() instanceof Arrow && e.getEntity().getShooter() instanceof Player && ((Player) e.getEntity().getShooter()).getUniqueId().toString().equalsIgnoreCase(getHandle().getUUID().toString())) {
            getHandle().completeChallenge();
            setHandle(null);
        }
    }
}
