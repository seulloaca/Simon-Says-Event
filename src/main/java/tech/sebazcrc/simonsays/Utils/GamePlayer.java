package tech.sebazcrc.simonsays.Utils;

import hu.trigary.advancementcreator.shared.Effect;
import hu.trigary.advancementcreator.shared.EffectObject;
import hu.trigary.advancementcreator.shared.RangeObject;
import hu.trigary.advancementcreator.shared.StatusEffectsObject;
import hu.trigary.advancementcreator.trigger.EffectsChangedTrigger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tech.sebazcrc.eventlib.library.MathUtils;
import tech.sebazcrc.simonsays.Challenges.Challenge;
import tech.sebazcrc.simonsays.Challenges.List.*;
import tech.sebazcrc.simonsays.Main;
import tech.sebazcrc.simonsays.library.XSound;

import java.util.Arrays;
import java.util.SplittableRandom;
import java.util.UUID;

public class GamePlayer {

    private UUID uuid;
    private String name;
    private Challenge challenge;

    private boolean isPendingSpectator;
    private boolean eliminated;

    public GamePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.eliminated = false;
        this.challenge = null;
        this.isPendingSpectator = false;
    }

    public void completeChallenge() {
        playSound(XSound.ENTITY_PLAYER_LEVELUP, 5.0F, 100.0F);
        sendMessage("¡Felicidades!, has completado el desafío: &a" + challenge.getName());

        for (GamePlayer player : Main.getInstance().getPlayers()) {
            if (!player.getUUID().toString().equalsIgnoreCase(getUUID().toString())) player.sendMessage("El jugador &b" + getName() + " &fha completado su desafío, &7" + getChallenge().getDescription()[0]);
        }
        setChallenge(null);
    }

    public void sendTitle(String s, String s1, int i) {
        Player p = getPlayer();
        if (p != null) p.sendTitle(Utils.format(s), Utils.format(s1), 1, i, 5);
    }

    public void playSound(XSound sound, Float volume, Float pitch) {
        Player p = getPlayer();
        if (p != null) p.playSound(p.getLocation(), sound.parseSound(), volume, pitch);
    }

    public void sendMessage(String msg) {
        Player p = getPlayer();
        if (p != null) p.sendMessage(Utils.format(Main.prefix + msg));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;

        if (challenge != null) {
            sendMessage("Simón dice... " + challenge.getDescription()[0]);
            playSound(XSound.BLOCK_NOTE_BLOCK_PLING, 10.0F, 7.0F);
        }
    }

    public void loose() {
        this.setEliminated(true);
        this.setChallenge(null);
        if (getPlayer() == null) setPendingSpectator(true);
    }

    public boolean hasCompletedChallenge() {
        return challenge == null && Main.getInstance().getState() != GameState.WAITING;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
        Player p = getPlayer();
        if (p != null && eliminated && Main.getInstance().getState() == GameState.PLAYING) p.setGameMode(GameMode.SPECTATOR);
    }

    public void assignChallenge() {
        if (eliminated) return;
        SplittableRandom random = new SplittableRandom();
        int c = MathUtils.random(99)+1;

        if (c >= 1 && c <= 25) {
            UseItemChallenge.UsableItem item = UseItemChallenge.UsableItem.values()[random.nextInt(UseItemChallenge.UsableItem.values().length)];
            setChallenge(new UseItemChallenge(this, item));
        } else if (c <= 45) {
            KillEntityChallenge.KillEntityType type = KillEntityChallenge.KillEntityType.values()[random.nextInt(KillEntityChallenge.KillEntityType.values().length)];
            setChallenge(new KillEntityChallenge(this, type));
        } else if (c <= 65) {
            setChallenge(new ShootArrowChallenge(this));
        } else if (c <= 70) {
            TameAnimalChallenge.TameAnimalType type = TameAnimalChallenge.TameAnimalType.values()[random.nextInt(TameAnimalChallenge.TameAnimalType.values().length)];
            setChallenge(new TameAnimalChallenge(this, type));
        } else if (c <= 73) {
            LevelUpgradeChallenge challenge = new LevelUpgradeChallenge(this);
            setChallenge(challenge);
        } else if (c <= 77) {
            SearchBiomeChallenge challenge = new SearchBiomeChallenge(this, SearchBiomeChallenge.SimonBiome.values()[random.nextInt(SearchBiomeChallenge.SimonBiome.values().length)]);
            setChallenge(challenge);
        } else {
            if (random.nextBoolean()) {
                FindItemChallenge.PickupItemType type = FindItemChallenge.PickupItemType.values()[random.nextInt(FindItemChallenge.PickupItemType.values().length)];
                setChallenge(new FindItemChallenge(this, type));
            } else {
                KillEntityChallenge.KillEntityType type = KillEntityChallenge.KillEntityType.values()[random.nextInt(KillEntityChallenge.KillEntityType.values().length)];
                setChallenge(new KillEntityChallenge(this, type));
            }
        }
    }

    public boolean isPendingSpectator() {
        return isPendingSpectator;
    }

    public void setPendingSpectator(boolean pendingSpectator) {
        isPendingSpectator = pendingSpectator;
    }
}
