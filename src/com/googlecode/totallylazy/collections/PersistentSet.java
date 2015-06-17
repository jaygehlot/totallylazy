package com.googlecode.totallylazy.collections;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Filterable;
import com.googlecode.totallylazy.Foldable;
import com.googlecode.totallylazy.Functor;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Segment;
import com.googlecode.totallylazy.Sequence;

import java.util.Set;

public interface PersistentSet<T> extends Set<T>, Iterable<T>, Segment<T>, PersistentCollection<T>, Functor<T>, Foldable<T>, Filterable<T> {
    Option<T> lookup(T value);

    Option<T> find(Predicate<? super T> predicate);

    @Override
    PersistentSet<T> empty();

    @Override
    PersistentSet<T> cons(T head);

    @Override
    PersistentSet<T> delete(T value);

    @Override
    PersistentSet<T> filter(Predicate<? super T> predicate);

    <NewT> PersistentSet<NewT> map(Function1<? super T, ? extends NewT> transformer);

    PersistentList<T> toPersistentList();

    Sequence<T> toSequence();

    Set<T> toSet();



    class functions extends Segment.functions {
        public static <T> Function1<PersistentSet<T>,Option<T>> get(final T value) {
            return new Function1<PersistentSet<T>, Option<T>>() {
                @Override
                public Option<T> call(PersistentSet<T> set) throws Exception {
                    return set.lookup(value);
                }
            };
        }
    }
}