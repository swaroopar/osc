/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

import { Button, Space } from 'antd';
import { useState } from 'react';
import SystemStatusIcon from './SystemStatusIcon';
import { SystemStatus, SystemStatusHealthStatusEnum } from '../../../xpanse-api/generated';
import { serviceApi } from '../../../xpanse-api/xpanseRestApiClient';

function SystemStatusBar(): JSX.Element {
  const [healthState, setHealthState] = useState<SystemStatusHealthStatusEnum>('NOK');
  const [isReloadSystemStatus, setReloadSystemStatus] = useState<boolean>(false);

  serviceApi
    .health()
    .then((systemStatus: SystemStatus) => setHealthState(systemStatus.healthStatus))
    .catch((error: any) => {
      console.error(error);
      setHealthState('NOK');
    });

  return (
    <Space>
      <Button
        className='.header-menu-button'
        icon={<SystemStatusIcon isSystemUp={healthState === 'OK'} />}
        onClick={() => setReloadSystemStatus(!isReloadSystemStatus)}
      >
        System Status
      </Button>
    </Space>
  );
}

export default SystemStatusBar;
