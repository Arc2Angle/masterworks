package com.masterworks.masterworks.util.stream;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface BiStream<T1, T2> {
    <R> Stream<R> map(BiFunction<? super T1, ? super T2, ? extends R> mapper);

    default <R> Stream<R> flatMap(
            BiFunction<? super T1, ? super T2, ? extends Stream<? extends R>> mapper) {
        return map(mapper).flatMap(Function.identity());
    }

    static <T1, T2> BiStream<T1, T2> zip(Stream<T1> stream1, Stream<T2> stream2) {
        return new BiStream<>() {
            @Override
            public <R> Stream<R> map(BiFunction<? super T1, ? super T2, ? extends R> mapper) {
                Impl.ZipIterator<T1, T2, R> zipper =
                        new Impl.ZipIterator<>(stream1.iterator(), stream2.iterator(), mapper);

                Spliterator<R> spliterator = Spliterators.spliteratorUnknownSize(zipper, 0);
                Stream<R> result = StreamSupport.stream(spliterator, false);

                result = result.onClose(stream1::close).onClose(stream2::close);
                return result;
            }
        };
    }

    static <T> BiStream<Integer, T> enumerate(Stream<T> stream) {
        return new BiStream<>() {
            @Override
            public <R> Stream<R> map(BiFunction<? super Integer, ? super T, ? extends R> mapper) {
                var zipper = new Impl.ZipIterator<>(Stream.iterate(0, i -> i + 1).iterator(),
                        stream.iterator(), mapper);

                Spliterator<R> spliterator = Spliterators.spliteratorUnknownSize(zipper, 0);
                Stream<R> result = StreamSupport.stream(spliterator, false);

                result = result.onClose(stream::close);
                return result;
            }
        };
    }

    class Impl {
        private record ZipIterator<T1, T2, R>(Iterator<T1> iter1, Iterator<T2> iter2,
                BiFunction<? super T1, ? super T2, ? extends R> mapper) implements Iterator<R> {

            @Override
            public boolean hasNext() {
                return iter1.hasNext() && iter2.hasNext();
            }

            @Override
            public R next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }

                return mapper.apply(iter1.next(), iter2.next());
            }
        }
    }
}
