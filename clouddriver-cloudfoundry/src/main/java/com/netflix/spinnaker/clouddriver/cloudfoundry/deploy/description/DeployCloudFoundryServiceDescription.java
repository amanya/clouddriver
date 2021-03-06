/*
 * Copyright 2018 Pivotal, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.clouddriver.cloudfoundry.deploy.description;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeployCloudFoundryServiceDescription extends AbstractCloudFoundryServiceDescription {
  private boolean userProvided = false;

  private List<Map<Object, Object>> manifest;

  @JsonIgnore private ServiceAttributes serviceAttributes;

  @JsonIgnore private UserProvidedServiceAttributes userProvidedServiceAttributes;

  @Data
  public static class ServiceAttributes {
    String service;
    String serviceInstanceName;
    String servicePlan;
    boolean updatable = true;
    boolean versioned = false;

    @Nullable Set<String> tags;

    @Nullable Map<String, Object> parameterMap;

    @JsonIgnore String previousInstanceName;
  }

  @Data
  public static class UserProvidedServiceAttributes {
    String serviceInstanceName;
    boolean updatable = true;
    boolean versioned = false;

    @Nullable Set<String> tags;

    @Nullable String syslogDrainUrl;

    @Nullable Map<String, Object> credentials;

    @Nullable String routeServiceUrl;

    @JsonIgnore String previousInstanceName;
  }
}
