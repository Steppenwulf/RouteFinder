package com.routeFinder.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
		getController().initialize();;
		assert(getController().getMapService().getLocations().isEmpty());
	}

	@Test
	public void defaultFileTest() {
		getController().setDefaultMap();
		assert(!getController().getMapService().getLocations().isEmpty());
	}
	
	@Test
	public void successRouteTest1() {
		getController().setDefaultMap();
		ResponseEntity<?> answer = getController().findRoute("Boston", "Newark");
		assert("yes".equalsIgnoreCase((String) answer.getBody()));
	}

	@Test
	public void successRouteTest2() {
		getController().setDefaultMap();
		ResponseEntity<?> answer = getController().findRoute("Boston", "Philadelphia");
		assert("yes".equalsIgnoreCase((String) answer.getBody()));
	}

	@Test
	public void failRouteTest() {
		getController().setDefaultMap();
		ResponseEntity<?> answer = getController().findRoute("Philadelphia", "Albany");
		assert("no".equalsIgnoreCase((String) answer.getBody()));
	}

}
