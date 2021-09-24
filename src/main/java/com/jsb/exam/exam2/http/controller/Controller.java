package com.jsb.exam.exam2.http.controller;

import com.jsb.exam.exam2.container.ContainerComponent;
import com.jsb.exam.exam2.http.Rq;

public abstract class Controller implements ContainerComponent {
	public abstract void performAction(Rq rq);
}
