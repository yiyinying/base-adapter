package com.test.observer;

/**
 * 男订阅者：观察者
 */
public class ManObserver implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        System.out.println(arg);
    }
}

