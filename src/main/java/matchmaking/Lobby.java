package matchmaking;

import net.message.Message;
import util.Identifiable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Lobby extends Identifiable {

    private List<RatedPlayer> playerList;


    private int maxPlayers;

    private boolean gameActive;


    public Lobby(int maxPlayers) {
        this.maxPlayers = maxPlayers;

        playerList = new ArrayList<RatedPlayer>();
        gameActive = false;
    }

    public void addPlayer(RatedPlayer player) {
        playerList.add(player);
        stateChanged();
    }

    public String getName() {
        return "Lobby name";
    }

    public void stateChanged() {

        Message message;
        try {
            message = LobbyMessageUtils.buildLobbyDataMessage(getID(), getPlayers());

        } catch (IOException e) {
            System.out.println("Error constructing lobby data message");
            return;
        }

        for (RatedPlayer player : playerList) {
            player.sendMessage(message);
        }

    }

    public Collection<RatedPlayer> getPlayers() {
        return playerList;
    }

}
