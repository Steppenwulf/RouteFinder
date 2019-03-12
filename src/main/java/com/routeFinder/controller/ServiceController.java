package com.routeFinder.controller;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.routeFinder.model.FileParser;
import com.routeFinder.model.MapService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class ServiceController {
	private static Logger LOG = LoggerFactory.getLogger(ServiceController.class);
	private static final String MAP_RESOURCE_NAME = "/map";
	private static final String CONNECTED_RESOURCE_NAME = "/connected";
	
	@Autowired
	private MapService mapService;
	
	@Autowired
	private FileParser fileParser;
	
	public FileParser getFileParser() {
		return fileParser;
	}

	public MapService getMapService() {
		return mapService;
	}

	/**
	 * Initializes to an empty map service - i.e. removes any existing data
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value="Initialize and remove any existing data")
	@RequestMapping(value=MAP_RESOURCE_NAME + "/initialize", method=RequestMethod.PUT)
	public ResponseEntity<?> initialize() {
		try {
			LOG.info("Initializing ServiceController");
			getMapService().initialize();
			return new ResponseEntity(getDefaultHeaders(), HttpStatus.CREATED);
		} catch(Exception e) {
			return new ResponseEntity(e.getMessage(), getDefaultHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value="Set the default map")
	@RequestMapping(value=MAP_RESOURCE_NAME + "/default", method=RequestMethod.PUT)
	public ResponseEntity<?> setDefaultMap() {
		try {
			LOG.info("Initializing Default Map");
			setMapFromFile(FileParser.DEFAULT_MAP_FILE);
			return new ResponseEntity(getDefaultHeaders(), HttpStatus.CREATED);
		} catch(Exception e) {
			return new ResponseEntity(e.getMessage(), getDefaultHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value="Initialize map from a file on the classpath")
	@RequestMapping(value=MAP_RESOURCE_NAME + "/file", method=RequestMethod.PUT)
	public ResponseEntity<?> setMapFromFile(
		@ApiParam(
				name = "fileName",
				value = "name of a file on the classpath",
				defaultValue = "city.txt")
		@RequestParam String fileName) {
		try {
			initialize();
			getFileParser().parseFile(fileName);
			LOG.info("Set map from file: " + fileName);
			LOG.info("Map: " + getMapService().getLocationsAsString());
			return new ResponseEntity(getDefaultHeaders(), HttpStatus.CREATED);
		} catch(FileNotFoundException e) {//expected error if the file doesn't exist when trying to load a file
			return new ResponseEntity(e.getMessage(), getDefaultHeaders(), HttpStatus.NOT_FOUND);
		} catch(Exception e) {
			return new ResponseEntity(e.getMessage(), getDefaultHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ApiOperation(value="Check if 2 cities are connected", response=String.class)
	@RequestMapping(value=CONNECTED_RESOURCE_NAME, method=RequestMethod.GET)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<?> findRoute(
			@ApiParam(
					name = "origin",
					value = "origin city",
					required = true)
			@RequestParam String origin,
			@ApiParam(
					name = "destination",
					value = "destination city",
					required = true)
			@RequestParam String destination) {
		try {
			if(origin == null || origin.isEmpty() || destination == null || destination.isEmpty()) {
				return new ResponseEntity("Origin and Destination are required", getDefaultHeaders(), HttpStatus.NOT_FOUND);
			}
			boolean found = getMapService().findRoute(origin, destination);
			String answer;
			if(found) {
				answer = "yes";
				LOG.info("Found route between " + origin + " and " + destination);
			} else {
				answer = "no";
				LOG.info("Could not find route between " + origin + " and " + destination);
			}
			return new ResponseEntity(answer, getDefaultHeaders(), HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity(e.getMessage(), getDefaultHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ApiOperation(value="Add a road to the current map, connecting two cities. The cities will be added if they are not yet on the map", response=String.class)
	@RequestMapping(value=CONNECTED_RESOURCE_NAME, method=RequestMethod.POST)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResponseEntity<?> addRoad(
			@ApiParam(
					name = "origin",
					value = "origin city",
					required = true)
			@RequestParam String origin,
			@ApiParam(
					name = "destination",
					value = "destination city",
					required = true)
			@RequestParam String destination) {
		try {
			if(origin == null || origin.isEmpty() || destination == null || destination.isEmpty()) {
				return new ResponseEntity("Origin and Destination are required", getDefaultHeaders(), HttpStatus.NOT_FOUND);
			}
			getMapService().addAdjacents(origin, destination);
			return new ResponseEntity(getDefaultHeaders(), HttpStatus.CREATED);
		} catch(Exception e) {
			return new ResponseEntity(e.getMessage(), getDefaultHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	protected HttpHeaders getDefaultHeaders() {
		HttpHeaders answer = new HttpHeaders();
		answer.set("Accept", "application/json");
		answer.set("Accept-Language", "en-US");
		return answer;
	}
}
