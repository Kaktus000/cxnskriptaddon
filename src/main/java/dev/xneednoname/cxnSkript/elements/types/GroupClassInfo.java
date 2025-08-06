package dev.xneednoname.cxnSkript.elements.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import de.cytooxien.realms.api.RealmPermissionProvider;
import de.cytooxien.realms.api.model.Group;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.Set;
import java.util.UUID;

public class GroupClassInfo {

    static {
        Classes.registerClass(new ClassInfo<>(Group.class, "group")
                .user("groups?")
                .name("Group")
                .description("Represents a permission group with name, prefix, color and permissions.")
                .since("1.0")
                .defaultExpression(new EventValueExpression<>(Group.class))
                .parser(new Parser<Group>() {
                    @Override
                    public @Nullable Group parse(String input, ParseContext context) {
                        RealmPermissionProvider provider = Bukkit.getServicesManager().load(RealmPermissionProvider.class);
                        if (provider == null) return null;
                        return provider.firstGroupWithName(input);
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return true; // Wichtig: Auf true setzen!
                    }

                    @Override
                    public String toString(Group group, int flags) {
                        return group.name();
                    }

                    @Override
                    public String toVariableNameString(Group group) {
                        return "group:" + group.uniqueId();
                    }
                })
                .serializer(new Serializer<Group>() {
                    @Override
                    public Fields serialize(Group group) throws NotSerializableException {
                        Fields fields = new Fields();
                        fields.putPrimitive("uuid", group.uniqueId().toString());
                        fields.putPrimitive("name", group.name());
                        fields.putPrimitive("prefix", GsonComponentSerializer.gson().serialize(group.prefix()));
                        fields.putPrimitive("color", String.valueOf(group.color()));
                        fields.putPrimitive("priority", group.priority());
                        fields.putObject("permissions", group.permissions());
                        return fields;
                    }

                    @Override
                    public void deserialize(Group group, Fields fields) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Group deserialize(Fields fields) throws StreamCorruptedException {
                        return Group.create(
                                UUID.fromString(fields.getPrimitive("uuid", String.class)),
                                fields.getPrimitive("name", String.class),
                                GsonComponentSerializer.gson().deserialize(fields.getPrimitive("prefix", String.class)),
                                fields.getPrimitive("color", String.class).charAt(0),
                                fields.getPrimitive("priority", Integer.class),
                                0, // requiredBoosts wird nicht serialisiert
                                (Set<String>) fields.getObject("permissions")
                        );
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return false;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }
                }));
    }
}