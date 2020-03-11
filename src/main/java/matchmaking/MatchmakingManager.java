package matchmaking;


import data.ClientLoginInfo;
import data.MapMediator;
import data.User;
import data.UserRepository;
import game.GameManager;
import net.IClient;
import net.message.Message;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MatchmakingManager {

    private final int GAME_MAX_PLAYERS = 8;
    private GameManager gameManager;
    private UserRepository userRepository;
    private MapMediator<String, ClientLoginInfo> mediator;
    private Map<IClient, RatedPlayer> playerMap;
    private LinkedList<RatedPlayer> playerQueue;
    private Map<Long, Lobby> lobbyMap;
    private Map<IClient, Lobby> clientLobbyMap;

    public MatchmakingManager(GameManager gameManager, UserRepository userRepository, MapMediator<String, ClientLoginInfo> mediator) {
        playerMap = new HashMap<IClient, RatedPlayer>();
        lobbyMap = new HashMap<Long, Lobby>();
        playerQueue = new LinkedList<RatedPlayer>();
        clientLobbyMap = new HashMap<IClient, Lobby>();

        this.gameManager = gameManager;
        this.userRepository = userRepository;
        this.mediator = mediator;
        //lobbyMap.put(5, new Lobby(5, "default test lobby"));
    }

    public void handleServerJoinRequest(String username, byte[] token, IClient client) throws IOException {
        ServerJoinService serverJoinService = new ServerJoinService(this, mediator, username, token, client);
        Thread serviceThread = new Thread(serverJoinService);
        serviceThread.start();
    }

    public void joinLobby(long lobbyID, IClient client) throws IOException {

        RatedPlayer player = playerMap.get(client);

        if (player == null) {
            // player has not been authenticated
            System.out.println("player has not been authenticated");
            return;
        }

        // check if lobby request is valid
        Lobby lobby = lobbyMap.get(lobbyID);

        if (lobby == null) {

            // TODO: send back a packet saying you can't join that lobby
            System.out.println("Could not locate lobby requested by user. ID: " + lobbyID);

        } else { // if (!lobby.isJoinable()){

            lobby.addPlayer(player);

        }
    }


    public void handleJoinQueueRequest(Message message, IClient client) throws IOException {

    }


    public void handleGameStartRequest(IClient client) {

    }

    public void sendLobbyList(IClient client) {
        try {
            Message message = LobbyMessageUtils.buildLobbyListMessage(lobbyMap.values());
            client.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void joinPlayer(IClient client, String username) {
        User user;
        try {
            user = userRepository.getUserByName(username);
        } catch (SQLException e) {
            System.out.println(String.format("Error retrieving user %s from database...", username));
            return;
        }

        if (user == null) {
            System.out.println("User not found in database");
            return;
        }

        RatedPlayer player = new RatedPlayer(user, client);
        playerMap.put(client, player);

        sendLobbyList(client);
    }

    public Lobby createLobby(IClient client) throws IOException {
        Lobby lobby = new Lobby(GAME_MAX_PLAYERS);

        RatedPlayer player = playerMap.get(client);
        lobby.addPlayer(player);
        clientLobbyMap.put(client, lobby);

        return null;
    }


    public Lobby getLobbyByID(int targetID) {
        return lobbyMap.get(targetID);
    }

    public void launchLobby(IClient client) {
        Lobby lobby = clientLobbyMap.get(client);

        // TODO: check so that only owner can start

        if (lobby != null) {

            gameManager.startNewGame(lobby);
        }

    }
}
