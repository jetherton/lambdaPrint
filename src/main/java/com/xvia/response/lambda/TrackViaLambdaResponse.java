package com.xvia.response.lambda;

public class TrackViaLambdaResponse {
	Boolean status;
	String data;

	public TrackViaLambdaResponse(Boolean status, String data) {
		this.status = status;
		this.data = data;
	}
	
	public TrackViaLambdaResponse() {
		
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
