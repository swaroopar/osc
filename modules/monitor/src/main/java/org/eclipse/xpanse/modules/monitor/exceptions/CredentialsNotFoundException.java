/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.modules.monitor.exceptions;

public class CredentialsNotFoundException extends RuntimeException {

    public CredentialsNotFoundException() {
        super("Credentials to connect to cloud provider not found");
    }

    public CredentialsNotFoundException(String message) {
        super("Credentials not found Exception:" + message);
    }

    public CredentialsNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }

}

