package game;

import util.Identifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkerNode extends Identifiable implements Runnable {

    private BlockingQueue<ZatackaGame> gameQueue;   // thread safe queue for introducing new games to node

    private List<ZatackaGame> games;
    private Map<Player, ZatackaGame> playerGameMap;

    private int maxGames;


    public WorkerNode(int maxGames) {
        games = new ArrayList<ZatackaGame>();
        playerGameMap = new HashMap<Player, ZatackaGame>();
        gameQueue = new LinkedBlockingQueue<ZatackaGame>();

        this.maxGames = maxGames;
    }

    public int getActiveGames() {
        return games.size();
    }

    public void removeGame(ZatackaGame game) {
        game.stop();

        for (Player player : game.getPlayers()) {
            playerGameMap.remove(player);
        }
    }


    public void addGame(ZatackaGame game) {
        gameQueue.add(game);
    }

    private void getNewGames() {
        gameQueue.drainTo(games);
        /*
        while (!gameQueue.isEmpty()) {
            try {
                games.add(gameQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
    }


    @Override
    public void run() {


        long lastCheckNano = 0;
        long currentNano;
        long checkInterval = 1000000000;

        while (true) {

            currentNano = System.nanoTime();

            if ((currentNano - lastCheckNano) > checkInterval) {
                lastCheckNano = currentNano;
                getNewGames();  // add queued games into node loop
            }


            if (getActiveGames() == 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {

                for (ZatackaGame game : games) {

                    float tickDelta = game.getTickDelta();

                    if (tickDelta >= 2.0f) {
                        System.out.println(String.format("[Worker: %d] Running behind by %f ticks", getID(), tickDelta));
                    }
                    if (tickDelta >= 1.0f) {
                        game.doTick();
                    }

                }
            }


        }


    }

    public void update() {

    }

    public void shutdown() {

    }


}
