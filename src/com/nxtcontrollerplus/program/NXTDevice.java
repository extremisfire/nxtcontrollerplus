package com.nxtcontrollerplus.program;

public class NXTDevice {
	private String name;
	private String address;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public NXTDevice(){
		this.name = null;
		this.address = null;
	}
	
	public NXTDevice(String name, String address){
		this.name = name;
		this.address = address;
	}

	public String toString(){
		return this.name+":"+this.address;
	}
}
