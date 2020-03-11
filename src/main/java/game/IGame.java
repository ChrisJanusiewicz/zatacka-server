package game;

public interface IGame {

    public void start();

    public void stop();

    public float getTickDelta();

    public void doTick();

}
