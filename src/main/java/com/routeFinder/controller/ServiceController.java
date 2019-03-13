package com.routeFinder.controller;

import java.io.IOException;

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

import com.routeFinder.io.FileParser;
import com.routeFinder.model.Graph;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class ServiceController {
	private static Logger LOG = LoggerFactory.getLogger(ServiceController.class);
	private static final String GRAPH_RESOURCE_NAME = "/graph";
	private static final String CONNECTED_RESOURCE_NAME = "/connected";
	
	@Autowired
	private Graph graph;
	
	@Autowired
	private FileParser fileParser;
	
	public FileParser getFileParser() {
		return fileParser;
	}

	public Graph getGraph() {
		return graph;
	}

	/**
	 * Initializes to an empty graph - i.e. removes any existing data
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value="Initialize graph and remove any existing data. Useful if you want to manually set data via 'POST connected' command")
	@ApiResponses({
		@ApiResponse(code=201, message="Created")
	})
	@RequestMapping(value=GRAPH_RESOURCE_NAME + "/initialize", method=RequestMethod.PUT)
	public ResponseEntity<?> initialize() {
		try {
			LOG.info("Emptying graph and initializing");
			getGraph().initialize();
			return new ResponseEntity(getDefaultHeaders(), HttpStatus.CREATED);
		} catch(Exception e) {
			LOG.error("Failed to initialize graph ", e);
			return new ResponseEntity(e.getMessage(), getDefaultHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ApiOperation(value="Sets the default graph. A graph contains the known locations (nodes) and roads between them (edges)")
	@RequestMapping(value=GRAPH_RESOURCE_NAME + "/default", method=RequestMethod.PUT)
	public ResponseEntity<?> setDefaultGraph() {
		try {
			LOG.info("Initializing Default Graph");
			setGraphFromFile(FileParser.DEFAULT_GRAPH_FILE);
			return new ResponseEntity(getDefaultHeaders(), HttpStatus.CREATED);
		} catch(Exception e) {
			LOG.error("Failed to set default Graph", e);
			return new ResponseEntity(e.getMessage(), getDefaultHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ApiOperation(value="Initialize the graph from a file on the classpath")
	@ApiResponses({
		@ApiResponse(code=201, message="Created"),
		@ApiResponse(code=404, message="Not Found")
	})
	@RequestMapping(value=GRAPH_RESOURCE_NAME + "/file", method=RequestMethod.PUT)
	public ResponseEntity<?> setGraphFromFile(
		@ApiParam(
				name = "fileName",
				value = "name of a file on the classpath",
				defaultValue = "city.txt")
		@RequestParam String fileName) {
		try {
			initialize();
			getFileParser().parseFile(fileName);
			LOG.info("Set graph from file: " + fileName);
			LOG.info("Graph: " + getGraph().getLocationsAsString());
			return new ResponseEntity(getDefaultHeaders(), HttpStatus.CREATED);
		} catch(IOException e) {//expected error if the file doesn't exist when trying to load a file
			LOG.info("Could not find file: " + fileName, e);
			return new ResponseEntity(e.getMessage(), getDefaultHeaders(), HttpStatus.NOT_FOUND);
		} catch(Exception e) {
			LOG.error("Failed to set Graph from File: " + fileName, e);
			return new ResponseEntity(e.getMessage(), getDefaultHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ApiOperation(value="Check if 2 cities are connected", response=String.class)
	@ApiResponses({
		@ApiResponse(code=200, message="OK", response = String.class),
		@ApiResponse(code=404, message="Not Found")
	})
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
			if(getGraph().isEmpty()) {//Convenience method to ensure a default graph is in place before you check if locations are connected.
				LOG.info("Graph is empty, so setting default graph");
				setDefaultGraph();
			}
			boolean found = getGraph().findRoute(origin, destination);
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
			LOG.error("Failed when trying to find if locations are connected", e);
			return new ResponseEntity(e.getMessage(), getDefaultHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ApiOperation(value="Add a road (edge) to the current graph, connecting two cities. The cities will be added if they are not yet on the graph", response=String.class)
	@ApiResponses({
		@ApiResponse(code=201, message="Created"),
		@ApiResponse(code=404, message="Not Found")
	})
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
			getGraph().addAdjacents(origin, destination);
			return new ResponseEntity(getDefaultHeaders(), HttpStatus.CREATED);
		} catch(Exception e) {
			LOG.error("Failed to add road between two locations", e);
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
