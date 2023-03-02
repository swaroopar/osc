/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

import { BaseAPIRequestFactory, RequiredError } from './baseapi';
import { Configuration } from '../configuration';
import { HttpMethod, RequestContext, ResponseContext } from '../http/http';
import { ObjectSerializer } from '../models/ObjectSerializer';
import { ApiException } from './exception';
import { isCodeInRange } from '../util';
import { SecurityAuthentication } from '../auth/auth';


import { Response } from '../models/Response';
import { ServiceStatus } from '../models/ServiceStatus';
import { SystemStatus } from '../models/SystemStatus';

/**
 * no description
 */
export class ServiceApiRequestFactory extends BaseAPIRequestFactory {

  /**
   */
  public async health(_options?: Configuration): Promise<RequestContext> {
    let _config = _options || this.configuration;

    // Path Params
    const localVarPath = '/xpanse/health';

    // Make Request Context
    const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
    requestContext.setHeaderParam('Accept', 'application/json, */*;q=0.8');


    const defaultAuth: SecurityAuthentication | undefined = _options?.authMethods?.default || this.configuration?.authMethods?.default;
    if (defaultAuth?.applySecurityAuthentication) {
      await defaultAuth?.applySecurityAuthentication(requestContext);
    }

    return requestContext;
  }

  /**
   */
  public async services(_options?: Configuration): Promise<RequestContext> {
    let _config = _options || this.configuration;

    // Path Params
    const localVarPath = '/xpanse/services';

    // Make Request Context
    const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
    requestContext.setHeaderParam('Accept', 'application/json, */*;q=0.8');


    const defaultAuth: SecurityAuthentication | undefined = _options?.authMethods?.default || this.configuration?.authMethods?.default;
    if (defaultAuth?.applySecurityAuthentication) {
      await defaultAuth?.applySecurityAuthentication(requestContext);
    }

    return requestContext;
  }

  /**
   * @param managedServiceName
   */
  public async start(managedServiceName: string, _options?: Configuration): Promise<RequestContext> {
    let _config = _options || this.configuration;

    // verify required parameter 'managedServiceName' is not null or undefined
    if (managedServiceName === null || managedServiceName === undefined) {
      throw new RequiredError('ServiceApi', 'start', 'managedServiceName');
    }


    // Path Params
    const localVarPath = '/xpanse/service/{managedServiceName}'
      .replace('{' + 'managedServiceName' + '}', encodeURIComponent(String(managedServiceName)));

    // Make Request Context
    const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.POST);
    requestContext.setHeaderParam('Accept', 'application/json, */*;q=0.8');


    const defaultAuth: SecurityAuthentication | undefined = _options?.authMethods?.default || this.configuration?.authMethods?.default;
    if (defaultAuth?.applySecurityAuthentication) {
      await defaultAuth?.applySecurityAuthentication(requestContext);
    }

    return requestContext;
  }

  /**
   * @param managedServiceName
   */
  public async state(managedServiceName: string, _options?: Configuration): Promise<RequestContext> {
    let _config = _options || this.configuration;

    // verify required parameter 'managedServiceName' is not null or undefined
    if (managedServiceName === null || managedServiceName === undefined) {
      throw new RequiredError('ServiceApi', 'state', 'managedServiceName');
    }


    // Path Params
    const localVarPath = '/xpanse/service/{managedServiceName}'
      .replace('{' + 'managedServiceName' + '}', encodeURIComponent(String(managedServiceName)));

    // Make Request Context
    const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.GET);
    requestContext.setHeaderParam('Accept', 'application/json, */*;q=0.8');


    const defaultAuth: SecurityAuthentication | undefined = _options?.authMethods?.default || this.configuration?.authMethods?.default;
    if (defaultAuth?.applySecurityAuthentication) {
      await defaultAuth?.applySecurityAuthentication(requestContext);
    }

    return requestContext;
  }

  /**
   * @param managedServiceName
   */
  public async stop(managedServiceName: string, _options?: Configuration): Promise<RequestContext> {
    let _config = _options || this.configuration;

    // verify required parameter 'managedServiceName' is not null or undefined
    if (managedServiceName === null || managedServiceName === undefined) {
      throw new RequiredError('ServiceApi', 'stop', 'managedServiceName');
    }


    // Path Params
    const localVarPath = '/xpanse/service/{managedServiceName}'
      .replace('{' + 'managedServiceName' + '}', encodeURIComponent(String(managedServiceName)));

    // Make Request Context
    const requestContext = _config.baseServer.makeRequestContext(localVarPath, HttpMethod.DELETE);
    requestContext.setHeaderParam('Accept', 'application/json, */*;q=0.8');


    const defaultAuth: SecurityAuthentication | undefined = _options?.authMethods?.default || this.configuration?.authMethods?.default;
    if (defaultAuth?.applySecurityAuthentication) {
      await defaultAuth?.applySecurityAuthentication(requestContext);
    }

    return requestContext;
  }

}

export class ServiceApiResponseProcessor {

