package com.masterworks.masterworks.util.streams;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import com.mojang.datafixers.util.Either;

public interface EitherStream<T1, T2> {
    <R> Stream<? extends R> map(Function<? super T1, ? extends R> on1,
            Function<? super T2, ? extends R> on2);

    default <R> Stream<? extends R> flatMap(
            Function<? super T1, ? extends Stream<? extends R>> onFirst,
            Function<? super T2, ? extends Stream<? extends R>> onSecond) {
        return map(onFirst, onSecond).flatMap(Function.identity());
    }

    static <T1, T2> EitherStream<T1, T2> from(Stream<Either<T1, T2>> stream) {
        return new EitherStream<>() {
            @Override
            public <R> Stream<R> map(Function<? super T1, ? extends R> on1,
                    Function<? super T2, ? extends R> on2) {
                return stream.map(either -> either.map(on1, on2));
            }

            @Override
            public <R> Stream<R> flatMap(Function<? super T1, ? extends Stream<? extends R>> on1,
                    Function<? super T2, ? extends Stream<? extends R>> on2) {
                return stream.flatMap(either -> either.map(on1, on2));
            }
        };
    }

    static <T1, T2, U> EitherStream<T1, T2> pick(Stream<Optional<T1>> stream1, Stream<T2> stream2) {
        return new EitherStream<>() {
            @Override
            public <R> Stream<? extends R> map(Function<? super T1, ? extends R> on1,
                    Function<? super T2, ? extends R> on2) {
                var picker =
                        new Impl.PickIterator<>(stream1.iterator(), stream2.iterator(), on1, on2);

                Spliterator<R> spliterator =
                        Spliterators.spliteratorUnknownSize(picker, Spliterator.NONNULL);
                Stream<R> result = StreamSupport.stream(spliterator, false);

                result = result.onClose(stream1::close).onClose(stream2::close);
                return result;
            }
        };
    }

    class Impl {
        private record PickIterator<T1, T2, R>(Iterator<Optional<T1>> iter1, Iterator<T2> iter2,
                Function<? super T1, ? extends R> on1, Function<? super T2, ? extends R> on2)
                implements Iterator<R> {

            @Override
            public boolean hasNext() {
                return iter1.hasNext() && iter2.hasNext();
            }

            @Override
            public R next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }

                Optional<T1> value1 = iter1.next();
                T2 value2 = iter2.next();

                // for dubious type inference reasons, orElseGet cannot be used here
                if (value1.isPresent()) {
                    return on1.apply(value1.get());
                } else {
                    return on2.apply(value2);
                }
            }

        }
    }
}
