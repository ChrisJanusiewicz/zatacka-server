package game;

import util.Vector2Int;

public class PlayerUpdate {
    public Vector2Int[] path;
    public byte[] jumpIndices;
    public byte playerID;

    public PlayerUpdate(byte playerID, Vector2Int[] path, byte[] jumpIndices) {
        this.playerID = playerID;
        this.path = path;
        this.jumpIndices = jumpIndices;
    }

}
