/*
 * Copyright (c) xgraza 2025
 */

package us.xgraza.xcmd.registry;

import us.xgraza.xcmd.executor.ICommandExecutor;

/**
 * @author xgraza
 * @since 08/06/2025
 * Skeleton class to create a command registry
 */
public interface ICommandRegistry
{
    /**
     * Get an executor via its alias
     *
     * @param alias the alias attached to the {@link ICommandExecutor}
     * @param <T>   the {@link ICommandExecutor} type
     * @return typeof {@link T}
     */
    <T extends ICommandExecutor> T getExecutor(final String alias);

    /**
     * @return The command start identifier (prefix)
     */
    String getCommandPrefix();
}
