/*
 * Copyright (c) 2019 Schibsted Media Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.clouddriver.aws.provider.agent

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStacksResult
import com.amazonaws.services.cloudformation.model.ListChangeSetsResult
import com.amazonaws.services.cloudformation.model.Stack
import com.amazonaws.services.ec2.AmazonEC2
import com.netflix.spectator.api.Registry
import com.netflix.spinnaker.cats.provider.ProviderCache
import com.netflix.spinnaker.clouddriver.aws.AmazonCloudProvider
import com.netflix.spinnaker.clouddriver.aws.cache.Keys
import com.netflix.spinnaker.clouddriver.aws.security.AmazonClientProvider
import com.netflix.spinnaker.clouddriver.aws.security.NetflixAmazonCredentials
import com.netflix.spinnaker.clouddriver.cache.OnDemandType
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class AmazonCloudFormationAgentSpec extends Specification {
  static String region = 'region'
  static String accountName = 'accountName'

  @Subject
  AmazonCloudFormationAgent agent

  @Shared
  ProviderCache providerCache = Mock(ProviderCache)

  @Shared
  AmazonEC2 ec2

  @Shared
  AmazonClientProvider acp

  @Shared
  Registry registry

  def setup() {
    ec2 = Mock(AmazonEC2)
    def creds = Stub(NetflixAmazonCredentials) {
      getName() >> accountName
    }
    acp = Mock(AmazonClientProvider)
    registry = Mock(Registry)
    agent = new AmazonCloudFormationAgent(acp, creds, region, registry)
  }

  @Unroll
  void "OnDemand request should be handled for type '#onDemandType' and provider '#provider': '#expected'"() {
    when:
    def result = agent.handles(onDemandType, provider)

    then:
    result == expected

    where:
    onDemandType                | provider               || expected
    OnDemandType.CloudFormation | AmazonCloudProvider.ID || true
    OnDemandType.CloudFormation | "other"                || false
    OnDemandType.Job            | AmazonCloudProvider.ID || false
  }

  @Unroll
  void "OnDemand request should be handled for the specific account and region"() {
    when:
    def result = agent.shouldHandle(data)

    then:
    result == expected

    where:
    data                                          | expected
    [:]                                           | true // backwards compatiblity
    [credentials: accountName, region: [region]]  | true
    [credentials: null, region: null]             | false
    [credentials: accountName, region: null]      | false
    [credentials: null, region: [region]]         | false
    [credentials: "other", region: [region]]      | false
    [credentials: accountName, region: ["other"]] | false
    [credentials: "other", region: ["other"]]     | false
  }

  void "should insert onDemand requests into onDemand NS"() {
    given:
    def postData = [ credentials: "accountName", stackName: "stackName", region: ["region"]]
    def stack1 = new Stack().withStackId("stack1").withStackStatus("CREATE_SUCCESS")
    def stack2 = new Stack().withStackId("stack1").withStackStatus("CREATE_SUCCESS")
    def amazonCloudFormation = Mock(AmazonCloudFormation)
    def stackResults = Mock(DescribeStacksResult)
    def providerCache = Mock(ProviderCache)
    def stackChangeSetsResults = Mock(ListChangeSetsResult)

    when:
    agent.handle(providerCache, postData)

    then:
    1 * acp.getAmazonCloudFormation(_, _) >> amazonCloudFormation
    1 * amazonCloudFormation.describeStacks(_) >> stackResults
    1 * stackResults.stacks >> [ stack1, stack2 ]
    2 * amazonCloudFormation.listChangeSets(_) >> stackChangeSetsResults
    2 * stackChangeSetsResults.getSummaries() >> new ArrayList()
    2 * providerCache.putCacheData(Keys.Namespace.ON_DEMAND.ns, _)
    }

}