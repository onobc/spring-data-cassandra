/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.cassandra.repository.support;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.repository.query.CassandraEntityInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFragmentsContributor;
import org.springframework.util.Assert;

/**
 * Cassandra-specific {@link RepositoryFragmentsContributor} contributing fragments based on the repository.
 * <p>
 * Implementations must define a no-args constructor.
 *
 * @author Chris Bono
 * @since 5.0
 */
public interface CassandraRepositoryFragmentsContributor extends RepositoryFragmentsContributor {

	CassandraRepositoryFragmentsContributor DEFAULT = DefaultCassandraRepositoryFragmentsContributor.INSTANCE;

	/**
	 * Returns a composed {@code CassandraRepositoryFragmentsContributor} that first applies this contributor to its inputs,
	 * and then applies the {@code after} contributor concatenating effectively both results. If evaluation of either
	 * contributors throws an exception, it is relayed to the caller of the composed contributor.
	 *
	 * @param after the contributor to apply after this contributor is applied.
	 * @return a composed contributor that first applies this contributor and then applies the {@code after} contributor.
	 */
	default CassandraRepositoryFragmentsContributor andThen(CassandraRepositoryFragmentsContributor after) {

		Assert.notNull(after, "CassandraRepositoryFragmentsContributor must not be null");

		return new CassandraRepositoryFragmentsContributor() {

			@Override
			public RepositoryFragments contribute(RepositoryMetadata metadata,
					CassandraEntityInformation<?, ?> entityInformation, CassandraOperations operations) {
				return CassandraRepositoryFragmentsContributor.this.contribute(metadata, entityInformation, operations)
						.append(after.contribute(metadata, entityInformation, operations));
			}

			@Override
			public RepositoryFragments describe(RepositoryMetadata metadata) {
				return CassandraRepositoryFragmentsContributor.this.describe(metadata).append(after.describe(metadata));
			}
		};
	}

	/**
	 * Creates {@link RepositoryFragments} based on {@link RepositoryMetadata} to add
	 * Cassandra-specific extensions.
	 *
	 * @param metadata repository metadata.
	 * @param entityInformation must not be {@literal null}.
	 * @param operations must not be {@literal null}.
	 * @return {@link RepositoryFragments} to be added to the repository.
	 */
	RepositoryFragments contribute(RepositoryMetadata metadata,
			CassandraEntityInformation<?, ?> entityInformation, CassandraOperations operations);

	/**
	 * Implementation of {@link CassandraRepositoryFragmentsContributor} that contributes empty fragments by default.
	 *
	 * @author Chris Bono
	 * @since 5.0
	 */
	enum DefaultCassandraRepositoryFragmentsContributor implements CassandraRepositoryFragmentsContributor {

		INSTANCE;

		@Override
		public RepositoryFragments contribute(RepositoryMetadata metadata,
				CassandraEntityInformation<?, ?> entityInformation, CassandraOperations operations) {
			return RepositoryFragments.empty();
		}

		@Override
		public RepositoryFragments describe(RepositoryMetadata metadata) {
			return RepositoryFragments.empty();
		}
	}
}
