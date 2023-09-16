package tech.sebazcrc.simonsays;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import tech.sebazcrc.eventlib.library.CC;
import tech.sebazcrc.simonsays.Utils.AdvancementManager;
import tech.sebazcrc.simonsays.Utils.GamePlayer;
import tech.sebazcrc.simonsays.Utils.GameState;
import tech.sebazcrc.simonsays.Utils.Utils;
import tech.sebazcrc.simonsays.library.ScoreHelper;
import tech.sebazcrc.simonsays.library.ScoreStringBuilder;
import tech.sebazcrc.simonsays.library.XSound;

public final class Main extends JavaPlugin implements CommandExecutor, Listener {

    private static Main instance;
    public static String prefix = "";

    private GameState state;
    private List<GamePlayer> players;

    public Map<Player, Integer> currentSubString;
    private ArrayList<String> lines;

    private int time = 0;
    private int rounds = 0;
    private int maxRounds = 15;

    public static boolean DEBUG = false;

    @Override
    public void onEnable() {
        instance = this;
        prefix = Utils.format("&8[&a&lSimón Dice&8] &7&l➤ &r&f");

        this.state = GameState.WAITING;
        this.players = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            GamePlayer gp = new GamePlayer(p.getUniqueId(), p.getName());
            this.players.add(gp);
        }

        this.lines = new ArrayList<>();

        lines.add("&6&lSebazCRC Projects");
        lines.add("&e&lS&6&lebazCRC Projects");
        lines.add("&e&lS&6&lebazCRC Projects");
        lines.add("&e&lSe&6&lbazCRC Projects");
        lines.add("&e&lSeb&6&lazCRC Projects");
        lines.add("&e&lSeba&6&lzCRC Projects");
        lines.add("&e&lSebaz&6&lCRC Projects");
        lines.add("&e&lSebazC&6&lRC Projects");
        lines.add("&e&lSebazCR&6&lC Projects");
        lines.add("&e&lSebazCRC &6&lProjects");
        lines.add("&e&lSebazCRC P&6&lrojects");
        lines.add("&e&lSebazCRC Pr&6&lojects");
        lines.add("&e&lSebazCRC Pro&6&ljects");
        lines.add("&e&lSebazCRC Proj&6&lects");
        lines.add("&e&lSebazCRC Proje&6&lcts");
        lines.add("&e&lSebazCRC Projec&6&lts");
        lines.add("&e&lSebazCRC Project&6&ls");
        lines.add("&e&lSebazCRC Projects");

        this.currentSubString = new HashMap<>();