  /**
   * Unwraps the actual response sent by the server from the response context and deserializes the response content
   * to the expected objects
   *
   * @params response Response returned by the server for a request to health
   * @throws ApiException if the response code was not in [200, 299]
   */
  public async health(response: ResponseContext): Promise<SystemStatus> {
    const contentType = ObjectSerializer.normalizeMediaType(response.headers['content-type']);
    if (isCodeInRange('404', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Not Found', body, response.headers);
    }
    if (isCodeInRange('400', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Bad Request', body, response.headers);
    }
    if (isCodeInRange('500', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Internal Server Error', body, response.headers);
    }
    if (isCodeInRange('200', response.httpStatusCode)) {
      const body: SystemStatus = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'SystemStatus', ''
      ) as SystemStatus;
      return body;
    }

    // Work around for missing responses in specification, e.g. for petstore.yaml
    if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
      const body: SystemStatus = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'SystemStatus', ''
      ) as SystemStatus;
      return body;
    }

    throw new ApiException<string | Blob | undefined>(response.httpStatusCode, 'Unknown API Status Code!', await response.getBodyAsAny(), response.headers);
  }

  /**
   * Unwraps the actual response sent by the server from the response context and deserializes the response content
   * to the expected objects
   *
   * @params response Response returned by the server for a request to services
   * @throws ApiException if the response code was not in [200, 299]
   */
  public async services(response: ResponseContext): Promise<Array<ServiceStatus>> {
    const contentType = ObjectSerializer.normalizeMediaType(response.headers['content-type']);
    if (isCodeInRange('404', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Not Found', body, response.headers);
    }
    if (isCodeInRange('400', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Bad Request', body, response.headers);
    }
    if (isCodeInRange('500', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Internal Server Error', body, response.headers);
    }
    if (isCodeInRange('200', response.httpStatusCode)) {
      const body: Array<ServiceStatus> = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Array<ServiceStatus>', ''
      ) as Array<ServiceStatus>;
      return body;
    }

    // Work around for missing responses in specification, e.g. for petstore.yaml
    if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
      const body: Array<ServiceStatus> = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Array<ServiceStatus>', ''
      ) as Array<ServiceStatus>;
      return body;
    }

    throw new ApiException<string | Blob | undefined>(response.httpStatusCode, 'Unknown API Status Code!', await response.getBodyAsAny(), response.headers);
  }

  /**
   * Unwraps the actual response sent by the server from the response context and deserializes the response content
   * to the expected objects
   *
   * @params response Response returned by the server for a request to start
   * @throws ApiException if the response code was not in [200, 299]
   */
  public async start(response: ResponseContext): Promise<Response> {
    const contentType = ObjectSerializer.normalizeMediaType(response.headers['content-type']);
    if (isCodeInRange('404', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Not Found', body, response.headers);
    }
    if (isCodeInRange('400', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Bad Request', body, response.headers);
    }
    if (isCodeInRange('500', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Internal Server Error', body, response.headers);
    }
    if (isCodeInRange('202', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      return body;
    }

    // Work around for missing responses in specification, e.g. for petstore.yaml
    if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      return body;
    }

    throw new ApiException<string | Blob | undefined>(response.httpStatusCode, 'Unknown API Status Code!', await response.getBodyAsAny(), response.headers);
  }

  /**
   * Unwraps the actual response sent by the server from the response context and deserializes the response content
   * to the expected objects
   *
   * @params response Response returned by the server for a request to state
   * @throws ApiException if the response code was not in [200, 299]
   */
  public async state(response: ResponseContext): Promise<ServiceStatus> {
    const contentType = ObjectSerializer.normalizeMediaType(response.headers['content-type']);
    if (isCodeInRange('404', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Not Found', body, response.headers);
    }
    if (isCodeInRange('400', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Bad Request', body, response.headers);
    }
    if (isCodeInRange('500', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Internal Server Error', body, response.headers);
    }
    if (isCodeInRange('200', response.httpStatusCode)) {
      const body: ServiceStatus = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'ServiceStatus', ''
      ) as ServiceStatus;
      return body;
    }

    // Work around for missing responses in specification, e.g. for petstore.yaml
    if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
      const body: ServiceStatus = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'ServiceStatus', ''
      ) as ServiceStatus;
      return body;
    }

    throw new ApiException<string | Blob | undefined>(response.httpStatusCode, 'Unknown API Status Code!', await response.getBodyAsAny(), response.headers);
  }

  /**
   * Unwraps the actual response sent by the server from the response context and deserializes the response content
   * to the expected objects
   *
   * @params response Response returned by the server for a request to stop
   * @throws ApiException if the response code was not in [200, 299]
   */
  public async stop(response: ResponseContext): Promise<Response> {
    const contentType = ObjectSerializer.normalizeMediaType(response.headers['content-type']);
    if (isCodeInRange('404', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Not Found', body, response.headers);
    }
    if (isCodeInRange('400', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Bad Request', body, response.headers);
    }
    if (isCodeInRange('500', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      throw new ApiException<Response>(response.httpStatusCode, 'Internal Server Error', body, response.headers);
    }
    if (isCodeInRange('202', response.httpStatusCode)) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      return body;
    }

    // Work around for missing responses in specification, e.g. for petstore.yaml
    if (response.httpStatusCode >= 200 && response.httpStatusCode <= 299) {
      const body: Response = ObjectSerializer.deserialize(
        ObjectSerializer.parse(await response.body.text(), contentType),
        'Response', ''
      ) as Response;
      return body;
    }

    throw new ApiException<string | Blob | undefined>(response.httpStatusCode, 'Unknown API Status Code!', await response.getBodyAsAny(), response.headers);
  }

}
