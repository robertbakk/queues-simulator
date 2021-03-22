import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private Thread[] threads;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;
    private int totalTime = 0;


    public Scheduler (int maxNoServers, int maxTasksPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        startThreads();
    }

    public void startThreads() {
        threads = new Thread[maxNoServers];
        servers = new ArrayList<>(maxNoServers);
        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server(maxTasksPerServer);
            servers.add(server);
            threads[i] = new Thread(server);
            threads[i].start();
        }
    }

    public void setStrategy () {
        strategy = new Strategy();
    }

    public void dispatchTask (Task t) {
        int i = strategy.addTask(servers,t);
        t.setWaitingTime(t.getProcessingTime() + i);
        totalTime += t.getWaitingTime();
    }

    public int getNoOfTasks() {
        int nr = 0;
        for (Server a : servers) {
            nr += a.getNoOfTasks();
        }
        return nr;
    }

    public List<Server> getServers() {
        return servers;
    }

    public int getTotalTime() {
        return totalTime;
    }
}
