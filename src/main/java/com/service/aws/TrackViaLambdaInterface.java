package com.service.aws;

import com.xvia.request.lambda.TrackViaLambdaRequest;
import com.xvia.response.lambda.TrackViaLambdaResponse;

public interface TrackViaLambdaInterface {
	public TrackViaLambdaResponse handleRequest(TrackViaLambdaRequest request);
}
