package com.handmadeoctopus.Engine;


public class TaskRun implements Runnable {

    Age age;

    // Created in Age class, runs calculations on separate thread
    public TaskRun(Age age) {
        this.age = age;
    }

    // Calculations are performed on another thread
    @Override
    public void run() {
        while(true) {
            age.calculateAll();
        }
    }
}
