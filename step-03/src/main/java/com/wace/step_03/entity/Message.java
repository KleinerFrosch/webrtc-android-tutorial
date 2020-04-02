package com.wace.step_03.entity;

/**
 * author：lwf
 * date：2020/4/1
 * description：
 */
public class Message {

	private String devId;
	private String method;
	private String sdpOffer;
	private String sdpMid;
	private int sdpMLineIndex;
	private String candidate;

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getSdpOffer() {
		return sdpOffer;
	}

	public void setSdpOffer(String sdpOffer) {
		this.sdpOffer = sdpOffer;
	}

	public String getSdpMid() {
		return sdpMid;
	}

	public void setSdpMid(String sdpMid) {
		this.sdpMid = sdpMid;
	}

	public int getSdpMLineIndex() {
		return sdpMLineIndex;
	}

	public void setSdpMLineIndex(int sdpMLineIndex) {
		this.sdpMLineIndex = sdpMLineIndex;
	}

	public String getCandidate() {
		return candidate;
	}

	public void setCandidate(String candidate) {
		this.candidate = candidate;
	}
}
