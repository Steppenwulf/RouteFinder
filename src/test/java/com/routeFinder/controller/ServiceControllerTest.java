package com.routeFinder.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceControllerTest {
	@Autowired
	private ServiceController controller;

	public ServiceController getController() {
		return controller;
	}

	@Test
	public void initializeTest() {
		ResponseEntity<?> answer = getController().initialize();;
		assert(answer.getStatusCode().equals(HttpStatus.CREATED) && getController().getGraph().isEmpty());
	}

	@Test
	public void defaultFileTest() {
		ResponseEntity<?> answer = getController().setDefaultGraph();
		assert(answer.getStatusCode().equals(HttpStatus.CREATED) && !getController().getGraph().isEmpty());
	}
	
	@Test
	public void anotherFileTest() {
		ResponseEntity<?> answer = getController().setGraphFromFile("city2.txt");
		assert(answer.getStatusCode().equals(HttpStatus.CREATED) && !getController().getGraph().isEmpty());
		answer = getController().findRoute("Vancouver", "Newark");
		assert("yes".equalsIgnoreCase((String) answer.getBody()));
	}
	
	@Test
	public void missingFileTest() {
		ResponseEntity<?> answer = getController().setGraphFromFile("e.e");
		assert(answer.getStatusCode().equals(HttpStatus.NOT_FOUND));
	}
	
	@Test
	public void successRouteTest1() {
		getController().setDefaultGraph();
		ResponseEntity<?> answer = getController().findRoute("Boston", "Newark");
		assert("yes".equalsIgnoreCase((String) answer.getBody()));
	}

	@Test
	public void successRouteTest2WithEmptyGraph() {
		getController().initialize();//lazy initialization should make this work
		ResponseEntity<?> answer = getController().findRoute("Boston", "Philadelphia");
		assert("yes".equalsIgnoreCase((String) answer.getBody()));
	}

	@Test
	public void failRouteTest() {
		getController().setDefaultGraph();
		ResponseEntity<?> answer = getController().findRoute("Philadelphia", "Albany");
		assert("no".equalsIgnoreCase((String) answer.getBody()));
	}
	
	@Test
	public void nullRouteTest() {
		getController().setDefaultGraph();
		ResponseEntity<?> answer = getController().findRoute(null, null);
		assert(answer.getStatusCode().equals(HttpStatus.NOT_FOUND));
	
	}
	
	@Test
	public void newRoadTest() {
		getController().setDefaultGraph();
		ResponseEntity<?> answer = getController().addRoad("Toronto", "Newark");
		assert(answer.getStatusCode().equals(HttpStatus.CREATED));
		ResponseEntity<?> routeAnswer = getController().findRoute("Boston", "Toronto");
		assert("yes".equalsIgnoreCase((String) routeAnswer.getBody()));
	}

	@Test
	public void nullAddRoadTest() {
		getController().setDefaultGraph();
		ResponseEntity<?> answer = getController().addRoad(null, null);
		assert(answer.getStatusCode().equals(HttpStatus.NOT_FOUND));
	
	}
}
