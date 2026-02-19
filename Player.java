
public class Player {

    private int row;
    private int col;
    private final int playerNumber;  // 1 또는 2
    private boolean arrived;   // 도착했는지
    private int finishTime;

    int countdown = 0;
    int range = 1;
    boolean hasItem = false;


    int trapcountdown = 0;
    boolean hastrap = false;

    private int diraction;

    public Player(int row, int col, int playerNumber) {
        this.row = row;
        this.col = col;
        this.playerNumber = playerNumber;
        this.arrived = false;
        this.finishTime = 0;
        this.diraction=1;
    }

    // Getter & Setter
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }

    public int getPlayerNumber() { return playerNumber; }

    public boolean isArrived() { return arrived; }
    public void setArrived(boolean arrived) { this.arrived = arrived; }

    public int getFinishTime() { return finishTime; }
    public void setFinishTime(int finishTime) { this.finishTime = finishTime; }

    //트랩


    public boolean isHastrap() {return hastrap;}
    public void setHastrap(boolean hastrap) {this.hastrap = hastrap;}

    public int getTrapcountdown() {return trapcountdown;}
    public void setTrapcountdown(int trapcountdown) {this.trapcountdown = trapcountdown;}

    // 아이템 관련 Getter & Setter
    public boolean isItemActive() { return hasItem; }
    public void setItemActive(boolean hasItem) { this.hasItem = hasItem; }

    public int getItemTimeLeft() { return countdown; }
    public void setItemTimeLeft(int countdown) { this.countdown = countdown; }

    public int getVisionRange() { return range; }
    public void setVisionRange(int range) { this.range = range; }

    
    //방향
    public int getDiraction() {
        return diraction;
    }

    public void setDiraction(int diraction) {
        this.diraction = diraction;
    }
}
