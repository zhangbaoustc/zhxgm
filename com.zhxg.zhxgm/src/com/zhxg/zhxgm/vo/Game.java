package com.zhxg.zhxgm.vo;

import java.io.Serializable;

public class Game implements Serializable {

	private static final long serialVersionUID = 1883966379335583018L;

	public Game(){
		
	}

	private String id;
	private String targetID;
	private String name;
	private String type;
	private String distance;
	private String bonus;
	private String date;
	private String jgDate;
	private String jgAddress;
	private String jgLongitude;
	private String jgLatitude;
	private String flyDate;
	private String flyAddress;
	private String flyLongitude;
	private String flyLatitude;
	private String referee;
	private String total;
	private String info;
	private String status;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTargetID() {
		return targetID;
	}

	public void setTargetID(String targetID) {
		this.targetID = targetID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getBonus() {
		return bonus;
	}

	public void setBonus(String bonus) {
		this.bonus = bonus;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getJgDate() {
		return jgDate;
	}

	public void setJgDate(String jgDate) {
		this.jgDate = jgDate;
	}

	public String getFlyAddress() {
		return flyAddress;
	}

	public void setFlyAddress(String flyAddress) {
		this.flyAddress = flyAddress;
	}

	public String getJgLongitude() {
		return jgLongitude;
	}

	public void setJgLongitude(String jgLongitude) {
		this.jgLongitude = jgLongitude;
	}

	public String getJgLatitude() {
		return jgLatitude;
	}

	public String getJgAddress() {
		return jgAddress;
	}

	public void setJgAddress(String jgAddress) {
		this.jgAddress = jgAddress;
	}

	public void setJgLatitude(String jgLatitude) {
		this.jgLatitude = jgLatitude;
	}

	public String getFlyDate() {
		return flyDate;
	}

	public void setFlyDate(String flyDate) {
		this.flyDate = flyDate;
	}

	public String getFlyLongitude() {
		return flyLongitude;
	}

	public void setFlyLongitude(String flyLongitude) {
		this.flyLongitude = flyLongitude;
	}

	public String getFlyLatitude() {
		return flyLatitude;
	}

	public void setFlyLatitude(String flyLatitude) {
		this.flyLatitude = flyLatitude;
	}
	
	public String toString(){
		return getName();
	}

	public String getReferee() {
		return referee;
	}

	public void setReferee(String referee) {
		this.referee = referee;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
