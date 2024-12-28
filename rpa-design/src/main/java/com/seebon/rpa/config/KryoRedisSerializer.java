package com.seebon.rpa.config;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import de.javakaffee.kryoserializers.*;
import de.javakaffee.kryoserializers.guava.*;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.lang.reflect.InvocationHandler;
import java.util.*;

/**
 * kryo serializer 缓存对象序列化
 *
 * @param <T>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class KryoRedisSerializer<T> implements RedisSerializer<T> {

    @Override
    public byte[] serialize(Object obj) throws SerializationException {
        if (obj == null) {
            return new byte[0];
        }
        Kryo kryo = kryos.borrow();
        Output output = new Output(256, -1);
        try {
            kryo.writeClassAndObject(output, obj);
            return output.toBytes();
        } finally {
            kryos.release(kryo);
            if (output != null) {
                try {
                    output.flush();
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Kryo kryo = kryos.borrow();
        Input input = null;
        try {
            input = new Input(bytes);
            return (T) kryo.readClassAndObject(input);
        } finally {
            kryos.release(kryo);
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static KryoPool kryos = new KryoPool.Builder(() -> {
        Kryo kryo = new KryoReflectionFactorySupport() {
            @Override
            public Serializer<?> getDefaultSerializer(final Class type) {
                if (EnumSet.class.isAssignableFrom(type)) {
                    return new EnumSetSerializer();
                }
                if (EnumMap.class.isAssignableFrom(type)) {
                    return new EnumMapSerializer();
                }
                if (Collection.class.isAssignableFrom(type)) {
                    return new CopyForIterateCollectionSerializer();
                }
                if (Map.class.isAssignableFrom(type)) {
                    return new CopyForIterateMapSerializer();
                }
                if (Date.class.isAssignableFrom(type)) {
                    return new DateSerializer(type);
                }
                return super.getDefaultSerializer(type);
            }
        };
        //支持对象循环引用
        kryo.setReferences(true);

        kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        kryo.register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
        kryo.register(Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
        kryo.register(Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
        kryo.register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());
        kryo.register(Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer());
        kryo.register(Collections.singletonMap("", "").getClass(), new CollectionsSingletonMapSerializer());
        kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
        kryo.register(InvocationHandler.class, new JdkProxySerializer());
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        SynchronizedCollectionsSerializer.registerSerializers(kryo);

        ImmutableListSerializer.registerSerializers(kryo);
        ImmutableSetSerializer.registerSerializers(kryo);
        ImmutableMapSerializer.registerSerializers(kryo);
        ImmutableMultimapSerializer.registerSerializers(kryo);
        ReverseListSerializer.registerSerializers(kryo);
        UnmodifiableNavigableSetSerializer.registerSerializers(kryo);

        ArrayListMultimapSerializer.registerSerializers(kryo);
        HashMultimapSerializer.registerSerializers(kryo);
        LinkedHashMultimapSerializer.registerSerializers(kryo);
        LinkedListMultimapSerializer.registerSerializers(kryo);
        TreeMultimapSerializer.registerSerializers(kryo);

        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        return kryo;
    }).softReferences().build();
}
