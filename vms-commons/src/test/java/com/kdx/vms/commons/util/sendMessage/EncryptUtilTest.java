package com.kdx.vms.commons.util.sendMessage;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by yangshuxi on 2018/3/16.
 */
public class EncryptUtilTest {

    Random random = new Random();

    @Test
    public void md5Digest() throws Exception {
        AtomicInteger atomicInteger = new AtomicInteger(100000);
        StopWatch sw = new StopWatch();
        sw.start();
        Set<Integer> set = Stream.generate(atomicInteger::incrementAndGet).parallel().limit(899999).collect(Collectors.toSet());
        set.stream().limit(400).forEach(System.out::println);
        sw.stop();

        System.out.println(sw.getTime());
    }

    @Test
    public void base64Encoder() throws Exception {
        Set<Integer> s = new HashSet<>();

//        StopWatch sw = new StopWatch();
//        sw.start();
        long start = System.nanoTime();
        while (s.size() < 400) {
            int atom = random.nextInt(899999) + 100000;
            s.add(atom);
//            System.out.println(atom);
        }
        System.out.println(System.nanoTime() - start);
//        sw.stop();
//        System.out.println(sw.getTime());
//        System.out.println(s.stream().distinct().count());

    }

    @Test
    public void base64Decoder() throws Exception {

    }

    static class Atom {
        String id;
        Integer v;

        public static Atom of(Integer v) {
            Atom a = new Atom();
            a.setId(UUID.randomUUID().toString());
            a.v = v;
            return a;
        }

        public String getId() {
            return id;
        }

        public Atom setId(String id) {
            this.id = id;
            return this;
        }

        public Integer getV() {
            return v;
        }

        public Atom setV(Integer v) {
            this.v = v;
            return this;
        }
    }

}