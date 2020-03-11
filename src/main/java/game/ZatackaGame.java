package game;

import net.IClient;
import net.events.DistributorListener;
import net.events.MessageDistributor;
import net.message.Message;
import net.message.MessageType;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class ZatackaGame implements IGame {

    private int targetTickRate;
    private long lastCheckNano;
    private long targetTickDelta;
    private long lastTickNano;
    private float tickDelta;  // How many ticks must be done to catch up

    private DistributorListener puListener;

    private List<Player> players;

    private Map<Long, Player> playerMap;

    private BlockingQueue<PlayerUpdate> updateQueue;

    private boolean running;

    public ZatackaGame(int targetTickRate) {

        this.targetTickRate = targetTickRate;
        targetTickDelta = 1000000000 / targetTickRate;

        running = false;

        this.players = new ArrayList<Player>();
        this.playerMap = new HashMap<Long, Player>();

        MessageDistributor distributor = new MessageDistributor();
        distributor.addHandler(MessageType.GAME_PLAYER_UPDATE, new PlayerUpdateHandler(this));

        puListener = new DistributorListener(distributor);

    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void start() {

        lastTickNano = System.nanoTime();
        lastCheckNano = System.nanoTime();
        running = true;

        Message message = null;
        try {
            message = GameMessageUtils.buildGameStateMessage(GameState.ACTIVE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        broadcast(message);

    }


    // executed on message receiving thread. must pass to worker node through blocking queue
    public void handlePlayerUpdate(PlayerUpdate pu, IClient client) {


    }

    public void broadcast(Message message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public byte getPlayerID(IClient client) {
        Player player = playerMap.get(client.getID());

        if (player == null) {
            System.out.println("Client is not registered to this game");
            return (byte) 255;
        }
        return player.getID();
    }


    @Override
    public void stop() {
        running = false;
    }

    @Override
    public float getTickDelta() {

        if (!running) {
            start();
        }

        long currNano = System.nanoTime();
        long deltaNano = currNano - lastCheckNano;
        lastCheckNano = currNano;

        tickDelta += (float) deltaNano / (float) targetTickDelta;

        return tickDelta;
    }

    @Override
    public void doTick() {
        if (tickDelta < 1) {
            System.err.println("Tried to perform tick update before it was necessary");
            return;
        }
        doGameTick();
    }

    // Add player to game and register listeners to handle GameUpdate Messages
    public void addPlayer(Player player) {
        this.players.add(player);
        this.playerMap.put(player.getClient().getID(), player);
        player.getClient().setListener(puListener);
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    private void doGameTick() {
        // long threadID = Thread.currentThread().getId();
        // System.out.println(String.format("[Thread: %d] Delta: %fms", threadID, delta / 1000000.0f));


        tickDelta -= 1;
    }

}
