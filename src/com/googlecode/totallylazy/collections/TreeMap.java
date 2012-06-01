package com.googlecode.totallylazy.collections;

import com.googlecode.totallylazy.None;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Segment;
import com.googlecode.totallylazy.comparators.Comparators;
import com.googlecode.totallylazy.iterators.SegmentIterator;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TreeMap<K, V> implements ImmutableMap<K, V> {
    private final ImmutableMap<K, V> left;
    private final K key;
    private final V value;
    private final ImmutableMap<K, V> right;
    private final Comparator<K> comparator;

    TreeMap(ImmutableMap<K, V> left, K key, V value, ImmutableMap<K, V> right, Comparator<K> comparator) {
        this.left = left;
        this.key = key;
        this.value = value;
        this.right = right;
        this.comparator = comparator;
    }

    static <K extends Comparable<? super K>, V> TreeMap<K, V> tree(ImmutableMap<K, V> left, Pair<K, V> pair, ImmutableMap<K, V> right) {
        return TreeMap.<K, V>tree(left, pair.first(), pair.second(), right, Comparators.<K>ascending());
    }

    static <K extends Comparable<? super K>, V> TreeMap<K, V> tree(ImmutableMap<K, V> left, K key, V value, ImmutableMap<K, V> right) {
        return new TreeMap<K, V>(left, key, value, right, Comparators.<K>ascending());
    }

    static <K, V> TreeMap<K, V> tree(ImmutableMap<K, V> left, K key, V value, ImmutableMap<K, V> right, Comparator<K> comparator) {
        return new TreeMap<K, V>(left, key, value, right, comparator);
    }

    static <K, V> TreeMap<K, V> tree(ImmutableMap<K, V> left, Pair<K, V> pair, ImmutableMap<K, V> right, Comparator<K> comparator) {
        return new TreeMap<K, V>(left, pair.first(), pair.second(), right, comparator);
    }

    static <K extends Comparable<? super K>, V> TreeMap<K, V> tree(K key, V value) {
        return tree(EmptyMap.<K, V>empty(), key, value, EmptyMap.<K, V>empty());
    }

    static <K, V> TreeMap<K, V> tree(K key, V value, Comparator<K> comparator) {
        return tree(EmptyMap.<K, V>empty(comparator), key, value, EmptyMap.<K, V>empty(comparator), comparator);
    }

    @Override
    public ImmutableList<Pair<K, V>> immutableList() {
        return join(ImmutableList.constructors.<Pair<K, V>>empty());
    }

    @Override
    public Option<V> get(K other) {
        int difference = comparator.compare(other, key);
        if (difference == 0) return Option.option(value);
        if (difference < 0) return left.get(other);
        return right.get(other);
    }

    @Override
    public ImmutableMap<K, V> put(K key, V value) {
        return cons(Pair.pair(key, value));
    }

    @Override
    public Option<V> find(Predicate<? super K> predicate) {
        if(predicate.matches(key)) return Option.some(value);
        Option<V> left = this.left.find(predicate);
        if(left.isEmpty()) return right.find(predicate);
        return left;
    }

    @Override
    public ImmutableMap<K, V> filterKeys(Predicate<? super K> predicate) {
        if(predicate.matches(key)) return tree(left.filterKeys(predicate), key, value, right.filterKeys(predicate), comparator);
        return left.filterKeys(predicate).join(right.filterKeys(predicate));
    }

    @Override
    public <C extends Segment<Pair<K, V>, C>> C join(C rest) {
        return left.join(right.join(rest).cons(Pair.pair(key, value)));
    }

    @Override
    public ImmutableMap<K, V> cons(Pair<K, V> newValue) {
        int difference = comparator.compare(newValue.first(), key);
        if (difference == 0) return tree(left, newValue, right, comparator);
        if (difference < 0) return tree(left.cons(newValue), key, value, right, comparator);
        return tree(left, key, value, right.cons(newValue), comparator);
    }

    @Override
    public boolean contains(K other) {
        int difference = comparator.compare(other, key);
        if (difference == 0) return key.equals(other);
        if (difference < 0) return left.contains(other);
        return right.contains(other);
    }

    @Override
    public int hashCode() {
        return 19 * value.hashCode() * left.hashCode() * right.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TreeMap && value.equals(((TreeMap) obj).value) && left.equals(((TreeMap) obj).left) && right.equals(((TreeMap) obj).right);
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left, value, right);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Pair<K, V> head() throws NoSuchElementException {
        return Pair.pair(key, value);
    }

    @Override
    public ImmutableMap<K, V> tail() throws NoSuchElementException {
        return left.join(right);
    }

    @Override
    public Iterator<Pair<K, V>> iterator() {
        return new SegmentIterator<Pair<K, V>, ImmutableList<Pair<K, V>>>(immutableList());
    }
}
