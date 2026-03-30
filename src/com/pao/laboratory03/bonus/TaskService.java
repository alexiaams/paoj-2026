package com.pao.laboratory03.bonus;

import java.util.*;
import java.util.stream.Collectors;

public class TaskService {
    private static TaskService instance;
    private Map<String, Task> tasksById;
    private Map<Priority, List<Task>> tasksByPriority;
    private List<String> auditLog;
    private int taskCounter;

    private TaskService() {
        tasksById = new LinkedHashMap<>();
        tasksByPriority = new LinkedHashMap<>();
        auditLog = new ArrayList<>();
        taskCounter = 0;

        for (Priority p : Priority.values()) {
            tasksByPriority.put(p, new ArrayList<>());
        }
    }

    public static synchronized TaskService getInstance() {
        if (instance == null) {
            instance = new TaskService();
        }
        return instance;
    }

    public Task addTask(String title, Priority priority) {
        taskCounter++;
        String taskId = String.format("T%03d", taskCounter);

        if (tasksById.containsKey(taskId)) {
            throw new DuplicateTaskException("Task-ul cu id-ul " + taskId + " exista deja");
        }

        Task task = new Task(taskId, title, priority);
        tasksById.put(taskId, task);
        tasksByPriority.get(priority).add(task);

        auditLog.add("[ADD] " + taskId + ": '" + title + "' (" + priority + ")");

        return task;
    }

    public void assignTask(String taskId, String assignee) {
        if (!tasksById.containsKey(taskId)) {
            throw new TaskNotFoundException("Task-ul '" + taskId + "' nu a fost gasit");
        }

        Task task = tasksById.get(taskId);
        task.setAssignee(assignee);
        auditLog.add("[ASSIGN] " + taskId + " -> " + assignee);
    }

    public void changeStatus(String taskId, Status newStatus) {
        if (!tasksById.containsKey(taskId)) {
            throw new TaskNotFoundException("Task-ul '" + taskId + "' nu a fost gasit");
        }

        Task task = tasksById.get(taskId);
        Status oldStatus = task.getStatus();

        if (!oldStatus.canTransitionTo(newStatus)) {
            throw new InvalidTransitionException(oldStatus, newStatus);
        }

        task.setStatus(newStatus);
        auditLog.add("[STATUS] " + taskId + ": " + oldStatus + " -> " + newStatus);
    }

    public List<Task> getTasksByPriority(Priority priority) {
        return new ArrayList<>(tasksByPriority.getOrDefault(priority, new ArrayList<>()));
    }

    public Map<Status, Long> getStatusSummary() {
        Map<Status, Long> summary = new LinkedHashMap<>();
        summary.put(Status.TODO, 0L);
        summary.put(Status.IN_PROGRESS, 0L);
        summary.put(Status.DONE, 0L);
        summary.put(Status.CANCELLED, 0L);

        for (Task task : tasksById.values()) {
            summary.put(task.getStatus(), summary.get(task.getStatus()) + 1);
        }

        return summary;
    }

    public List<Task> getUnassignedTasks() {
        return tasksById.values().stream()
                .filter(task -> task.getAssignee() == null)
                .collect(Collectors.toList());
    }

    public void printAuditLog() {
        for (String entry : auditLog) {
            System.out.println(entry);
        }
    }

    public double getTotalUrgencyScore(int baseDays) {
        double total = 0.0;
        for (Task task : tasksById.values()) {
            if (task.getStatus() != Status.DONE && task.getStatus() != Status.CANCELLED) {
                total += task.getPriority().calculateScore(baseDays);
            }
        }
        return total;
    }
}
