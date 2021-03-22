import java.util.List;

public class Strategy {


    public int addTask(List<Server> servers, Task t) {
        int period = servers.get(0).getWaitingPeriod().intValue();

        for (Server b : servers) {
            if (b.getWaitingPeriod().intValue() < period) {
                period = b.getWaitingPeriod().intValue();
            }
        }

        for (Server b : servers) {
            if (b.getWaitingPeriod().intValue() == period) {
                b.addTask(t);
                break;
            }
        }
        return period;
    }
}
