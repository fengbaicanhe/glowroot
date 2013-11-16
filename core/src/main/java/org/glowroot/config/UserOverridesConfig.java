/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glowroot.config;

import checkers.igj.quals.Immutable;
import checkers.nullness.quals.Nullable;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import dataflow.quals.Pure;

import org.glowroot.config.JsonViews.UiView;
import org.glowroot.markers.UsedByJsonBinding;

/**
 * Immutable structure to hold the user overrides config.
 * 
 * @author Trask Stalnaker
 * @since 0.5
 */
@Immutable
public class UserOverridesConfig {

    @Nullable
    private final String userId;
    // store threshold of -1 means use general config store threshold
    // for session traces, the real threshold is the minimum of this and the general threshold
    private final int storeThresholdMillis;
    private final boolean fineProfiling;
    private final String version;

    static UserOverridesConfig getDefault() {
        final String userId = null;
        final int storeThresholdMillis = 0;
        final boolean fineProfiling = true;
        return new UserOverridesConfig(userId, storeThresholdMillis, fineProfiling);
    }

    public static Overlay overlay(UserOverridesConfig base) {
        return new Overlay(base);
    }

    @VisibleForTesting
    public UserOverridesConfig(@Nullable String userId, int storeThresholdMillis,
            boolean fineProfiling) {
        this.userId = userId;
        this.storeThresholdMillis = storeThresholdMillis;
        this.fineProfiling = fineProfiling;
        version = VersionHashes.sha1(userId, storeThresholdMillis, fineProfiling);
    }

    @Nullable
    public String getUserId() {
        return userId;
    }

    public int getStoreThresholdMillis() {
        return storeThresholdMillis;
    }

    public boolean isFineProfiling() {
        return fineProfiling;
    }

    @JsonView(UiView.class)
    public String getVersion() {
        return version;
    }

    @Override
    @Pure
    public String toString() {
        return Objects.toStringHelper(this)
                .add("userId", userId)
                .add("storeThresholdMillis", storeThresholdMillis)
                .add("fineProfiling", fineProfiling)
                .add("version", version)
                .toString();
    }

    // for overlaying values on top of another config using ObjectMapper.readerForUpdating()
    @UsedByJsonBinding
    public static class Overlay {

        @Nullable
        private String userId;
        private int storeThresholdMillis;
        private boolean fineProfiling;

        private Overlay(UserOverridesConfig base) {
            userId = base.userId;
            storeThresholdMillis = base.storeThresholdMillis;
            fineProfiling = base.fineProfiling;
        }
        public void setUserId(@Nullable String userId) {
            this.userId = userId;
        }
        public void setStoreThresholdMillis(int storeThresholdMillis) {
            this.storeThresholdMillis = storeThresholdMillis;
        }
        public void setFineProfiling(boolean fineProfiling) {
            this.fineProfiling = fineProfiling;
        }
        public UserOverridesConfig build() {
            return new UserOverridesConfig(userId, storeThresholdMillis, fineProfiling);
        }
    }
}
