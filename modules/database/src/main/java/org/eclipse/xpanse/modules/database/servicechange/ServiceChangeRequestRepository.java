/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.modules.database.servicechange;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.UUID;
import org.eclipse.xpanse.modules.database.CustomJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.lang.Nullable;

/** Interface to access default JPA methods. */
public interface ServiceChangeRequestRepository
        extends CustomJpaRepository<ServiceChangeRequestEntity, UUID>,
                JpaSpecificationExecutor<ServiceChangeRequestEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // it is not possible to read this config from spring properties.
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "2000")})
    @Override
    List<ServiceChangeRequestEntity> findAll(
            @Nullable Specification<ServiceChangeRequestEntity> spec);
}
