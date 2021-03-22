import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class SimulationManager implements Runnable {
    public int timeLimit;
    public int maxProcessingTime;
    public int minProcessingTime;
    public int maxArrivalTime;
    public int minArrivalTime;
    public int numberOfServers;
    public int numberOfClients;

    public String in;
    public String out;

    private Scheduler scheduler;

    private List<Task> generatedTasks;


    private SimulationManager(String in, String out) {
        this.in = in;
        this.out = out;
        deleteFileContents();
        readFile();
        scheduler = new Scheduler(numberOfServers,numberOfClients);
        scheduler.setStrategy();
        generateNRandomTasks();
    }

    public void deleteFileContents() {
        PrintWriter sterge = null;
        try {
            sterge = new PrintWriter(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        sterge.print("");
        sterge.close();
    }

    public void readFile() {
        File fisier = new File(in);
        Scanner myReader = null;
        try {
            myReader = new Scanner(fisier);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int i = 0;
        while (myReader.hasNextLine()) {
            i++;
            String data = myReader.nextLine();
            if (i == 1)
                numberOfClients = Integer.parseInt(data);
            if (i == 2)
                numberOfServers = Integer.parseInt(data);
            if (i == 3)
                timeLimit = Integer.parseInt(data);
            if (i == 4) {
                String[] minMaxArrivalTime = data.split(",");
                minArrivalTime = Integer.parseInt(minMaxArrivalTime[0]);
                maxArrivalTime = Integer.parseInt(minMaxArrivalTime[1]);
            }
            if (i == 5) {
                String[] minMaxProcessingTime = data.split(",");
                minProcessingTime = Integer.parseInt(minMaxProcessingTime[0]);
                maxProcessingTime = Integer.parseInt(minMaxProcessingTime[1]);
            }
        }
    }

    private void generateNRandomTasks() {
        Random rand = new Random();
        generatedTasks = new ArrayList<Task>(numberOfClients);
        int aTime, pTime;
        for (int i = 0; i < numberOfClients; i++){
            aTime = rand.nextInt(maxArrivalTime - minArrivalTime) + minArrivalTime;
            pTime = rand.nextInt(maxProcessingTime - minProcessingTime) + minProcessingTime;
            generatedTasks.add(new Task(i + 1, aTime, pTime));
        }
        Collections.sort((ArrayList) generatedTasks);
    }

    public void run() {
        int currentTime = 0;
        List<Task> toRemove = new ArrayList<>(generatedTasks.size());
        while (currentTime < timeLimit && (!generatedTasks.isEmpty() || scheduler.getNoOfTasks() != 0)) {

            for (Task a : generatedTasks) {
                if (currentTime == a.getArrivalTime()) {
                    scheduler.dispatchTask(a);
                    toRemove.add(a);
                }
            }
            generatedTasks.removeAll(toRemove);
            printInFile(currentTime);
            currentTime++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!generatedTasks.isEmpty() || scheduler.getNoOfTasks() != 0)
            printOk(false,currentTime);
        else printOk(true,currentTime);
        printAvgWaitingTime();
        stopServers();
    }

    public void printOk(boolean ok, int currentTime) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream(out, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (!ok) {
            pw.write("\nNot all clients processed. Need more time.\n");
        }
        else {
            int i = 1;
            pw.write("Time " + currentTime + "\n");
            pw.write("Waiting clients: \n");
            for (Server a : scheduler.getServers()) {
                pw.write("Queue " + i + ": closed \n");
                i++;
            }
            pw.write("\nAll clients processed. No more clients in queues.\n");
        }
        pw.close();
    }

    public void stopServers() {
        for (Server a : scheduler.getServers())
        {
            a.setOpened(false);
            synchronized (a) {
                a.notify();
            }
        }
    }

    public void printAvgWaitingTime() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream(out, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        float avg = ((float)scheduler.getTotalTime()) / numberOfClients;
        pw.write("Average waiting time: " + avg + "\n");
        pw.close();
    }

    public void printInFile(int currentTime) {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(out, true));
            pw.write("Time " + currentTime + "\n");
            pw.write("Waiting clients: ");
            for (Task a : generatedTasks) {
                pw.write(a.toString());
            }
            pw.write("\n");
            int i = 1;
            for (Server a : scheduler.getServers()) {
                pw.write("Queue " + i + ": ");
                pw.write(a.toString());
                if (a.getWaitingPeriod().intValue() == 0)
                    pw.write("closed");
                i++;
                pw.write("\n");
            }
            pw.write("\n");
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        SimulationManager gen = new SimulationManager(args[0],args[1]);
        Thread t = new Thread(gen);
        t.start();
    }
}
