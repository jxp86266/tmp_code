package com.bdx.Flowable;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.common.engine.impl.cfg.standalone.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "dbx flowable test!" );
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
        	      .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
        	      .setJdbcUsername("sa")
        	      .setJdbcPassword("")
        	      .setJdbcDriver("org.h2.Driver")
        	      .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngine processEngine = cfg.buildProcessEngine();
        
        //使用了RepositoryService，可以从ProcessEngine对象中检索。
        //使用RepositoryService，通过传 递XML 文件的位置并调用deploy （） 方法来实际执行它，创建一个新的 部署 
        
        RepositoryService  repositoryService=processEngine.getRepositoryService();
        Deployment deployment=repositoryService.createDeployment()
        		.addClasspathResource("holiday-request.bpmn20.xml")
        		.deploy();
        
        //通过RepositoryService创建一个 新的ProcessDefinitionQuery对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
        		  .deploymentId(deployment.getId())
        		  .singleResult();
        		System.out.println("Found process definition : " + processDefinition.getName());
        	
        //	启动流程实例，我们需要提供一些初始 流程变量 。通常情况下，当某个进程被自动触发时
        //您将通过呈现给用户的表单或通过REST API获取这些表单	
        		Scanner scanner= new Scanner(System.in);

        		System.out.println("Who are you?");
        		String employee = scanner.nextLine();

        		System.out.println("How many holidays do you want to request?");
        		Integer nrOfHolidays = Integer.valueOf(scanner.nextLine());

        		System.out.println("Why do you need them?");
        		String description = scanner.nextLine();
        		
        		
        //
        		RuntimeService runtimeService = processEngine.getRuntimeService();

        		Map<String, Object> variables = new HashMap<String, Object>();
        		variables.put("employee", employee);
        		variables.put("nrOfHolidays", nrOfHolidays);
        		variables.put("description", description);
        		ProcessInstance processInstance =
        		  runtimeService.startProcessInstanceByKey("holidayRequest", variables);
        		
        		
        		TaskService taskService = processEngine.getTaskService();
        		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
        		System.out.println("You have " + tasks.size() + " tasks:");
        		for (int i=0; i<tasks.size(); i++) {
        		  System.out.println((i+1) + ") " + tasks.get(i).getName());
        		}
        		
        		
        		System.out.println("Which task would you like to complete?");
        		int taskIndex = Integer.valueOf(scanner.nextLine());
        		Task task = tasks.get(taskIndex - 1);
        		Map<String, Object> processVariables = taskService.getVariables(task.getId());
        		System.out.println(processVariables.get("employee") + " wants " +
        		    processVariables.get("nrOfHolidays") + " of holidays. Do you approve this?");
        		
        		
        //Working with historical data  org.flowable.CallExternalSystemDelegate"
        		HistoryService historyService = processEngine.getHistoryService();
        		List<HistoricActivityInstance> activities =
        		  historyService.createHistoricActivityInstanceQuery()
        		   .processInstanceId(processInstance.getId())
        		   .finished()
        		   .orderByHistoricActivityInstanceEndTime().asc()
        		   .list();

        		for (HistoricActivityInstance activity : activities) {
        		  System.out.println(activity.getActivityId() + " took "
        		    + activity.getDurationInMillis() + " milliseconds");
        		}

    }
}
