/*
 * Copyright 2020 Armory, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.netflix.spinnaker.clouddriver.deploy;

import com.netflix.spinnaker.clouddriver.orchestration.VersionedCloudProviderOperation;
import com.netflix.spinnaker.kork.annotations.Beta;
import java.util.List;

@Beta
public abstract class DescriptionValidator<T> implements VersionedCloudProviderOperation {
  private static final String VALIDATOR_SUFFIX = "Validator";

  public static String getValidatorName(String description) {
    return description + VALIDATOR_SUFFIX;
  }

  public static String getOperationName(String validator) {
    if (validator == null || validator.length() == 0) {
      return validator;
    }
    if (validator.endsWith(VALIDATOR_SUFFIX)) {
      return validator.substring(0, validator.length() - VALIDATOR_SUFFIX.length());
    }
    return validator;
  }

  public abstract void validate(List<T> priorDescriptions, T description, ValidationErrors errors);
}