        Objects.requireNonNull(getCommand("simon")).setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);

        AdvancementManager.getInstance().loadRootAdvance();

        tickAll();
    }

    private void tickAll() {
        Bukkit.getScheduler().runTaskTimer(instance, () -> {

            for (GamePlayer player : getPlayers()) {
                if (player.getPlayer() != null) tickScoreboard(player);
            }

            if (getState() == GameState.PLAYING) {
                time++;

                int module = (time % (10*60));
                int reaming = (10*60) - module;

                if (reaming == 45 || reaming == 30 || reaming == 20 || reaming == 15 || reaming <= 10 || module == 0) {
                    if (module == 0) {
                        rounds++;
                        List<String> names = getPlayers().stream().filter(player -> player.getChallenge() != null && !player.isEliminated()).map(GamePlayer::getName).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
                        Bukkit.broadcastMessage(String.valueOf(module));

                        int reamingPlayers = 0;
                        String ronda = "¡Comienza la ronda " + (rounds+1) + "!";

                        if (names.size() > 0) {
                            for (GamePlayer gp : getPlayers()) {
                                gp.sendMessage((names.size() > 1 ? "Los jugadores &b" + Joiner.on(", ").join(names) + "&f no han logrado" : "El jugador &b" + names.get(0) + " &fno ha logrado") + " completar su desafío.");
                                gp.playSound(XSound.ENTITY_WITHER_DEATH, 5.0F, 1.0F);

                                if (names.contains(gp.getName())) {
                                    gp.loose();
                                } else {
                                    gp.assignChallenge();
                                    reamingPlayers++;
                                }
                            }
                        } else {
                            for (GamePlayer gp : getPlayers()) {
                                gp.playSound(XSound.BLOCK_NOTE_BLOCK_PLING, 5.0F, 10.0F);
                                gp.sendMessage("¡Ningún jugador ha sido eliminado!.");
                                if (!gp.isEliminated()) {
                                    gp.assignChallenge();
                                    reamingPlayers++;
                                }
                            }
                        }

                        if (reamingPlayers <= 1) {
                            endGame(reamingPlayers <= 0);
                        } else if ((rounds+1) >= maxRounds) {
                            Bukkit.broadcastMessage(CC.formatText("&e¡Han alcanzado el máximo de rondas! (&b" + maxRounds + "&e)."));
                            endGame(true);
                        } else {
                            AdvancementManager.getInstance().addNewAdvancement(ronda, ronda, "rn" + rounds, Material.GOLDEN_APPLE);
                        }

                    } else {
                        for (GamePlayer gp : getPlayers()) {
                            gp.playSound(XSound.UI_BUTTON_CLICK, 5.0F, 1.0F);
                            gp.sendMessage("¡Quedan &b" + reaming + " &fsegundos para cumplir los desafíos!");
                        }
                    }
                }
            }
        }, 0L, 20L);
    }

    private void endGame(boolean forceEnd) {
        state = GameState.ENDING;

        GamePlayer winner = null;
        if (!forceEnd) {
            winner = getPlayers().stream().filter(gp -> gp.getChallenge() != null).collect(Collectors.toList()).get(0);
            Player w = winner.getPlayer();

            SplittableRandom random = new SplittableRandom();
            if (w != null) {
                for (int i = 0; i < 9; i++) {
                    org.bukkit.Location l = w.getWorld().getHighestBlockAt(w.getLocation().clone().add((random.nextBoolean() ? 1 : -1) * (random.nextInt(10) + 6), 0, (random.nextBoolean() ? 1 : -1) * (random.nextInt(10) + 6))).getLocation();

                    Firework firework = l.getWorld().spawn(l, Firework.class);
                    FireworkMeta meta = firework.getFireworkMeta();

                    meta.addEffect(FireworkEffect.builder().with(random.nextBoolean() ? FireworkEffect.Type.CREEPER : FireworkEffect.Type.STAR).withColor(Color.RED).build());

                    firework.setFireworkMeta(meta);
                    firework.detonate();
                }
            }
        }

        if (winner != null && winner.getChallenge() != null) {
            winner.getChallenge().setHandle(null);
            winner.setChallenge(null);
        }

        for (GamePlayer player : getPlayers()) {

            Player p = player.getPlayer();
            player.setEliminated(false);
            player.setChallenge(null);
            player.setPendingSpectator(false);
            player.playSound(XSound.BLOCK_NOTE_BLOCK_PLING, 10.0F, 1.0F);

            if (forceEnd) {
                player.sendTitle("&e¡Partida terminada!", "&7¡Nadie ganó!", 20 * 6);
                player.sendMessage("Ningún jugador ha conseguido la victoria.");

                if (p != null)
                    p.setGameMode(GameMode.SURVIVAL);
                continue;
            }

            if (player.getUUID() != winner.getUUID()) {
                player.sendTitle("&c¡DERROTA!", "&7¡No ganaste esta vez!", 20 * 8);
            } else {
                player.sendTitle("&2¡Victoria!", "&7¡Felicidades!", 20 * 8);
            }
            player.sendMessage("&7¡El jugador &b" + winner.getName() + " &7ha ganado la partida!.");
        }



        AdvancementManager.getInstance().unloadAdvancements(false);
        AdvancementManager.getInstance().loadRootAdvance();
    }

    private void tickScoreboard(GamePlayer gp) {
        Player p = gp.getPlayer();
        updateScoreboard(gp);

        if (!currentSubString.containsKey(p)) {
            currentSubString.put(p, 0);
        } else {
            int plus = this.currentSubString.get(p) + 1;
            if (plus > this.lines.size() - 1)
                plus = 0;
            this.currentSubString.replace(p, plus);
        }

        ScoreHelper.getByPlayer(p).setTitle(Utils.format(lines.get(this.currentSubString.get(p))));
    }

    private void createScoreboard(Player p) {
        if (!ScoreHelper.hasScore(p)) ScoreHelper.createScore(p).setTitle("&6&lSebazCRC Projects");
    }

    private void updateScoreboard(GamePlayer gp) {
        Player p = gp.getPlayer();
        createScoreboard(p);

        ScoreHelper helper = ScoreHelper.getByPlayer(p);
        String s = getScoreboardLines(gp);

        String[] split = s.split("\n");
        List<String> lines = new ArrayList<>();

        for (String str2 : split) {
            lines.add(Utils.format(str2));
        }

        helper.setSlotsFromList(lines);

    }


    private String getScoreboardLines(GamePlayer p) {
        ScoreStringBuilder b = new ScoreStringBuilder(true);

        if (getState() == GameState.PLAYING || getState() == GameState.ENDING) {

            int showing = (time % 2);
            if (showing == 1) {
                b.add("&3Jugadores: &f" + (int) getPlayers().stream().filter(player -> !player.isEliminated() && player.getPlayer() != null).count());
            } else {
                b.add("&3Espectadores: &f" + (int) getPlayers().stream().filter(player -> player.isEliminated() && player.getPlayer() != null).count());
            }
            b.add(" ");

            if (getState() != GameState.ENDING) {
                if (!p.isEliminated()) {
                    b.add("&3Simón dice:");
                    b.add("&f" + (p.getChallenge() == null ? "Nada." : Utils.format(p.getChallenge().getName())));
                    b.add(" ");
                }
                b.add("&3Ronda: &b" + (rounds+1) + "/" + maxRounds);
                b.add("&3Tiempo: &f" + getTime());
                b.add(" ");
            }

        } else {
            b.add("&6&lESPERANDO...");
            b.add(" ");
            b.add("&3Jugadores: &f" + Bukkit.getOnlinePlayers().size());
        }

        return b.build();
    }

    private String getTime() {
        int hrs = time / 3600;
        int minAndSec = time % 3600;
        int min = minAndSec / 60;
        int sec = minAndSec % 60;

        return (hrs > 9 ? hrs : "0" + hrs) + ":" + (min > 9 ? min : "0" + min) + ":" + (sec > 9 ? sec : "0" + sec);
    }

    @Override
    public void onDisable() {
        AdvancementManager.getInstance().unloadAdvancements(true);
        if (state == GameState.PLAYING) endGame(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Utils.format("&cComando incompleto."));
            return false;
        }

        if (!sender.hasPermission("simonsays.use")) {
            sender.sendMessage(Utils.format("&cNo tienes permisos."));
            return false;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (state != GameState.WAITING) {
                sender.sendMessage(Utils.format("&cEl juego ya ha comenzado."));
                return false;
            }

            sender.sendMessage(Utils.format("&aHas comenzado la partida."));
            startGame(false);
            return true;
        } else if (args[0].equalsIgnoreCase("restart")) {
            if (state == GameState.WAITING) {
                sender.sendMessage(Utils.format("&cEl juego no ha comenzado."));
                return false;
            }

            sender.sendMessage(Utils.format("&aHas reiniciado la partida."));
            startGame(true);
            return true;
        } else if (args[0].equalsIgnoreCase("time")) {
            this.time = (9*60) + 50;
            return true;
        } else if (args[0].equalsIgnoreCase("advance")) {
            String key = args[1];
            Bukkit.getUnsafe().removeAdvancement(new NamespacedKey(instance, key));
            return true;
        } else if (args[0].equalsIgnoreCase("data")) {
            Bukkit.reloadData();
            return true;
        } else if (args[0].equalsIgnoreCase("debug")) {
            DEBUG = !DEBUG;
            sender.sendMessage("Debug cambiado a: " + DEBUG);
            return true;
        }

        sender.sendMessage(Utils.format("&cNo se ha encontrado ese sub-comando."));
        return false;
    }

    private void startGame(boolean restart) {
        rounds = 0;
        time = 0;

        new BukkitRunnable() {

            private int reaming = 5;

            @Override
            public void run() {
                if (reaming > 0) {
                    for (GamePlayer gp : getPlayers()) {
                        gp.sendTitle("&6&l" + reaming, "¡Prepárate!", 80);
                        gp.playSound(XSound.BLOCK_NOTE_BLOCK_PLING, 10.0F, 6.0F);
                    }
                    reaming--;
                    return;
                }

                for (GamePlayer gp : getPlayers()) {
                    Player p = gp.getPlayer();

                    if (restart) {
                        gp.setEliminated(false);
                        gp.setPendingSpectator(false);
                        gp.setChallenge(null);
                    }

                    if (p == null) {
                        gp.setChallenge(null);
                        gp.setEliminated(true);
                        continue;
                    }

                    gp.sendTitle("&e¡Buena suerte!", "", 40);
                    gp.sendMessage("&e¡La partida ha comenzado!");
                    gp.playSound(XSound.ENTITY_ELDER_GUARDIAN_CURSE, 5.0F, 1.0F);
                    gp.assignChallenge();

                    World w = Bukkit.getWorld("world");
                    p.teleport(w.getHighestBlockAt(new org.bukkit.Location(w, 0, 0, 0)).getLocation().clone().add(0, 3, 0));

                    p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                    p.setFoodLevel(20);
                    p.setExp(0);
                    p.setLevel(0);
                    p.setGameMode(GameMode.SURVIVAL);
                }
                AdvancementManager.getInstance().addNewAdvancement("¡Bienvenidos a Simón dice!", "¡Bienvenidos a Simón dice!", "welcmsg", Material.OAK_SIGN);

                Main.this.state = GameState.PLAYING;
                this.cancel();
            }
        }.runTaskTimer(instance, 0L, 20L);
    }

    public static Main getInstance() {
        return instance;
    }

    public GameState getState() {
        return state;
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public GamePlayer getPlayer(String name) {
        for (GamePlayer player : getPlayers()) {
            if (player.getName().equalsIgnoreCase(name)) return player;
        }
        return null;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        GamePlayer gp = getPlayer(e.getPlayer().getName());
        if (gp == null) {
            gp = new GamePlayer(e.getPlayer().getUniqueId(), e.getPlayer().getName());
            if (state == GameState.PLAYING) {
                gp.setEliminated(true);
                gp.setChallenge(null);
                gp.sendMessage("&eEres un espectador de esta ronda, ya que has entrado en media partida.");
                gp.sendTitle("&aEspectador", "&7Espera a que finalicen", 20*5);
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
            players.add(gp);
        }

        if (gp.isPendingSpectator()) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
            gp.setPendingSpectator(false);
        }

        updateScoreboard(gp);
        gp.getPlayer().setScoreboard(ScoreHelper.getByPlayer(gp.getPlayer()).getScoreboard());
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.isCancelled()) return;

        World w = e.getLocation().getWorld();
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) return;

        if (e.getEntity() instanceof Monster && w.getEnvironment() == World.Environment.NETHER) {

            if (w.getLivingEntities().stream().filter(entity -> entity instanceof Monster).count() > 50) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (state != GameState.PLAYING && !e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (state != GameState.PLAYING) {
                e.setCancelled(true);
                return;
            }

            Player p = (Player) e.getEntity();
            if (getPlayer(p.getName()).isEliminated())
                e.setCancelled(true);
        }
    }

    public int getMaxRounds() {
        return maxRounds;
    }
}
