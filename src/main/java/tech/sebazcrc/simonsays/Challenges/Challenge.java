package tech.sebazcrc.simonsays.Challenges;

import org.bukkit.event.Listener;
import tech.sebazcrc.simonsays.Main;
import tech.sebazcrc.simonsays.Utils.GamePlayer;

public class Challenge implements Listener {

    private String name;
    private String[] description;
    private GamePlayer handle;

    public Challenge(GamePlayer handle, String name, String[] description) {
        this.name = name;
        this.description = description;
        this.handle = handle;

        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public String getName() {
        return name;
    }

    public String[] getDescription() {
        return description;
    }

    public GamePlayer getHandle() {
        return handle;
    }

    public void setHandle(GamePlayer handle) {
        this.handle = handle;
    }
}
