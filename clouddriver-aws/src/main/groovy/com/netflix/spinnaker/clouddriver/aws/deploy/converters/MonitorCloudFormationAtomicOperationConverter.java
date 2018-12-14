/*
 * Copyright (c) 2018 Schibsted Media Group. All rights reserved
 */

package com.netflix.spinnaker.clouddriver.aws.deploy.converters;

import com.netflix.spinnaker.clouddriver.aws.AmazonOperation;
import com.netflix.spinnaker.clouddriver.aws.deploy.description.DeployCloudFormationDescription;
import com.netflix.spinnaker.clouddriver.aws.deploy.ops.DeployCloudFormationAtomicOperation;
import com.netflix.spinnaker.clouddriver.aws.deploy.ops.MonitorCloudFormationAtomicOperation;
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperation;
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperations;
import com.netflix.spinnaker.clouddriver.security.AbstractAtomicOperationsCredentialsSupport;
import org.springframework.stereotype.Component;

import java.util.Map;

@AmazonOperation(AtomicOperations.DEPLOY_CLOUDFORMATION)
@Component("monitorCloudFormationDescription")
public class MonitorCloudFormationAtomicOperationConverter extends AbstractAtomicOperationsCredentialsSupport {
  @Override
  public AtomicOperation convertOperation(Map input) {
    return new MonitorCloudFormationAtomicOperation(convertDescription(input));
  }

  @Override
  public MonitorCloudFormationDescription convertDescription(Map input) {
    MonitorCloudFormationAtomicOperation converted = getObjectMapper()
        .convertValue(input, MonitorCloudFormationAtomicOperation.class);
    converted.setCredentials(getCredentialsObject((String) input.get("credentials")));
    return converted;
  }
}
