package com.bdx.Flowable;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class CallExternalSystemDelegate implements JavaDelegate{


	public void execute(DelegateExecution execution) {
		// TODO Auto-generated method stub
		System.out.println("delegate out put");
	}
}