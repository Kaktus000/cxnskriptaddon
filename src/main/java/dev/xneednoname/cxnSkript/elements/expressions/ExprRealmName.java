package dev.xneednoname.cxnSkript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
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

public class ExprRealmName extends SimpleExpression<Object> {

    private static final LegacyComponentSerializer SERIALIZER_TO_ADVENTURE = LegacyComponentSerializer.builder()
            .hexColors()
            .character('&')
            .build();

    private static final LegacyComponentSerializer SERIALIZER_TO_MINIMESSAGE = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    static {
        Skript.registerExpression(
                ExprRealmName.class,
                Object.class,
                ExpressionType.PROPERTY,
                "[the] realm['s] name"
        );
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "the realm name";
    }

    @Override
    @Nullable
    protected Object[] get(Event event) {
        RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
        if (provider == null) {
            Skript.warning("RealmInformationProvider service not available");
            return null;
        }

        try {
            Component nameComponent = provider.realmDisplayName();
            // Konvertiere zu MiniMessage-Format (Skript native <#rrggbb>)
            String miniMessageString = convertToMiniMessageFormat(nameComponent);
            return new Object[]{miniMessageString};
        } catch (Exception e) {
            Skript.warning("Failed to get realm name: " + e.getMessage());
            return null;
        }
    }

    /**
     * Konvertiert eine Adventure-Komponente in MiniMessage-Format
     */
    private String convertToMiniMessageFormat(Component component) {
        // Zuerst zu Legacy-Text mit ungewöhnlichem Hex-Format (MiniMessage-kompatibel)
        String legacyText = SERIALIZER_TO_MINIMESSAGE.serialize(component);

        // Ersetze &x&r&r&g&g&b&b durch <#rrggbb>
        return legacyText.replaceAll("&x&([0-9A-Fa-f])&([0-9A-Fa-f])&([0-9A-Fa-f])&([0-9A-Fa-f])&([0-9A-Fa-f])&([0-9A-Fa-f])", "<#$1$2$3$4$5$6>");
    }

    /**
     * Konvertiert MiniMessage-Format zurück zu Adventure-Komponente
     */
    private Component convertFromMiniMessageFormat(String miniMessageText) {
        // Ersetze <#rrggbb> durch &x&r&r&g&g&b&b
        String legacyText = miniMessageText.replaceAll("<#([0-9A-Fa-f]{6})>", "&x&$1&$2&$3&$4&$5&$6")
                .replaceAll("<#([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])>", "&x&$1&$2&$3&$4&$5&$6");

        return SERIALIZER_TO_MINIMESSAGE.deserialize(legacyText);
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? CollectionUtils.array(Object.class) : null;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (mode != Changer.ChangeMode.SET || delta == null || delta.length == 0 || delta[0] == null) {
            return;
        }

        Component component = null;

        if (delta[0] instanceof Component) {
            component = (Component) delta[0];
        } else if (delta[0] instanceof String) {
            String newName = (String) delta[0];
            if (newName.isEmpty()) {
                return;
            }
            // Verwende MiniMessage-Format für die Eingabe
            component = convertFromMiniMessageFormat(newName);
        } else {
            // Keine automatische Konvertierung mehr, da Converters deprecated
            Skript.warning("Cannot convert " + delta[0].getClass().getSimpleName() + " to Component. Please use String or Component.");
            return;
        }

        final Component finalComponent = component;

        // Async execution for rate-limited API call
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("CxnSkript"), () -> {
            RealmInformationProvider provider = Bukkit.getServicesManager().load(RealmInformationProvider.class);
            if (provider == null) {
                Skript.warning("RealmInformationProvider service not available");
                return;
            }

            try {
                Action<Void> action = provider.changeName(finalComponent);

                // Handle result in main thread
                Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CxnSkript"), () -> {
                    if (action.rateLimited()) {
                        Skript.warning("Realm name update rate-limited! Try again later.");
                        return;
                    }

                    if (!action.success()) {
                        Throwable error = action.throwable();
                        String errorMsg = error != null ? error.getMessage() : "Unknown error";
                        Skript.warning("Failed to update realm name: " + errorMsg);
                    }
                });
            } catch (Exception e) {
                Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("CxnSkript"), () -> {
                    Skript.warning("Error while updating realm name: " + e.getMessage());
                });
            }
        });
    }
}