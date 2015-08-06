package com.meral.restaws.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.UserRecordResult;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.meral.restaws.NameDateIpUaId;
import com.meral.restaws.core.Saying;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
	private final String template;
	private final String defaultName;
	private final AtomicLong counter;

	// In the AWS tutorial it says that for parallel processing it is better to
	// assign multiple partition keys per shard. At the moment there is only one
	// shard, so it doesn't matter, but should be considered if the system upscales.
	private final int numberOfAvailableShards = 1;
	private final int partitionKeysPerShard = 3;
	private final int partitonKeysToUse = 
			numberOfAvailableShards * partitionKeysPerShard;

	private final ObjectMapper JSON = new ObjectMapper();
	
//	private KinesisProducer kp  = new KinesisProducer();

	private int requestCounter = 0;

	public HelloWorldResource(String template, String defaultName) {
		this.template = template;
		this.defaultName = defaultName;
		this.counter = new AtomicLong();
	}

	@GET
	@Timed
	public Saying sayHello(@QueryParam("name") Optional<String> name, 
			@HeaderParam("User-Agent") String userAgent,
			@Context HttpServletRequest request) throws JsonProcessingException {
		KinesisProducer kp  = new KinesisProducer();
		//3- it receives the request status (failure or success) from kinesis
		FutureCallback<UserRecordResult> myCallback = 
				new FutureCallback<UserRecordResult>() {     
			@Override public void onFailure(Throwable t) {
				System.out.println("onFailure: " + t + ", cause: " + t.getCause() + ", message: " + t.getMessage() + ", trace" + t.getStackTrace());
				for (StackTraceElement ste : t.getStackTrace()) {
					System.out.println(ste);
				}
			};     
			@Override 
			public void onSuccess(UserRecordResult result) { 
				System.out.println("onSucess: " + result + ", seq: " + result.getSequenceNumber() +
						", shardid:" + result.getShardId());
			};
		};
		// Send document to Kinesis:
		byte[] bytes  = makeMyJSON(name, request.getRemoteAddr(), userAgent);
		//sends one datum to the kinesis stream my_stream
		//String.valueOf(partitonKeysToUse)=1***
		//1-sends the datum
		ListenableFuture<UserRecordResult> f = kp.addUserRecord("my_kinesis_stream", String.valueOf(partitonKeysToUse % requestCounter++), 
				ByteBuffer.wrap(bytes));
		
		//2-tells the computer what to do when it receives an answer from kinesis after receiving the datum
		//whilst its waiting for the request status it can still compute other processes,i.e.listen another datum
		Futures.addCallback(f, myCallback);

		final String value = String.format(template, name.or(defaultName));
		return new Saying(counter.incrementAndGet(), value);
	}

	private byte[] makeMyJSON (Optional<String> name, String address, String userAgent) {
		NameDateIpUaId ndiui = makeNDIUI(name, address, userAgent);
		return makeMyJSONFromNDIUI(ndiui);
	}

	private byte[] makeMyJSONFromNDIUI(NameDateIpUaId ndiui) {
		byte[] bytes = null;
		try {
			bytes = JSON.writeValueAsBytes(ndiui);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	private NameDateIpUaId makeNDIUI(Optional<String> name, String address, String userAgent) {
		String nameToLog;
		if (name.isPresent()) {
			nameToLog = name.get();
		} else {
			nameToLog = "Stranger";
		}
		return new NameDateIpUaId(nameToLog, new Date(), address, userAgent);
	}
}