package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;

public class MemoryManagementSimulator extends Application {
    private MemoryManager memoryManager;
    private List<PCB> readyQueue;
    private List<PCB> jobQueue;
    private VBox memoryView;
    private ListView<String> readyListView;
    private ListView<String> jobListView;
    private int currentTime;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Memory Management Simulator");

        memoryManager = new MemoryManager();
        readyQueue = FileHandler.readProcesses("ready.txt");
        jobQueue = FileHandler.readProcesses("job.txt");
        currentTime = 0;

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        HBox hbox = new HBox(15);
        memoryView = new VBox(10);
        readyListView = new ListView<>();
        jobListView = new ListView<>();
        Button nextStepButton = new Button("Next Step");

        TitledPane readyQueuePane = new TitledPane("Ready Queue", readyListView);
        readyQueuePane.setCollapsible(false);
        TitledPane jobQueuePane = new TitledPane("Job Queue", jobListView);
        jobQueuePane.setCollapsible(false);

        updateReadyListView();
        updateJobListView();
        nextStepButton.setOnAction(e -> advanceSimulation());

        VBox leftPane = new VBox(10, new Label("Memory View"), memoryView);
        VBox rightPane = new VBox(10, readyQueuePane, jobQueuePane);

        hbox.getChildren().addAll(leftPane, rightPane);
        root.getChildren().addAll(hbox, nextStepButton);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
        allocateReadyQueue();
    }

    private void allocateReadyQueue() {
        for (PCB process : readyQueue) {
            process.arrivalTime = currentTime;
            memoryManager.allocateProcess(process);
        }
        updateMemoryView();
    }

    private void advanceSimulation() {
        currentTime++;
        memoryManager.advanceTime();
        bringInNewJobs();
        updateReadyListView();
        updateJobListView();
        updateMemoryView();
    }

    private void bringInNewJobs() {
        if (!jobQueue.isEmpty()) {
            List<PCB> tempQueue = new ArrayList<>(jobQueue);
            for (PCB process : tempQueue) {
                process.arrivalTime = currentTime;
                if (memoryManager.allocateProcess(process)) {
                    jobQueue.remove(process);
                }
            }
        }
    }

    private void updateReadyListView() {
        readyListView.getItems().clear();
        for (PCB process : memoryManager.getAllocatedProcesses()) {
            readyListView.getItems().add("Process " + process.id + ": Size=" + process.size + ", Time Left=" + (process.time - (currentTime - process.arrivalTime)));
        }
    }

    private void updateJobListView() {
        jobListView.getItems().clear();
        for (PCB process : jobQueue) {
            jobListView.getItems().add("Process " + process.id + ": Size=" + process.size + ", Time=" + process.time);
        }
    }

    private void updateMemoryView() {
        memoryView.getChildren().clear();
        List<MemoryManager.MemoryBlock> freeBlocks = memoryManager.getFreeBlocks();
        List<PCB> allocatedProcesses = memoryManager.getAllocatedProcesses();

        // Draw memory blocks
        for (PCB process : allocatedProcesses) {
            Rectangle rect = new Rectangle(20, process.size * 0.2);
            rect.setFill(Color.LIGHTGREEN);
            memoryView.getChildren().add(new Label("Process " + process.id + ": Base=" + process.base + ", Limit=" + process.limit));
            memoryView.getChildren().add(rect);
        }

        // Draw free blocks
        for (MemoryManager.MemoryBlock block : freeBlocks) {
            Rectangle rect = new Rectangle(20, block.size * 0.2);
            rect.setFill(Color.LIGHTCORAL);
            memoryView.getChildren().add(new Label("Free Block: Start=" + block.start + ", Size=" + block.size));
            memoryView.getChildren().add(rect);
        }
    }
}
