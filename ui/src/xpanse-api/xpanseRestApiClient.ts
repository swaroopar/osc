/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

import { ConfigurationParameters, createConfiguration } from './generated/configuration';
import { ServerConfiguration, ServiceApi, ServiceVendorApi } from './generated';

const customConfiguration: ConfigurationParameters = {};
customConfiguration.baseServer = new ServerConfiguration<{}>(process.env.REACT_APP_XPANSE_API_URL as string, {});

const configuration = createConfiguration(customConfiguration);

export const serviceVendorApi = new ServiceVendorApi(configuration);
export const serviceApi = new ServiceApi(configuration);
