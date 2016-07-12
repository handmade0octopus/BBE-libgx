package com.handmadeoctopus.Engine;


public class TaskRun implements Runnable {

    Age age;

    public TaskRun(Age age) {
        this.age = age;
    }

    @Override
    public void run() {
        while(true) {
            age.calculateAll();
        }
    }
}
