package com.meral.restaws;


import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.meral.restaws.health.TemplateHealthCheck;
import com.meral.restaws.resources.HelloWorldResource;

public class HelloWorldApplication extends Application<RestAWSConfiguration> {
	public static void main(String[] args) throws Exception {
		//AWS stuff:
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider().getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (~/.aws/credentials), and is in valid format.",
							e);
		}
		System.out.println("credentials: " + credentials + ", key: " + credentials.getAWSSecretKey());

		new HelloWorldApplication().run(args);
	}

	@Override
	public String getName() {
		return "hello-world";
	}

	@Override
	public void initialize(Bootstrap<RestAWSConfiguration> bootstrap) {
		// nothing to do yet
	}


	@Override
	public void run(RestAWSConfiguration configuration,
			Environment environment) {
		final HelloWorldResource resource = new HelloWorldResource(
				configuration.getTemplate(),
				configuration.getDefaultName()
				);
		final TemplateHealthCheck healthCheck =
				new TemplateHealthCheck(configuration.getTemplate());
		environment.healthChecks().register("template", healthCheck);
		environment.jersey().register(resource);
	}
}
