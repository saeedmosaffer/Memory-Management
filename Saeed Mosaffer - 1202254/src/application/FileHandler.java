package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    public static List<PCB> readProcesses(String fileName) {
        List<PCB> processes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                int id = Integer.parseInt(parts[0]);
                int size = Integer.parseInt(parts[1]);
                int time = Integer.parseInt(parts[2]);
                processes.add(new PCB(id, size, time, 0, -1, -1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return processes;
    }
}
