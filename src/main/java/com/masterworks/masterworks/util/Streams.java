package com.masterworks.masterworks.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams {
    public static class Zip<A, B> {
        private final Stream<A> streamA;
        private final Stream<B> streamB;

        public Zip(Stream<A> a, Stream<B> b) {
            this.streamA = a;
            this.streamB = b;
        }

        public <R> Stream<R> map(BiFunction<? super A, ? super B, ? extends R> mapper) {
            Iterator<A> iterA = streamA.iterator();
            Iterator<B> iterB = streamB.iterator();

            Iterator<R> resultIter = new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return iterA.hasNext() && iterB.hasNext();
                }

                @Override
                public R next() {
                    return mapper.apply(iterA.next(), iterB.next());
                }
            };

            Spliterator<R> spliterator =
                    Spliterators.spliteratorUnknownSize(resultIter, Spliterator.NONNULL);
            return StreamSupport.stream(spliterator, false);
        }

        public <R> Stream<R> flatMap(
                BiFunction<? super A, ? super B, ? extends Stream<? extends R>> mapper) {
            return map(mapper).flatMap(Function.identity());
        }
    }

    public static <A, B> Zip<A, B> zip(Stream<A> a, Stream<B> b) {
        return new Zip<>(a, b);
    }
}
