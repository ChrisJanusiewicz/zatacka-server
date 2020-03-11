package game;

import matchmaking.Lobby;
import matchmaking.RatedPlayer;
import net.IClient;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    // hold list of games, clients assigned to each game.
    // when message received from client, find game using map

    private Map<IClient, Player> clientPlayerMap;
    private Map<ZatackaGame, WorkerNode> gameWorkerMap;
    private Map<Player, ZatackaGame> playerGameMap;

    private WorkerNode[] workers;
    private Thread[] workerThreads;
    private int workerCount;

    private int mapWidth;
    private int mapHeight;

    public GameManager(int mapWidth, int mapHeight, int workerCount) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        clientPlayerMap = new HashMap<IClient, Player>();
        gameWorkerMap = new HashMap<ZatackaGame, WorkerNode>();
        playerGameMap = new HashMap<Player, ZatackaGame>();

        this.workerCount = workerCount;

        initWorkers();
    }

    private void initWorkers() {
        workers = new WorkerNode[workerCount];
        workerThreads = new Thread[workerCount];

        for (int i = 0; i < workerCount; i++) {
            workers[i] = new WorkerNode(4);
            workerThreads[i] = new Thread(workers[i]);
            workerThreads[i].start();
        }
    }

    private WorkerNode getLeastBusyWorker() {
        int minIndex = -1;
        int minGames = Integer.MAX_VALUE;

        for (int i = 0; i < workerCount; i++) {
            int load = workers[i].getActiveGames();
            if (load < minGames) {
                minGames = load;
                minIndex = i;
            }
        }
        return workers[minIndex];
    }

    public void startNewGame(Lobby lobby) {
        WorkerNode worker = getLeastBusyWorker();

        ZatackaGame game = new ZatackaGame(30);

        byte b = 0;
        for (RatedPlayer rp : lobby.getPlayers()) {
            Player player = new Player(rp.getClient(), b++);
            game.addPlayer(player);
        }

        worker.addGame(game);
    }

}