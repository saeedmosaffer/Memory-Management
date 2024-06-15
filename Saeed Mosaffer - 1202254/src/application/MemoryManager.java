package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MemoryManager {
    private final int totalMemory = 2048 - 512; // Memory size after OS allocation
    private List<MemoryBlock> freeBlocks;
    private List<PCB> allocatedProcesses;
    private int time;

    public MemoryManager() {
        freeBlocks = new ArrayList<>();
        freeBlocks.add(new MemoryBlock(0, totalMemory));
        allocatedProcesses = new ArrayList<>();
        time = 0;
    }

    public boolean allocateProcess(PCB process) {
        for (MemoryBlock block : freeBlocks) {
            if (block.size >= process.size) {
                process.base = block.start;
                process.limit = block.start + process.size - 1;
                allocatedProcesses.add(process);
                block.start += process.size;
                block.size -= process.size;
                if (block.size == 0) {
                    freeBlocks.remove(block);
                }
                return true;
            }
        }
        return false;
    }

    public void deallocateProcess(int processId) {
        PCB process = allocatedProcesses.stream().filter(p -> p.id == processId).findFirst().orElse(null);
        if (process != null) {
            freeBlocks.add(new MemoryBlock(process.base, process.size));
            allocatedProcesses.remove(process);
            compactMemory();
        }
    }

    public void advanceTime() {
        time++;
        List<PCB> toDeallocate = allocatedProcesses.stream()
            .filter(p -> time >= p.arrivalTime + p.time)
            .collect(Collectors.toList());

        for (PCB process : toDeallocate) {
            deallocateProcess(process.id);
        }
    }

    private void compactMemory() {
        if (freeBlocks.size() > 3) {
            List<PCB> tempProcesses = new ArrayList<>(allocatedProcesses);
            allocatedProcesses.clear();
            freeBlocks.clear();
            freeBlocks.add(new MemoryBlock(0, totalMemory));
            Collections.sort(tempProcesses, (p1, p2) -> p1.base - p2.base);
            for (PCB process : tempProcesses) {
                allocateProcess(process);
            }
        }
    }

    public List<MemoryBlock> getFreeBlocks() {
        return freeBlocks;
    }

    public List<PCB> getAllocatedProcesses() {
        return allocatedProcesses;
    }

    public static class MemoryBlock {
        int start;
        int size;

        public MemoryBlock(int start, int size) {
            this.start = start;
            this.size = size;
        }
    }
}
