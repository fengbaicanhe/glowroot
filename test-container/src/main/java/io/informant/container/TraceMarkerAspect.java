/*
 * Copyright 2011-2013 the original author or authors.
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
package io.informant.container;

import io.informant.api.MessageSupplier;
import io.informant.api.MetricName;
import io.informant.api.PluginServices;
import io.informant.api.Span;
import io.informant.api.weaving.BindTarget;
import io.informant.api.weaving.BindTraveler;
import io.informant.api.weaving.IsEnabled;
import io.informant.api.weaving.OnAfter;
import io.informant.api.weaving.OnBefore;
import io.informant.api.weaving.Pointcut;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
public class TraceMarkerAspect {

    private static final PluginServices pluginServices =
            PluginServices.get("informant-test-container");

    @Pointcut(typeName = "io.informant.container.TraceMarker|io.informant.testkit.TraceMarker",
            methodName = "traceMarker", metricName = "mock trace marker")
    public static class TraceMarkerAdvice {

        private static final MetricName metricName =
                pluginServices.getMetricName(TraceMarkerAdvice.class);

        @IsEnabled
        public static boolean isEnabled() {
            return pluginServices.isEnabled();
        }

        @OnBefore
        public static Span onBefore(@BindTarget Object target) {
            String targetClassName = target.getClass().getName();
            return pluginServices.startTrace("trace marker / " + targetClassName,
                    MessageSupplier.from("{}.traceMarker()", targetClassName), metricName);
        }

        @OnAfter
        public static void onAfter(@BindTraveler Span span) {
            span.end();
        }
    }
}
