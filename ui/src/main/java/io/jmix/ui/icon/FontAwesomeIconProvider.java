/*
 * Copyright 2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jmix.ui.icon;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import io.jmix.core.JmixOrder;
import io.jmix.ui.theme.ThemeConstantsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.jmix.core.common.util.Preconditions.checkNotEmptyString;

@Component("ui_FontAwesomeIconProvider")
@Order(JmixOrder.LOWEST_PRECEDENCE - 40)
public class FontAwesomeIconProvider implements IconProvider {

    private static final Logger log = LoggerFactory.getLogger(FontAwesomeIconProvider.class);

    protected static final List<String> FONT_AWESOME_PREFIXES = ImmutableList.of("font-icon:", "font-awesome-icon:");

    protected static final LoadingCache<String, Resource> iconsCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<String, Resource>() {
                @Nullable
                @Override
                public Resource load(@Nonnull String iconPath) {
                    return getIconNonCached(iconPath);
                }
            });

    @Autowired
    protected ThemeConstantsManager themeConstantsManager;

    @Nullable
    protected static Resource getIconNonCached(String iconName) {
        Resource resource = null;

        try {
            resource = ((Resource) FontAwesome.class
                    .getDeclaredField(iconName)
                    .get(null));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            log.warn("There is no icon with name {} in the FontAwesome icon set", iconName);
        }

        return resource;
    }

    @Override
    public Resource getIconResource(String iconPath) {
        checkNotEmptyString(iconPath, "Icon path should not be empty");

        String iconName = iconPath.contains(":") ? iconPath.split(":")[1] : iconPath;

        return iconsCache.getUnchecked(iconName);
    }

    @Override
    public boolean canProvide(@Nullable String iconPath) {
        if (iconPath == null || iconPath.isEmpty() || !isFontIconsEnabled()) {
            return false;
        }

        for (String prefix : FONT_AWESOME_PREFIXES) {
            if (iconPath.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    protected boolean isFontIconsEnabled() {
        return themeConstantsManager.getConstants().getBoolean("jmix.ui.useFontIcons", true);
    }
}