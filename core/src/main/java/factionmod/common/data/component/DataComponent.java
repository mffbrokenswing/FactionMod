package factionmod.common.data.component;

import lombok.NonNull;
import lombok.Value;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

@Value
public class DataComponent<T> {

    private String modId;
    private String name;
    private T baseValue;
    private Class<T> type;
    private Function<T, NBTTagCompound> serializer;
    private Function<NBTTagCompound, T> deserializer;

    public NBTTagCompound serialize(@NonNull Object o) {
        return this.serializer.apply(this.type.cast(o));
    }

    public T deserialize(NBTTagCompound nbt) {
        return this.deserializer.apply(nbt);
    }

    public String getNamespace() {
        return this.getModId() + ":" + this.getName();
    }

    // Help functions

    public static <T> DataComponent<T> of(String modId, String name, T baseValue, Class<T> type,
                                          Function<T, NBTTagCompound> serializer,
                                          Function<NBTTagCompound, T> deserializer)
    {
        return new DataComponent<>(modId, name, baseValue, type, serializer, deserializer);
    }

    private static <T> DataComponent<T> wrapped(String modId, String name, T baseValue, Class<T> type,
                                                TriConsumer<NBTTagCompound, String, T> serializer,
                                                BiFunction<NBTTagCompound, String, T> deserializer) {
        return DataComponent.of(
                modId,
                name,
                baseValue,
                type,
                value ->  {
                    NBTTagCompound nbt = new NBTTagCompound();
                    serializer.accept(nbt, "value", value);
                    return nbt;
                },
                nbt -> deserializer.apply(nbt, "value")
        );
    }

    public static DataComponent<String> ofString(String modId, String name, String baseValue) {
        return wrapped(modId, name, baseValue, String.class, NBTTagCompound::setString, NBTTagCompound::getString);
    }

    public static DataComponent<Integer> ofInt(String modId, String name, int baseValue) {
        return wrapped(modId, name, baseValue, Integer.class, NBTTagCompound::setInteger, NBTTagCompound::getInteger);
    }

    public static DataComponent<Float> ofFloat(String modId, String name, float baseValue) {
        return wrapped(modId, name, baseValue, Float.class, NBTTagCompound::setFloat, NBTTagCompound::getFloat);
    }

    public static DataComponent<Boolean> ofBool(String modId, String name, boolean baseValue) {
        return wrapped(modId, name, baseValue, Boolean.class, NBTTagCompound::setBoolean, NBTTagCompound::getBoolean);
    }

    public static DataComponent<Byte> ofByte(String modId, String name, byte baseValue) {
        return wrapped(modId, name, baseValue, Byte.class, NBTTagCompound::setByte, NBTTagCompound::getByte);
    }

    public static DataComponent<Long> ofLong(String modId, String name, long baseValue) {
        return wrapped(modId, name, baseValue, Long.class, NBTTagCompound::setLong, NBTTagCompound::getLong);
    }

    public static DataComponent<UUID> ofUUID(String modId, String name, UUID baseValue) {
        return wrapped(modId, name, baseValue, UUID.class, NBTTagCompound::setUniqueId, NBTTagCompound::getUniqueId);
    }

}
