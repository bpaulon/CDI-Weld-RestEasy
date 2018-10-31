package bcp.cdi.model;

import lombok.Value;

@Value
public class City {

	private Long id;
	private String name;
	private int population;

}