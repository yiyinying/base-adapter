package com.test.observer;

/**
 * 测试
 */
public class Test {
	public static void main(String[] arge) {
		//创建被观察者
		NewspaperObservable newspaperObservable = new NewspaperObservable();
		//添加2个观察者	
		newspaperObservable.addObserver(new ManObserver());
		newspaperObservable.addObserver(new ManObserver());
		//被观察者通知所有男订阅者
		newspaperObservable.notifyAllMan("have a new paper");
	}
}

