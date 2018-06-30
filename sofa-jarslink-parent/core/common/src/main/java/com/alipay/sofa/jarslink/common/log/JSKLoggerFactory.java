/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.jarslink.common.log;

import com.alipay.sofa.common.log.LoggerSpaceManager;

/**
 * Logger Factory for SOFAJarslink
 *
 * @author qilong.zql
 * @since 0.1.0
 */
public class JSKLoggerFactory {
    public static final String  SOFA_ARK_LOGGER_SPACE        = "com.alipay.sofa.jarslink";

    private static final String SOFA_ARK_DEFAULT_LOGGER_NAME = "com.alipay.sofa.jarslink";

    public static JSKLogger     defaultLogger                = getLogger(SOFA_ARK_DEFAULT_LOGGER_NAME);

    public static JSKLogger getLogger(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return getLogger(clazz.getCanonicalName());
    }

    public static JSKLogger getLogger(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return new JSKLogger(LoggerSpaceManager.getLoggerBySpace(name, SOFA_ARK_LOGGER_SPACE));
    }

    public static JSKLogger getDefaultLogger() {
        return defaultLogger;
    }
}