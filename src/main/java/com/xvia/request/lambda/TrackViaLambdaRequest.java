package com.xvia.request.lambda;

public class TrackViaLambdaRequest {
	// API host name access token is valid against
	String apiHost;
	// user who initiated lambda request's access token
	String accessToken;
	Long tableId;
	Long viewId;
	Long formId;
	Long recordId;
	
	public TrackViaLambdaRequest(){
		
	}

	public TrackViaLambdaRequest(String apiHost, String accessToken, Long tableId, Long viewId, Long formId, Long recordId){
		this.apiHost = apiHost;
		this.accessToken = accessToken;
		this.tableId = tableId;
		this.viewId = viewId;
		this.formId = formId;
		this.recordId = recordId;
	}
	
	public String getApiHost() {
		return apiHost;
	}

	public void setApiHost(String apiHost) {
		this.apiHost = apiHost;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public Long getViewId() {
		return viewId;
	}

	public void setViewId(Long viewId) {
		this.viewId = viewId;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
}

