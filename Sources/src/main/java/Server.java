import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    private boolean opened = true;


    public Server(int maxTasks) {
        tasks = new ArrayBlockingQueue<Task>(maxTasks);
        waitingPeriod = new AtomicInteger(0);
    }

    public void addTask(Task newTask) {
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getProcessingTime());
        synchronized (this) {
            notify();
        }
    }

    public void run() {
        while (opened) {
            synchronized (this) {
                while (tasks.isEmpty() && opened) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                if (!tasks.isEmpty()) {
                    Task t = tasks.peek();
                    int time = t.getProcessingTime();

                    for (int i = 0; i < time; i++) {
                        Thread.sleep(999);
                        t.setProcessingTime(t.getProcessingTime() - 1);
                        waitingPeriod.addAndGet(-1);
                    }
                    tasks.remove(t);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public int getNoOfTasks() {
        return tasks.size();
    }

    public String toString() {
        String s = "";
        for (Task a : tasks) {
            s += a;
        }
        return s;
    }
}

