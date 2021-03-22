public class Task implements Comparable {
    private int arrivalTime;
    private int ID;
    private int processingTime;
    private int waitingTime;


    public Task (int ID, int arrivalTime, int processingTime) {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.processingTime = processingTime;

    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public int getID() {
        return ID;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public String toString() {
        return "("+getID()+ "," + getArrivalTime()+ "," + getProcessingTime() + ")";
    }

    @Override
    public int compareTo(Object x) {
        return this.arrivalTime-((Task)x).arrivalTime;
    }
}
