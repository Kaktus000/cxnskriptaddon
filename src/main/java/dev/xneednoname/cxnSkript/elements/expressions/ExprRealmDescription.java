package dev.xneednoname.cxnSkript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import de.cytooxien.realms.api.Action;
import de.cytooxien.realms.api.RealmInformationProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprRealmDescription extends SimpleExpression<String> {

    private static final LegacyComponentSerializer SERIALIZER_TO_ADVENTURE = LegacyComponentSerializer.builder()
            .hexColors()
            .character('&')
            .build();

    private static final LegacyComponentSerializer SERIALIZER_TO_LEGACY_STRING = LegacyComponentSerializer.legacySection();

    static {
        Skript.registerExpression(
                ExprRealmDescription.class,
                String.class,
                ExpressionType.PROPERTY,
                "[the] realm['s] description"
        );
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "the realm description";
    }

    @Override
    @Nullable
    protected String[] get(Event event) {
        RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
        if (provider == null) {
            Skript.warning("RealmInformationProvider service not available");
            return null;
        }

        try {
            String description = provider.description();
            Component component = SERIALIZER_TO_LEGACY_STRING.deserialize(description);
            return new String[]{SERIALIZER_TO_ADVENTURE.serialize(component)};
        } catch (Exception e) {
            Skript.warning("Failed to get realm description: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? CollectionUtils.array(String.class) : null;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (mode != Changer.ChangeMode.SET || delta == null || delta.length == 0 || !(delta[0] instanceof String)) {
            return;
        }

        String newDescription = (String) delta[0];
        if (newDescription == null || newDescription.isEmpty()) {
            return;
        }

        // Async execution for rate-limited API call
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("CxnSkript"), () -> {
            RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
            if (provider == null) {
                Skript.warning("RealmInformationProvider service not available");
                return;
            }

            try {
                Component component = SERIALIZER_TO_ADVENTURE.deserialize(newDescription);
                String legacyDescription = SERIALIZER_TO_LEGACY_STRING.serialize(component);
                Action<Void> action = provider.changeDescription(legacyDescription);

                // Handle result in main thread
                Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CxnSkript"), () -> {
                    if (action.rateLimited()) {
                        Skript.warning("Realm description update rate-limited! Try again later.");
                        return;
                    }

                    if (!action.success()) {
                        Throwable error = action.throwable();
                        String errorMsg = error != null ? error.getMessage() : "Unknown error";
                        Skript.warning("Failed to update realm description: " + errorMsg);
                    }
                });
            } catch (Exception e) {
                Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CxnSkript"), () -> {
                    Skript.warning("Error while updating realm description: " + e.getMessage());
                });
            }
        });
    }
}